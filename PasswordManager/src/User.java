import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

interface UserInterface {
    String getLoginUsername();
    void setLoginUsername(String loginUsername);
    String getLoginPassword();
    void setLoginPassword(String loginPassword);
    List<AccountInterface> getAccounts();
    void addAccount(AccountInterface account);
    void removeAccount(AccountInterface account);
    AccountInterface findAccountByUsername(String username);
}

public class User implements UserInterface {

    private String loginUsername;
    private String loginPassword;
    private List<AccountInterface> accounts;

    public User(String loginUsername, String loginPassword) {
        this.loginPassword = loginPassword;
        this.loginUsername = loginUsername;
        this.accounts = new ArrayList<>();
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public List<AccountInterface> getAccounts() {
        return accounts;
    }

    public void addAccount(AccountInterface account) {
        accounts.add(account);
    }

    public void removeAccount(AccountInterface account) {
        accounts.remove(account);
    }

    public AccountInterface findAccountByUsername(String username) {
        for (AccountInterface account : accounts) {
            if (account.getUsername().equals(username)) {
                return account;
            }
        }
        return null;
    }

    public static User loadUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(username) && userDetails[1].equals(password)) {
                    return new User(username, password);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading user: " + e.getMessage());
        }
        return null;
    }

    public static void registerUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.csv", true))) {
            writer.write(username + "," + password + "\n");
        } catch (IOException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }
}
