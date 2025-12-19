package Energy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister, btnShowRegister;
    private JComboBox<String> cmbRole;
    private JPanel mainPanel, loginPanel, registerPanel;
    private CardLayout cardLayout;
    
    // Registration fields
    private JTextField txtRegUsername, txtRegEmail, txtRegFullName;
    private JPasswordField txtRegPassword, txtRegConfirmPassword;
    private JComboBox<String> cmbRegRole;
    
    public LoginForm() {
        initializeDatabase();
        initializeUI();
    }
    
    private void initializeDatabase() {
        DatabaseSetup.createDatabase();
    }
    
    private void initializeUI() {
        setTitle("Energy Assistant Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create card layout for switching between login and register
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(240, 245, 250));
        
        // Create login panel
        loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");
        
        // Create registration panel
        registerPanel = createRegisterPanel();
        mainPanel.add(registerPanel, "REGISTER");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(240, 245, 250));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 245, 250));
        
        JLabel lblTitle = new JLabel("ENERGY ASSISTANT", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        
        JLabel lblSubtitle = new JLabel("Management System", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Login form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername, gbc);
        
        // Password
        gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword, gbc);
        
        // Role
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        
        gbc.gridx = 1;
        cmbRole = new JComboBox<>(new String[]{"user", "admin"});
        formPanel.add(cmbRole, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 245, 250));
        
        btnLogin = createStyledButton("Login", new Color(70, 130, 180));
        btnShowRegister = createStyledButton("Create Account", new Color(34, 139, 34));
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnShowRegister);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Event listeners
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        btnShowRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearRegisterForm();
                cardLayout.show(mainPanel, "REGISTER");
            }
        });
        
        // Enter key support for login
        txtPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));
        
        // Header
        JLabel lblTitle = new JLabel("Create New Account", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Registration form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:*"), gbc);
        
        gbc.gridx = 1;
        txtRegFullName = new JTextField(20);
        formPanel.add(txtRegFullName, gbc);
        
        // Username
        gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(new JLabel("Username:*"), gbc);
        
        gbc.gridx = 1;
        txtRegUsername = new JTextField(20);
        formPanel.add(txtRegUsername, gbc);
        
        // Email
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        txtRegEmail = new JTextField(20);
        formPanel.add(txtRegEmail, gbc);
        
        // Password
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(new JLabel("Password:*"), gbc);
        
        gbc.gridx = 1;
        txtRegPassword = new JPasswordField(20);
        formPanel.add(txtRegPassword, gbc);
        
        // Confirm Password
        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(new JLabel("Confirm Password:*"), gbc);
        
        gbc.gridx = 1;
        txtRegConfirmPassword = new JPasswordField(20);
        formPanel.add(txtRegConfirmPassword, gbc);
        
        // Role
        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(new JLabel("Role:*"), gbc);
        
        gbc.gridx = 1;
        cmbRegRole = new JComboBox<>(new String[]{"user", "admin"});
        formPanel.add(cmbRegRole, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 245, 250));
        
        btnRegister = createStyledButton("Register", new Color(34, 139, 34));
        JButton btnBackToLogin = createStyledButton("Back to Login", new Color(108, 117, 125));
        
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBackToLogin);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Event listeners
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });
        
        btnBackToLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearLoginForm();
                cardLayout.show(mainPanel, "LOGIN");
            }
        });
        
        // Enter key support for registration
        txtRegConfirmPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return button;
    }
    
    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String selectedRole = (String) cmbRole.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password!", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM customer WHERE Username = ? AND PasswordHash = ? AND Role = ? AND IsActive = 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, selectedRole);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Update last login
                updateLastLogin(rs.getInt("CustomerID"));
                
                // Create user object
                User user = new User(
                    rs.getInt("CustomerID"),
                    rs.getString("Username"),
                    rs.getString("PasswordHash"),
                    rs.getString("Email"),
                    rs.getString("FullName"),
                    rs.getString("Role"),
                    rs.getString("LastLogin"),
                    rs.getBoolean("IsActive")
                );
                
                JOptionPane.showMessageDialog(this, 
                    "Login Successful!\nWelcome " + user.getFullName(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Open main dashboard
                openDashboard(user);
                dispose();
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid credentials or role mismatch!\n\n" +
                    "Default Admin: admin/admin123\n" +
                    "Or create a new account.", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + ex.getMessage(), 
                "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void performRegistration() {
        String username = txtRegUsername.getText().trim();
        String password = new String(txtRegPassword.getPassword());
        String confirmPassword = new String(txtRegConfirmPassword.getPassword());
        String email = txtRegEmail.getText().trim();
        String fullName = txtRegFullName.getText().trim();
        String role = (String) cmbRegRole.getSelectedItem();
        
        // Validation
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all required fields marked with *!", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Username must be at least 3 characters long!", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 6 characters long!", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match!", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if username already exists
        if (checkUsernameExists(username)) {
            JOptionPane.showMessageDialog(this, 
                "Username already exists! Please choose a different username.", 
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO customer (Username, PasswordHash, Email, FullName, Role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, fullName);
            pstmt.setString(5, role);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Registration successful!\n\nYou can now login with your credentials.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearRegisterForm();
                cardLayout.show(mainPanel, "LOGIN");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Registration error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean checkUsernameExists(String username) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM customer WHERE Username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error checking username: " + ex.getMessage());
        }
        return false;
    }
    
    private void updateLastLogin(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE customer SET LastLogin = CURRENT_TIMESTAMP WHERE CustomerID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error updating last login: " + ex.getMessage());
        }
    }
    
    private void clearLoginForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
    }
    
    private void clearRegisterForm() {
        txtRegUsername.setText("");
        txtRegPassword.setText("");
        txtRegConfirmPassword.setText("");
        txtRegEmail.setText("");
        txtRegFullName.setText("");
        cmbRegRole.setSelectedIndex(0);
    }
    
    private void openDashboard(User user) {
        // Open the main dashboard
        new MainDashboard(user).setVisible(true);
        dispose(); // Close the login form
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}