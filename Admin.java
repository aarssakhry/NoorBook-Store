/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package noorbookstore;

// ==========================================
// NAME: ZULAIKHA (2514396) 
// ==========================================

//INHERITANCE
public class Admin extends User {
    
    //ENCAPSULATION
    private String role; //e.g. "Super Admin", "Product Manager"
    
    //Constructor
    public Admin(String userID, String name, String email, String password, String role){
      super(userID, name, email, password);
      this.role = role;
    }
    
    //Getter method
    public String getRole(){
        return role;
    }
    
    //Setter method
    public void setRole(String role){
        this.role = role;
    }
    
    //POLYMORPHISM
    //displays an admin-specific dashboard
    @Override
    public void showDashboard(){
        System.out.println("===================================================");
        System.out.println("           NoorBook Store - Admin Panel           ");
        System.out.println("===================================================");
        System.out.println(" Admin Name: " + getName());
        System.out.println(" Role: " + role);
        System.out.println(" Access: Add / Edit/ Delete Products");
        System.out.println("       : View All Customer Orders");
        System.out.println("===================================================");
    }
}
