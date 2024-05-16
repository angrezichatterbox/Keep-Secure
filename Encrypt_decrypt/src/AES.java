import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AES {
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "1234567891234567".getBytes();

    public static void main(String args[]) throws Exception {
        Key key = generateKey();
        String encryptedValue = encrypt("ohio gyatt rizzler level 6969", key);
        decrypt(encryptedValue, key);
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }

    public static String encrypt(String valueToEnc, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encValue = cipher.doFinal(valueToEnc.getBytes());
        byte[] encryptedByteValue = Base64.encodeBase64(encValue);  // Correct usage of Base64

        System.out.println("Encrypted Value :: " + new String(encryptedByteValue));
        return new String(encryptedByteValue);
    }

    public static String decrypt(String encryptedValue, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedBytes = Base64.decodeBase64(encryptedValue.getBytes());  // Correct usage of Base64

        byte[] decValue = cipher.doFinal(decodedBytes);
        System.out.println("Decrypted Value :: " + new String(decValue));
        return new String(decValue);
    }
}
