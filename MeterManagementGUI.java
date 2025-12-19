package Energy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeterManagementGUI extends JPanel {
    private JTable meterTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, clearButton;
    private MeterDAO meterDAO;
    
    // Form components
    private JTextField meterIdField, meterNameField, descriptionField, priceField;
    private JComboBox<String> customerComboBox, categoryComboBox, statusComboBox;
    private JButton saveButton, cancelButton;
    private JDialog formDialog;
    
    private boolean isEditMode = false;
    private int currentEditMeterId = -1;

    public MeterManagementGUI() {
        meterDAO = new MeterDAO();
        initializeUI();
        loadMeterData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Meter Management", JLabel.CENTER);
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
                searchMeters();
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
        String[] columnNames = {"ID", "Customer", "Meter Name", "Description", "Category", "Price", "Status", "Created"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        meterTable = new JTable(tableModel);
        meterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        meterTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        meterTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(meterTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addButton = new JButton("Add Meter");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddMeterForm();
            }
        });

        editButton = new JButton("Edit Meter");
        editButton.setBackground(new Color(33, 150, 243));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditMeterForm();
            }
        });

        deleteButton = new JButton("Delete Meter");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMeter();
            }
        });

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(255, 152, 0));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMeterData();
            }
        });

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    public void loadMeterData() {
        tableModel.setRowCount(0);
        List<String[]> meters = meterDAO.getAllMeters();
        
        for (String[] meter : meters) {
            tableModel.addRow(meter);
        }
        
        // Show success message only if not called from search
        if (searchField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Data refreshed successfully! Total meters: " + meters.size(), 
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchMeters() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadMeterData();
            return;
        }

        tableModel.setRowCount(0);
        List<String[]> allMeters = meterDAO.getAllMeters();
        
        for (String[] meter : allMeters) {
            for (String field : meter) {
                if (field != null && field.toLowerCase().contains(searchText)) {
                    tableModel.addRow(meter);
                    break;
                }
            }
        }
    }

    private void clearSearch() {
        searchField.setText("");
        loadMeterData();
    }

    private void showAddMeterForm() {
        isEditMode = false;
        currentEditMeterId = -1;
        showMeterForm();
    }

    private void showEditMeterForm() {
        int selectedRow = meterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a meter to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int meterId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String[] meterData = meterDAO.getMeterById(meterId);
        
        if (meterData != null) {
            isEditMode = true;
            currentEditMeterId = meterId;
            showMeterForm(meterData);
        }
    }

    private void showMeterForm() {
        showMeterForm(null);
    }

    private void showMeterForm(String[] meterData) {
        // Get the parent frame for the dialog
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        
        formDialog = new JDialog(parentWindow, (isEditMode ? "Edit Meter" : "Add Meter"));
        formDialog.setSize(500, 400);
        formDialog.setLocationRelativeTo(parentWindow);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Meter ID (only for edit)
        if (isEditMode) {
            formPanel.add(new JLabel("Meter ID:"));
            meterIdField = new JTextField();
            meterIdField.setEditable(false);
            meterIdField.setText(String.valueOf(currentEditMeterId));
            formPanel.add(meterIdField);
        }

        // Customer
        formPanel.add(new JLabel("Customer:*"));
        customerComboBox = new JComboBox<>();
        loadCustomersToComboBox();
        formPanel.add(customerComboBox);

        // Meter Name
        formPanel.add(new JLabel("Meter Name:*"));
        meterNameField = new JTextField();
        formPanel.add(meterNameField);

        // Description
        formPanel.add(new JLabel("Description:*"));
        descriptionField = new JTextField();
        formPanel.add(descriptionField);

        // Category
        formPanel.add(new JLabel("Category:*"));
        categoryComboBox = new JComboBox<>();
        List<String> categories = meterDAO.getCategories();
        for (String category : categories) {
            categoryComboBox.addItem(category);
        }
        formPanel.add(categoryComboBox);

        // Price
        formPanel.add(new JLabel("Price:*"));
        priceField = new JTextField();
        formPanel.add(priceField);

        // Status
        formPanel.add(new JLabel("Status:*"));
        statusComboBox = new JComboBox<>();
        List<String> statusOptions = meterDAO.getStatusOptions();
        for (String status : statusOptions) {
            statusComboBox.addItem(status);
        }
        formPanel.add(statusComboBox);

        // Pre-fill data if in edit mode
        if (isEditMode && meterData != null) {
            // Set customer
            String customerName = meterData.length > 2 ? meterData[2] : "";
            for (int i = 0; i < customerComboBox.getItemCount(); i++) {
                String item = customerComboBox.getItemAt(i);
                if (item.contains(customerName)) {
                    customerComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            meterNameField.setText(meterData.length > 3 ? meterData[3] : "");
            descriptionField.setText(meterData.length > 4 ? meterData[4] : "");
            categoryComboBox.setSelectedItem(meterData.length > 5 ? meterData[5] : "Residential");
            priceField.setText(meterData.length > 6 ? meterData[6] : "0.0");
            statusComboBox.setSelectedItem(meterData.length > 7 ? meterData[7] : "Active");
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        saveButton = new JButton("Save");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMeter();
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
        List<String[]> customers = meterDAO.getAllCustomersForDropdown();
        customerComboBox.removeAllItems();
        customerComboBox.addItem("Select Customer");
        for (String[] customer : customers) {
            customerComboBox.addItem(customer[0] + " - " + customer[1]);
        }
    }

    private void saveMeter() {
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
            
            String meterName = meterNameField.getText().trim();
            String description = descriptionField.getText().trim();
            String category = (String) categoryComboBox.getSelectedItem();
            String priceText = priceField.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();

            // Validation
            if (meterName.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please fill in all required fields.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
                if (price < 0) {
                    JOptionPane.showMessageDialog(formDialog, 
                        "Price cannot be negative.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please enter a valid price value.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success;
            if (isEditMode) {
                success = meterDAO.updateMeter(currentEditMeterId, customerId, meterName, description, category, price, status);
            } else {
                success = meterDAO.addMeter(customerId, meterName, description, category, price, status);
            }

            if (success) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Meter " + (isEditMode ? "updated" : "added") + " successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                formDialog.dispose();
                loadMeterData();
            } else {
                JOptionPane.showMessageDialog(formDialog, 
                    "Failed to " + (isEditMode ? "update" : "add") + " meter.", 
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

    private void deleteMeter() {
        int selectedRow = meterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a meter to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int meterId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String meterName = tableModel.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete meter: " + meterName + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = meterDAO.deleteMeter(meterId);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Meter deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadMeterData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete meter.", 
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
                
                JFrame frame = new JFrame("Meter Management - Standalone");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1200, 700);
                frame.setLocationRelativeTo(null);
                frame.add(new MeterManagementGUI());
                frame.setVisible(true);
            }
        });
    }
}