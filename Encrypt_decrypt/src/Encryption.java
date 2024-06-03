import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.nio.charset.StandardCharsets;
import java.security.Key;

interface Encryption {
    String encrypt(String valueToEnc) throws EncryptionException;
    String decrypt(String encryptedValue) throws EncryptionException;
}

class EncryptionException extends Exception {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

abstract class AbstractEncryption implements Encryption {
    private Key key;

    public AbstractEncryption(String algorithm, byte[] keyValue) {
        setKey(new SecretKeySpec(keyValue, algorithm));
    }

    protected Key getKey() {
        return key;
    }

    protected void setKey(Key key) {
        this.key = key;
    }
}

class AESEncryption extends AbstractEncryption {
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "1234567891234567".getBytes(StandardCharsets.UTF_8);

    public AESEncryption() {
        super(ALGORITHM, keyValue);
    }

    @Override
    public String encrypt(String valueToEnc) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            byte[] encValue = cipher.doFinal(valueToEnc.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedByteValue = Base64.encodeBase64(encValue);

            return new String(encryptedByteValue, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("Error while encrypting", e);
        }
    }

    @Override
    public String decrypt(String encryptedValue) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, getKey());

            byte[] decodedBytes = Base64.decodeBase64(encryptedValue.getBytes(StandardCharsets.UTF_8));
            byte[] decValue = cipher.doFinal(decodedBytes);

            return new String(decValue, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("Error while decrypting", e);
        }
    }

    private static String getAlgorithm() {
        return ALGORITHM;
    }
}
