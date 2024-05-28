import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GeneratorUI extends JFrame implements ActionListener {
    JPanel panel;
    JLabel user_label, password_label, message;
    JTextField userName_text,password_text;
    JButton submit, cancel;
    JTable table;
    DefaultTableModel tableModel;

    GeneratorUI() {
        JPanel topPanel=new JPanel(new GridLayout(1,2));
        user_label = new JLabel();
        user_label.setText("Number of passwords required : ");
        userName_text = new JTextField();
        topPanel.add(user_label);
        topPanel.add(userName_text);
        
        password_label = new JLabel();
        password_label.setText("Length of password :");
        password_text = new JTextField();
        topPanel.add(password_label);
        topPanel.add(password_text);

        submit = new JButton("Generate passwords");

        String[] coulmnNames={"Password"};
        tableModel= new DefaultTableModel(coulmnNames,0);
        table=new JTable(tableModel);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel = new JPanel(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(submit, BorderLayout.SOUTH);
        panel.add(tablePanel, BorderLayout.EAST);
        message = new JLabel();
        panel.add(message, BorderLayout.CENTER);
        // panel.add(submit);
        // panel.add(new JScrollPane(table));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        submit.addActionListener(this);
        add(panel, BorderLayout.CENTER);
        setTitle("Password Generator");
        setSize(450, 350);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GeneratorUI();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == submit) {
            
            String numberOfPasswordsString = userName_text.getText();
            String passwordLengthString = String.valueOf(password_text.getText());
            
            try {
                int numberOfPasswords = Integer.parseInt(numberOfPasswordsString);
                int passwordLength = Integer.parseInt(passwordLengthString);

                System.out.println("Number of Passwords: " + numberOfPasswords);
                System.out.println("Password Length: " + passwordLength);

                Generator generator= new Generator();
                generator.setLength(passwordLength);
                generator.setTotal(numberOfPasswords);

                String[] passwords= generator.generatePassword();

                tableModel.setRowCount(0);
                for(String password : passwords){
                    tableModel.addRow(new Object[]{password});
                }
            } catch (NumberFormatException e) {
               
                message.setText("Please enter valid integer values");
            }
        }
    }
}