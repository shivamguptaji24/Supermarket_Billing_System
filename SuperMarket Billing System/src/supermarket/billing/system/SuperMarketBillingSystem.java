/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package supermarket.billing.system;

/**
 *
 * @author Shivam Gupta
 */

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;

public class SuperMarketBillingSystem {

    private JFrame frmLoginWindow;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SuperMarketBillingSystem window = new SuperMarketBillingSystem();
                    window.frmLoginWindow.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SuperMarketBillingSystem() {
        DatabaseOperations.dbInit(); // Initialize the database connection
        initialize();
    }

    private void initialize() {
        // Main Frame with rounded corners and shadow
        frmLoginWindow = new JFrame();
        frmLoginWindow.setTitle("Supermarket Billing System");
        frmLoginWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Maximizes the frame to full-screen
        frmLoginWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmLoginWindow.setLocationRelativeTo(null);
        frmLoginWindow.setUndecorated(true);
        frmLoginWindow.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

        // Custom Panel with Gradient and modern background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(34, 45, 60); // Dark background
                Color color2 = new Color(0, 102, 204); // Cool blue accents
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());
        frmLoginWindow.getContentPane().add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        // Header Label with updated font style
        JLabel lblWelcome = new JLabel("SuperMarket Billing System");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Poppins", Font.BOLD, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblWelcome, gbc);

        // Instruction Label with modern font
        JLabel lblInstruction = new JLabel("Select Login Profile");
        lblInstruction.setForeground(Color.WHITE);
        lblInstruction.setFont(new Font("Poppins", Font.PLAIN, 18));
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Ensure the label spans both columns
        panel.add(lblInstruction, gbc);

        // Admin Button with hover effect and smooth transition
        JButton btnAdmin = createStyledButton("Admin", new Color(60, 123, 255), new Color(38, 94, 218));
        btnAdmin.addActionListener(e -> {
            frmLoginWindow.dispose();
            new AdminLogin(); // Navigate to Admin Login
        });
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1; // Admin button spans only 1 column
        panel.add(btnAdmin, gbc);

        // Cashier Button with hover effect and smooth transition
        JButton btnCashier = createStyledButton("Cashier", new Color(66, 185, 85), new Color(49, 139, 67));
        btnCashier.addActionListener(e -> {
            frmLoginWindow.dispose();
            new CashierLogin(); // Navigate to Cashier Login
        });
        gbc.gridx = 1;
        panel.add(btnCashier, gbc);
    }

    /**
     * Creates a modern-styled button with hover effects and animation.
     */
    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Poppins", Font.PLAIN, 18));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setPreferredSize(new Dimension(140, 50));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Hover effect with smooth transition
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        // Adding smooth transition animation
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setRolloverEnabled(true);

        return button;
    }
}