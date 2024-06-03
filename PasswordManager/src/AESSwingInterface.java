import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Key;
import org.apache.commons.codec.binary.Base64;

public class AESSwingInterface extends JFrame implements ActionListener {
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "1234567891234567".getBytes();

    private JTextField inputField;
    private JTextArea outputArea;
    private JButton encryptButton;
    private JButton decryptButton;

    public AESSwingInterface() {
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
            Key key = generateKey();
            if (e.getSource() == encryptButton) {
                String encryptedValue = encrypt(input, key);
                outputArea.setText("Encrypted Value: " + encryptedValue);
            } else if (e.getSource() == decryptButton) {
                String decryptedValue = decrypt(input, key);
                outputArea.setText("Decrypted Value: " + decryptedValue);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage());
        }
    }

    private static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGORITHM);
    }

    private static String encrypt(String valueToEnc, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = cipher.doFinal(valueToEnc.getBytes());
        byte[] encryptedByteValue = Base64.encodeBase64(encValue);
        return new String(encryptedByteValue);
    }

    private static String decrypt(String encryptedValue, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.decodeBase64(encryptedValue.getBytes());
        byte[] decValue = cipher.doFinal(decodedBytes);
        return new String(decValue);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AESSwingInterface();
            }
        });
    }
}