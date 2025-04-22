/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package supermarket.billing.system;

/**
 *
 * @author Shivam Gupta
 */
public class Product {
    private String name;
    private double price;

    // Constructor
    public Product(String name) {
        this.name = name;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
