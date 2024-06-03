import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Key;
import org.apache.commons.codec.binary.Base64;

class EncryptionException extends Exception {
    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
class DecryptionException extends Exception {
    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
interface Encryptor {
    String encrypt(String value) throws EncryptionException;
}
interface Decryptor {
    String decrypt(String value) throws DecryptionException;
}
class CryptoUtils implements Encryptor, Decryptor {
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "1234567891234567".getBytes();
    private Key key;

    public CryptoUtils() throws Exception {
        this.key = generateKey();
    }

    private Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGORITHM);
    }
    @Override
    public String encrypt(String valueToEnc) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encValue = cipher.doFinal(valueToEnc.getBytes());
            byte[] encryptedByteValue = Base64.encodeBase64(encValue);
            return new String(encryptedByteValue);
        } catch (Exception e) {
            throw new EncryptionException("Encryption error", e);
        }
    }
    public String encrypt(byte[] valueToEnc) throws EncryptionException {
        return encrypt(new String(valueToEnc));
    }

    @Override
    public String decrypt(String encryptedValue) throws DecryptionException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.decodeBase64(encryptedValue.getBytes());
            byte[] decValue = cipher.doFinal(decodedBytes);
            return new String(decValue);
        } catch (Exception e) {
            throw new DecryptionException("Decryption error", e);
        }
    }
    public String decrypt(byte[] encryptedValue) throws DecryptionException {
        return decrypt(new String(encryptedValue));
    }
}
public class AESSwingInterface extends JFrame implements ActionListener {
    private JTextField inputField;
    private JTextArea outputArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private Encryptor encryptor;
    private Decryptor decryptor;

    public AESSwingInterface() {
        try {
            encryptor = new CryptoUtils();
            decryptor = new CryptoUtils();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error initializing encryption utilities: " + e.getMessage());
            return;
        }
        setTitle("AES Encryption/Decryption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 1));
        inputField = new JTextField(20);
        inputPanel.add(new JLabel("Enter the password or word:"));
        inputPanel.add(inputField);
        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(this);
        buttonPanel.add(encryptButton);
        decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(this);
        buttonPanel.add(decryptButton);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String input = inputField.getText();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password or word.");
            return;
        }
        try {
            if (e.getSource() == encryptButton) {
                String encryptedValue = encryptor.encrypt(input);
                outputArea.setText("Encrypted Value: " + encryptedValue);
            } else if (e.getSource() == decryptButton) {
                String decryptedValue = decryptor.decrypt(input);
                outputArea.setText("Decrypted Value: " + decryptedValue);
            }
        } catch (EncryptionException | DecryptionException ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage());
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AESSwingInterface();
            }
        });
    }
}
