/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package supermarket.billing.system;

/**
 *
 * @author Shivam Gupta
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class CashierPanel extends JFrame {
    private int cashierId;  // Global variable to store the cashier's ID
    private JTable productsTable;
    private JTextArea billTextArea, previewTextArea;
    private JTextField searchField, discountField;
    private JComboBox<String> customerBox, productBox;
    private JSpinner quantitySpinner;
    private JButton createNewOrderButton, discardOrderButton, addToBillButton, generateBillButton, applyDiscountButton;
    private int currentOrderID = 0;
    private List<BillItem> billItems = new ArrayList<>(); // List to store items added to the bill
    private int billNumber = 1;

    public CashierPanel(int cashierId) {
        this.cashierId = cashierId;
        applyTheme();  // Apply the Material Design theme
        
        setTitle("Supermarket Billing System");
        setUndecorated(true);  // Removes the title bar and control buttons
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Maximizes the frame to full-screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));
        
        // Header and Toolbar Panel (combining both header and toolbar)
        JPanel headerToolbarPanel = new JPanel(new BorderLayout());
        headerToolbarPanel.setBackground(new Color(38, 50, 56));

        // Header Section (Center)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(38, 50, 56));
        JLabel titleLabel = new JLabel("Supermarket Billing System", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 30));
        headerPanel.add(titleLabel);

        // Toolbar Section (Right)
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbarPanel.setBackground(new Color(38, 50, 56));

        // Change Theme Button
        JButton btnToggleTheme = new JButton("Change Theme");
        btnToggleTheme.addActionListener(e -> toggleTheme());

        // Logout Button with Icon
        JButton btnLogout = new JButton("Logout");
        btnLogout.setToolTipText("Logout");
        btnLogout.addActionListener(e -> onLogout());

        // Add buttons to toolbar
        toolbarPanel.add(btnToggleTheme);
        toolbarPanel.add(btnLogout);

        // Add both header and toolbar to the headerToolbarPanel
        headerToolbarPanel.add(headerPanel, BorderLayout.CENTER);
        headerToolbarPanel.add(toolbarPanel, BorderLayout.EAST);

        // Add the header and toolbar panel to the main frame
        add(headerToolbarPanel, BorderLayout.NORTH);

        // Main Content Panel with dynamic layout
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 25, 25));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(contentPanel, BorderLayout.CENTER);

        // Left Panel: Products Table
        JPanel leftPanel = createProductsPanel();
        contentPanel.add(leftPanel);

        // Right Panel: Billing and Controls and Cashier Details Section
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(createBillingPanel(), BorderLayout.CENTER);  // Keep the Billing Panel in the center

        // Create the Cashier Detail Section and add it to the rightPanel
        JPanel cashierDetailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        cashierDetailsPanel.setBorder(BorderFactory.createTitledBorder("Cashier Details"));

        // Fetch cashier details from the database
        Cashier cashier = DatabaseOperations.getCashierDetails(cashierId);

        if (cashier != null) {
            JLabel lblCashierId = new JLabel("Cashier ID:");
            JLabel lblCashierName = new JLabel("Cashier Name:");
            JLabel lblCashierContact = new JLabel("Cashier Contact:");
            JLabel lblCashierEmail = new JLabel("Cashier Email:");

            JTextField txtCashierId = new JTextField(String.valueOf(cashier.getId()));
            txtCashierId.setEditable(false);  // Make Cashier ID non-editable
            JTextField txtCashierName = new JTextField(cashier.getName());
            txtCashierName.setEditable(false);
            JTextField txtCashierContact = new JTextField(cashier.getContact());
            txtCashierContact.setEditable(false);
            JTextField txtCashierEmail = new JTextField(cashier.getEmail());
            txtCashierEmail.setEditable(false);

            // Add to Cashier Details panel
            cashierDetailsPanel.add(lblCashierId);
            cashierDetailsPanel.add(txtCashierId);
            cashierDetailsPanel.add(lblCashierName);
            cashierDetailsPanel.add(txtCashierName);
            cashierDetailsPanel.add(lblCashierContact);
            cashierDetailsPanel.add(txtCashierContact);
            cashierDetailsPanel.add(lblCashierEmail);
            cashierDetailsPanel.add(txtCashierEmail);

            // Add Cashier Details panel to the right panel
            rightPanel.add(cashierDetailsPanel, BorderLayout.NORTH);
        }
        
        // Add the right panel to the main content panel
        contentPanel.add(rightPanel);
    
        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(38, 50, 56));
        JLabel footerLabel = new JLabel("Developed by Shivam Gupta", SwingConstants.CENTER);
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
        
        setVisible(true);
                
        loadAllProducts();  // Load all products initially
    }
    
    // Applying Material Design theme
    private void applyTheme() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());  // Material design with light theme
        } catch (Exception e) {
            showErrorDialog("Failed to Apply Theme", e);
        }
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
            // Record logout time in the database
            DatabaseOperations.recordCashierLogout(cashierId);
            
            // Dispose of the panel and show the main screen again
            this.dispose();
            SuperMarketBillingSystem.main(new String[]{});  // Call the main method
        }
    }
    
    private void showErrorDialog(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Load all products into the table
    private void loadAllProducts() {
        try {
            DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
            model.setRowCount(0);  // Clear existing rows
            DatabaseOperations.loadProductInCashierPanel(model);  // Assumes this method retrieves all products
            // Adjust column widths after loading data
            adjustColumnWidths(productsTable);
        } catch (SQLException ex) {
            Logger.getLogger(CashierPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "An error occurred while loading products. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
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

    // Create the left panel with products table and search
    private JPanel createProductsPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(20, 20));
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(189, 189, 189)), "Available Products", 0, 0, new Font("Roboto", Font.BOLD, 16), new Color(63, 81, 181)));

        // Top Section: Includes Form Section and Search Section
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(35);
        searchField.setFont(new Font("Roboto", Font.PLAIN, 14));
        searchField.setToolTipText("Search by Product Name or ID");
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);

        JButton searchButton = createButton("Search", new Color(63, 81, 181), "search-icon.png");
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);

        JButton refreshButton = createButton("Refresh", new Color(63, 81, 181), "refresh-icon.png");
        refreshButton.addActionListener(e -> loadAllProducts());
        searchPanel.add(refreshButton);

        topSection.add(searchPanel, BorderLayout.CENTER);

        productsTable = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Name", "Price", "Quantity", "Status"}));
        productsTable.setFont(new Font("Roboto", Font.PLAIN, 14));
        productsTable.setRowHeight(30);
        productsTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        JScrollPane productsScrollPane = new JScrollPane(productsTable);
        
        leftPanel.add(topSection, BorderLayout.NORTH);
        leftPanel.add(productsScrollPane, BorderLayout.CENTER);

        return leftPanel;
    }

    // Create the right panel with billing area and controls
    private JPanel createBillingPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
        rightPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(189, 189, 189)), "Bill Details", 0, 0, new Font("Roboto", Font.BOLD, 16), new Color(63, 81, 181)));

        // Billing TextArea
        billTextArea = new JTextArea(20, 50);
        billTextArea.setEditable(false);
        billTextArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        JScrollPane billScrollPane = new JScrollPane(billTextArea);
        billScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(117, 117, 117)), "Receipt", 0, 0, new Font("Roboto", Font.BOLD, 16), new Color(63, 81, 181)));
        clearBillArea(billTextArea);
        rightPanel.add(billScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        // Create Bill Button
        JButton createBillButton = createButton("Create Bill", new Color(76, 175, 80), "create-bill-icon.png");
        createBillButton.addActionListener(e -> createBillAction());
        buttonPanel.add(createBillButton);

        // Search Bill Button
        JButton searchBillButton = createButton("Search Bill", new Color(63, 81, 181), "search-bill-icon.png");
        searchBillButton.addActionListener(e -> searchBillAction());
        buttonPanel.add(searchBillButton);

        // Extra Button
        JButton printButton = createButton("Print", new Color(255, 193, 7), "extra-icon.png");
        printButton.addActionListener(e -> printAction());
        buttonPanel.add(printButton);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        return rightPanel;
    }
    
    // Action for Create Bill
    private void createBillAction() {
        JDialog createBillDialog = new JDialog(this, "Create Bill", true);
        createBillDialog.setSize(1200, 800);  // Increased size for both panels
        createBillDialog.setLocationRelativeTo(this);
        createBillDialog.setLayout(new BorderLayout(10, 10));
        
        // Input Panel (top side)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBorder(new TitledBorder("Bill Details"));
        
        // Date & Time (non-editable)
        JLabel dateTimeLabel = new JLabel("Date & Time:");
        JTextField dateTimeField = new JTextField();
        dateTimeField.setEditable(false);
        dateTimeField.setText(getCurrentDateTime()); // Set current date & time

        // Bill Number (non-editable)
        JLabel billNoLabel = new JLabel("Bill No:");
        JTextField billNoField = new JTextField(String.valueOf(billNumber));
        billNoField.setEditable(false);

        // Customer Name
        JLabel customerLabel = new JLabel("Customer Name:");
        JTextField customerField = new JTextField(20);
        
        // Customer Contact
        JLabel customerContact = new JLabel("Customer Contact:");
        JTextField customerContactField = new JTextField(20);
        
        // Continue Button
        JButton continueButton = new JButton("Continue");
        
        // Header Panel for Cart
        JPanel cartHeaderPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Name | Contact No.");  // Initial header
        cartHeaderPanel.add(headerLabel, BorderLayout.WEST);


//        // Product Selection
//        JLabel productLabel = new JLabel("Product:");
//        JComboBox<String> productComboBox = new JComboBox<>();
//        try {
//            DatabaseOperations.updateCombox("products", productComboBox);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        // Price (non-editable)
//        JLabel priceLabel = new JLabel("Price:");
//        JTextField priceField = new JTextField();
//        priceField.setEditable(false);
//
//        // Quantity
//        JLabel quantityLabel = new JLabel("Quantity:");
//        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        // Add components to input panel
        inputPanel.add(dateTimeLabel);
        inputPanel.add(dateTimeField);
        inputPanel.add(billNoLabel);
        inputPanel.add(billNoField);
        inputPanel.add(customerLabel);
        inputPanel.add(customerField);
        inputPanel.add(customerContact);
        inputPanel.add(customerContactField);
        inputPanel.add(continueButton);
//        inputPanel.add(productLabel);
//        inputPanel.add(productComboBox);
//        inputPanel.add(priceLabel);
//        inputPanel.add(priceField);
//        inputPanel.add(quantityLabel);
//        inputPanel.add(quantitySpinner);



        
        // Product Display Panel
        JPanel productDisplayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Left-aligned with horizontal and vertical gaps
        productDisplayPanel.setBorder(new TitledBorder("Products"));

        // Set a fixed size for the product display panel
        productDisplayPanel.setPreferredSize(new Dimension(775, 400)); // Adjust the size as needed
        
        try {
            // Fetch product details from the database
            List<Product> products = DatabaseOperations.getProducts();

            for (Product product : products) {
                // Create a panel for each product (it will be added to FlowLayout)
                JPanel productPanel = new JPanel(new BorderLayout());
                productPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                // Create a button for the product name
                JButton productButton = new JButton("<html><div align='center' style='word-wrap: break-word;'>" + product.getName() + "</div></html>");
                productButton.setHorizontalAlignment(SwingConstants.CENTER);
                productButton.setVerticalAlignment(SwingConstants.CENTER);

                // Set the button size to make it square
                int buttonSize = 120; // Set square size for button
                productButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
                productButton.setMinimumSize(new Dimension(buttonSize, buttonSize));
                productButton.setMaximumSize(new Dimension(buttonSize, buttonSize));

                // Add action listener to the button to add product to the bill
                productButton.addActionListener(e -> addProductToBill(product));

                // Add the button to the product panel
                productPanel.add(productButton, BorderLayout.CENTER);

                // Add the product panel to the product display panel using FlowLayout
                productDisplayPanel.add(productPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Cart Panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(new TitledBorder("Cart"));
        JTextArea cartTextArea = new JTextArea();
        cartTextArea.setEditable(false);
        cartPanel.add(new JScrollPane(cartTextArea), BorderLayout.CENTER);
        

        cartPanel.add(cartHeaderPanel, BorderLayout.NORTH);
        
        // Total Section
        JPanel totalPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        totalPanel.setBorder(new TitledBorder("Total"));
        JLabel totalItemsLabel = new JLabel("Total Items:");
        JLabel totalQuantityLabel = new JLabel("Total Quantity:");
        JLabel totalPriceLabel = new JLabel("Total Price:");
        totalPanel.add(totalItemsLabel);
        totalPanel.add(totalQuantityLabel);
        totalPanel.add(totalPriceLabel);
        
        // Payment Options Panel
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        paymentPanel.setBorder(new TitledBorder("Payment Options"));
        JButton cashButton = new JButton("Cash");
        JButton creditCardButton = new JButton("Credit Card");
        JButton upiButton = new JButton("UPI");
        JButton splitPaymentButton = new JButton("Split Payment");
        paymentPanel.add(cashButton);
        paymentPanel.add(creditCardButton);
        paymentPanel.add(upiButton);
        paymentPanel.add(splitPaymentButton);
        
        // Create the right-side container panel
        JPanel rightSidePanel = new JPanel();
        rightSidePanel.setLayout(new BoxLayout(rightSidePanel, BoxLayout.Y_AXIS)); // Arrange vertically
        
        // Set a fixed size for the right side panel
        rightSidePanel.setPreferredSize(new Dimension(400, 400)); // Adjust the size as needed

        // Add cartPanel, totalPanel, and paymentPanel to the rightSidePanel
        rightSidePanel.add(cartPanel);
        rightSidePanel.add(totalPanel);
        rightSidePanel.add(paymentPanel);
        
        // Adding components to the dialog
        createBillDialog.add(productDisplayPanel, BorderLayout.WEST);
        createBillDialog.add(inputPanel, BorderLayout.NORTH);
        createBillDialog.add(rightSidePanel, BorderLayout.EAST);       // Right side container with cart, total, and payment panels
        
        // Action for Continue Button
        continueButton.addActionListener(e -> {
            // Update header with customer details
            headerLabel.setText(customerField.getText() + " | " + customerContactField.getText());

            // Disable input fields
            customerField.setEditable(false);
            customerContactField.setEditable(false);
        });

//        // Bill Details Area (right side)
//        previewTextArea = new JTextArea();
//        previewTextArea.setEditable(false);
//        JScrollPane scrollPane = new JScrollPane(previewTextArea);
//
//        // Split Pane: Left - Input Panel, Right - Bill Details Display
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, scrollPane);
//        splitPane.setDividerLocation(500);  // Adjust the divider position
//        createBillDialog.add(splitPane, BorderLayout.CENTER);

//        // ActionListener for Product ComboBox
//        productComboBox.addActionListener(e -> {
//            String selectedProduct = (String) productComboBox.getSelectedItem();
//            if (selectedProduct != null) {
//                try {
//                    String price = DatabaseOperations.getPriceForProduct(selectedProduct);
//                    priceField.setText(price);
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(createBillDialog, "Error fetching price!", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });

//        // Button Panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton addButton = createButton("Add Item", new Color(76, 175, 80), null);
//        JButton generateButton = createButton("Generate Bill", new Color(76, 175, 80), null);
//        JButton cancelButton = createButton("Cancel", new Color(244, 67, 54), null);

        // Add Button ActionListener (Add product to bill)
//        addButton.addActionListener(e -> {
//            String customerName = customerField.getText().trim();
//            String selectedProduct = (String) productComboBox.getSelectedItem();
//            String price = priceField.getText().trim();
//            int quantity = (int) quantitySpinner.getValue();
//
//            if (customerName.isEmpty() || selectedProduct == null || price.isEmpty()) {
//                JOptionPane.showMessageDialog(createBillDialog, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Add the item to the bill items list
//            double itemPrice = Double.parseDouble(price);
//            BillItem billItem = new BillItem(selectedProduct, itemPrice, quantity);
//            billItems.add(billItem);
//
//            // Update bill display
//            updateBillDisplay();
//
//            // Reset the fields for adding another item
//            productComboBox.setSelectedIndex(0);
//            priceField.setText("");
//            quantitySpinner.setValue(1);
//        });
//
//        // Generate Bill Button (Complete the bill and show the total)
//        generateButton.addActionListener(e -> {
//            if (billItems.isEmpty()) {
//                JOptionPane.showMessageDialog(createBillDialog, "No items in the bill!", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//                        
//            // Clear the bill area and set the default header
//            clearBillArea(billTextArea);
//
//            StringBuilder billDetails = new StringBuilder();
//            // Clear the bill area and set the default header
//            billDetails.append("====================================================================================\n");
//            billDetails.append("                             Bill Receipt                                     \n");
//            billDetails.append("====================================================================================\n");
//            billDetails.append("Bill No: ").append(billNumber).append("\n");
//            billDetails.append("Date: ").append(getCurrentDateTime()).append("\n");
//            billDetails.append("====================================================================================\n");
//            billDetails.append("Customer: ").append(customerField.getText()).append("\n");
//            billDetails.append(String.format("%-20s%-20s%-20s%-10s\n", "Product", "Price (₹)", "Quantity", "Total (₹)", ""));
//            billDetails.append("--------------------------------------------------------------------------------------\n");
//
//            double totalAmount = 0;
//            // Loop through the bill items and display them
//            for (BillItem item : billItems) {
//                billDetails.append(String.format("%-20s%-20.2f%-20d%-10.2f\n", 
//                    item.getProductName(), 
//                    item.getPrice(), 
//                    item.getQuantity(),
//                    item.getTotalPrice()));
//                totalAmount += item.getTotalPrice();
//            }
//
//            billDetails.append("\n");
//            billDetails.append("--------------------------------------------------------------------------------------\n");
//            billDetails.append(String.format("%-20s%-20s%-20s%-10.2f\n", "Total Amount", "", "", totalAmount));
//            billDetails.append("====================================================================================\n");
//
//            // Append the formatted bill to the JTextArea
//            billTextArea.setText(billDetails.toString());
//            
//            // Call the saveBillToDatabase() method to save bill and bill items to the database
//            String customerName = customerField.getText();
//            LocalDateTime currentDateTime = LocalDateTime.now(); // Get the current date and time
//            List<BillItem> itemsToSave = new ArrayList<>(billItems); // Copy the billItems list to pass to the method
//
//            // Save to the database
//            DatabaseOperations.saveBillToDatabase(billNumber, customerName, currentDateTime, totalAmount, itemsToSave);
//
//            // Increment the bill number and clear the items list
//            billNumber++;
//            billItems.clear();
//
//            // Show success message
//            JOptionPane.showMessageDialog(this, "Bill generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//            createBillDialog.dispose();
//        });
//
//        // Cancel Button Action Listener
//        cancelButton.addActionListener(e -> createBillDialog.dispose());
//
//        buttonPanel.add(addButton);
//        buttonPanel.add(generateButton);
//        buttonPanel.add(cancelButton);
//
//        // Add the button panel to the dialog
//        createBillDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show the dialog
        createBillDialog.setVisible(true);
    }
    
    // Method to add product to the bill
private void addProductToBill(Product product) {
    // Implement logic to add the product to the bill
    // For example, update the bill display and total amount
}

    // Utility to get current date and time
    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        return now.format(formatter);
    }

    // Helper function to update the bill display
    private void updateBillDisplay() {
        StringBuilder displayText = new StringBuilder();
        for (BillItem item : billItems) {
            displayText.append(item.toString()).append("\n");
        }
        previewTextArea.setText(displayText.toString());
    }

    // Action for Search Bill
    private void searchBillAction() {
        // Prompt the user to enter a Bill Number
        String billNoString = JOptionPane.showInputDialog(this, "Enter Bill Number to search:", "Search Bill", JOptionPane.QUESTION_MESSAGE);

        if (billNoString == null || billNoString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bill Number cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Convert the input to an integer
            int billNo = Integer.parseInt(billNoString);

            // Call the method to retrieve bill data from the database
            Bill bill = DatabaseOperations.retrieveBillFromDatabase(billNo);

            if (bill == null) {
                JOptionPane.showMessageDialog(this, "Bill not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Display the retrieved bill details in a formatted way
            StringBuilder billDetails = new StringBuilder();
            billDetails.append("====================================================================================\n");
            billDetails.append("                             Bill Receipt                                     \n");
            billDetails.append("====================================================================================\n");
            billDetails.append("Bill No: ").append(bill.getBillNo()).append("\n");
            billDetails.append("Date: ").append(bill.getDateTime()).append("\n");
            billDetails.append("====================================================================================\n");
            billDetails.append("Customer: ").append(bill.getCustomerName()).append("\n");
            billDetails.append(String.format("%-20s%-20s%-20s%-10s\n", "Product", "Price (₹)", "Quantity", "Total (₹)", ""));
            billDetails.append("--------------------------------------------------------------------------------------\n");

            double totalAmount = 0;
            // Loop through the bill items and display them
            for (BillItem item : bill.getBillItems()) {
                billDetails.append(String.format("%-20s%-20.2f%-20d%-10.2f\n", 
                    item.getProductName(), 
                    item.getPrice(), 
                    item.getQuantity(),
                    item.getTotalPrice()));
                totalAmount += item.getTotalPrice();
            }

            billDetails.append("\n");
            billDetails.append("--------------------------------------------------------------------------------------\n");
            billDetails.append(String.format("%-20s%-20s%-20s%-10.2f\n", "Total Amount", "", "", totalAmount));
            billDetails.append("====================================================================================\n");

            // Show the bill details in a JTextArea or similar component
            JTextArea billTextArea = new JTextArea();
            billTextArea.setText(billDetails.toString());
            billTextArea.setEditable(false); // Make it read-only
            JOptionPane.showMessageDialog(this, new JScrollPane(billTextArea), "Bill Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Bill Number format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Action for Print Button
    private void printAction() {
        // Create a PrinterJob instance
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        // Set the printable object (in this case, the content of the JTextArea)
        printerJob.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            // Retrieve the text from the JTextArea (billTextArea)
            String billText = billTextArea.getText();

            // Set up the font and draw the text
            graphics.setFont(new Font("Monospaced", Font.PLAIN, 12));
            graphics.drawString(billText, 100, 100); // Adjust position as needed

            return Printable.PAGE_EXISTS;
        });

        // Show the print dialog to the user
        if (printerJob.printDialog()) {
            try {
                // Print the content if the user confirmed
                printerJob.print();
                JOptionPane.showMessageDialog(this, "Printing...", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Error printing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Create custom buttons with icons and actions
    private JButton createButton(String text, Color color, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Set icon if available: button.setIcon(new ImageIcon(iconPath));
        return button;
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Call DatabaseOperations to get filtered data
            DefaultTableModel model = (DefaultTableModel) productsTable.getModel();
            model.setRowCount(0); // Clear the existing rows
            DatabaseOperations.searchProducts(searchText, model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while searching.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void addToBill() {
        try {
            String prodID = productBox.getSelectedItem().toString().split("-")[0];
            int quantity = (int) quantitySpinner.getValue();
            String[] product = DatabaseOperations.getProd(Integer.parseInt(prodID));
            float price = quantity * Float.parseFloat(product[2]);
            DatabaseOperations.addOrderItems(currentOrderID, Integer.parseInt(prodID), quantity, price);
            billTextArea.append(prodID + "\t" + product[1] + "\t" + quantity + "\t" + price + "\n");
            billTextArea.updateUI();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void applyDiscount() {
        try {
            // Get the discount entered by the user
            String discountText = discountField.getText().trim();

            // Check if the discount field is empty
            if (discountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a discount percentage.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse the discount percentage
            float discountPercentage = Float.parseFloat(discountText);

            // Validate the discount percentage
            if (discountPercentage < 0 || discountPercentage > 100) {
                JOptionPane.showMessageDialog(this, "Enter a valid discount percentage (0-100).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the total price before the discount
            float totalBeforeDiscount = DatabaseOperations.getTotalPrice(currentOrderID);

            // Calculate the discount amount and the total after applying the discount
            float discountAmount = (totalBeforeDiscount * discountPercentage) / 100;
            float totalAfterDiscount = totalBeforeDiscount - discountAmount;

            // Append the discount details to the bill
            billTextArea.append("\n------------------------------");
            billTextArea.append("\nDiscount Applied: " + discountPercentage + "%");
            billTextArea.append("\nDiscount Amount: -Rs. " + discountAmount);
            billTextArea.append("\nFinal Total After Discount: Rs. " + totalAfterDiscount);
            billTextArea.append("\n------------------------------");
            billTextArea.updateUI();

            // Show a confirmation message
            JOptionPane.showMessageDialog(this, "Discount applied successfully! Final Total: Rs. " + totalAfterDiscount);

        } catch (NumberFormatException e) {
            // Handle the case where the discount is not a valid number
            JOptionPane.showMessageDialog(this, "Please enter a numeric value for the discount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            // Handle any SQL errors that may occur when fetching the total price
            Logger.getLogger(CashierPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "An error occurred while applying the discount. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateBill() {
        try {
            // Display the initial items and their costs in the bill
            billTextArea.setText("Receipt for Order ID: " + currentOrderID + "\n\n");
            billTextArea.append("------------------------------------------------\n");
            billTextArea.updateUI();

            // Get the total price before discount
            float totalBeforeDiscount = DatabaseOperations.getTotalPrice(currentOrderID);

            if (totalBeforeDiscount > 0) {
                // Check if discount has been applied
                String discountText = discountField.getText().trim();
                if (!discountText.isEmpty()) {
                    // Apply discount logic if discount is set
                    float discountPercentage = Float.parseFloat(discountText);
                    float discountAmount = (totalBeforeDiscount * discountPercentage) / 100;
                    float totalAfterDiscount = totalBeforeDiscount - discountAmount;

                    // Add discount information to the bill
                    billTextArea.append("\n------------------------------------------------");
                    billTextArea.append("\nDiscount: " + discountPercentage + "% (-Rs. " + discountAmount + ")");
                    billTextArea.append("\n------------------------------------------------");
                    billTextArea.append("\nFinal Total: Rs. " + totalAfterDiscount);
                    billTextArea.updateUI();

                    JOptionPane.showMessageDialog(this, "Bill Generated Successfully.\nTotal Amount: Rs. " + totalAfterDiscount, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // If no discount, show the original total
                    billTextArea.append("\n------------------------------------------------");
                    billTextArea.append("\nTotal: Rs. " + totalBeforeDiscount);
                    billTextArea.updateUI();
                    JOptionPane.showMessageDialog(this, "Bill Generated Successfully.\nTotal Amount: Rs. " + totalBeforeDiscount, "Success", JOptionPane.INFORMATION_MESSAGE);
                }

                billTextArea.append("\nThank you for shopping with us!");
                billTextArea.updateUI();

            } else {
                JOptionPane.showMessageDialog(this, "No items in the order. Cannot generate bill.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            currentOrderID = 0;
            createNewOrderButton.setEnabled(true);
            customerBox.setEnabled(true);
            productBox.setEnabled(false);
            quantitySpinner.setEnabled(false);
            applyDiscountButton.setEnabled(false);
            addToBillButton.setEnabled(false);
            generateBillButton.setEnabled(false);
            discardOrderButton.setEnabled(false);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating bill: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }



    private void createNewOrder() {
        clearBillArea(billTextArea);
        try {
            String custID = customerBox.getSelectedItem().toString();
            currentOrderID = DatabaseOperations.createNewOrder(Integer.parseInt(custID.split("-")[0]));
            billTextArea.append("Customer: " + custID.split("-")[1] + "\n");
            billTextArea.append("Order ID: " + currentOrderID + "\n\n");
            billTextArea.append("ProdID\tName\tQty\tPrice\n");
            createNewOrderButton.setEnabled(false);
            customerBox.setEnabled(false);
            productBox.setEnabled(true);
            quantitySpinner.setEnabled(true);
            applyDiscountButton.setEnabled(true);
            addToBillButton.setEnabled(true);
            generateBillButton.setEnabled(true);
            discardOrderButton.setEnabled(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void discardOrder() {
        try {
            DatabaseOperations.discardOrder(currentOrderID);
            clearBillArea(billTextArea);
            currentOrderID = 0;
            createNewOrderButton.setEnabled(true);
            customerBox.setEnabled(true);
            productBox.setEnabled(false);
            quantitySpinner.setEnabled(false);
            addToBillButton.setEnabled(false);
            generateBillButton.setEnabled(false);
            discardOrderButton.setEnabled(false);
        } catch (SQLException ex) {
        ex.printStackTrace();
       }
    }



    
    private void clearBillArea(JTextArea billTextArea) {
        billTextArea.setText("\t------------------- Gupta Super Market --------------------\n");
    }
    
    public static void main(String[] args) {
    // No longer needed, since CashierPanel is created via handleLogin()
    }
}