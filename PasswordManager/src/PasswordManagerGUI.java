import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.sql.*;


interface AccountInterface {
    String getUsername();
    String getPassword();
    void setPassword(String password);
    void setDetails(String details);
    String toString();
    String getDetails();
}

class Account implements AccountInterface {
    private String username;
    private String password;
    private String details;

    public Account(String username, String password, String details) {
        this.username = username;
        this.password = password;
        this.details = details;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return username + "," + password + "," + details;
    }

    public static Account fromCSV(String csvLine) {
        String[] values = csvLine.split(",");
        return new Account(values[0], values[1], values[2]);
    }
    @Override
    public String getPassword() {
        throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
    }

    @Override
    public String getDetails() {        
        throw new UnsupportedOperationException("Unimplemented method 'getDetails'");
    }
}

interface UserInterface {
    void addAccount(AccountInterface account);
    void removeAccount(AccountInterface account);
    AccountInterface findAccountByUsername(String username);
    ArrayList<AccountInterface> getAccounts();
    String getLoginUsername();
    void setLoginUsername(String username);
    String getLoginPassword();
    void setLoginPassword(String password);
}

class User implements UserInterface {
    private String loginUsername;
    private String loginPassword;
    private ArrayList<AccountInterface> accounts;

    public User(String loginUsername, String loginPassword) {
        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;
        this.accounts = new ArrayList<>();
    }

    public static User loadUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/PasswordManager", "gauthammohanraj", "your_password");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(username, password);
                user.setLoginUsername(username);
                user.setLoginPassword(password);
                PreparedStatement accountStmt = conn.prepareStatement("SELECT * FROM Accounts WHERE user_id = ?");
                accountStmt.setInt(1, rs.getInt("id"));
                ResultSet accountRs = accountStmt.executeQuery();
                
