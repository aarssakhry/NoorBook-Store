/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package noorbookstore;

// ==========================================
// NAME: NUR ATIQAH (2518126) 
// ==========================================

public class Product {

    private String productID;
    private String title;
    private String author;
    private double price;
    private int stock;
    private String category;

    public Product(String productID, String title, String author, double price, int stock, String category) {

        this.productID = productID;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public String getProductID() { return productID; }
    public void setProductID(String productID) { this.productID = productID; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isAvailable() {
        return stock > 0;
    }

    @Override
    public String toString() {

        return productID + " - "
                + title + " - RM"
                + price;
    }
}
