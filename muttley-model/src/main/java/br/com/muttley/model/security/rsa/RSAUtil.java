package br.com.muttley.model.security.rsa;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * @author Joel Rodrigues Moreira on 08/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class RSAUtil {
    private static final String PUBLIC_KEY = "PUBLIC KEY";
    private static final String PRIVATE_KEY = "PRIVATE KEY";

    public static KeyPair createKeyPair() {
        return createKeyPair(1024);
    }

    public static KeyPair createKeyPair(final int size) {
        return createKeyPair(size, generateRandomString(1024));
    }

    public static KeyPair createKeyPair(final int size, final String seed) {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            final SecureRandom random = new SecureRandom();

            random.setSeed(seed.getBytes());
            keyGen.initialize(size, random); //Specify the RSA key length
            KeyPair keyPair = keyGen.generateKeyPair();

            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(final String fileName, final PrivateKey privateKey) {
        write(fileName, PRIVATE_KEY, privateKey);
    }

    public static void write(final File file, final PrivateKey privateKey) {
        write(file, PRIVATE_KEY, privateKey);
    }

    public static void write(final String fileName, final PublicKey publicKey) {
        write(fileName, PUBLIC_KEY, publicKey);
    }

    public static void write(final File file, final PublicKey publicKey) {
        write(file, PUBLIC_KEY, publicKey);
    }

    private static void write(final String fileName, final String description, final Key key) {
        write(new File(fileName), description, key);
    }

    private static void write(final File file, final String description, final Key key) {
        file.getParentFile().mkdirs();
        try (final PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            pemWriter.writeObject(new PemObject(description, key.getEncoded()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateRandomString(final int size) {
        final int leftLimit = 48; // numeral '0'
        final int rightLimit = 122; // letter 'z'

        return new Random()
                .ints(leftLimit, rightLimit + 1)
                .parallel()
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static byte[] parsePEMFile(File pemFile) {
        if (!pemFile.isFile() || !pemFile.exists()) {
            throw new RuntimeException(String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
        }
        try {
            return parsePEMFile(new FileReader(pemFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] parsePEMFile(final InputStream pemFile) {
        return parsePEMFile(new InputStreamReader(pemFile));
    }

    private static byte[] parsePEMFile(final Reader pemFile) {
        try (final PemReader reader = new PemReader(pemFile)) {
            return reader.readPemObject().getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] parsePEMFile(final String description, final String value) {
        return new PemObject(description, value.getBytes()).getContent();
    }

    private static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
        PublicKey publicKey = null;
        try {
            final KeyFactory kf = KeyFactory.getInstance(algorithm);
            final EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not reconstruct the public key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            System.out.println("Could not reconstruct the public key");
        }

        return publicKey;
    }

    private static PrivateKey getPrivateKey(byte[] keyBytes, String algorithm) {
        PrivateKey privateKey = null;
        try {
            final KeyFactory kf = KeyFactory.getInstance(algorithm);
            final EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not reconstruct the private key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            System.out.println("Could not reconstruct the private key");
        }

        return privateKey;
    }

    public static PublicKey readPublicKeyFromString(String value) {
        return getPublicKey(parsePEMFile(PUBLIC_KEY, value), "RSA");
    }

    public static PublicKey readPublicKeyFromFile(String filepath) {
        return getPublicKey(parsePEMFile(new File(filepath)), "RSA");
    }

    public static PrivateKey readPrivateKeyFromString(final String value) {
        return getPrivateKey(parsePEMFile(PRIVATE_KEY, value), "RSA");
    }

    public static PrivateKey readPrivateKeyFromFile(final InputStream reader) {
        return getPrivateKey(parsePEMFile(reader), "RSA");
    }

    public static PrivateKey readPrivateKeyFromFile(String filepath) {
        return getPrivateKey(parsePEMFile(new File(filepath)), "RSA");
    }

    public static String encrypt(final Key key, final String message) {
        return encrypt(key, message.getBytes(UTF_8));
    }

    public static String encrypt(final Key key, final byte[] content) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(content));
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(final Key key, final String encryptedMessage) {
        return decrypt(key, encryptedMessage, UTF_8);
    }


    public static String decrypt(final Key key, final String encryptedMessage, final Charset charset) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)), charset);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(final PrivateKey key) {
        return toString(PRIVATE_KEY, key);
    }

    public static String toString(final PublicKey key) {
        return toString(PUBLIC_KEY, key);
    }

    public static String toString(final String description, final Key key) {
        try (final StringWriter stringWriter = new StringWriter(); final PemWriter pemWriter = new PemWriter(stringWriter)) {
            pemWriter.writeObject(new PemObject(description, key.getEncoded()));
            pemWriter.flush();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException("Cannot format certificate to PEM format", e);
        }
    }
}
