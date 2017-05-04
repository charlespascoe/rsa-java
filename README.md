# An Implementation of RSA in Java #

This was for a University assignment a while back; it is one possible way of implementing RSA from scratch, without using Java's BigInteger class. However, since it was created on a limited timescale, it has not been fully optimised, so it is slow, since the focus was on *how* the program was built.

## Usage ##

To build:

`$ ./gradlew build`

To generate a key pair:

`$ ./rsa-java -n 2048 'key_name'`

This will create two files: `key_name.json`, which contains public and private key information, and `key_name_pub.json`, which only contains public key information. Both can be used for encryption, but only `key_name.json` can be used for decryption.

To encrypt a file:

`$ ./rsa-java -e -f input.tar -o encrypted.bin -k key_name_pub.json`

If `-f` or `--input-file` is not defined, then it will read from `stdin`. If `-o` or `--output-file` is not defined, then it will write to `stdout`.

To decrypt a file:

`$ ./rsa-java -d -f encrypted.bin -o decrypted.tar -k key_name.json`

Other useful flags:

* `-i` - Pass a string for encryption
* `-H` - Output as hex
