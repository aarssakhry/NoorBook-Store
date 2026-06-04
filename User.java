/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package noorbookstore;

/**
 *
 * @author USER
 */
//ABSTRACTION
abstract class User {
    
    //ENCAPSULATION
    private String userID;
    private String name;
    private String email;
    private String password;
    
    //Constructor
    public User(String userID, String name, String email, String password){
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    //Getters method
    public String getUserID(){
        return userID;
    }
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }
    
    //Setters method
    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPassword(String password){
        this.password = password;
    }
    
    //ABSTRACTION
    //POLYMORPHISM
    public abstract void showDashboard();
}
