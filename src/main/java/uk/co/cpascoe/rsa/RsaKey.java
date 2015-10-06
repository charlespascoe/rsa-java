package uk.co.cpascoe.rsa;


public abstract class RsaKey {
    public abstract BigInteger publicExponentation(BigInteger val);
    public abstract byte[] exportDer();
    public abstract String exportPem();

    // public static RsaKey importDer(byte[]) {

    // }

    // public static RsaKey importPem(String) {

    // }
}
