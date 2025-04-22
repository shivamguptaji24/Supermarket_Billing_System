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
import javax.swing.border.EmptyBorder;

public class AdminLogin  {

  private JFrame frame;
  private JTextField adminNameField;
  private JPasswordField passwordField;
  private final String  adminName = "Admin";
  private final String password = "1234";

  public AdminLogin() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    // Frame settings
    frame = new JFrame();
    frame.setBounds(100, 100, 450, 300);
    frame.setTitle("Admin Login");
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
    JLabel lblTitle = new JLabel("Admin Login");
    lblTitle.setForeground(Color.WHITE);
    lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
    lblTitle.setHorizontalAlignment(JLabel.CENTER);
    lblTitle.setBounds(0, 30, 450, 40);
    panel.add(lblTitle);

    // Admin Name label and field
    JLabel lblAdmin = new JLabel("Admin Name");
    lblAdmin.setForeground(Color.WHITE);
    lblAdmin.setFont(new Font("Arial", Font.PLAIN, 16));
    lblAdmin.setBounds(40, 90, 120, 25);
    panel.add(lblAdmin);

    adminNameField = new JTextField();
    adminNameField.setBounds(200, 90, 200, 30);
    adminNameField.setFont(new Font("Arial", Font.PLAIN, 14));
    adminNameField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));  // Add padding inside the text field
    panel.add(adminNameField);

    // Password label and field
    JLabel lblPassword = new JLabel("Password");
    lblPassword.setForeground(Color.WHITE);
    lblPassword.setFont(new Font("Arial", Font.PLAIN, 16));
    lblPassword.setBounds(40, 140, 120, 25);
    panel.add(lblPassword);

    passwordField = new JPasswordField();
    passwordField.setBounds(200, 140, 200, 30);
    passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
    passwordField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));  // Add padding inside the text field
    panel.add(passwordField);

    // Login button with modern style
    JButton btnLogin = new JButton("Login");
    btnLogin.setBackground(new Color(0, 123, 255));  // Blue background
    btnLogin.setForeground(Color.WHITE);
    btnLogin.setFont(new Font("Arial", Font.PLAIN, 16));
    btnLogin.setFocusPainted(false);  // Removes the focus border
    btnLogin.setBorderPainted(false);  // Removes the border
    btnLogin.setOpaque(true);  // Makes the background color visible
    btnLogin.setBounds(150, 200, 150, 40);
    btnLogin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (adminNameField.getText().equals(adminName) && password.equals(new String(passwordField.getPassword()))) {
          frame.dispose();
          new AdminPanel(); // Navigate to the Admin panel.
        } else {
          JOptionPane.showMessageDialog(btnLogin, "Incorrect Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    panel.add(btnLogin);

    frame.setVisible(true);
  }
}