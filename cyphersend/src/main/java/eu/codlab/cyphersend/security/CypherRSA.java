package eu.codlab.cyphersend.security;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import eu.codlab.cyphersend.dbms.config.controller.ConfigController;
import eu.codlab.cyphersend.dbms.config.model.Config;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class CypherRSA {
    public static final String ALGORITHM = "RSA";

    public static KeyPair generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(2048);
            final KeyPair key = keyGen.generateKeyPair();

            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static byte[] exportPublicKey(PublicKey key) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key.getEncoded());
        return x509EncodedKeySpec.getEncoded();
    }

    public static byte [] exportPrivateKey(PrivateKey key){
        PKCS8EncodedKeySpec x509EncodedKeySpec = new PKCS8EncodedKeySpec(key.getEncoded());
        return x509EncodedKeySpec.getEncoded();
    }

    public static PublicKey importPublicKey(byte[] encoded)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    public static PrivateKey importPrivateKey(byte [] encoded)
        throws NoSuchAlgorithmException,
                InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
    }

    public static void saveKeyPublicPrivate(Context context, PublicKey publicKey, PrivateKey privateKey){
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        ConfigController.getInstance(context)
                .setConfig(ConfigController.PUBLIC_KEY, new String(Base64Coder.encode(x509EncodedKeySpec.getEncoded())))
                .setConfig(ConfigController.PRIVATE_KEY, new String(Base64Coder.encode(pkcs8EncodedKeySpec.getEncoded())));
    }

    public static void saveKeyPair(Context context, KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        saveKeyPublicPrivate(context, publicKey, privateKey);
    }

    public static KeyPair loadKeyPair(Context context)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        if (!areKeysPresent(context))
            return null;

        Config pub = ConfigController.getInstance(context).getConfig(ConfigController.PUBLIC_KEY);
        Config priv = ConfigController.getInstance(context).getConfig(ConfigController.PRIVATE_KEY);
        byte[] encodedPublicKey = Base64Coder.decode(pub.getContent());
        byte[] encodedPrivateKey = Base64Coder.decode(priv.getContent());

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    public static PublicKey decodePublicKey(String string) {
        return decodePublicKey(Base64Coder.decode(string));
    }

    public static PublicKey decodePublicKey(byte[] key) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            return publicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean areKeysPresent(Context context) {
        Config pub =ConfigController.getInstance(context).getConfig(ConfigController.PUBLIC_KEY);
        Config priv =ConfigController.getInstance(context).getConfig(ConfigController.PRIVATE_KEY);
        return pub != null && pub.isContentSet() && priv != null && priv.isContentSet();
    }

    private static byte[] append(byte[] prefix, byte[] suffix) {
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i = 0; i < prefix.length; i++) {
            toReturn[i] = prefix[i];
        }
        for (int i = 0; i < suffix.length; i++) {
            toReturn[i + prefix.length] = suffix[i];
        }
        return toReturn;
    }

    public static byte[] encrypt(String text, PrivateKey key) {
        byte[] cipherText = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytes = text.getBytes();//"UTF-8"

            cipherText = blockCipher(cipher, bytes, Cipher.ENCRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public static byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytes = text.getBytes();//"UTF-8"

            cipherText = blockCipher(cipher, bytes, Cipher.ENCRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public static String decrypt(byte[] text, PublicKey key) {
        byte[] dectyptedText = null;
        try {

            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = blockCipher(cipher, text, Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(dectyptedText);//"UTF-8"
    }

    public static String decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {

            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = blockCipher(cipher, text, Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(dectyptedText);//"UTF-8"
    }

    private static byte[] blockCipher(Cipher cipher, byte[] bytes, int mode) throws IllegalBlockSizeException, BadPaddingException {
        // string initialize 2 buffers.
        // scrambled will hold intermediate results
        byte[] scrambled = new byte[0];

        // toReturn will hold the total result
        byte[] toReturn = new byte[0];
        // if we encrypt we use 100 byte long blocks. Decryption requires 128 byte long blocks (because of RSA)
        int length = (mode == Cipher.ENCRYPT_MODE) ? 200 : 256;

        // another buffer. this one will hold the bytes that have to be modified in this step
        byte[] buffer = new byte[length];

        for (int i = 0; i < bytes.length; i++) {

            // if we filled our buffer array we have our block ready for de- or encryption
            if ((i > 0) && (i % length == 0)) {
                //execute the operation
                scrambled = cipher.doFinal(buffer);
                // add the result to our total result.
                toReturn = append(toReturn, scrambled);
                // here we calculate the length of the next buffer required
                int newlength = length;

                // if newlength would be longer than remaining bytes in the bytes array we shorten it.
                if (i + length > bytes.length) {
                    newlength = bytes.length - i;
                }
                // clean the buffer array
                buffer = new byte[newlength];
            }
            // copy byte into our buffer.
            buffer[i % length] = bytes[i];
        }

        // this step is needed if we had a trailing buffer. should only happen when encrypting.
        // example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
        scrambled = cipher.doFinal(buffer);

        // final step before we can return the modified data.
        toReturn = append(toReturn, scrambled);

        return toReturn;
    }

}
