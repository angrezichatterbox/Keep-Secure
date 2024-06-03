import java.io.*;
import java.util.Scanner;

interface PasswordManagerInterface {
    void addAccount(String username, String password, String details);
    void removeAccount(String username);
    void editAccount(String username, String newPassword, String newDetails);
    void viewAccounts();
    void editLoginDetails(String newUsername, String newPassword);
}

public class PasswordManager {
    private UserInterface user;
    private String filePath;

    public PasswordManager(UserInterface user) {
        this.user = user;
        this.filePath = user.getLoginUsername() + "_accounts.csv";
        loadAccounts();
    }

    public void addAccount(String username, String password, String details) {
        AccountInterface newAccount = new Account(username, password, details);
        user.addAccount(newAccount);
        saveAccounts();
    }

    public void removeAccount(String username) {
        AccountInterface account = user.findAccountByUsername(username);
        if (account != null) {
            user.removeAccount(account);
            saveAccounts();
        } else {
            System.out.println("Account not found.");
        }
    }

    public void editAccount(String username, String newPassword, String newDetails) {
        AccountInterface account = user.findAccountByUsername(username);
        if (account != null) {
            account.setPassword(newPassword);
            account.setDetails(newDetails);
            saveAccounts();
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
        user.setLoginUsername(newUsername);
        user.setLoginPassword(newPassword);
        saveAccounts();
    }

    private void saveAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(user.getLoginUsername() + "," + user.getLoginPassword() + "\n");
            for (AccountInterface account : user.getAccounts()) {
                writer.write(account.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    private void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line != null) {
                String[] loginDetails = line.split(",");
                user.setLoginUsername(loginDetails[0]);
                user.setLoginPassword(loginDetails[1]);

                while ((line = reader.readLine()) != null) {
                    String[] accountDetails = line.split(",");
                    AccountInterface account = new Account(accountDetails[0], accountDetails[1], accountDetails[2]);
                    user.addAccount(account);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserInterface user = null;
        while (user == null) {
            System.out.println("1) Register");
            System.out.println("2) Login");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                System.out.print("Enter a username: ");
                String username = scanner.nextLine();
                System.out.print("Enter a password: ");
                String password = scanner.nextLine();
                User.registerUser(username, password);
                System.out.println("Registration successful. Please login.");
            } else if (choice == 2) {
                System.out.print("Enter your username: ");
                String username = scanner.nextLine();
                System.out.print("Enter your password: ");
                String password = scanner.nextLine();
                user = User.loadUser(username, password);
                if (user == null) {
                    System.out.println("Invalid credentials. Please try again.");
                } else {
                    System.out.println("Login successful.");
                }
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }

        PasswordManager manager = new PasswordManager(user);

        int option;
        do {
            System.out.println("Menu | Select an option");
            System.out.println("1) View my accounts");
            System.out.println("2) Remove an account");
            System.out.println("3) Add a new account");
            System.out.println("4) Edit an account");
            System.out.println("5) Edit the login details");
            System.out.println("6) Encrypt And Decrypt Password");
            System.out.println("7) Generate Password");
            System.out.println("0) Log out");

            option = scanner.nextInt();
            scanner.nextLine();  // consume newline

            switch (option) {
                case 1:
                    manager.viewAccounts();
                    break;
                case 2:
                    System.out.print("Enter the username of the account to remove: ");
                    String usernameToRemove = scanner.nextLine();
                    manager.removeAccount(usernameToRemove);
                    break;
                case 3:
                    System.out.print("Enter the username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter the password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter the details: ");
                    String details = scanner.nextLine();
                    manager.addAccount(username, password, details);
                    break;
                case 4:
                    System.out.print("Enter the username of the account to edit: ");
                    String usernameToEdit = scanner.nextLine();
                    System.out.print("Enter the new password: ");
                    String newPassword = scanner.nextLine();
                    System.out.print("Enter the new details: ");
                    String newDetails = scanner.nextLine();
                    manager.editAccount(usernameToEdit, newPassword, newDetails);
                    break;
                case 5:
                    System.out.print("Enter the new login username: ");
                    String newLoginUsername = scanner.nextLine();
                    System.out.print("Enter the new login password: ");
                    String newLoginPassword = scanner.nextLine();
                    manager.editLoginDetails(newLoginUsername, newLoginPassword);
                    break;
                case 6:
                    System.out.println("To be Implemented");
                    break;
                case 7:
                    System.out.println("To Be Implemented");
                    break;
                case 0:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (option != 0);

        scanner.close();
    }
}
