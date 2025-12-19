package Energy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PlanManagementGUI extends JPanel {
    private JTable planTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, clearButton;
    private PlanDAO planDAO;
    
    // Form components
    private JTextField planIdField, planNameField, descriptionField, tariffRateField, monthlyFeeField;
    private JComboBox<String> customerComboBox, planTypeComboBox, billingCycleComboBox;
    private JButton saveButton, cancelButton;
    private JDialog formDialog;
    
    private boolean isEditMode = false;
    private int currentEditPlanId = -1;

    public PlanManagementGUI() {
        planDAO = new PlanDAO();
        initializeUI();
        loadPlanData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Plan Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel with table
        add(createTablePanel(), BorderLayout.CENTER);

        // Bottom panel with buttons
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPlans();
            }
        });
        searchPanel.add(searchButton);
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSearch();
            }
        });
        searchPanel.add(clearButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Customer", "Plan Name", "Description", "Tariff Rate", "Monthly Fee", "Created"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        planTable = new JTable(tableModel);
        planTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        planTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        planTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(planTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addButton = new JButton("Add Plan");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddPlanForm();
            }
        });

        editButton = new JButton("Edit Plan");
        editButton.setBackground(new Color(33, 150, 243));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditPlanForm();
            }
        });

        deleteButton = new JButton("Delete Plan");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePlan();
            }
        });

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(255, 152, 0));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPlanData();
            }
        });

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    public void loadPlanData() {
        tableModel.setRowCount(0);
        List<String[]> plans = planDAO.getAllPlans();
        
        for (String[] plan : plans) {
            tableModel.addRow(plan);
        }
        
        // Show success message only if not called from search
        if (searchField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Data refreshed successfully! Total plans: " + plans.size(), 
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchPlans() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadPlanData();
            return;
        }

        tableModel.setRowCount(0);
        List<String[]> allPlans = planDAO.getAllPlans();
        
        for (String[] plan : allPlans) {
            for (String field : plan) {
                if (field != null && field.toLowerCase().contains(searchText)) {
                    tableModel.addRow(plan);
                    break;
                }
            }
        }
    }

    private void clearSearch() {
        searchField.setText("");
        loadPlanData();
    }

    private void showAddPlanForm() {
        isEditMode = false;
        currentEditPlanId = -1;
        showPlanForm();
    }

    private void showEditPlanForm() {
        int selectedRow = planTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a plan to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int planId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String[] planData = planDAO.getPlanById(planId);
        
        if (planData != null) {
            isEditMode = true;
            currentEditPlanId = planId;
            showPlanForm(planData);
        }
    }

    private void showPlanForm() {
        showPlanForm(null);
    }

    private void showPlanForm(String[] planData) {
        // Get the parent frame for the dialog
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        
        formDialog = new JDialog(parentWindow, (isEditMode ? "Edit Plan" : "Add Plan"));
        formDialog.setSize(500, 450);
        formDialog.setLocationRelativeTo(parentWindow);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Plan ID (only for edit)
        if (isEditMode) {
            formPanel.add(new JLabel("Plan ID:"));
            planIdField = new JTextField();
            planIdField.setEditable(false);
            planIdField.setText(String.valueOf(currentEditPlanId));
            formPanel.add(planIdField);
        }

        // Customer
        formPanel.add(new JLabel("Customer:*"));
        customerComboBox = new JComboBox<>();
        loadCustomersToComboBox();
        formPanel.add(customerComboBox);

        // Plan Name
        formPanel.add(new JLabel("Plan Name:*"));
        planNameField = new JTextField();
        formPanel.add(planNameField);

        // Description
        formPanel.add(new JLabel("Description:*"));
        descriptionField = new JTextField();
        formPanel.add(descriptionField);

        // Plan Type
        formPanel.add(new JLabel("Plan Type:"));
        planTypeComboBox = new JComboBox<>();
        List<String> planTypes = planDAO.getPlanTypes();
        for (String planType : planTypes) {
            planTypeComboBox.addItem(planType);
        }
        formPanel.add(planTypeComboBox);

        // Tariff Rate
        formPanel.add(new JLabel("Tariff Rate:*"));
        tariffRateField = new JTextField();
        formPanel.add(tariffRateField);

        // Monthly Fee
        formPanel.add(new JLabel("Monthly Fee:*"));
        monthlyFeeField = new JTextField();
        formPanel.add(monthlyFeeField);

        // Billing Cycle
        formPanel.add(new JLabel("Billing Cycle:"));
        billingCycleComboBox = new JComboBox<>();
        List<String> billingCycles = planDAO.getBillingCycles();
        for (String billingCycle : billingCycles) {
            billingCycleComboBox.addItem(billingCycle);
        }
        formPanel.add(billingCycleComboBox);

        // Pre-fill data if in edit mode
        if (isEditMode && planData != null) {
            // Set customer
            String customerName = planData.length > 2 ? planData[2] : "";
            for (int i = 0; i < customerComboBox.getItemCount(); i++) {
                String item = customerComboBox.getItemAt(i);
                if (item.contains(customerName)) {
                    customerComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            planNameField.setText(planData.length > 3 ? planData[3] : "");
            descriptionField.setText(planData.length > 4 ? planData[4] : "");
            tariffRateField.setText(planData.length > 5 ? planData[5] : "0.0000");
            monthlyFeeField.setText(planData.length > 6 ? planData[6] : "0.00");
            
            // Set default selections for combo boxes
            planTypeComboBox.setSelectedIndex(0);
            billingCycleComboBox.setSelectedIndex(0);
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        saveButton = new JButton("Save");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePlan();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formDialog.dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        formDialog.add(formPanel, BorderLayout.CENTER);
        formDialog.add(buttonPanel, BorderLayout.SOUTH);
        formDialog.setVisible(true);
    }

    private void loadCustomersToComboBox() {
        List<String[]> customers = planDAO.getAllCustomersForDropdown();
        customerComboBox.removeAllItems();
        customerComboBox.addItem("Select Customer");
        for (String[] customer : customers) {
            customerComboBox.addItem(customer[0] + " - " + customer[1]);
        }
    }

    private void savePlan() {
        try {
            // Validate customer selection
            if (customerComboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please select a customer.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected customer ID
            String customerSelection = (String) customerComboBox.getSelectedItem();
            int customerId = Integer.parseInt(customerSelection.split(" - ")[0]);
            
            String planName = planNameField.getText().trim();
            String description = descriptionField.getText().trim();
            String tariffRateText = tariffRateField.getText().trim();
            String monthlyFeeText = monthlyFeeField.getText().trim();

            // Validation
            if (planName.isEmpty() || description.isEmpty() || tariffRateText.isEmpty() || monthlyFeeText.isEmpty()) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please fill in all required fields.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate plan name
            if (!isEditMode && !planDAO.isPlanNameUnique(planName, 0)) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Plan name already exists. Please choose a different name.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isEditMode && !planDAO.isPlanNameUnique(planName, currentEditPlanId)) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Plan name already exists. Please choose a different name.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            double tariffRate;
            double monthlyFee;
            
            try {
                tariffRate = Double.parseDouble(tariffRateText);
                if (tariffRate < 0 || tariffRate > 1.0) {
                    JOptionPane.showMessageDialog(formDialog, 
                        "Tariff rate must be between 0 and 1.0.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please enter a valid tariff rate value.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                monthlyFee = Double.parseDouble(monthlyFeeText);
                if (monthlyFee < 0) {
                    JOptionPane.showMessageDialog(formDialog, 
                        "Monthly fee cannot be negative.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please enter a valid monthly fee value.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success;
            if (isEditMode) {
                success = planDAO.updatePlan(currentEditPlanId, customerId, planName, description, tariffRate, monthlyFee);
            } else {
                success = planDAO.addPlan(customerId, planName, description, tariffRate, monthlyFee);
            }

            if (success) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Plan " + (isEditMode ? "updated" : "added") + " successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                formDialog.dispose();
                loadPlanData();
            } else {
                JOptionPane.showMessageDialog(formDialog, 
                    "Failed to " + (isEditMode ? "update" : "add") + " plan.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(formDialog, 
                "An error occurred: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePlan() {
        int selectedRow = planTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a plan to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int planId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String planName = tableModel.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete plan: " + planName + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = planDAO.deletePlan(planId);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Plan deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPlanData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete plan.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Main method for standalone testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getLookAndFeel());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                JFrame frame = new JFrame("Plan Management - Standalone");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1200, 700);
                frame.setLocationRelativeTo(null);
                frame.add(new PlanManagementGUI());
                frame.setVisible(true);
            }
        });
    }
}

