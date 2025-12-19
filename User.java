package Energy;

public class User {
    private int customerId;
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private String role;
    private String lastLogin;
    private boolean isActive;
    
    public User(int customerId, String username, String passwordHash, String email, 
                String fullName, String role, String lastLogin, boolean isActive) {
        this.customerId = customerId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }
    
    // Getters
    public int getCustomerId() { return customerId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getLastLogin() { return lastLogin; }
    public boolean isActive() { return isActive; }
    
    // Setters
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
    public void setActive(boolean active) { isActive = active; }
}
