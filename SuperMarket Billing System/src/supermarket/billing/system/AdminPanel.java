/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package supermarket.billing.system;

/**
 *
 * @author Shivam Gupta
 */

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class AdminPanel extends JFrame {

    // Fields for Cashier Management
    private JTextField cashierNameField, cashierPasswordField, cashierEmailField, cashierContactField;
    private JTable cashierTable;

    // Fields for Product Management
    private JTextField productNameField, priceField, quantityField;
    private JTable productTable;
    
    // Fields for Stock Management
    private JTable stockTable;
    
    // Cashier Login Table fields
    private JTable cashierLoginTable;
    private DefaultTableModel cashierLoginTableModel;
    private int srNo = 1; // To track serial number for logins

    public AdminPanel() {
        // Basic Frame Setup
        setTitle("Supermarket Admin Panel");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Apply FlatLaf Theme
        applyTheme();

        // Create Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Add Tabs
        tabbedPane.addTab("Cashiers", new ImageIcon("icons/cashier.png"), createCashierPanel());
        tabbedPane.addTab("Products", new ImageIcon("icons/product.png"), createProductPanel());
        tabbedPane.addTab("Stock", new ImageIcon("icons/stock.png"), createStockPanel());
        tabbedPane.addTab("Cashier Logins", new ImageIcon("icons/login.png"), createCashierLoginPanel());
        tabbedPane.addTab("Reports", new ImageIcon("icons/report.png"), createReportsPanel());

        // Add Change Listener to Tabbed Pane to Auto-Refresh Tables
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0:
                    handleRefreshCashiers();
                    break;
                case 1:
                    handleRefreshProducts();
                    break;
                case 2:
                    handleRefreshStock();
                    break;
                case 3:
                    handleRefreshCashierLogins();
                    break;
            }
        });
        
        // Add Toolbar
        addToolbar();

        setVisible(true);
        
        // Set up the timer to refresh the cashier login details every 10 seconds
        Timer timer = new Timer(10000, e -> handleRefreshCashierLogins()); // Refresh every 10 seconds
        timer.start();
    }

    // ---- Helper Methods for Setup ----

    private void applyTheme() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            showErrorDialog("Failed to Apply Theme", e);
        }
    }

    private void addToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Change Theme Button
        JButton btnToggleTheme = new JButton("Change Theme");
        btnToggleTheme.addActionListener(e -> toggleTheme());
        
        // Logout Button with Icon
        JButton btnLogout = new JButton("Logout");
        btnLogout.setToolTipText("Logout");
        btnLogout.addActionListener(e -> onLogout());

        // Add buttons to toolbar
        toolbar.add(btnToggleTheme);
        toolbar.add(btnLogout);
        
        // Add toolbar to the top of the frame
        getContentPane().add(toolbar, BorderLayout.NORTH);
    }
    
    private void toggleTheme() {
        try {
            if (UIManager.getLookAndFeel().getName().contains("Dark")) {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            showErrorDialog("Failed to Toggle Theme", ex);
        }
    }
    
    // ---- Handle Logout Logic ----
    private void onLogout() {
        int option = JOptionPane.showOptionDialog(this,
                "Are you sure you want to Logout?", "Logout?",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new Object[]{"Yes", "No"}, "Yes");
        
        if (option == 0) {
            this.dispose();
            SuperMarketBillingSystem.main(new String[]{});  // Call the main method
        }
    }
    
    public static void adjustColumnWidths(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();

        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Minimum width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    
    
    
    
    
    // ------------------------------------------------------- Cashier Panel ---------------------------------------------------------------------------
    
    
    
    
    
    private JPanel createCashierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Section: Includes Form Section and Search Section
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Search Section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Search aligned horizontally
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search")); // Titled border for clarity
        searchPanel.add(new JLabel("Search:"));

        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> handleSearchCashiers(searchField.getText()));
        searchPanel.add(btnSearch);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> handleRefreshCashiers());
        searchPanel.add(btnRefresh);

        JButton btnExport = new JButton("Export Cashiers");
        btnExport.addActionListener(e -> handleExportData("cashiers"));
        searchPanel.add(btnExport);

        // Add searchPanel to topSection
        topSection.add(searchPanel);
        
        // Form Section
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Cashier Form"));

        formPanel.add(new JLabel("Name:", JLabel.LEFT));
        cashierNameField = new JTextField();
        formPanel.add(cashierNameField);

        formPanel.add(new JLabel("Password:", JLabel.LEFT));
        cashierPasswordField = new JTextField();
        formPanel.add(cashierPasswordField);

        formPanel.add(new JLabel("E-mail ID:", JLabel.LEFT));
        cashierEmailField = new JTextField();
        formPanel.add(cashierEmailField);

        formPanel.add(new JLabel("Contact No.:", JLabel.LEFT));
        cashierContactField = new JTextField();
        formPanel.add(cashierContactField);
        
        JButton btnAddCashier = new JButton("Add Cashier");
        btnAddCashier.addActionListener(e -> handleAddCashier());
        formPanel.add(btnAddCashier);

        JButton btnUpdateCashier = new JButton("Update Cashier");
        btnUpdateCashier.addActionListener(e -> handleUpdateCashier());
        formPanel.add(btnUpdateCashier);

        JButton btnDelete = new JButton("Delete Cashier");
        btnDelete.addActionListener(e -> handleDeleteCashiers());
        formPanel.add(btnDelete);        
        
        // Add formPanel and buttons to topSection
        topSection.add(formPanel);
        
        // Add topSection to the main panel
        panel.add(topSection, BorderLayout.NORTH);

        // Table Section
        cashierTable = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Cashier ID", "Name", "Password", "E-mail ID", "Contact No."}));
        cashierTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(cashierTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        handleRefreshCashiers();

        return panel;
    }

    // ---- Functionality ----
    private void handleSearchCashiers(String query) {
        try {
            // Perform search query on the database
            DatabaseOperations.searchCashiers(query, (DefaultTableModel) cashierTable.getModel());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Search Cashier: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleAddCashier() {
        try {
            DatabaseOperations.addCashier(
                cashierNameField.getText(),
                cashierPasswordField.getText(),
                cashierEmailField.getText(),
                cashierContactField.getText()
            );
            showInfoDialog("Cashier Added Successfully");
            clearFields();
            handleRefreshCashiers();
        } catch (SQLException ex) {
            showErrorDialog("Failed to Add Cashier", ex);
        }
    }

    private void handleUpdateCashier() {
        try {
            int cashierId = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Cashier ID to Update:"));
            String newName = JOptionPane.showInputDialog(this, "Enter New Name:");
            String newPassword = JOptionPane.showInputDialog(this, "Enter New Password:");
            String newEmail = JOptionPane.showInputDialog(this, "Enter New E-mail ID:");
            String newContact = JOptionPane.showInputDialog(this, "Enter New Contact No:");

            DatabaseOperations.updateCashier(cashierId, newName, newPassword, newEmail, newContact);
            showInfoDialog("Cashier Updated Successfully");
            handleRefreshCashiers();
        } catch (SQLException | NumberFormatException ex) {
            showErrorDialog("Failed to Update Cashier", ex);
        }
    }

    private void handleDeleteCashiers() {
        try {
            // Get all selected rows
            int[] selectedRows = cashierTable.getSelectedRows();
            if (selectedRows.length > 0) {
                // Confirm deletion
                int confirmation = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete the selected cashiers?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    // Collect cashier IDs to delete
                    List<Integer> cashierIds;
                    cashierIds = new ArrayList<>();
                    for (int row : selectedRows) {
                        int cashierId = (int) cashierTable.getValueAt(row, 0); // Get cashier ID from table
                        cashierIds.add(cashierId);
                    }
                    // Delete cashier
                    DatabaseOperations.deleteProducts(cashierIds);
                    JOptionPane.showMessageDialog(this, 
                        "Selected cashiers deleted successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    // Refresh the table data
                    handleRefreshCashiers();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select one or more cashiers to delete.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Failed to delete cashiers: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRefreshCashiers() {
        try {
            DatabaseOperations.loadData((DefaultTableModel) cashierTable.getModel(), "cashiers");
        } catch (SQLException ex) {
            showErrorDialog("Failed to Refresh Cashiers", ex);
        }
    }

    private JTable createTable(String[] columns) {
        return new JTable(new DefaultTableModel(new Object[][]{}, columns));
    }

    private void clearFields() {
        cashierNameField.setText("");
        cashierPasswordField.setText("");
        cashierEmailField.setText("");
        cashierContactField.setText("");
    }

    private void showErrorDialog(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    
    
    
    // ------------------------------------------------------- Product Panel ---------------------------------------------------------------------------

    
    
    
    
    private JPanel createProductPanel() {
        // Main panel for the UI
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Section: Includes Form Section and Search Section
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Search Section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Search aligned horizontally
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search")); // Titled border for clarity
        searchPanel.add(new JLabel("Search:"));

        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> handleSearchProducts(searchField.getText()));
        searchPanel.add(btnSearch);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> handleRefreshProducts());
        searchPanel.add(btnRefresh);

        JButton btnExportProducts = new JButton("Export Products");
        btnExportProducts.addActionListener(e -> handleExportData("products"));
        searchPanel.add(btnExportProducts);

        // Add searchPanel to topSection
        topSection.add(searchPanel);
        
        // Form Section
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10)); // Adjusted to 2x2 grid for alignment
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Form")); // Titled border for clarity

        formPanel.add(new JLabel("Product Name:", JLabel.LEFT));
        productNameField = new JTextField();
        formPanel.add(productNameField);

        formPanel.add(new JLabel("Price:", JLabel.LEFT));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Quantity:", JLabel.LEFT));
        quantityField = new JTextField();
        formPanel.add(quantityField);
        
        JButton btnAddProduct = new JButton("Add Product");
        btnAddProduct.addActionListener(e -> handleAddProduct());
        formPanel.add(btnAddProduct);

        JButton btnDeleteProduct = new JButton("Delete Product");
        btnDeleteProduct.addActionListener(e -> handleDeleteProduct());
        formPanel.add(btnDeleteProduct);

        // Add formPanel and buttons to topSection
        topSection.add(formPanel);

        // Add topSection to the main panel
        panel.add(topSection, BorderLayout.NORTH);

        // Table Section
        productTable = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Product ID", "Name", "Price", "Quantity"}));
        productTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load product data initially
        handleRefreshProducts();

        return panel;
    }
    
    private void handleSearchProducts(String query) {
        try {
            // Perform search query on the database
            DatabaseOperations.searchProducts(query, (DefaultTableModel) productTable.getModel());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Search Products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleAddProduct() {
        try {
            String name = productNameField.getText().trim();
            Float priceText = Float.parseFloat(priceField.getText().trim());
            int quantityText = Integer.parseInt(quantityField.getText().trim());
            DatabaseOperations.addProduct(name, priceText, quantityText);
            JOptionPane.showMessageDialog(this, "Product Added Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            productNameField.setText("");
            priceField.setText("");
            
            // Refresh the Table Data
            handleRefreshProducts();
            
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Add Product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeleteProduct() {
        try {
            // Get all selected rows
            int[] selectedRows = productTable.getSelectedRows();
            if (selectedRows.length > 0) {
                // Confirm deletion
                int confirmation = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete the selected products?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    // Collect product IDs to delete
                    List<Integer> productIds;
                    productIds = new ArrayList<>();
                    for (int row : selectedRows) {
                        int productId = (int) productTable.getValueAt(row, 0); // Get product ID from table
                        productIds.add(productId);
                    }
                    // Delete products
                    DatabaseOperations.deleteProducts(productIds);
                    JOptionPane.showMessageDialog(this, 
                        "Selected products deleted successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    // Refresh the table data
                    handleRefreshProducts();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select one or more products to delete.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Failed to delete products: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRefreshProducts() {
        try {
            DatabaseOperations.loadData((DefaultTableModel) productTable.getModel(), "products");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Refresh Products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Add Export Data Functionality
    private void handleExportData(String tableName) {
        try {
            String filePath = "D:\\Download";
            DatabaseOperations.exportData(tableName, filePath);
            JOptionPane.showMessageDialog(this, "Data Exported Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Export Data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    // ------------------------------------------------------- Stock Panel ---------------------------------------------------------------------------

    
    
    
    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Section: Includes Search Section
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Search and Buttons Section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(new JLabel("Search:"));
        
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> handleSearchStock(searchField.getText()));
        searchPanel.add(btnSearch);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> handleRefreshStock());
        searchPanel.add(btnRefresh);
        
        JButton btnExportStock = new JButton("Export Stock");
        btnExportStock.addActionListener(e -> handleExportData("stock"));
        searchPanel.add(btnExportStock);
        
        JButton btnUpdateStock = new JButton("Update Stock");
        btnUpdateStock.addActionListener(e -> handleUpdateStock());
        searchPanel.add(btnUpdateStock);

        // Add searchPanel to topSection
        topSection.add(searchPanel);

        panel.add(topSection, BorderLayout.NORTH);

        // Table Section
        stockTable = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Product ID", "Name", "Price", "Quantity", "Status", "Created At", "Last Updated"}));
        JScrollPane scrollPane = new JScrollPane(stockTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load stock data initially
        handleRefreshStock();

        return panel;
    }

    private void handleSearchStock(String query) {
        try {
            // Perform search query on the database
            DatabaseOperations.searchStock(query, (DefaultTableModel) stockTable.getModel());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Search Stock: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handle updating stock quantity and status
    private void handleUpdateStock() {
        try {
            int selectedRow = stockTable.getSelectedRow();
            if (selectedRow >= 0) {
                int productId = (int) stockTable.getValueAt(selectedRow, 0);
                int currentQuantity = (int) stockTable.getValueAt(selectedRow, 3);
                // Prompt user for the new quantity
                String input = JOptionPane.showInputDialog(this, "Enter New Quantity:", currentQuantity);
                if (input != null) {
                    int newQuantity = Integer.parseInt(input);

                    // Update stock in the database
                    DatabaseOperations.updateStockQuantity(productId, newQuantity);

                    // Show success message
                    JOptionPane.showMessageDialog(this, "Stock Updated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh the stock table data
                    handleRefreshStock();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a stock record to update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity Entered", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Update Stock: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Handle refreshing stock data to show latest information
    private void handleRefreshStock() {
        try {
            DatabaseOperations.loadStockData((DefaultTableModel) stockTable.getModel());
            adjustColumnWidths(stockTable);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Refresh Stock: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    




    // ------------------------------------------------------- Cashier's Login Panel ---------------------------------------------------------------------------

    private JPanel createCashierLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top Section: Includes Search Section
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Search and Refresh Section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(new JLabel("Search:"));
        
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton btnSearch = new JButton("Search");
//        btnSearch.addActionListener(e -> handleSearchCashierLogins(searchField.getText()));
        searchPanel.add(btnSearch);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> handleRefreshCashierLogins());
        searchPanel.add(btnRefresh);
        
        JButton btnExportCashierLogins = new JButton("Export Cashier Logins");
        btnExportCashierLogins.addActionListener(e -> handleExportData("cashier_logins"));
        searchPanel.add(btnExportCashierLogins);


        // Add searchPanel to topSection
        topSection.add(searchPanel);
        
        panel.add(topSection, BorderLayout.NORTH);

        // Column Names for Cashier Login Table
        String[] columnNames = {"Sr. No.", "Name", "Cashier ID", "Login Time", "Logout Time", "Date"};

        // Initialize Table Model for Cashier Login Details
        cashierLoginTableModel = new DefaultTableModel(columnNames, 0);
        cashierLoginTable = new JTable(cashierLoginTableModel);
        JScrollPane scrollPane = new JScrollPane(cashierLoginTable);

        // Add the table to the panel
        panel.add(scrollPane, BorderLayout.CENTER);
        
        handleRefreshCashierLogins();

        return panel;
    }

    // ---- Helper Method to Refresh Cashier Logins Tab ----
    private void handleRefreshCashierLogins() {
        try {
            DatabaseOperations.fetchCashierLogins(cashierLoginTableModel);  // Fetch and update the table with fresh data
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to Refresh Cashier's Login Details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AdminPanel();
    }
    
    
    
    
    
    
    
    
    


    // ------------------------------------------------------- Reports Panel ---------------------------------------------------------------------------




    


    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create dropdown to select report type
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"Weekly Sales", "Monthly Sales"});
        JButton btnGenerateReport = new JButton("Generate Report");

        filterPanel.add(new JLabel("Select Report Type:"));
        filterPanel.add(reportTypeComboBox);
        filterPanel.add(btnGenerateReport);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Create table to display sales data
        JTable reportTable = createTable(new String[]{"Sr. No.", "Cashier Name", "Sales Amount", "Date"});
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // Handle "Generate Report" button click
        btnGenerateReport.addActionListener(e -> {
            try {
                String reportType = (String) reportTypeComboBox.getSelectedItem();
                DefaultTableModel model = (DefaultTableModel) reportTable.getModel();
                if ("Weekly Sales".equals(reportType)) {
                    DatabaseOperations.generateWeeklyReport(model);
                } else if ("Monthly Sales".equals(reportType)) {
                    DatabaseOperations.generateMonthlyReport(model);
                }
            } catch (SQLException ex) {
                showErrorDialog("Failed to Generate Report", ex);
            }
        });

        return panel;
    }
}