/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package noorbookstore;

/**
 *
 * @author USER
 */
//INHERITANCE
public class Customer extends User {
    
    //ENCAPSULATION
    private String[] orderHistory; //empty array to store order records
    private int orderCount; //to track how many orders have been added
    private String membershipTier; //e.g. "Bronze", "Silver", "Gold"
    
    //Constructor
    public Customer(String userID, String name, String email, String password, String membershipTier){
        super(userID, name, email, password);
        this.membershipTier = membershipTier;
        this.orderHistory = new String [100];
        this.orderCount = 0;
    }
    
    //Getters method
    public String getMembershipTier(){
        return membershipTier;
    }
    public int getOrderCount(){
        return orderCount;
    }
    public String[] getOrderHistory(){
        return orderHistory;
    }
    
    //Setter method
    public void setMembershipTier(String tier){
        this.membershipTier = tier;
    }
    
    //add order to history
    public void addOrder(String orderSummary){
        orderHistory[orderCount] = orderSummary;
        orderCount++;
        System.out.println("Order recorded: " + orderSummary);
    }
    
    //POLYMORPHISM
    //displays a customer-specific dashboard
    @Override
    public void showDashboard(){
        System.out.println("===================================================");
        System.out.println("         NoorBook Store - Customer Portal"          );
        System.out.println("===================================================");
        System.out.println(" Welcome, " + getName() + "!");
        System.out.println(" Membership Tier: " + getMembershipTier());
        System.out.println(" Past Orders: " + orderCount);
        System.out.println("Browse our latest Islamic books & more.");
        System.out.println("===================================================");
    }
}
