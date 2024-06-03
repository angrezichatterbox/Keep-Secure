import java.security.Key;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;


public class AES {
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "1234567891234567".getBytes();
   
    public static void main(String args[]) throws Exception {
        Scanner obj1 = new Scanner(System.in);
        Key key = generateKey();
        System.out.println("Enter the password That has to be encrypted");
        String var = obj1.nextLine();
        String encryptedValue = encrypt(var, key);
        decrypt(encryptedValue, key);
        obj1.close();
    }

    static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }

    public static String encrypt(String valueToEnc, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encValue = cipher.doFinal(valueToEnc.getBytes());
        byte[] encryptedByteValue = Base64.encodeBase64(encValue);  

        System.out.println("Encrypted Value :: " + new String(encryptedByteValue));
        return new String(encryptedByteValue);
    }

    public static String decrypt(String encryptedValue, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedBytes = Base64.decodeBase64(encryptedValue.getBytes());  

        byte[] decValue = cipher.doFinal(decodedBytes);
        System.out.println("Decrypted Value :: " + new String(decValue));
        return new String(decValue);
    }
}
