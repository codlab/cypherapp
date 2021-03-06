package eu.codlab.cyphersend;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import eu.codlab.cyphersend.security.CypherRSA;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class Test {

    public static boolean test(KeyPair keys){

        String originalText="ORIGINAL TEXTE";
        // Encrypt the string using the public key
        final PublicKey publicKey = keys.getPublic();
        final byte[] cipherText = CypherRSA.encrypt(originalText, publicKey);

        // Decrypt the cipher text using the private key.
        final PrivateKey privateKey = keys.getPrivate();
        final String plainText = CypherRSA.decrypt(cipherText, privateKey);

        // Printing the Original, Encrypted and Decrypted Text
        return plainText.equals(originalText);

    }
}
