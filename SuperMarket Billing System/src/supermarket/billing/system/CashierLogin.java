/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package supermarket.billing.system;

/**
 *
 * @author Shivam Gupta
 */

import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class CashierLogin {

    private JFrame frame;
    private JTextField cashierIdField;
    private JPasswordField cashierPasswordField;
    private JButton btnLogin;

    public CashierLogin() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // Frame settings
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setTitle("Cashier Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);  // Removes default window decorations
        frame.setLocationRelativeTo(null);  // Center the window

        // Panel with padding and background color
        JPanel panel = new JPanel();
        panel.setBackground(new Color(33, 37, 41));  // Dark theme background
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));  // Adds padding around the content
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        // Title label
        JLabel lblTitle = new JLabel("Cashier Login");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        lblTitle.setBounds(0, 30, 450, 40);
        panel.add(lblTitle);

        // Cashier Name label and field
        JLabel lblCashier = new JLabel("Cashier ID");
        lblCashier.setForeground(Color.WHITE);
        lblCashier.setFont(new Font("Arial", Font.PLAIN, 16));
        lblCashier.setBounds(40, 90, 120, 25);
        panel.add(lblCashier);

        cashierIdField = new JTextField();
        cashierIdField.setBounds(200, 90, 200, 30);
        cashierIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        cashierIdField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));  // Add padding inside the text field
        panel.add(cashierIdField);

        // Password label and field
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPassword.setBounds(40, 140, 120, 25);
        panel.add(lblPassword);

        cashierPasswordField = new JPasswordField();
        cashierPasswordField.setBounds(200, 140, 200, 30);
        cashierPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        cashierPasswordField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));  // Add padding inside the text field
        panel.add(cashierPasswordField);

        // Login button with modern style
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(40, 167, 69));  // Green background
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.PLAIN, 16));
        btnLogin.setFocusPainted(false);  // Removes the focus border
        btnLogin.setBorderPainted(false);  // Removes the border
        btnLogin.setOpaque(true);  // Makes the background color visible
        btnLogin.setBounds(150, 200, 150, 40);
        btnLogin.addActionListener(e -> handleLogin());
        panel.add(btnLogin);

        frame.setVisible(true);
        
    }

    private void handleLogin() {
        String cashierIdText = cashierIdField.getText();
        String password = new String(cashierPasswordField.getPassword());

        if (cashierIdText.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(btnLogin, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int cashierId = Integer.parseInt(cashierIdText);
            
            if (DatabaseOperations.authenticateCashier(cashierId, password)) {
                DatabaseOperations.recordCashierLogin(cashierId);  // Record login details
                // Pass cashierId to CashierPanel constructor
                SwingUtilities.invokeLater(() -> new CashierPanel(cashierId));                
                frame.dispose(); // Close login window
            } else {
                JOptionPane.showMessageDialog(btnLogin, "Invalid ID or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(btnLogin, "Login Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}