/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package noorbookstore;

/**
 *
 * @author User
 */

// ==========================================
// NAME: NUR ATIQAH (2518126) 
// ==========================================

public class BookCategories {

    // Attributes
    private String categoryID;
    private String categoryName;
    private String categoryDescription;
    
    // Constructor
    public BookCategories(String categoryID, String categoryName, String categoryDescription) {

        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryID() {return categoryID;}
    public void setCategoryID(String categoryID) {this.categoryID = categoryID;}
      
    public String getCategoryName() {return categoryName;}
    public void setCategoryName(String categoryName) {this.categoryName = categoryName;}

    public String getCategoryDescription() {return categoryDescription;}
    public void setCategoryDescription(String categoryDescription) {this.categoryDescription = categoryDescription;}

    public boolean isIslamicCategory() {

        return categoryName.equalsIgnoreCase("Quran") 
                || categoryName.equalsIgnoreCase("Hadith") 
                || categoryName.equalsIgnoreCase("Tafsir")
                || categoryName.equalsIgnoreCase("Motivational Books") 
                || categoryName.equalsIgnoreCase("Children's Islamic Learning");
    }

    @Override
    public String toString() {

        return categoryName;
    }
}
