/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package noorbookstore;

// ==========================================
// NAME: ZULAIKHA HANANI (2514396) 
// ==========================================

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private String membershipTier;
    private List<String> orderHistory;

    public Customer(String userID, String name, String email, String password, String membershipTier) {
        super(userID, name, email, password);
        this.membershipTier = membershipTier;
        this.orderHistory = new ArrayList<>();
    }

    public String getMembershipTier() { return membershipTier; }
    public void setMembershipTier(String membershipTier) { this.membershipTier = membershipTier; }
    public void addOrder(String orderDetails) { orderHistory.add(orderDetails); }
    public List<String> getOrderHistory() { return orderHistory; }

    // Helper method to resolve the dynamic Interface strategy
    public Membership getMembershipStrategy() {
        switch (membershipTier.toLowerCase()) {
            case "gold": return new GoldMembership();
            case "silver": return new SilverMembership();
            case "bronze": return new BronzeMembership();
            default: return new StandardMembership();
        }
    }
}
