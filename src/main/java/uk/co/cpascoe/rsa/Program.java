package uk.co.cpascoe.rsa;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import uk.co.cpascoe.rsa.crypto.*;

public class Program {
    private static class HexOutputStream extends OutputStream {
        private OutputStream strm;

        public HexOutputStream(OutputStream strm) {
            this.strm = strm;
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.strm.write(Utils.bytesToHex(b).getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.write(Arrays.copyOfRange(b, off, off + len));
        }

        @Override
        public void write(int b) throws IOException {
            this.write(new byte[] {(byte)b});
        }

        @Override
        public void flush() throws IOException {
            this.strm.flush();
        }

        @Override
        public void close() throws IOException {
            this.strm.close();
        }
    }

    public static void main(String[] args) {
        try {

            if (args.length == 0) {
                System.err.println("No options provided");
                return;
            }

            boolean outputAsHex = false;

            int keySize = 0;
            InputStream input = System.in;
            OutputStream output = System.out;

            boolean encrypt = false;
            boolean decrypt = false;

            String keyFile = "";

            List<String> argList = new ArrayList<String>();

            for (String arg : args) { argList.add(arg); }

            while (argList.size() > 0) {
                String opt = argList.remove(0);

                if (opt.equals("-i") || opt.equals("--input")) {
                    if (argList.size() == 0) {
                        System.err.println("Expected input text after " + opt);
                        return;
                    }

                    String inputText = argList.remove(0);
                    input = new ByteArrayInputStream(inputText.getBytes(StandardCharsets.UTF_8));
                } else if (opt.equals("-f") || opt.equals("--input-file")) {
                    if (argList.size() == 0) {
                        System.err.println("Expected file name after " + opt);
                        return;
                    }

                    String filename = argList.remove(0);

                    try {
                        input = new FileInputStream(filename);
                    } catch (FileNotFoundException ex) {
                        System.err.printf("Input file '%s' not found%n", filename);
                        return;
                    }
                } else if (opt.equals("-o") || opt.equals("--output-file")) {
                    if (argList.size() == 0) {
                        System.err.println("Expected file name after " + opt);
                        return;
                    }

                    String filename = argList.remove(0);

                    try {
                        output = new FileOutputStream(filename, false);
                    } catch (FileNotFoundException ex) {
                        System.err.println("Cannot write to ouput file");
                        return;
                    } catch (SecurityException ex) {
                        System.err.println("Security manager has prevented writing to output file");
                        return;
                    }
                } else if (opt.equals("-n") || opt.equals("--new-key")) {
                    if (argList.size() < 2) {
                        System.err.println("Expected key size and key name after " + opt);
                        return;
                    }

                    String keySizeString = argList.remove(0);
                    String keyName = argList.remove(0);

                    try {
                        keySize = Integer.parseInt(keySizeString);
                    } catch (NumberFormatException ex) {
                        System.err.printf("Invalid key size: '%s', expected integer", keySizeString);
                        return;
                    }

                    if (keySize != 1024 && keySize != 2048 && keySize != 4096) {
                        System.err.println("Key size must be 1024, 2048, or 4096 bits");
                        return;
                    }

                    Program.generateKey(keySize, keyName);
                    return;
                } else if (opt.equals("-H") || opt.equals("--output-hex")) {
                    outputAsHex = true;
                } else if (opt.equals("-e") || opt.equals("--encrypt")) {
                    encrypt = true;
                } else if (opt.equals("-d") || opt.equals("--decrypt")) {
                    decrypt = true;
                } else if (opt.equals("-k") || opt.equals("--key-file")) {
                    if (argList.size() < 1) {
                        System.err.println("Expected key size and key name after " + opt);
                        return;
                    }

                    keyFile = argList.remove(0);
                }
            }

            if (encrypt && decrypt) {
                System.err.println("Cannot encrypt and decrypt!");
                return;
            }

            if (!encrypt && !decrypt) {
                System.err.println("You need to either encrypt or decrypt");
                return;
            }

            if (outputAsHex) {
                output = new HexOutputStream(output);
            }

            if (encrypt) {
                String json = Program.readJson(keyFile);

                RsaKey key;

                try {
                    key = RsaKey.importFromJson(json);
                } catch (Exception ex) {
                    System.err.println("Failed to parse key");
                    return;
                }

                RsaProvider rp = new RsaProvider(key);
                rp.encrypt(input, output);
            }

            if (decrypt) {
                String json = Program.readJson(keyFile);

                RsaPrivateKey key;
                try {
                    key = RsaPrivateKey.importFromJson(Program.readJson(keyFile));
                } catch (Exception ex) {
                    System.err.println("Failed to parse key (are you sure it's a private key?)");
                    return;
                }

                RsaProvider rp = new RsaProvider(key);
                rp.decrypt(input, output);
            }

            output.flush();
            output.close();

            System.err.println("Done!");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static void generateKey(int keySize, String name) {
        System.err.printf("Generating %s bit RSA Key, please wait... ", keySize);
        RsaPrivateKey key = new RsaPrivateKey(keySize);
        System.err.println("Done!");

        System.err.printf("Writing private key information to %s.json\n", name);
        try (PrintWriter writer = new PrintWriter(new FileWriter(name + ".json"))) {
            writer.write(key.exportToJson());
        } catch (IOException ex) {
            System.err.println("Unable to write private key");
            return;
        }

        System.err.printf("Writing public key information to %s_pub.json\n", name);
        try (PrintWriter writer = new PrintWriter(new FileWriter(name + "_pub.json"))) {
            writer.write(key.exportPublicKey().exportToJson());
        } catch (IOException ex) {
            System.err.println("Unable to write public key");
            return;
        }
    }

    private static String readJson(String filename) throws IOException {
        StringBuilder str = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line).append("\n");
            }
        }
        return str.toString();
    }
}
