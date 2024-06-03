import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EncryptionUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Encryption UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 300);

            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel inputLabel = new JLabel("Enter text to encrypt/decrypt:");
            JTextField inputField = new JTextField(20);
            JButton encryptButton = new JButton("Encrypt");
            JButton decryptButton = new JButton("Decrypt");
            JTextField outputField = new JTextField(20);
            outputField.setEditable(false);
            JLabel resultLabel = new JLabel("", JLabel.CENTER);

            AESEncryption aesEncryption = new AESEncryption();

            encryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inputText = inputField.getText();
                    try {
                        String encryptedValue = aesEncryption.encrypt(inputText);
                        outputField.setText(encryptedValue);
                        resultLabel.setText("Encrypted: " + encryptedValue);
                    } catch (EncryptionException ex) {
                        resultLabel.setText("Encryption error: " + ex.getMessage());
                    }
                }
            });

            decryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String encryptedText = outputField.getText();
                    try {
                        String decryptedValue = aesEncryption.decrypt(encryptedText);
                        resultLabel.setText("Decrypted: " + decryptedValue);
                    } catch (EncryptionException ex) {
                        resultLabel.setText("Decryption error: " + ex.getMessage());
                    }
                }
            });

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            contentPane.add(inputLabel, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            contentPane.add(inputField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            contentPane.add(encryptButton, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            contentPane.add(decryptButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            contentPane.add(outputField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            contentPane.add(resultLabel, gbc);

            frame.setVisible(true);
        });
    }
}
