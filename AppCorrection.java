package noorbookstore;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App extends Application {

    // ==========================================
    // NAME: AMIRAH ARISSA (2517166) 
    // ==========================================
    // UI Layout Nodes
    private GridPane bookGalleryGrid;
    private ComboBox<BookCategories> categoryFilter;
    private TextField searchField;
    private ListView<Product> cartListView;
    
    private Button addToCartButton;
    private Button removeFromCartButton;
    private Button checkoutButton;
    
    private final ObservableList<Product> availableProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> cartItems = FXCollections.observableArrayList();

    private Customer loggedInCustomer;
    private Admin loggedInAdmin;
    private Product selectedProductFromGrid = null;
    
    private boolean isAdminModeChoice = false;

    public static void main(String[] args) {
        launch(args); 
    }

    @Override
    public void start(Stage primaryStage) {
        showRoleSelectionInterface(primaryStage);
    }

    private void showRoleSelectionInterface(Stage stage) {
        VBox rootSelectionBox = new VBox(20);
        rootSelectionBox.setAlignment(Pos.CENTER);
        rootSelectionBox.setPadding(new Insets(40));
        rootSelectionBox.setStyle("-fx-background-color: #f0f4f1;");

        Label welcomeTitle = new Label("Welcome to NoorBook Store");
        welcomeTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1b5e20;");
        
        Label selectionSubPrompt = new Label("Please select your access gateway channel:");
        selectionSubPrompt.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b5563;");

        Button enterAsBuyerBtn = new Button("Enter as Customer");
        enterAsBuyerBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 260px; -fx-pref-height: 45px;");
        
        Button enterAsAdminBtn = new Button("Enter as System Admin");
        enterAsAdminBtn.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 260px; -fx-pref-height: 45px;");

        rootSelectionBox.getChildren().addAll(welcomeTitle, selectionSubPrompt, enterAsBuyerBtn, enterAsAdminBtn);

        enterAsBuyerBtn.setOnAction(e -> {
            isAdminModeChoice = false;
            showLoginAndRegisterStage(stage);
        });

        enterAsAdminBtn.setOnAction(e -> {
            isAdminModeChoice = true;
            showLoginAndRegisterStage(stage);
        });

        stage.setScene(new Scene(rootSelectionBox, 450, 350));
        stage.setTitle("NoorBook Store - Gateway Portal Selection");
        stage.show();
    }

    // ==========================================
    // NAME: AMIRAH ARISSA (2517166) 
    // ==========================================
    private void showLoginAndRegisterStage(Stage stage) {
        StackPane rootStack = new StackPane();
        rootStack.setPadding(new Insets(25));
        rootStack.setStyle("-fx-background-color: #f4f6f7;");

        // SIGN IN INTERFACE
        VBox loginBox = new VBox(12);
        loginBox.setMaxWidth(340);
        loginBox.setAlignment(Pos.CENTER_LEFT);
        loginBox.setPadding(new Insets(20));
        loginBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8;");

        // ==========================================
        // NAME: NUR ATIQAH (2518126) 
        // ==========================================
        Label loginTitle = new Label(isAdminModeChoice ? "Admin Authentication Panel" : "Customer Log-In Hub");
        loginTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + (isAdminModeChoice ? "#0d47a1;" : "#1b5e20;"));
        
        TextField loginEmailField = new TextField();
        loginEmailField.setPromptText(isAdminModeChoice ? "username@admin.com" : "Enter email");
        
        PasswordField loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("Enter password");
        
        Label adminRoleLabel = new Label("Select Admin Role:");
        ComboBox<String> adminRoleDropdown = new ComboBox<>();
        adminRoleDropdown.getItems().addAll("Super Admin", "Product Manager");
        adminRoleDropdown.setValue("Super Admin");
        adminRoleDropdown.setMaxWidth(Double.MAX_VALUE);
        
        if (!isAdminModeChoice) {
            adminRoleLabel.setVisible(false);
            adminRoleDropdown.setVisible(false);
        }
        
        Button loginSubmitBtn = new Button(isAdminModeChoice ? "Verify Admin Credentials" : "Sign In");
        loginSubmitBtn.setStyle("-fx-background-color: " + (isAdminModeChoice ? "#0d47a1;" : "#2e7d32;") + " -fx-text-fill: white; -fx-font-weight: bold;");
        loginSubmitBtn.setMaxWidth(Double.MAX_VALUE);

        HBox registerLinkRow = new HBox(5);
        registerLinkRow.setAlignment(Pos.CENTER);
        Label newBuyerLabel = new Label("Need an account?");
        Hyperlink registerLink = new Hyperlink("Create one here");
        registerLink.setStyle("-fx-text-fill: #1565c0; -fx-underline: true;");
        registerLinkRow.getChildren().addAll(newBuyerLabel, registerLink);

        if (isAdminModeChoice) {
            registerLinkRow.setVisible(false);
        }

        Button backToPortalBtn = new Button("← Back to Role Selection");
        backToPortalBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6b7280; -fx-underline: true;");

        loginBox.getChildren().addAll(loginTitle, new Label("Email Address Account:"), loginEmailField, 
                                      new Label("Password Access Code:"), loginPasswordField);
        
        if (isAdminModeChoice) {
            loginBox.getChildren().addAll(adminRoleLabel, adminRoleDropdown);
        }
        
        loginBox.getChildren().addAll(loginSubmitBtn, registerLinkRow, backToPortalBtn);

        // ==========================================
        // NAME: AMIRAH ARISSA (2517166) 
        // ==========================================
        // REGISTRATION LAYOUT
        VBox registerBox = new VBox(11);
        registerBox.setMaxWidth(340);
        registerBox.setAlignment(Pos.CENTER_LEFT);
        registerBox.setPadding(new Insets(20));
        registerBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8;");
        registerBox.setVisible(false); 

        Label registerTitle = new Label("Join NoorBook Membership");
        registerTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1565c0;");
        
        TextField regNameField = new TextField();
        regNameField.setPromptText("Enter real full name");
        
        TextField regEmailField = new TextField();
        regEmailField.setPromptText("example@gmail.com");
        
        PasswordField regPasswordField = new PasswordField();
        regPasswordField.setPromptText("Create secure password code");
        
        ComboBox<String> membershipTierDropdown = new ComboBox<>();
        membershipTierDropdown.getItems().addAll("None", "Bronze", "Silver", "Gold");
        membershipTierDropdown.setValue("None"); 
        membershipTierDropdown.setMaxWidth(Double.MAX_VALUE);

        // ==========================================
        // NAME: AMIRAH ARISSA (2517166) 
        // ==========================================
        // REGISTER PAGE FOR NEW BUYER
        Button registerSubmitBtn = new Button("Register Account");
        registerSubmitBtn.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold;");
        registerSubmitBtn.setMaxWidth(Double.MAX_VALUE);

        Hyperlink backToLoginLink = new Hyperlink("← Cancel and Login");
        backToLoginLink.setStyle("-fx-text-fill: #555555;");
        
        registerBox.getChildren().addAll(registerTitle, 
                                         new Label("Full Name:"), regNameField, 
                                         new Label("Email Address:"), regEmailField, 
                                         new Label("Password Code:"), regPasswordField, 
                                         new Label("Select Membership Tier Plan:"), membershipTierDropdown,
                                         registerSubmitBtn, backToLoginLink);

        rootStack.getChildren().addAll(loginBox, registerBox);

        registerLink.setOnAction(e -> { loginBox.setVisible(false); registerBox.setVisible(true); });
        backToLoginLink.setOnAction(e -> { registerBox.setVisible(false); loginBox.setVisible(true); });
        backToPortalBtn.setOnAction(e -> showRoleSelectionInterface(stage));

        loginSubmitBtn.setOnAction(e -> {
            String email = loginEmailField.getText().trim();
            String password = loginPasswordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please completely fill in email and password fields.").showAndWait();
                return;
            }

           // ==========================================
           // NAME: NUR ATIQAH (2518126) - use ai
           // ==========================================
            if (isAdminModeChoice) {
                // RESTRICTION CHECK: Email validation constraint pattern checker rule
                if (!email.toLowerCase().endsWith("@admin.com")) {
                    new Alert(Alert.AlertType.ERROR, "Access Denied! Admin emails must end with '@admin.com'.").showAndWait();
                    return;
                }

               // PASSWORD: Accept any password text entered by user
                String selectedRole = adminRoleDropdown.getValue();
                String generatedUsername = email.split("@")[0]; // Dynamically grabs the prefix name segment
                
                loggedInAdmin = new Admin("A-GLOBAL", generatedUsername, email, password, selectedRole);
                
                VBox adminRootContainer = new VBox(15);
                stage.setScene(new Scene(adminRootContainer, 950, 620));
                stage.setTitle("NoorBook Store - Controlled System Admin Workspace");
                showAdminDashboard(stage, adminRootContainer);
                 }
            else {
                if (!email.toLowerCase().contains("@gmail.com")) {
                    new Alert(Alert.AlertType.ERROR, "Invalid email pattern! Must consist of '@gmail.com'.").showAndWait();
                    return;
                }

                // ==========================================
                // NAME: AMIRAH ARISSA (2517166) 
                // ==========================================
                boolean customerFound = false;
                File userFile = new File("customer_database.txt");
                if (userFile.exists()) {
                    try (Scanner fileScanner = new Scanner(userFile)) {
                        while (fileScanner.hasNextLine()) {
                            String line = fileScanner.nextLine();
                            if (line.trim().isEmpty()) continue;
                            String[] data = line.split("\\|");
                            if (data.length >= 5) {
                                if (data[2].equalsIgnoreCase(email) && data[3].equals(password)) {
                                    loggedInCustomer = new Customer(data[0], data[1], data[2], data[3], data[4]);
                                    customerFound = true;
                                    break; 
                                }
                            }
                        }
                    } catch (FileNotFoundException ex) {
                        System.out.println("Customer file loader error.");
                    }
                }
                
                if (customerFound) {
                    proceedToMainStorefront(stage);
                } else {
                    new Alert(Alert.AlertType.ERROR, "No matching account profile found.").showAndWait();
                }
            }
        });

        // ==========================================
        // NAME: AMIRAH ARISSA (2517166) 
        // ==========================================
        registerSubmitBtn.setOnAction(e -> {
            String name = regNameField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = regPasswordField.getText();
            String chosenTier = membershipTierDropdown.getValue();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "All fields must be completed.").showAndWait();
                return;
            }

            if (!email.toLowerCase().contains("@gmail.com")) {
                new Alert(Alert.AlertType.ERROR, "Registration rejected! Email format must consist of '@gmail.com'.").showAndWait();
                return;
            }
            // EXCEPTION HANDLING (AI) - SOFIYA
            // Check if email already exists
            File emailCheckFile = new File("customer_database.txt");
            if (emailCheckFile.exists()) {
                try (Scanner emailScanner = new Scanner(emailCheckFile)) {
                  while (emailScanner.hasNextLine()) {
                    String line = emailScanner.nextLine();
            if (!line.trim().isEmpty()) {
                    String[] data = line.split("\\|");
            if (data.length >= 5 && data[2].equalsIgnoreCase(email)) {
                    new Alert(Alert.AlertType.ERROR,
                            "Registration failed! This email is already registered.")
                            .showAndWait();
                    return;
                }
            }
        }

    } catch (FileNotFoundException ex) {

        System.out.println("Customer database file not found.");
    }
}

            //Loop to count existing users and generate new user ID 
            int nextUserNumber = 001;
            File existingUserFile = new File("customer_database.txt");
            
