/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package supermarket.billing.system;

/**
 *
 * @author Shivam Gupta
 */

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DatabaseOperations {
    // Database connection details
    private static final String URL = "jdbc:sqlite:supermarket.db";
    
    private static Connection conn;

    // Initialize the database with all the required tables
    public static void dbInit() {
        try {
            conn = DriverManager.getConnection(URL);
            Statement statement = conn.createStatement();
            statement.execute("PRAGMA foreign_keys = ON;");

            String createTables = "CREATE TABLE IF NOT EXISTS cashiers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "contact VARCHAR(15)" +
                    "); " +

                    "CREATE TABLE IF NOT EXISTS products (" +
                    "product_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "price REAL NOT NULL CHECK (price >= 0), " +
                    "quantity INT NOT NULL CHECK (quantity >= 0)" +
                    "); " +

                    "CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "customer_id INT NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE" +
                    "); " +

                    "CREATE TABLE IF NOT EXISTS order_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE" +
                    "); " +
                    
                    
                    "CREATE TABLE IF NOT EXISTS stock (" +
                    "product_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "status TEXT CHECK(status IN ('In Stock', 'Out of Stock')) NOT NULL, " +
                    "PRIMARY KEY (product_id), " +
                    "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE" +
                    "); " +
                    
                    "CREATE TABLE IF NOT EXISTS sales (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "sale_date TEXT NOT NULL, " +
                    "total_amount FLOAT NOT NULL, " +
                    "quantity INT NOT NULL" +
                    ");" ;

            statement.executeUpdate(createTables);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // ------------------------------------------------------------ ADMIN PANEL -----------------------------------------------------------------------
        
    // Common Method to load data into a JTable model (For Cashiers and Products)
    public static void loadData(DefaultTableModel model, String table) throws SQLException {
        model.setRowCount(0);
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);

        ResultSet rs = ps.executeQuery();
        Object[] row = new Object[model.getColumnCount()]; 
        while (rs.next()) {
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i+1);
            }
            model.addRow(row);
        }
        ps.close();
        conn.close();
    }
    
    // Method to search for Cashiers
    public static void searchCashiers(String searchText, DefaultTableModel model) throws SQLException {
        String query = "SELECT * FROM cashiers WHERE name LIKE ? OR email LIKE ? OR contact LIKE ?";
        conn = DriverManager.getConnection(URL);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + searchText + "%");
            ps.setString(2, "%" + searchText + "%");
            ps.setString(3, "%" + searchText + "%");
            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0); // Clear existing rows in the table model
                boolean hasRecords = false; // Flag to check if records exist
                while (rs.next()) {
                    hasRecords = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    String contact = rs.getString("contact");
                    model.addRow(new Object[]{id, name, password, email, contact});
                }
                // If no records were found, display a "No Records Found" message
                if (!hasRecords) {
                    model.addRow(new Object[]{"-", "No Records Found", "-", "-", "-"});
                }
            }
        }
        conn.close();
    }
    
    // Method to add new Cashier
    public static void addCashier(String name, String password, String email, String contact) throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        String query = "INSERT INTO cashiers (name, password, email, contact) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, contact);
            ps.executeUpdate();
        }
    }
    
    // Method to update Cashier details
    public static void updateCashier(int id, String name, String password, String email, String contact) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("UPTEXT cashiers SET name = ?, password = ?, email = ?, contact = ? WHERE id = ?");
        ps.setString(1, name);
        ps.setString(2, password);
        ps.setString(3, email);
        ps.setString(4, contact);
        ps.setInt(5, id);
        ps.executeUpdate();

        ps.close();
        conn.close();
    }
    
    // Method to delete multiple records from a specific table
    public static void deleteMultiple(List<Integer> ids, String table) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return; // Nothing to delete
        }

        String placeholders = String.join(",", ids.stream().map(id -> "?").toArray(String[]::new));
        String sql = "DELETE FROM " + table + " WHERE id IN (" + placeholders + ")";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set the IDs in the placeholders
            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }

            int rowsAffected = ps.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " records from " + table);
        }
    }
    
    // Method to search for a product by name or part of the name
    public static void searchProducts(String searchText, DefaultTableModel model) throws SQLException {
        String query = "SELECT * FROM products WHERE product_id LIKE ? OR name LIKE ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Set search parameters with wildcards for partial matching
            ps.setString(1, "%" + searchText + "%");
            ps.setString(2, "%" + searchText + "%");

            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0); // Clear the previous data in the table
                boolean hasRecords = false; // Flag to check if records exist

                while (rs.next()) {
                    hasRecords = true; // Records were found

                    int productId = rs.getInt("product_id"); // Correct field name for product ID
                    String name = rs.getString("name");
                    float price = rs.getFloat("price");
                    int quantity = rs.getInt("quantity");
                    model.addRow(new Object[]{productId, name, price, quantity}); // Add data to the table
                }

                // If no records were found, display a "No Records Found" message
                if (!hasRecords) {
                    model.addRow(new Object[]{"-", "No Records Found", "-"});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Re-throw exception to be handled elsewhere if needed
        }
    }

    
    // Method to add a new product
    public static void addProduct(String name, Float price, int quantity) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, name);
        ps.setFloat(2, price);
        ps.setInt(3, quantity);
        ps.executeUpdate();
        
        // Get generated product ID
        ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int productId = generatedKeys.getInt(1);

                // Add the new product to stock (with initial quantity = 0)
                addProductToStock(productId, name, price, quantity);
            }
        ps.close();
        conn.close();
    }
    
    // Add product to stock when a product is added
    public static void addProductToStock(int productId, String name, Float price, int quantity) {
        PreparedStatement ps = null;

        try {
            // Insert product into stock table
            String insertStockQuery = "INSERT INTO stock (product_id, name, price, quantity, status) VALUES (?, ?, ?, ?, 'In Stock')";
            ps = conn.prepareStatement(insertStockQuery);
            ps.setInt(1, productId);
            ps.setString(2, name);
            ps.setFloat(3, price);
            ps.setInt(4, quantity);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product added to stock successfully.");
            } else {
                System.out.println("Failed to add product to stock.");
            }
        } catch (SQLException ex) {
            System.err.println("SQL Exception while adding product to stock: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException ex) {
                System.err.println("Error closing statement: " + ex.getMessage());
            }
        }
    }
    
    // Method to delete multiple products
    public static void deleteProducts(List<Integer> productIds) throws SQLException {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs list cannot be null or empty.");
        }

        String placeholders = String.join(",", Collections.nCopies(productIds.size(), "?"));
        String sql = "DELETE FROM products WHERE product_id IN (" + placeholders + ")";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set product IDs in the query
            for (int i = 0; i < productIds.size(); i++) {
                ps.setInt(i + 1, productIds.get(i));
            }

            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected + " product(s) deleted successfully.");
        } catch (SQLException ex) {
            throw new SQLException("Failed to delete products: " + ex.getMessage());
        }
    }
    
    // Delete product from stock when a product is deleted
    public static void deleteProductFromStock(int productId) throws SQLException {
        String query = "DELETE FROM stock WHERE product_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }
    
    // Load stock data into the JTable
    public static void loadStockData(DefaultTableModel tableModel) throws SQLException {
        String query = "SELECT * FROM stock s INNER JOIN products p ON s.product_id = p.product_id";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(query)) {
             ResultSet resultSet = ps.executeQuery(query);

            // Clear existing rows
            tableModel.setRowCount(0);

            // Add data to the table model
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                String status = resultSet.getString("status");
                String createdAt = resultSet.getString("created_at");
                String updatedAt = resultSet.getString("updated_at");
                tableModel.addRow(new Object[]{productId, name, price, quantity, status, createdAt, updatedAt});
            }
        }
    }
    
    // Search stock by product_id, name, quantity, and last updated
    public static void searchStock(String query, DefaultTableModel tableModel) throws SQLException {
        String sqlQuery = "SELECT * FROM stock s " +
                          "JOIN products p ON s.product_id = p.product_id " +
                          "WHERE p.name LIKE ? " +
                          "OR s.status LIKE ? " +
                          "OR CAST(s.updated_at AS CHAR) LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            // Prepare search query pattern for LIKE clause
            String searchPattern = "%" + query + "%";
            ps.setString(1, searchPattern); // Search by product name
            ps.setString(2, searchPattern); // Search by quantity
            ps.setString(3, searchPattern); // Search by updated_at timestamp

            ResultSet resultSet = ps.executeQuery();

            // Clear existing rows in the table model
            tableModel.setRowCount(0);

            boolean hasResults = false;

            // Populate the table model with results
            while (resultSet.next()) {
                hasResults = true; // Indicate that results were found
                // Fetch all columns from stock table and related product info
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                String status = resultSet.getString("status");
                String createdAt = resultSet.getString("created_at");
                String updatedAt = resultSet.getString("updated_at");
                tableModel.addRow(new Object[]{productId, productName, price, quantity, status, createdAt, updatedAt});
            }

            // Show "No records found" if no results were retrieved
            if (!hasResults) {
                JOptionPane.showMessageDialog(null, "No records found.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            // Print the error message for debugging
            e.printStackTrace();
            throw e;  // Re-throw the exception to handle it elsewhere if needed
        }
    }
    
    // Method to update stock quantity
    public static void updateStockQuantity(int productId, int newQuantity) throws SQLException {
        Connection conn = null;
        PreparedStatement psStock = null;
        PreparedStatement psProduct = null;

        try {
            // Establish connection
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false); // Start a transaction

            // Update quantity in the stock table
            String updateStockQuery = "UPTEXT stock SET quantity = ?, status = ? WHERE product_id = ?";
            psStock = conn.prepareStatement(updateStockQuery);
            psStock.setInt(1, newQuantity);
            // If quantity is 0, set status to 'Out of Stock', else 'In Stock'
            String status = newQuantity == 0 ? "Out of Stock" : "In Stock";
            psStock.setString(2, status);
            psStock.setInt(3, productId);

            int rowsAffectedStock = psStock.executeUpdate();
            if (rowsAffectedStock > 0) {
                System.out.println("Stock updated successfully.");
            } else {
                System.out.println("Failed to update stock.");
            }

            // Update quantity in the products table as well
            String updateProductQuery = "UPTEXT products SET quantity = ? WHERE product_id = ?";
            psProduct = conn.prepareStatement(updateProductQuery);
            psProduct.setInt(1, newQuantity);
            psProduct.setInt(2, productId);

            int rowsAffectedProduct = psProduct.executeUpdate();
            if (rowsAffectedProduct > 0) {
                System.out.println("Product quantity updated successfully.");
            } else {
                System.out.println("Failed to update product quantity.");
            }

            // Commit the transaction
            conn.commit();

        } catch (SQLException ex) {
            System.err.println("SQL Exception while updating stock: " + ex.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of error
                    System.out.println("Transaction rolled back.");
                } catch (SQLException e) {
                    System.err.println("Error during rollback: " + e.getMessage());
                }
            }
        } finally {
            try {
                if (psStock != null) psStock.close();
                if (psProduct != null) psProduct.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error closing resources: " + ex.getMessage());
            }
        }
    }
    
    public static void fetchCashierLogins(DefaultTableModel tableModel) throws SQLException {
        String query = "SELECT * FROM cashier_logins";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear the existing table data
            tableModel.setRowCount(0);

            // Iterate over the result set and add rows to the table model
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("sr_no");
                row[1] = rs.getString("name");
                row[2] = rs.getString("cashier_id");
                row[3] = rs.getTimestamp("login_time");
                row[4] = rs.getTimestamp("logout_time");
                row[5] = rs.getDate("date");

                tableModel.addRow(row);  // Add row to table model
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void generateWeeklyReport(DefaultTableModel model) throws SQLException {
        String query = """
            SELECT 
                TEXT_FORMAT(sale_date, '%Y-%u') AS week,
                SUM(total_amount) AS total_sales,
                SUM(quantity) AS total_quantity
            FROM sales
            GROUP BY week
            ORDER BY week DESC
        """;
        generateReport(query, model, "Week");
    }

    public static void generateMonthlyReport(DefaultTableModel model) throws SQLException {
        String query = """
            SELECT 
                TEXT_FORMAT(sale_date, '%Y-%m') AS month,
                SUM(total_amount) AS total_sales,
                SUM(quantity) AS total_quantity
            FROM sales
            GROUP BY month
            ORDER BY month DESC
        """;
        generateReport(query, model, "Month");
    }

    private static void generateReport(String query, DefaultTableModel model, String periodColumn) throws SQLException {
        conn = DriverManager.getConnection(URL);
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            model.setRowCount(0); // Clear previous data
            while (rs.next()) {
                String period = rs.getString(periodColumn.equals("Week") ? "week" : "month");
                double totalSales = rs.getDouble("total_sales");
                int totalQuantity = rs.getInt("total_quantity");
                model.addRow(new Object[]{period, totalSales, totalQuantity});
            }
        } finally {
            conn.close();
        }
    }




    
    
    
    
    
    // ---------------------------------------------------------- CASHIER PANEL ------------------------------------------------------------------------
    
    // Method to Authenticate the Cashiers
    public static boolean authenticateCashier(int cashierId, String password) throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        String query = "SELECT * FROM cashiers WHERE id = ? AND password = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, cashierId);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        return rs.next(); // Returns true if credentials match
    }
    
    public static String getCashierNameById(int cashierId) {
        String sql = "SELECT name FROM cashiers WHERE id = ?";
        String cashierName = null;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cashierId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cashierName = rs.getString("name");
            } else {
                System.out.println("Cashier ID not found.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve cashier name: " + e.getMessage());
        }

        return cashierName;
    }
    
    public static void recordCashierLogin(int cashierId) {
        String query = "INSERT INTO cashier_logins (name, cashier_id, login_time, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            // Assuming you have a method to get the cashier's name by their ID
            String cashierName = getCashierNameById(cashierId);

            ps.setString(1, cashierName);
            ps.setInt(2, cashierId);
            ps.setTime(3, new java.sql.Time(System.currentTimeMillis()));
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Method to record logout time in the cashier_logins table
    public static void recordCashierLogout(int cashierId) {
        String query = "UPTEXT cashier_logins SET logout_time = ? WHERE cashier_id = ? AND date = ? AND logout_time IS NULL";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Set current time for logout time
            ps.setTime(1, new java.sql.Time(System.currentTimeMillis()));

            // Set the cashier's ID
            ps.setInt(2, cashierId);

            // Set the current date for the session
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));

            // Execute the update query
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error recording logout time: ");
        }
    }
        
    public static Cashier getCashierDetails(int cashierId) {
        String query = "SELECT id, name, email, contact FROM cashiers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, cashierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cashier cashier = new Cashier();
                    cashier.setId(rs.getInt("id"));
                    cashier.setName(rs.getString("name"));
                    cashier.setEmail(rs.getString("email"));
                    cashier.setContact(rs.getString("contact"));
                    return cashier;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching cashier details for ID " + cashierId);
        }
        return null;
    }
    
    // Method to load Products in the Cashier Panel
    public static void loadProductInCashierPanel(DefaultTableModel model) throws SQLException {
        String query = "SELECT product_id, name, price, quantity, status FROM stock";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[] {
                    rs.getInt("product_id"),        // Product ID
                    rs.getString("name"),   // Product Name
                    rs.getDouble("price"),  // Product Price
                    rs.getInt("quantity"),  // Product Quantity
                    rs.getString("status")  // Product Status
                };
                model.addRow(row); // Add the row to the table model
            }
        }
    }
    
    // Method to fetch products from the database
    public static List<Product> getProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        
        // SQL query to fetch product details
        String query = "SELECT name FROM products";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Iterate over the result set and create Product objects
            while (rs.next()) {
                String name = rs.getString("name");
                Product product = new Product(name);
                products.add(product);
            }
        }
        
        return products;
    }

    
    
    
    
    
    
    // Method to update JComboBox with data from the database
    public static void updateCombox(String table, JComboBox<String> cbx) throws SQLException {
        cbx.removeAllItems();
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            cbx.addItem(rs.getString("name"));
        }

        rs.close();
        ps.close();
        conn.close();
    }
    
    public static String getPriceForProduct(String productName) throws SQLException {
        String query = "SELECT price FROM products WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, productName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("price");
            }
        }
        return "0"; // Default price if product not found
    }

    public static void saveBillToDatabase(int billNo, String customerName, LocalDateTime dateTime, double totalAmount, List<BillItem> billItems) {
        String billsQuery = "INSERT INTO bills (bill_no, customer_name, date_time, total_amount) VALUES (?, ?, ?, ?)";
        String billItemsQuery = "INSERT INTO bill_Items (bill_no, name, price, quantity, total_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL); // Get DB connection
             PreparedStatement billsStmt = conn.prepareStatement(billsQuery);
             PreparedStatement billItemsStmt = conn.prepareStatement(billItemsQuery)) {

            // Insert into bills table
            billsStmt.setInt(1, billNo);
            billsStmt.setString(2, customerName);
            billsStmt.setTimestamp(3, Timestamp.valueOf(dateTime));
            billsStmt.setDouble(4, totalAmount);
            billsStmt.executeUpdate();

            // Insert into bill_Items table
            for (BillItem item : billItems) {
                billItemsStmt.setInt(1, billNo);
                billItemsStmt.setString(2, item.getProductName());
                billItemsStmt.setDouble(3, item.getPrice());
                billItemsStmt.setInt(4, item.getQuantity());
                billItemsStmt.setDouble(5, item.getTotalPrice());
                billItemsStmt.addBatch();
            }
            billItemsStmt.executeBatch(); // Execute batch insert for bill items

            System.out.println("Bill saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving bill to the database!");
        }
    }
    
    public static Bill retrieveBillFromDatabase(int billNo) {
        Bill bill = null;
        String queryBill = "SELECT * FROM bills WHERE bill_no = ?";
        String queryItems = "SELECT * FROM bill_items WHERE bill_no = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement billStatement = conn.prepareStatement(queryBill);
             PreparedStatement itemsStatement = conn.prepareStatement(queryItems)) {

            // Query the bill
            billStatement.setInt(1, billNo);
            ResultSet billResult = billStatement.executeQuery();

            if (billResult.next()) {
                // Retrieve bill information
                String customerName = billResult.getString("customer_name");
                LocalDateTime dateTime = billResult.getTimestamp("date_time").toLocalDateTime();
                double totalAmount = billResult.getDouble("total_amount");
                bill = new Bill(billNo, customerName, dateTime, totalAmount);

                // Query the bill items
                itemsStatement.setInt(1, billNo);
                ResultSet itemsResult = itemsStatement.executeQuery();
                List<BillItem> billItems = new ArrayList<>();

                while (itemsResult.next()) {
                    String name = itemsResult.getString("name");
                    double price = itemsResult.getDouble("price");
                    int quantity = itemsResult.getInt("quantity");
                    BillItem item = new BillItem(name, price, quantity);
                    billItems.add(item);
                }

                // Set the bill items
                bill.setBillItems(billItems);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bill;
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Method to create a new order
    public static int createNewOrder(int custID) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("INSERT INTO orders (customer_id, date) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, custID);
        ps.setDate(2, Date.valueOf(java.time.LocalDate.now()));
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int oid = rs.getInt(1);

        rs.close();
        ps.close();
        conn.close();

        return oid;
    }

    // Method to add items to an order
    public static void addOrderItems(int orderID, int prodID, int quantity, Float price) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)");

        ps.setInt(1, orderID);
        ps.setInt(2, prodID);
        ps.setInt(3, quantity);
        ps.setFloat(4, price);
        ps.executeUpdate();

        ps.close();
        conn.close();
    }

    // Method to discard/delete an order
    public static void discardOrder(int orderID) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("DELETE FROM order_items WHERE order_id = ?");
        ps.setInt(1, orderID);
        ps.executeUpdate();

        ps = conn.prepareStatement("DELETE FROM orders WHERE id = ?");
        ps.setInt(1, orderID);
        ps.executeUpdate();

        ps.close();
        conn.close();
    }

    // Method to delete the latest item from an order
    public static void deleteOrderItem(int orderID) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("DELETE FROM order_items WHERE id = (SELECT MAX(id) FROM order_items WHERE order_id = ?)");

        ps.setInt(1, orderID);
        ps.executeUpdate();

        ps.close();
        conn.close();
    }

    // Method to calculate the total price of an order
    public static float getTotalPrice(int orderID) throws SQLException {
        float price = 0;

        // Ensure the connection is properly closed using try-with-resources
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement("SELECT SUM(price * quantity) AS total_price FROM order_items WHERE order_id = ?")) {

            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // Check if there are any rows
                    price = rs.getFloat("total_price"); // Use the column label to retrieve the value
                }
            }
        }

        return price; // Return the calculated total price
    }



    



    // Method to retrieve product details
    public static String[] getProd(int id) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE id = ?");

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();

        String[] product = {rs.getString("id"), rs.getString("name"), rs.getString("price")};

        rs.close();
        ps.close();
        conn.close();

        return product;
    }

    // Method to add a new customer
    public static void addCustomer(String name, String phone, String email, String address) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)");

        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);
        ps.setString(4, address);
        ps.executeUpdate();

        ps.close();
        conn.close();
    }
    
    // Method to update customer details
    public static void updateCustomer(int id, String name, String phone, String email, String address) throws SQLException {
        conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("UPTEXT customers SET name = ?, phone = ?, email = ?, address = ? WHERE id = ?");
        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);
        ps.setString(4, address);
        ps.setInt(5, id);
        ps.executeUpdate();

        ps.close();
        conn.close();
    }

    // Method to search for customers
    public static void searchCustomers(String searchText, DefaultTableModel model) throws SQLException {
        String query = "SELECT * FROM customers WHERE name LIKE ? OR phone LIKE ? OR email LIKE ?";
        conn = DriverManager.getConnection(URL);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + searchText + "%");
            ps.setString(2, "%" + searchText + "%");
            ps.setString(3, "%" + searchText + "%");
            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0); // Clear existing rows in the table model
                boolean hasRecords = false; // Flag to check if records exist
                while (rs.next()) {
                    hasRecords = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    model.addRow(new Object[]{id, name, phone, email, address});
                }
                // If no records were found, display a "No Records Found" message
                if (!hasRecords) {
                    model.addRow(new Object[]{"-", "No Records Found", "-", "-", "-"});
                }
            }
        }
        conn.close();
    }

    // Method to export data to a CSV file
    public static void exportData(String tableName, String filePath) throws SQLException {
        conn = DriverManager.getConnection(URL);
        String query = "SELECT * FROM " + tableName;
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        try (FileWriter writer = new FileWriter(filePath)) {
            int columnCount = rs.getMetaData().getColumnCount();
            // Write column headers
            for (int i = 1; i <= columnCount; i++) {
                writer.append(rs.getMetaData().getColumnName(i));
                if (i < columnCount) writer.append(",");
            }
            writer.append("\n");

            // Write rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.append(rs.getString(i));
                    if (i < columnCount) writer.append(",");
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ps.close();
        conn.close();
    }





    

    



    

    

}