                while (accountRs.next()) {
                    Account account = new Account(accountRs.getString("username"), accountRs.getString("password"), accountRs.getString("details"));
                    user.addAccount(account);
                }
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void registerUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/PasswordManager", "gauthammohanraj", "your_password");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (username, password) VALUES (?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                throw new IllegalArgumentException("Username already exists.");
            } else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addAccount(AccountInterface account) {
        accounts.add(account);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/PasswordManager", "gauthammohanraj", "your_password");
             PreparedStatement userStmt = conn.prepareStatement("SELECT id FROM Users WHERE username = ?");
             PreparedStatement accountStmt = conn.prepareStatement("INSERT INTO Accounts (username, password, details, user_id) VALUES (?, ?, ?, ?)")) {

            userStmt.setString(1, loginUsername);
            ResultSet rs = userStmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");

                accountStmt.setString(1, account.getUsername());
                accountStmt.setString(2, account.getPassword());
                accountStmt.setString(3, account.getDetails());
                accountStmt.setInt(4, userId);
                accountStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAccount(AccountInterface account) {
        accounts.remove(account);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/PasswordManager", "gauthammohanraj", "your_password");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Accounts WHERE username = ? AND user_id = (SELECT id FROM Users WHERE username = ?)")) {

            stmt.setString(1, account.getUsername());
            stmt.setString(2, loginUsername);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AccountInterface findAccountByUsername(String username) {
        for (AccountInterface account : accounts) {
            if (account.getUsername().equals(username)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public ArrayList<AccountInterface> getAccounts() {
        return accounts;
    }

    @Override
    public String getLoginUsername() {
        return loginUsername;
    }

    @Override
    public void setLoginUsername(String username) {
        this.loginUsername = username;
    }

    @Override
    public String getLoginPassword() {
        return loginPassword;
    }

    @Override
    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }
}

class PasswordManager {
    private UserInterface user;

    public PasswordManager(UserInterface user) {
        this.user = user;
    }

    public void addAccount(String username, String password, String details) {
        AccountInterface newAccount = new Account(username, password, details);
        user.addAccount(newAccount);
    }

    public void removeAccount(String username) {
        AccountInterface account = user.findAccountByUsername(username);
        if (account != null) {
            user.removeAccount(account);
        } else {
            System.out.println("Account not found.");
        }
    }

    public void editAccount(String username, String newPassword, String newDetails) {
        AccountInterface account = user.findAccountByUsername(username);
        if (account != null) {
            account.setPassword(newPassword);
            account.setDetails(newDetails);
            // Update the account in the database
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/PasswordManager", "gauthammohanraj", "your_password");
                 PreparedStatement stmt = conn.prepareStatement("UPDATE Accounts SET password = ?, details = ? WHERE username = ? AND user_id = (SELECT id FROM Users WHERE username = ?)")) {

                stmt.setString(1, newPassword);
                stmt.setString(2, newDetails);
                stmt.setString(3, username);
                stmt.setString(4, user.getLoginUsername());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    public void viewAccounts() {
        for (AccountInterface account : user.getAccounts()) {
            System.out.println(account);
        }
    }

    public void editLoginDetails(String newUsername, String newPassword) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/PasswordManager", "gauthammohanraj", "your_password");
             PreparedStatement stmt = conn.prepareStatement("UPDATE Users SET username = ?, password = ? WHERE username = ?")) {

            stmt.setString(1, newUsername);
            stmt.setString(2, newPassword);
            stmt.setString(3, user.getLoginUsername());
            stmt.executeUpdate();

            user.setLoginUsername(newUsername);
            user.setLoginPassword(newPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


public class PasswordManagerGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User user;
    private PasswordManager manager;

    public PasswordManagerGUI() {
        setTitle("Password Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        showLoginPage();
        showManagerPage();

        cardLayout.show(mainPanel, "login");
        setVisible(true);
    }

    private void showLoginPage() {
        JPanel loginPanel = new JPanel(new GridLayout(4, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        JLabel messageLabel = new JLabel("");
        loginPanel.add(userLabel);
        loginPanel.add(userField);
        loginPanel.add(passLabel);
        loginPanel.add(passField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);
        loginPanel.add(new JLabel()); // Placeholder
        loginPanel.add(messageLabel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                user = User.loadUser(username, password);
                if (user != null) {
                    manager = new PasswordManager(user);
                    cardLayout.show(mainPanel, "manager");
                    messageLabel.setText("");
                } else {
                    messageLabel.setText("Invalid credentials!");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                try {
                    User.registerUser(username, password);
                    JOptionPane.showMessageDialog(null, "Registration successful. Please login.");
                    messageLabel.setText("");
                } catch (IllegalArgumentException ex) {
                    messageLabel.setText(ex.getMessage());
                }
            }
        });

        mainPanel.add(loginPanel, "login");
    }

    private void showManagerPage() {
        JPanel managerPanel = new JPanel(new BorderLayout());

        JTextArea accountDisplay = new JTextArea();
        accountDisplay.setEditable(false);

        JButton viewAccountsButton = new JButton("View Accounts");
        JButton addAccountButton = new JButton("Add Account");
        JButton editAccountButton = new JButton("Edit Account");
        JButton removeAccountButton = new JButton("Remove Account");
        JButton editLoginDetailsButton = new JButton("Edit Login Details");
        JButton logoutButton = new JButton("Log Out");

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1));
        buttonPanel.add(viewAccountsButton);
        buttonPanel.add(addAccountButton);
        buttonPanel.add(editAccountButton);
        buttonPanel.add(removeAccountButton);
        buttonPanel.add(editLoginDetailsButton);
        buttonPanel.add(logoutButton);

        managerPanel.add(buttonPanel, BorderLayout.WEST);
        managerPanel.add(new JScrollPane(accountDisplay), BorderLayout.CENTER);

        viewAccountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountDisplay.setText("");
                for (AccountInterface account : user.getAccounts()) {
                    accountDisplay.append("Username: " + account.getUsername() + "\n");
                    accountDisplay.append("Password: " + account.getPassword() + "\n");
                    accountDisplay.append("Details: " + account.getDetails() + "\n");
                    accountDisplay.append("--------------------\n");
                }
            }
        });

        addAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddAccountPage();
            }
        });

        editAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditAccountPage();
            }
        });

        removeAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRemoveAccountPage();
            }
        });

        editLoginDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditLoginDetailsPage();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                user = null;
                manager = null;
                cardLayout.show(mainPanel, "login");
            }
        });
        JButton generatorButton = new JButton("Password Generator");
buttonPanel.add(generatorButton);

generatorButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        GeneratorUI generatorUI = new GeneratorUI(PasswordManagerGUI.this);
        generatorUI.setVisible(true);
        setVisible(false);
    }
});

JButton aesButton = new JButton("AES Encryption/Decryption");
buttonPanel.add(aesButton);

aesButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        AESSwingInterface aesInterface = new AESSwingInterface();
        aesInterface.setVisible(true);
    }
});
        mainPanel.add(managerPanel, "manager");
    }

    private void showAddAccountPage() {
        JPanel addAccountPanel = new JPanel(new GridLayout(5, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JLabel detailsLabel = new JLabel("Details:");
        JTextField detailsField = new JTextField();
        JButton addButton = new JButton("Add Account");
        JButton backButton = new JButton("Back");

        JLabel messageLabel = new JLabel("");
        addAccountPanel.add(userLabel);
        addAccountPanel.add(userField);
        addAccountPanel.add(passLabel);
        addAccountPanel.add(passField);
        addAccountPanel.add(detailsLabel);
        addAccountPanel.add(detailsField);
        addAccountPanel.add(addButton);
        addAccountPanel.add(backButton);
        addAccountPanel.add(new JLabel());
        addAccountPanel.add(messageLabel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                String details = detailsField.getText();
                manager.addAccount(username, password, details);
                messageLabel.setText("Account added successfully!");
                cardLayout.show(mainPanel, "manager");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "manager");
            }
        });

        mainPanel.add(addAccountPanel, "addAccount");
        cardLayout.show(mainPanel, "addAccount");
    }

    private void showEditAccountPage() {
        JPanel editAccountPanel = new JPanel(new GridLayout(5, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("New Password:");
        JPasswordField passField = new JPasswordField();
        JLabel detailsLabel = new JLabel("New Details:");
        JTextField detailsField = new JTextField();
        JButton editButton = new JButton("Edit Account");
        JButton backButton = new JButton("Back");

        JLabel messageLabel = new JLabel("");
        editAccountPanel.add(userLabel);
        editAccountPanel.add(userField);
        editAccountPanel.add(passLabel);
        editAccountPanel.add(passField);
        editAccountPanel.add(detailsLabel);
        editAccountPanel.add(detailsField);
        editAccountPanel.add(editButton);
        editAccountPanel.add(backButton);
        editAccountPanel.add(new JLabel()); 
        editAccountPanel.add(messageLabel);

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                String details = detailsField.getText();
                manager.editAccount(username, password, details);
                messageLabel.setText("Account edited successfully!");
                cardLayout.show(mainPanel, "manager");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "manager");
            }
        });

        mainPanel.add(editAccountPanel, "editAccount");
        cardLayout.show(mainPanel, "editAccount");
    }

    private void showRemoveAccountPage() {
        JPanel removeAccountPanel = new JPanel(new GridLayout(4, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JButton removeButton = new JButton("Remove Account");
        JButton backButton = new JButton("Back");

        JLabel messageLabel = new JLabel("");
        removeAccountPanel.add(userLabel);
        removeAccountPanel.add(userField);
        removeAccountPanel.add(removeButton);
        removeAccountPanel.add(backButton);
        removeAccountPanel.add(new JLabel()); 
        removeAccountPanel.add(messageLabel);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                manager.removeAccount(username);
                messageLabel.setText("Account removed successfully!");
                cardLayout.show(mainPanel, "manager");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "manager");
            }
        });

        mainPanel.add(removeAccountPanel, "removeAccount");
        cardLayout.show(mainPanel, "removeAccount");
    }

    private void showEditLoginDetailsPage() {
        JPanel editLoginPanel = new JPanel(new GridLayout(4, 2));
        JLabel userLabel = new JLabel("New Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("New Password:");
        JPasswordField passField = new JPasswordField();
        JButton editButton = new JButton("Edit Login Details");
        JButton backButton = new JButton("Back");

        JLabel messageLabel = new JLabel("");
        editLoginPanel.add(userLabel);
        editLoginPanel.add(userField);
        editLoginPanel.add(passLabel);
        editLoginPanel.add(passField);
        editLoginPanel.add(editButton);
        editLoginPanel.add(backButton);
        editLoginPanel.add(new JLabel());
        editLoginPanel.add(messageLabel);

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                manager.editLoginDetails(username, password);
                messageLabel.setText("Login details updated successfully!");
                cardLayout.show(mainPanel, "manager");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "manager");
            }
        });

        mainPanel.add(editLoginPanel, "editLogin");
        cardLayout.show(mainPanel, "editLogin");
    }

    public static void main(String[] args) {
        new PasswordManagerGUI();
    }
}
