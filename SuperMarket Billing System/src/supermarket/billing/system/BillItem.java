/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package supermarket.billing.system;

/**
 *
 * @author Shivam Gupta
 */

public class BillItem {
    private String productName;
    private double price;
    private int quantity;

    // Constructor
    public BillItem(String productName, double price, int quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getter for the product name
    public String getProductName() {
        return productName;
    }

    // Getter for price
    public double getPrice() {
        return price;
    }

    // Getter for quantity
    public int getQuantity() {
        return quantity;
    }

    // Method to calculate the total price for the item (price * quantity)
    public double getTotalPrice() {
        return price * quantity;
    }

    // Override toString method to display the item in a formatted manner
    @Override
    public String toString() {
        return String.format("%-20s%-20.2f%-20d%-10.2f\n", 
                             productName, 
                             price, 
                             quantity,
                             getTotalPrice());
    }
}