// EXCEPTION HANDLING (AI)- SOFIYA            
            if(existingUserFile.exists()) {
               try (Scanner counterScanner = new Scanner(existingUserFile)) {
                 while(counterScanner.hasNextLine()) {
                 String line = counterScanner.nextLine();
            if(!line.trim().isEmpty()) {
                nextUserNumber++;
            }
        }

    } catch (FileNotFoundException ex) {
        System.out.println("Customer database file not found.");
    }
}
            String generatedUserID = "U" + nextUserNumber;
            loggedInCustomer = new Customer(generatedUserID, name, email, password, chosenTier);
            saveCustomerToFile(loggedInCustomer);
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Profile Saved");
            successAlert.setContentText("Account registration complete.");
            successAlert.showAndWait();

            regNameField.clear(); regEmailField.clear(); regPasswordField.clear();
            membershipTierDropdown.setValue("None");
            loginEmailField.setText(email); 
            registerBox.setVisible(false);
            loginBox.setVisible(true);
        });

        stage.setScene(new Scene(rootStack, 420, 520));
        stage.show();
    }
    
    private void showAdminDashboard(Stage stage, VBox rootContainer) {
        rootContainer.getChildren().clear();
        rootContainer.setPadding(new Insets(20));
        rootContainer.setStyle("-fx-background-color: #ffffff;");

        System.out.println(" Admin Name: " + loggedInAdmin.getName());
        System.out.println(" Role: " + loggedInAdmin.getRole());
        
        boolean isSuperAdmin = loggedInAdmin.getRole().equalsIgnoreCase("Super Admin");

        if (isSuperAdmin) {
            System.out.println(" Access: Add / Edit / Delete Products");
            System.out.println("       : View All Customer Orders");
        } else {
            System.out.println(" Access: View Product Inventory & History Logs Check");
        }

        VBox identityBadgeBox = new VBox(4);
        identityBadgeBox.setPadding(new Insets(10));
        identityBadgeBox.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-radius: 4;");
        Label adminNameLabel = new Label("👑 Admin User: " + loggedInAdmin.getName());
        adminNameLabel.setStyle("-fx-font-weight: bold;");
        Label adminRoleLabel = new Label("💼 Role: " + loggedInAdmin.getRole());
        adminRoleLabel.setStyle("-fx-text-fill: #1e3a8a; -fx-font-weight: bold;");
        Label adminAccessLabel = new Label(isSuperAdmin ? "Access: Add / Edit / Delete Products | View All Customer Orders" : "Access: View Product Inventory & History Logs Check");
        adminAccessLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #b45309;");
        identityBadgeBox.getChildren().addAll(adminNameLabel, adminRoleLabel, adminAccessLabel);
        rootContainer.getChildren().add(identityBadgeBox);

        HBox splitBodyLayout = new HBox(15);
        VBox.setVgrow(splitBodyLayout, Priority.ALWAYS);

        VBox logViewFrame = new VBox(8);
        HBox.setHgrow(logViewFrame, Priority.ALWAYS);
        logViewFrame.getChildren().add(new Label("📜 System Global Transaction Ledger Logs:"));
        ListView<String> historyListView = new ListView<>();
        ObservableList<String> historyItems = FXCollections.observableArrayList();
        
        try (Scanner fs = new Scanner(new File("order_history_database.txt"))) {
            while (fs.hasNextLine()) {
                String l = fs.nextLine();
                if (l.trim().isEmpty()) continue;
                String[] parts = l.split("\\|");
                if (parts.length >= 4) {
                    historyItems.add("📅 " + parts[0] + "\n👤 Buyer: " + parts[1] + "\n📦 Books: " + parts[2] + "\n💰 Amount: " + parts[3]);
                }
            }
        } catch (FileNotFoundException e) { historyItems.add("No orders logged yet."); }
        historyListView.setItems(historyItems);
        logViewFrame.getChildren().add(historyListView);
        splitBodyLayout.getChildren().add(logViewFrame);

        if (isSuperAdmin) {
            TabPane superAdminTabPane = new TabPane();
            superAdminTabPane.setPrefWidth(480);
            
            Tab customerTab = new Tab("Customer Memberships");
            customerTab.setClosable(false);
            VBox custBox = new VBox(8);
            custBox.setPadding(new Insets(10));
            
            ListView<String> customerListView = new ListView<>();
            ObservableList<String> customerData = FXCollections.observableArrayList();
            List<String> rawCustomerLines = new ArrayList<>();
            
            Runnable refreshCustomersWorker = () -> {
                customerData.clear(); rawCustomerLines.clear();
                File f = new File("customer_database.txt");
                if (f.exists()) {
                    try (Scanner sc = new Scanner(f)) {
                        while (sc.hasNextLine()) {
                            String s = sc.nextLine();
                            if (s.trim().isEmpty()) continue;
                            rawCustomerLines.add(s);
                            String[] p = s.split("\\|");
                            customerData.add("ID: " + p[0] + " | Name: " + p[1] + "\n📧 Email: " + p[2] + " | Tier: " + p[4]);
                        }
                    } catch (Exception ex) { System.out.println("Err mapping users."); }
                }
                customerListView.setItems(customerData);
            };
            refreshCustomersWorker.run();
            
            ComboBox<String> tierBox = new ComboBox<>();
            tierBox.getItems().addAll("None", "Bronze", "Silver", "Gold");
            tierBox.setValue("None");
            tierBox.setMaxWidth(Double.MAX_VALUE);
            
            Button updateTierBtn = new Button("Update Customer's Membership Status");
            updateTierBtn.setStyle("-fx-background-color: #d97706; -fx-text-fill: white; -fx-font-weight: bold;");
            updateTierBtn.setMaxWidth(Double.MAX_VALUE);
            
            updateTierBtn.setOnAction(e -> {
                int idx = customerListView.getSelectionModel().getSelectedIndex();
                if (idx >= 0) {
                    String[] profile = rawCustomerLines.get(idx).split("\\|");
                    profile[4] = tierBox.getValue();
                    rawCustomerLines.set(idx, String.join("|", profile));
                    try (PrintWriter pw = new PrintWriter(new File("customer_database.txt"))) {
                        for (String line : rawCustomerLines) pw.println(line);
                    } catch (Exception ex) { System.out.println("Err saving membership."); }
                    new Alert(Alert.AlertType.INFORMATION, "Membership tier altered!").showAndWait();
                    refreshCustomersWorker.run();
                }
            });
            custBox.getChildren().addAll(new Label("Select User Profile Row:"), customerListView, new Label("Set Level Target:"), tierBox, updateTierBtn);
            customerTab.setContent(custBox);

            Tab inventoryCRUDTab = new Tab("Catalog CRUD Tool");
            inventoryCRUDTab.setClosable(false);
            VBox crudBox = new VBox(6);
            crudBox.setPadding(new Insets(10));
            
            ListView<Product> prodListView = new ListView<>(availableProducts);
            prodListView.setPrefHeight(180);
            
            TextField idInpt = new TextField(); idInpt.setPromptText("Product Code ID (e.g. B09)");
            TextField titleInpt = new TextField(); titleInpt.setPromptText("Book Title Description");
            TextField authInpt = new TextField(); authInpt.setPromptText("Author Writer name");
            TextField priceInpt = new TextField(); priceInpt.setPromptText("Price Value (RM)");
            TextField stockInpt = new TextField(); stockInpt.setPromptText("Stock Units Qty");
            TextField catInpt = new TextField(); catInpt.setPromptText("Category (Quran, Hadith, etc)");

            prodListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selectedProd) -> {
                if (selectedProd != null) {
                    idInpt.setText(selectedProd.getProductID()); idInpt.setEditable(false);
                    titleInpt.setText(selectedProd.getTitle());
                    authInpt.setText(selectedProd.getAuthor());
                    priceInpt.setText(String.valueOf(selectedProd.getPrice()));
                    stockInpt.setText(String.valueOf(selectedProd.getStock()));
                    catInpt.setText(selectedProd.getCategory());
                }
            });
            
            HBox crudActionButtonsRow = new HBox(8);
            Button addBtn = new Button("Add Product"); addBtn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white;");
            Button editBtn = new Button("Edit Product"); editBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white;");
            Button delBtn = new Button("Delete Product"); delBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white;");
            crudActionButtonsRow.getChildren().addAll(addBtn, editBtn, delBtn);
            
            // sofiya
            addBtn.setOnAction(e -> {

    idInpt.setEditable(true);

    // Empty field validation
    if (idInpt.getText().isEmpty() ||
        titleInpt.getText().isEmpty() ||
        priceInpt.getText().isEmpty() ||
        stockInpt.getText().isEmpty() ||
        catInpt.getText().isEmpty()) {

        new Alert(Alert.AlertType.WARNING,
                "All fields including category must be filled.")
                .showAndWait();

        return;
    }

    // Valid category checker
    String categoryInput = catInpt.getText().trim();

    boolean validCategory =
            categoryInput.equalsIgnoreCase("Quran") ||
            categoryInput.equalsIgnoreCase("Hadith") ||
            categoryInput.equalsIgnoreCase("Tafsir") ||
            categoryInput.equalsIgnoreCase("Motivational Books") ||
            categoryInput.equalsIgnoreCase("Children's Islamic Learning");

    if (!validCategory) {

        new Alert(Alert.AlertType.ERROR,
                "Invalid category!\n\n" +
                "Allowed categories only:\n" +
                "- Quran\n" +
                "- Hadith\n" +
                "- Tafsir\n" +
                "- Motivational Books\n" +
                "- Children's Islamic Learning")
                .showAndWait();

        return;
    }

    // Duplicate Book ID checker
    for (Product existingProduct : availableProducts) {

        if (existingProduct.getProductID()
                .equalsIgnoreCase(idInpt.getText().trim())) {

            new Alert(Alert.AlertType.ERROR,
                    "Duplicate Book ID detected!\n" +
                    "Each book must have unique code.")
                    .showAndWait();

            return;
        }
    }

    try {

        Product newBook = new Product(
                idInpt.getText().trim(),
                titleInpt.getText().trim(),
                authInpt.getText().trim(),
                Double.parseDouble(priceInpt.getText()),
                Integer.parseInt(stockInpt.getText()),
                categoryInput
        );

        availableProducts.add(newBook);

        saveInventoryToFile();

        prodListView.refresh();

        // Clear fields
        idInpt.clear();
        titleInpt.clear();
        authInpt.clear();
        priceInpt.clear();
        stockInpt.clear();
        catInpt.clear();

        new Alert(Alert.AlertType.INFORMATION,
                "Product added successfully!")
                .showAndWait();

    } catch (NumberFormatException ex) {

        new Alert(Alert.AlertType.ERROR,
                "Price must be decimal number and stock must be integer.")
                .showAndWait();
    }
});

             editBtn.setOnAction(e -> {
                Product selected = prodListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    //Validate fields are not empty before editing
                    if(titleInpt.getText().isEmpty() || priceInpt.getText().isEmpty() || stockInpt.getText().isEmpty()){
                        new Alert(Alert.AlertType.WARNING, "Title, price, and stock fields cannot be empty.").showAndWait();
                        return;
                    }
                    try{
                        //Update all editable fields
                       selected.setTitle(titleInpt.getText()); 
                       selected.setAuthor(authInpt.getText());
                       selected.setPrice(Double.parseDouble(priceInpt.getText())); 
                       selected.setStock(Integer.parseInt(stockInpt.getText()));
                       selected.setCategory(catInpt.getText());
                       //Save updated inventory to file and refresh list
                       saveInventoryToFile();
                       prodListView.refresh();
                       //Clear fields and re-enable ID field after edit
                       idInpt.clear();
                       titleInpt.clear();
                       authInpt.clear();
                       priceInpt.clear();
                       stockInpt.clear();
                       catInpt.clear();
                       idInpt.setEditable(true);
                       new Alert(Alert.AlertType.INFORMATION, "Product \"" + selected.getTitle() + "\" updates successfully!").showAndWait();
                    }
                    catch(NumberFormatException ex){
                        new Alert(Alert.AlertType.WARNING, "Price must be a number (e.g. 45.90) and stock must be a whole number.").showAndWait();
                    }
                }
                else{
                    new Alert(Alert.AlertType.WARNING, "Please select a product from the list to edit.").showAndWait();
                }
            });
            
            delBtn.setOnAction(e->{
                Product selected = prodListView.getSelectionModel().getSelectedItem();
                if (selected!=null) {
                availableProducts.remove(selected);
                saveInventoryToFile();
                new Alert(Alert.AlertType.INFORMATION, "Item removed from inventory records.").showAndWait();
                }
            });

            crudBox.getChildren().addAll(prodListView, idInpt, titleInpt, authInpt, priceInpt, stockInpt, catInpt, crudActionButtonsRow);
            inventoryCRUDTab.setContent(crudBox);
            
            superAdminTabPane.getTabs().addAll(customerTab, inventoryCRUDTab);
            splitBodyLayout.getChildren().add(superAdminTabPane);
            
        } else {
            VBox managerFrame = new VBox(8);
            managerFrame.setPrefWidth(440);
            managerFrame.getChildren().add(new Label("📦 Product Inventory Tracker Status Map (Read-Only View):"));
            
            ListView<String> readOnlyInvView = new ListView<>();
            ObservableList<String> invItems = FXCollections.observableArrayList();
            for (Product p : availableProducts) {
                invItems.add("Code: " + p.getProductID() + " | Title: " + p.getTitle() + "\n🏷️ Price: RM" + p.getPrice() + " | Units Remainder: " + p.getStock() + " [" + p.getCategory() + "]");
            }
            readOnlyInvView.setItems(invItems);
            managerFrame.getChildren().add(readOnlyInvView);
            splitBodyLayout.getChildren().add(managerFrame);
        }

        rootContainer.getChildren().add(splitBodyLayout);

        Button logoutSessionBtn = new Button("Save & Log Out Admin Gateway Context");
        logoutSessionBtn.setStyle("-fx-background-color: #991b1b; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 38px;");
        logoutSessionBtn.setMaxWidth(Double.MAX_VALUE);
        logoutSessionBtn.setOnAction(e -> {
            loggedInAdmin = null;
            showRoleSelectionInterface(stage);
        });
        rootContainer.getChildren().add(logoutSessionBtn);
    }

    // ==========================================
    // NAME: AMIRAH ARISSA (2517166) 
    // ==========================================
    private void proceedToMainStorefront(Stage primaryStage) {
        VBox rootContainer = new VBox(14);
        rootContainer.setPadding(new Insets(15));
        rootContainer.setStyle("-fx-background-color: #f7f9fa;");

        Label headerTitle = new Label("NoorBook Store - Islamic Knowledge Hub");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1b5e20;");
        
        Label customerGreeting = new Label("Active User Session: " + loggedInCustomer.getName() + " (" + loggedInCustomer.getMembershipTier() + " Member)");
        customerGreeting.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563;");
        rootContainer.getChildren().addAll(headerTitle, customerGreeting);

        HBox controlPanelRow = new HBox(12);
        controlPanelRow.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField(); 
        searchField.setPromptText("Search book or author...");
        searchField.setPrefWidth(150);

        // ==========================================
        // NAME: AMIRAH ARISSA (2517166) 
        // ==========================================
        categoryFilter = new ComboBox<>(); 
        categoryFilter.getItems().addAll(
            new BookCategories("CAT0", "All Categories", "Show all items"),
            new BookCategories("CAT1", "Quran", "Holy Quran editions"),
            new BookCategories("CAT2", "Hadith", "Prophetic traditions"),
            new BookCategories("CAT3", "Tafsir", "Exegesis collections"),
            new BookCategories("CAT4", "Motivational Books","Inspirational books" ), 
            new BookCategories("CAT5", "Children's Islamic Learning", "Islamic educational")
        );
        categoryFilter.setValue(categoryFilter.getItems().get(0)); 
        
         // ==========================================
        // NAME: SYAHIRAH (2516300) ! USE AI !
        // ==========================================
        Button joinMembershipBtn = new Button("🌟 Join Membership Plan Now");
        joinMembershipBtn.setStyle("-fx-background-color: #ff9100; -fx-text-fill: black; -fx-font-weight: bold;");
        
        if (!loggedInCustomer.getMembershipTier().equalsIgnoreCase("None")) {
            joinMembershipBtn.setVisible(false);
        }

        joinMembershipBtn.setOnAction(e -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Bronze", "Bronze", "Silver", "Gold");
            dialog.setTitle("Membership Enrollment Portal");
            dialog.setContentText("Choose a premium membership tier:");
            
            dialog.showAndWait().ifPresent(chosenTier -> {
                loggedInCustomer.setMembershipTier(chosenTier);
                updateCustomerTierInDatabaseFile(loggedInCustomer.getEmail(), chosenTier);
                
                new Alert(Alert.AlertType.INFORMATION, "Congratulations! You are now a premium " + chosenTier + " Member!").showAndWait();
                customerGreeting.setText("Active User Session: " + loggedInCustomer.getName() + " (" + loggedInCustomer.getMembershipTier() + " Member)");
                joinMembershipBtn.setVisible(false); 
            });
        });

        controlPanelRow.getChildren().addAll(new Label("Filter:"), searchField, categoryFilter, joinMembershipBtn);
        rootContainer.getChildren().add(controlPanelRow);

        HBox bodySplitViewLayout = new HBox(20);
        VBox.setVgrow(bodySplitViewLayout, Priority.ALWAYS);

        VBox galleryWrapperFrame = new VBox(6);
        HBox.setHgrow(galleryWrapperFrame, Priority.ALWAYS);
        Label shelfLabel = new Label("Available Books on Shelves:");
        bookGalleryGrid = new GridPane(); 
        bookGalleryGrid.setHgap(10); bookGalleryGrid.setVgap(10);
        bookGalleryGrid.setPadding(new Insets(10));
        bookGalleryGrid.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0;");
        
        refreshDynamicStorefrontCatalog("", categoryFilter.getValue());
        galleryWrapperFrame.getChildren().addAll(shelfLabel, bookGalleryGrid);
        bodySplitViewLayout.getChildren().add(galleryWrapperFrame);

        VBox rightSideControlPanelBox = new VBox(10);
        rightSideControlPanelBox.setPrefWidth(280);
        
        Label cartLabel = new Label("Shopping Basket:");
        cartListView = new ListView<>(cartItems);
        cartListView.setPrefHeight(140);

        HBox actionAdjustmentButtonsRow = new HBox(8);
        addToCartButton = new Button("Add Item"); 
        addToCartButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        removeFromCartButton = new Button("Remove"); 
        removeFromCartButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        actionAdjustmentButtonsRow.getChildren().addAll(addToCartButton, removeFromCartButton);

        checkoutButton = new Button("Confirm Checkout & Log Order"); 
        checkoutButton.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold;");
        checkoutButton.setMaxWidth(Double.MAX_VALUE);

        Label personalHistoryLabel = new Label("📜 Your Past Order History Logs:");
        personalHistoryLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3a8a;");
        ListView<String> customerPersonalHistoryListView = new ListView<>();
        customerPersonalHistoryListView.setPrefHeight(150);
        ObservableList<String> personalHistoryItemsList = FXCollections.observableArrayList();
        
        Runnable parsePersonalHistoryLogsWorker = () -> {
            personalHistoryItemsList.clear();
            File histFile = new File("order_history_database.txt");
            if (histFile.exists()) {
                try (Scanner historyFileScanner = new Scanner(histFile)) {
                    while (historyFileScanner.hasNextLine()) {
                        String currentLine = historyFileScanner.nextLine();
                        if (currentLine.trim().isEmpty()) continue;
                        String[] partitionTokens = currentLine.split("\\|");
                        if (partitionTokens.length >= 4) {
                            String emailContextDescriptor = partitionTokens[1];
                            if (emailContextDescriptor.contains(loggedInCustomer.getEmail())) {
                                personalHistoryItemsList.add("📅 " + partitionTokens[0] + "\n📦 Content: " + partitionTokens[2] + "\n💰 Cost: " + partitionTokens[3]);
                            }
                        }
                    }
                } catch (FileNotFoundException ex) { System.out.println("History mapping context exception."); }
            }
            if (personalHistoryItemsList.isEmpty()) {
                personalHistoryItemsList.add("No checkouts logged under your profile yet.");
            }
            customerPersonalHistoryListView.setItems(personalHistoryItemsList);
        };
        parsePersonalHistoryLogsWorker.run();

        rightSideControlPanelBox.getChildren().addAll(cartLabel, cartListView, actionAdjustmentButtonsRow, checkoutButton, personalHistoryLabel, customerPersonalHistoryListView);
        bodySplitViewLayout.getChildren().add(rightSideControlPanelBox);
        rootContainer.getChildren().add(bodySplitViewLayout);

        searchField.textProperty().addListener((obs, oldV, newV) -> refreshDynamicStorefrontCatalog(newV, categoryFilter.getValue()));
        categoryFilter.setOnAction(e -> refreshDynamicStorefrontCatalog(searchField.getText(), categoryFilter.getValue()));

        addToCartButton.setOnAction(e -> {
            if (selectedProductFromGrid != null) {
                int quantityInCart = 0;
                for (Product item : cartItems) {
                    if (item.getProductID().equals(selectedProductFromGrid.getProductID())) quantityInCart++;
                }
                try {
                    if (quantityInCart >= selectedProductFromGrid.getStock()) {
                        throw new BookOutOfStockException("Only " + selectedProductFromGrid.getStock() + " unit(s) available.");
                    }
                    cartItems.add(selectedProductFromGrid);
                } catch (BookOutOfStockException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Please pick an item from the shelf first.").showAndWait();
            }
        });

        removeFromCartButton.setOnAction(e -> {
            Product targeted = cartListView.getSelectionModel().getSelectedItem();
            if (targeted != null) cartItems.remove(targeted);
        });
        
        checkoutButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Your active basket remains empty.").showAndWait();
            } else {
                StringBuilder invoiceBreakdownBuffer = new StringBuilder("Items Checked Out:\n");
                double structuralAggregateCost = 0.0;
                String customerTier = loggedInCustomer.getMembershipTier();

                for (Product item : cartItems) {
                    double originalPrice = item.getPrice();
                    double discountedPrice = Membership.applyMembershipDiscount(customerTier, originalPrice);
                    
                    invoiceBreakdownBuffer.append("- ").append(item.getTitle())
                        .append(" (RM ").append(String.format("%.2f", discountedPrice)).append(")\n");
                    structuralAggregateCost += discountedPrice;
                    item.setStock(item.getStock() - 1); 
                }

                saveOrderToHistoryFile(loggedInCustomer.getName(), loggedInCustomer.getEmail(), invoiceBreakdownBuffer.toString(), structuralAggregateCost);
                loggedInCustomer.addOrder("Total Cost: RM " + String.format("%.2f", structuralAggregateCost) + " | Units: " + cartItems.size());

                Alert orderSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                orderSuccessAlert.setTitle("Transaction Approved");
                orderSuccessAlert.setContentText(invoiceBreakdownBuffer.toString() + "\nTotal Price: RM " + String.format("%.2f", structuralAggregateCost));
                orderSuccessAlert.showAndWait();

                cartItems.clear();
                saveInventoryToFile(); 
                
                parsePersonalHistoryLogsWorker.run();
                refreshDynamicStorefrontCatalog(searchField.getText(), categoryFilter.getValue());
            }
        });

        Button customerLogoutSessionBtn = new Button("Save & Log Out Customer Session");
        customerLogoutSessionBtn.setStyle("-fx-background-color: #991b1b; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-height: 38px;");
        customerLogoutSessionBtn.setMaxWidth(Double.MAX_VALUE);
        customerLogoutSessionBtn.setOnAction(e -> {
            loggedInCustomer = null;
            cartItems.clear();
            showRoleSelectionInterface(primaryStage);
        });
        rootContainer.getChildren().add(customerLogoutSessionBtn);

        primaryStage.setScene(new Scene(rootContainer, 850, 580));
        primaryStage.setTitle("NoorBook Store - Active Storefront Session");
        primaryStage.show();
    }

    private void refreshDynamicStorefrontCatalog(String textKeyword, BookCategories activeCategoryChoice) {
        bookGalleryGrid.getChildren().clear(); 
        int columnPosIndexTracker = 0;
        int rowPosIndexTracker = 0;

        for (Product targetProduct : availableProducts) {
            boolean standardNameMatch = targetProduct.getTitle().toLowerCase().contains(textKeyword.toLowerCase()) ||
                                        targetProduct.getAuthor().toLowerCase().contains(textKeyword.toLowerCase());

            boolean activeCategoryMatch = activeCategoryChoice.getCategoryName().equalsIgnoreCase("All Categories") ||
                                          targetProduct.getCategory().equalsIgnoreCase(activeCategoryChoice.getCategoryName());

            if (standardNameMatch && activeCategoryMatch) {
                String gridTileTextDescriptor = targetProduct.getTitle() + "\nBy: " + targetProduct.getAuthor() + 
                                                "\nRM " + String.format("%.2f", targetProduct.getPrice()) + " (" + (targetProduct.getStock() > 0 ? "Stock: " + targetProduct.getStock() : "OUT") + ")";

                Button itemCardTileButton = new Button(gridTileTextDescriptor);
                itemCardTileButton.setPrefSize(160, 85);
                itemCardTileButton.setStyle("-fx-text-alignment: center; -fx-background-color: #e8f5e9; -fx-border-color: #81c784; -fx-border-radius: 4;");

                if (!targetProduct.isAvailable()) {
                    itemCardTileButton.setStyle("-fx-text-alignment: center; -fx-background-color: #ffebee; -fx-border-color: #ef9a9a; -fx-text-fill: #7d7d7d;");
                }

                itemCardTileButton.setOnAction(evt -> {
                    if (targetProduct.isAvailable()) {
                        selectedProductFromGrid = targetProduct;
                        bookGalleryGrid.getChildren().forEach(node -> {
                            if (node instanceof Button) node.setStyle("-fx-text-alignment: center; -fx-background-color: #e8f5e9; -fx-border-color: #81c784;");
                        });
                        itemCardTileButton.setStyle("-fx-text-alignment: center; -fx-background-color: #a5d6a7; -fx-border-color: #2e7d32; -fx-border-width: 2;");
                    }
                });

                bookGalleryGrid.add(itemCardTileButton, columnPosIndexTracker, rowPosIndexTracker);
                columnPosIndexTracker++;

                if (columnPosIndexTracker > 1) { 
                    columnPosIndexTracker = 0;
                    rowPosIndexTracker++;
                }
            }
        }
    }

    private void updateCustomerTierInDatabaseFile(String email, String dynamicTierPlan) {
        List<String> tempProfiles = new ArrayList<>();
        File file = new File("customer_database.txt");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    String[] tokens = line.split("\\|");
                    if (tokens.length >= 5 && tokens[2].equalsIgnoreCase(email)) {
                        tokens[4] = dynamicTierPlan;
                        line = String.join("|", tokens);
                    }
                    tempProfiles.add(line);
                }
            } catch (Exception ex) { System.out.println("Error processing file records stream."); }
            
            try (PrintWriter writer = new PrintWriter(file)) {
                for (String prof : tempProfiles) writer.println(prof);
            } catch (Exception ex) { System.out.println("Error saving updates."); }
        }
    }

    // ==========================================
    // NAME: AMIRAH ARISSA (2517166) 
    // ==========================================
    private void saveInventoryToFile() {
        try (PrintWriter writer = new PrintWriter("inventory_database.txt")) { 
            for (Product targetProduct : availableProducts) {
                writer.println(targetProduct.getProductID() + "|" + targetProduct.getTitle() + "|" + targetProduct.getAuthor() + "|" + targetProduct.getPrice() + "|" + targetProduct.getStock() + "|" + targetProduct.getCategory());
            }
        } catch (IOException e) { System.out.println("Error saving inventory file."); }
    }

    private void loadInventory() {
        availableProducts.clear();
        try (Scanner fileScanner = new Scanner(new File("inventory_database.txt"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\\|");
                availableProducts.add(new Product(data[0], data[1], data[2], Double.parseDouble(data[3]), Integer.parseInt(data[4]), data[5]));
            }
        } catch (FileNotFoundException e) { System.out.println("inventory_database.txt missing."); }
    }

    // ==========================================
    // NAME: AMIRAH ARISSA (2517166) 
    // ==========================================
    private void saveOrderToHistoryFile(String buyerName, String buyerEmail, String itemsSummary, double totalCost) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = currentDateTime.format(formatter);

        // USE AI-- 1ST LINE
        try (PrintWriter writer = new PrintWriter(new java.io.FileWriter("order_history_database.txt", true))) {
            writer.println(formattedTimestamp + "|" + buyerName + " (" + buyerEmail + ")|" + itemsSummary.trim() + "|" + "RM " + String.format("%.2f", totalCost));
        } catch (IOException e) { System.out.println("Error appending history record."); }
    }

    // ==========================================
    // NAME: AMIRAH ARISSA (2517166) 
    // ==========================================
    private void saveCustomerToFile(Customer customer) {
        // USE AI-- 1ST LINE
        try (PrintWriter writer = new PrintWriter(new java.io.FileWriter("customer_database.txt", true))) {
            writer.println(customer.getUserID() + "|" + customer.getName() + "|" + customer.getEmail() + "|" + customer.getPassword() + "|" + customer.getMembershipTier());
        } catch (IOException e) { System.out.println("Error saving customer text database."); }
    }
}
