interface AccountInterface {
    String getUsername();
    void setUsername(String username);
    
    String getPassword();
    void setPassword(String password);
    
    String getDetails();
    void setDetails(String details);
    
    String toString();
}

public class Account implements AccountInterface {
    private String username;
    private String password;
    private String details;

    public Account(String username, String password, String details) {
        this.username = username;
        this.password = password;
        this.details = details;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String toString() {
        return username + "," + password + "," + details;
    }
}
