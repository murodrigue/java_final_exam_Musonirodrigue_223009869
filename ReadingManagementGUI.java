package Energy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReadingManagementGUI extends JPanel {
    private JTable readingTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, clearButton;
    private ReadingDAO readingDAO;
    
    // Form components
    private JTextField readingIdField, amountField, referenceNumberField;
    private JComboBox<String> customerComboBox, meterComboBox, readingTypeComboBox, statusComboBox;
    private JTextField readingDateField;
    private JButton saveButton, cancelButton;
    private JDialog formDialog;
    
    private boolean isEditMode = false;
    private int currentEditReadingId = -1;

    public ReadingManagementGUI() {
        readingDAO = new ReadingDAO();
        initializeUI();
        loadReadingData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Reading Management", JLabel.CENTER);
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
                searchReadings();
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
        String[] columnNames = {"ID", "Customer", "Meter", "Amount", "Reading Date", "Type", "Reference", "Status", "Created"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        readingTable = new JTable(tableModel);
        readingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        readingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        readingTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(readingTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addButton = new JButton("Add Reading");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddReadingForm();
            }
        });

        editButton = new JButton("Edit Reading");
        editButton.setBackground(new Color(33, 150, 243));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditReadingForm();
            }
        });

        deleteButton = new JButton("Delete Reading");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteReading();
            }
        });

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(255, 152, 0));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReadingData();
            }
        });

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    public void loadReadingData() {
        tableModel.setRowCount(0);
        List<String[]> readings = readingDAO.getAllReadings();
        
        for (String[] reading : readings) {
            tableModel.addRow(reading);
        }
        
        // Show success message only if not called from search
        if (searchField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Data refreshed successfully! Total readings: " + readings.size(), 
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchReadings() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadReadingData();
            return;
        }

        tableModel.setRowCount(0);
        List<String[]> allReadings = readingDAO.getAllReadings();
        
        for (String[] reading : allReadings) {
            for (String field : reading) {
                if (field != null && field.toLowerCase().contains(searchText)) {
                    tableModel.addRow(reading);
                    break;
                }
            }
        }
    }

    private void clearSearch() {
        searchField.setText("");
        loadReadingData();
    }

    private void showAddReadingForm() {
        isEditMode = false;
        currentEditReadingId = -1;
        showReadingForm();
    }

    private void showEditReadingForm() {
        int selectedRow = readingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a reading to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int readingId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String[] readingData = readingDAO.getReadingById(readingId);
        
        if (readingData != null) {
            isEditMode = true;
            currentEditReadingId = readingId;
            showReadingForm(readingData);
        }
    }

    private void showReadingForm() {
        showReadingForm(null);
    }

    private void showReadingForm(String[] readingData) {
        // Get the parent frame for the dialog
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        
        formDialog = new JDialog(parentWindow, (isEditMode ? "Edit Reading" : "Add Reading"));
        formDialog.setSize(500, 500);
        formDialog.setLocationRelativeTo(parentWindow);

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Reading ID (only for edit)
        if (isEditMode) {
            formPanel.add(new JLabel("Reading ID:"));
            readingIdField = new JTextField();
            readingIdField.setEditable(false);
            readingIdField.setText(String.valueOf(currentEditReadingId));
            formPanel.add(readingIdField);
        }

        // Customer
        formPanel.add(new JLabel("Customer:*"));
        customerComboBox = new JComboBox<>();
        loadCustomersToComboBox();
        customerComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMetersComboBox();
            }
        });
        formPanel.add(customerComboBox);

        // Meter
        formPanel.add(new JLabel("Meter:*"));
        meterComboBox = new JComboBox<>();
        formPanel.add(meterComboBox);

        // Amount
        formPanel.add(new JLabel("Amount (kWh):*"));
        amountField = new JTextField();
        formPanel.add(amountField);

        // Reading Date
        formPanel.add(new JLabel("Reading Date:*"));
        readingDateField = new JTextField();
        readingDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(readingDateField);

        // Reading Type
        formPanel.add(new JLabel("Reading Type:*"));
        readingTypeComboBox = new JComboBox<>();
        List<String> readingTypes = readingDAO.getReadingTypes();
        for (String readingType : readingTypes) {
            readingTypeComboBox.addItem(readingType);
        }
        formPanel.add(readingTypeComboBox);

        // Reference Number
        formPanel.add(new JLabel("Reference Number:"));
        referenceNumberField = new JTextField();
        formPanel.add(referenceNumberField);

        // Status
        formPanel.add(new JLabel("Status:*"));
        statusComboBox = new JComboBox<>();
        List<String> statusOptions = readingDAO.getStatusOptions();
        for (String status : statusOptions) {
            statusComboBox.addItem(status);
        }
        formPanel.add(statusComboBox);

        // Pre-fill data if in edit mode
        if (isEditMode && readingData != null) {
            // Set customer
            String customerName = readingData.length > 3 ? readingData[3] : "";
            for (int i = 0; i < customerComboBox.getItemCount(); i++) {
                String item = customerComboBox.getItemAt(i);
                if (item.contains(customerName)) {
                    customerComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set meter (will be populated after customer selection)
            final String meterName = readingData.length > 4 ? readingData[4] : "";
            // Wait for meter combo box to be populated
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < meterComboBox.getItemCount(); i++) {
                        String item = meterComboBox.getItemAt(i);
                        if (item.contains(meterName)) {
                            meterComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            });
            
            amountField.setText(readingData.length > 5 ? readingData[5] : "0.0");
            readingDateField.setText(readingData.length > 6 ? readingData[6] : "");
            
            // Set reading type
            String readingType = readingData.length > 7 ? readingData[7] : "";
            readingTypeComboBox.setSelectedItem(readingType);
            
            referenceNumberField.setText(readingData.length > 8 ? readingData[8] : "");
            
            // Set status
            String status = readingData.length > 9 ? readingData[9] : "";
            statusComboBox.setSelectedItem(status);
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        saveButton = new JButton("Save");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveReading();
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
        List<String[]> customers = readingDAO.getAllCustomersForDropdown();
        customerComboBox.removeAllItems();
        customerComboBox.addItem("Select Customer");
        for (String[] customer : customers) {
            customerComboBox.addItem(customer[0] + " - " + customer[1]);
        }
    }

    private void updateMetersComboBox() {
        meterComboBox.removeAllItems();
        
        if (customerComboBox.getSelectedIndex() == 0) {
            meterComboBox.addItem("Select Customer First");
            return;
        }

        // Get selected customer ID
        String customerSelection = (String) customerComboBox.getSelectedItem();
        int customerId = Integer.parseInt(customerSelection.split(" - ")[0]);
        
        List<String[]> meters = readingDAO.getMetersForCustomer(customerId);
        meterComboBox.addItem("Select Meter");
        for (String[] meter : meters) {
            meterComboBox.addItem(meter[0] + " - " + meter[1]);
        }
    }

    private void saveReading() {
        try {
            // Validate customer selection
            if (customerComboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please select a customer.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate meter selection
            if (meterComboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please select a meter.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected customer ID and meter ID
            String customerSelection = (String) customerComboBox.getSelectedItem();
            int customerId = Integer.parseInt(customerSelection.split(" - ")[0]);
            
            String meterSelection = (String) meterComboBox.getSelectedItem();
            int meterId = Integer.parseInt(meterSelection.split(" - ")[0]);
            
            String amountText = amountField.getText().trim();
            String readingDate = readingDateField.getText().trim();
            String readingType = (String) readingTypeComboBox.getSelectedItem();
            String referenceNumber = referenceNumberField.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();

            // Validation
            if (amountText.isEmpty() || readingDate.isEmpty()) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please fill in all required fields.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount < 0) {
                    JOptionPane.showMessageDialog(formDialog, 
                        "Amount cannot be negative.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please enter a valid amount value.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate date format (basic validation)
            if (!readingDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Please enter date in YYYY-MM-DD format.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success;
            if (isEditMode) {
                success = readingDAO.updateReading(currentEditReadingId, customerId, meterId, amount, 
                                                 readingDate, readingType, referenceNumber, status);
            } else {
                success = readingDAO.addReading(customerId, meterId, amount, readingDate, 
                                              readingType, referenceNumber, status);
            }

            if (success) {
                JOptionPane.showMessageDialog(formDialog, 
                    "Reading " + (isEditMode ? "updated" : "added") + " successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                formDialog.dispose();
                loadReadingData();
            } else {
                JOptionPane.showMessageDialog(formDialog, 
                    "Failed to " + (isEditMode ? "update" : "add") + " reading.", 
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

    private void deleteReading() {
        int selectedRow = readingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a reading to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int readingId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String referenceNumber = tableModel.getValueAt(selectedRow, 6).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete reading with reference: " + referenceNumber + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = readingDAO.deleteReading(readingId);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Reading deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadReadingData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete reading.", 
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
                
                JFrame frame = new JFrame("Reading Management - Standalone");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1200, 700);
                frame.setLocationRelativeTo(null);
                frame.add(new ReadingManagementGUI());
                frame.setVisible(true);
            }
        });
    }
}