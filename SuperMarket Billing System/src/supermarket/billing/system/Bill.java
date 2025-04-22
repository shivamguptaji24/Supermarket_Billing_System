/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package supermarket.billing.system;

/**
 *
 * @author Shivam Gupta
 */

import java.time.LocalDateTime;
import java.util.List;

public class Bill {
    private int billNo;
    private String customerName;
    private LocalDateTime dateTime;
    private double totalAmount;
    private List<BillItem> billItems;  // Assuming you have a BillItem class

    // Constructor
    public Bill(int billNo, String customerName, LocalDateTime dateTime, double totalAmount) {
        this.billNo = billNo;
        this.customerName = customerName;
        this.dateTime = dateTime;
        this.totalAmount = totalAmount;
    }

    // Getter and Setter methods
    public int getBillNo() {
        return billNo;
    }

    public void setBillNo(int billNo) {
        this.billNo = billNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }
}