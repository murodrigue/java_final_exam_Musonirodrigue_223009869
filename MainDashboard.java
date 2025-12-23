package Energy;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
public class MainDashboard extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private CustomerDAO customerDAO;
    private MeterDAO meterDAO;
    private ReadingDAO readingDAO;
    private PlanDAO planDAO;
    
    public MainDashboard(User user) {
        this.currentUser = user;
        initializeDAOs();
        initializeUI();
    }
    
    private void initializeDAOs() {
        customerDAO = new CustomerDAO();
        meterDAO = new MeterDAO();
        readingDAO = new ReadingDAO();
        planDAO = new PlanDAO();
    }
    
    private void initializeUI() {
        setTitle("Energy Assistant Management System - " + currentUser.getRole().toUpperCase() + " Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab(" Dashboard", createDashboardPanel());
        tabbedPane.addTab(" Customer Management", createCustomerPanel());
        tabbedPane.addTab("Meter Management", createMeterPanel());
        tabbedPane.addTab(" Reading Management", createReadingPanel());
        tabbedPane.addTab(" Plan Management", createPlanPanel());
        tabbedPane.addTab("Energy Analytics", createAnalyticsPanel());
        
        // Admin only tabs
        if ("admin".equals(currentUser.getRole())) {
            tabbedPane.addTab("System Admin", createAdminPanel());
        }
        
        add(tabbedPane);
        
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));
        
        // Header
        JLabel headerLabel = new JLabel("Energy Assistant Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0, 102, 204));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setBackground(new Color(240, 245, 250));
        
        // Statistics cards
        contentPanel.add(createStatCard("Total Customers", "", String.valueOf(customerDAO.getTotalCustomers()), new Color(70, 130, 180)));
        contentPanel.add(createStatCard("Active Meters", "", String.valueOf(meterDAO.getTotalMeters()), new Color(34, 139, 34)));
        contentPanel.add(createStatCard("Monthly Consumption", "", "1,250 kWh", new Color(255, 140, 0)));
        contentPanel.add(createStatCard("Energy Plans", "", String.valueOf(planDAO.getTotalPlans()), new Color(147, 112, 219)));
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Recent activity
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        activityPanel.setBackground(Color.WHITE);
        
        JTextArea activityArea = new JTextArea();
        activityArea.setFont(new Font("Arial", Font.PLAIN, 12));
        activityArea.setText("New customer registration: John Doe\n" +
                           "Meter reading recorded: 245.5 kWh\n" +
                           "Maintenance scheduled for Meter #123\n" +
                           "New energy plan created: Premium Residential\n" +
                           "System backup completed successfully");
        activityArea.setEditable(false);
        
        activityPanel.add(new JScrollPane(activityArea), BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(activityPanel, BorderLayout.CENTER);
        southPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panel.add(southPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String icon, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.DARK_GRAY);
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createCustomerPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Customer Management", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = createStyledButton("Refresh", new Color(70, 130, 180));
        JButton btnAddCustomer = createStyledButton("Add Customer", new Color(34, 139, 34));
        JButton btnEditCustomer = createStyledButton("Edit Customer", new Color(255, 165, 0));
        JButton btnDeleteCustomer = createStyledButton("Delete Customer", new Color(220, 53, 69));
        
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnAddCustomer);
        buttonPanel.add(btnEditCustomer);
        buttonPanel.add(btnDeleteCustomer);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"ID", "Username", "Email", "Full Name", "Role", "Last Login", "Status"};
        final DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        final JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customer List"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        loadCustomerData(model);
        
        // Button actions
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadCustomerData(model);
                JOptionPane.showMessageDialog(panel, "Customer data refreshed!");
            }
        });
        
        btnAddCustomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddCustomerDialog(model);
            }
        });
        
        btnEditCustomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int customerId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    showEditCustomerDialog(model, customerId);
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a customer to edit!");
                }
            }
        });
        
        btnDeleteCustomer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int customerId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    String username = model.getValueAt(selectedRow, 1).toString();
                    deleteCustomer(model, customerId, username);
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a customer to delete!");
                }
            }
        });
        
        return panel;
    }
    
    private JPanel createMeterPanel() {
        // Use the integrated MeterManagementGUI panel
        MeterManagementGUI meterPanel = new MeterManagementGUI();
        return (JPanel) meterPanel;
    }
    
    private JPanel createReadingPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Reading Management", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = createStyledButton("Refresh", new Color(70, 130, 180));
        JButton btnAddReading = createStyledButton("Add Reading", new Color(34, 139, 34));
        JButton btnEditReading = createStyledButton("Edit Reading", new Color(255, 165, 0));
        JButton btnDeleteReading = createStyledButton("Delete Reading", new Color(220, 53, 69));
        
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnAddReading);
        buttonPanel.add(btnEditReading);
        buttonPanel.add(btnDeleteReading);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"ID", "Customer", "Meter", "Amount", "Date", "Type", "Reference", "Status", "Created"};
        final DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        final JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reading List"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        loadReadingData(model);
        
        // Button actions
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadReadingData(model);
                JOptionPane.showMessageDialog(panel, "Reading data refreshed!");
            }
        });
        
        btnAddReading.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddReadingDialog(model);
            }
        });
        
        btnEditReading.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int readingId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    showEditReadingDialog(model, readingId);
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a reading to edit!");
                }
            }
        });
        
        btnDeleteReading.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int readingId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    deleteReading(model, readingId);
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a reading to delete!");
                }
            }
        });
        
        return panel;
    }
    
    private JPanel createPlanPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Plan Management", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = createStyledButton("Refresh", new Color(70, 130, 180));
        JButton btnAddPlan = createStyledButton("Add Plan", new Color(34, 139, 34));
        JButton btnEditPlan = createStyledButton("Edit Plan", new Color(255, 165, 0));
        JButton btnDeletePlan = createStyledButton("Delete Plan", new Color(220, 53, 69));
        
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnAddPlan);
        buttonPanel.add(btnEditPlan);
        buttonPanel.add(btnDeletePlan);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"ID", "Customer", "Plan Name", "Description", "Tariff Rate", "Monthly Fee", "Created"};
        final DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        final JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Plan List"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        loadPlanData(model);
        
        // Button actions
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadPlanData(model);
                JOptionPane.showMessageDialog(panel, "Plan data refreshed!");
            }
        });
        
        btnAddPlan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddPlanDialog(model);
            }
        });
        
        btnEditPlan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int planId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    showEditPlanDialog(model, planId);
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a plan to edit!");
                }
            }
        });
        
        btnDeletePlan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int planId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                    String planName = model.getValueAt(selectedRow, 2).toString();
                    deletePlan(model, planId, planName);
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a plan to delete!");
                }
            }
        });
        
        return panel;
    }
    
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));
        
        JLabel titleLabel = new JLabel("Energy Analytics & Reports", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setBackground(new Color(240, 245, 250));
        
        // Analytics cards
        contentPanel.add(createAnalyticsCard("Consumption Trends", "", "View energy usage patterns over time"));
        contentPanel.add(createAnalyticsCard("Cost Analysis", "", "Analyze electricity costs and savings"));
        contentPanel.add(createAnalyticsCard("Peak Usage", "", "Identify peak consumption hours"));
        contentPanel.add(createAnalyticsCard("Efficiency Report", "", "Energy efficiency recommendations"));
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 245, 250));
        
        JLabel titleLabel = new JLabel("System Administration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel adminPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        adminPanel.setBackground(new Color(240, 245, 250));
        
        adminPanel.add(createAdminButton("User Management", ""));
        adminPanel.add(createAdminButton("Database Backup", ""));
        adminPanel.add(createAdminButton("System Logs", ""));
        adminPanel.add(createAdminButton("Settings", ""));
        
        panel.add(adminPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAnalyticsCard(String title, String icon, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel descLabel = new JLabel(description, JLabel.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton createAdminButton(String text, String icon) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 80));
        return button;
    }
    
    // Data loading methods
    private void loadCustomerData(DefaultTableModel model) {
        model.setRowCount(0);
        List<String[]> customers = customerDAO.getAllCustomers();
        for (String[] customer : customers) {
            model.addRow(customer);
        }
    }
    
    private void loadMeterData(DefaultTableModel model) {
        model.setRowCount(0);
        List<String[]> meters = meterDAO.getAllMeters();
        for (String[] meter : meters) {
            model.addRow(meter);
        }
    }
    
    private void loadReadingData(DefaultTableModel model) {
        model.setRowCount(0);
        List<String[]> readings = readingDAO.getAllReadings();
        for (String[] reading : readings) {
            model.addRow(reading);
        }
    }
    
    private void loadPlanData(DefaultTableModel model) {
        model.setRowCount(0);
        List<String[]> plans = planDAO.getAllPlans();
        for (String[] plan : plans) {
            model.addRow(plan);
        }
    }
    
    // CRUD Operation Methods for Customers
    private void showAddCustomerDialog(final DefaultTableModel model) {
        final JDialog dialog = new JDialog(this, "Add New Customer", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        final JTextField txtUsername = new JTextField();
        final JTextField txtEmail = new JTextField();
        final JTextField txtFullName = new JTextField();
        final JPasswordField txtPassword = new JPasswordField();
        final JComboBox<String> cmbRole = new JComboBox<>(new String[]{"user", "admin"});
        final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        
        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(txtFullName);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cmbRole);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cmbStatus);
        
        JPanel buttonPanel = new JPanel();
        JButton btnSave = createStyledButton("Save", new Color(34, 139, 34));
        JButton btnCancel = createStyledButton("Cancel", new Color(108, 117, 125));
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (customerDAO.addCustomer(
                    txtUsername.getText(),
                    new String(txtPassword.getPassword()),
                    txtEmail.getText(),
                    txtFullName.getText(),
                    cmbRole.getSelectedItem().toString(),
                    cmbStatus.getSelectedItem().equals("Active")
                )) {
                    loadCustomerData(model);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(MainDashboard.this, "Customer added successfully!");
                }
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void showEditCustomerDialog(final DefaultTableModel model, final int customerId) {
        String[] customerData = customerDAO.getCustomerById(customerId);
        if (customerData == null) {
            JOptionPane.showMessageDialog(this, "Customer not found!");
            return;
        }
        
        final JDialog dialog = new JDialog(this, "Edit Customer", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        final JTextField txtUsername = new JTextField(customerData[1]);
        final JTextField txtEmail = new JTextField(customerData[2]);
        final JTextField txtFullName = new JTextField(customerData[3]);
        final JPasswordField txtPassword = new JPasswordField();
        final JComboBox<String> cmbRole = new JComboBox<>(new String[]{"user", "admin"});
        cmbRole.setSelectedItem(customerData[4]);
        final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setSelectedItem(customerData[6]);
        
        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password (leave blank to keep current):"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(txtFullName);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cmbRole);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cmbStatus);
        
        JPanel buttonPanel = new JPanel();
        JButton btnSave = createStyledButton("Save", new Color(34, 139, 34));
        JButton btnCancel = createStyledButton("Cancel", new Color(108, 117, 125));
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String password = new String(txtPassword.getPassword());
                if (customerDAO.updateCustomer(
                    customerId,
                    txtUsername.getText(),
                    password.isEmpty() ? null : password,
                    txtEmail.getText(),
                    txtFullName.getText(),
                    cmbRole.getSelectedItem().toString(),
                    cmbStatus.getSelectedItem().equals("Active")
                )) {
                    loadCustomerData(model);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(MainDashboard.this, "Customer updated successfully!");
                }
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void deleteCustomer(final DefaultTableModel model, int customerId, String username) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete customer '" + username + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (customerDAO.deleteCustomer(customerId)) {
                loadCustomerData(model);
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete customer!");
            }
        }
    }
    
    // Placeholder implementations for other CRUD operations
    private void showAddReadingDialog(DefaultTableModel model) {
        JOptionPane.showMessageDialog(this, "Add Reading functionality - Implement similar to Add Customer");
    }
    
    private void showEditReadingDialog(DefaultTableModel model, int readingId) {
        JOptionPane.showMessageDialog(this, "Edit Reading functionality - Implement similar to Edit Customer");
    }
    
    private void deleteReading(final DefaultTableModel model, int readingId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this reading?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (readingDAO.deleteReading(readingId)) {
                loadReadingData(model);
                JOptionPane.showMessageDialog(this, "Reading deleted successfully!");
            }
        }
    }
    
    private void showAddPlanDialog(DefaultTableModel model) {
        JOptionPane.showMessageDialog(this, "Add Plan functionality - Implement similar to Add Customer");
    }
    
    private void showEditPlanDialog(DefaultTableModel model, int planId) {
        JOptionPane.showMessageDialog(this, "Edit Plan functionality - Implement similar to Edit Customer");
    }
    
    private void deletePlan(final DefaultTableModel model, int planId, String planName) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete plan '" + planName + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (planDAO.deletePlan(planId)) {
                loadPlanData(model);
                JOptionPane.showMessageDialog(this, "Plan deleted successfully!");
            }
        }
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
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            dispose();
        }
        
    }
    private JPanel createPlanPanel1() {
        // Use the integrated PlanManagementGUI panel
        PlanManagementGUI planPanel = new PlanManagementGUI();
        return planPanel;
    }

}

