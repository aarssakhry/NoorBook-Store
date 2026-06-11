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
import java.util.Scanner;

// Primary Application Launcher & Storefront UI Engine
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
    
    // Internal Data Lists 
    private final ObservableList<Product> availableProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> cartItems = FXCollections.observableArrayList();

    private Customer loggedInCustomer;
    private Product selectedProductFromGrid = null;

    public static void main(String[] args) {
        launch(args); 
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NoorBook Store - Secure Gateway");

        // Bootstrap data models 
        initializeMockDatabase();

        // Show login page first for user that already register else click register for those new member
        showLoginAndRegisterStage(primaryStage);
    }

    // =========================================================================
    // LATEST GATEWAY: LOGIN & REGISTRATION 
    // =========================================================================
    private void showLoginAndRegisterStage(Stage stage) {
        StackPane rootStack = new StackPane();
        rootStack.setPadding(new Insets(30));
        rootStack.setStyle("-fx-background-color: #f4f6f7;");

        // ----------------- PANEL 1: LOGIN CONTAINER -----------------
        VBox loginBox = new VBox(12);
        loginBox.setMaxWidth(320);
        loginBox.setAlignment(Pos.CENTER_LEFT);
        loginBox.setPadding(new Insets(20));
        loginBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        Label loginTitle = new Label("Login to NoorBook Store");
        loginTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1b5e20;");
        
        TextField loginEmailField = new TextField();
        loginEmailField.setPromptText("Enter your email address");
        
        PasswordField loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("Enter your password");
        
        Button loginSubmitBtn = new Button("Sign In");
        loginSubmitBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        loginSubmitBtn.setMaxWidth(Double.MAX_VALUE);

        HBox registerLinkRow = new HBox(5);
        registerLinkRow.setAlignment(Pos.CENTER);
        Label newBuyerLabel = new Label("New buyer?");
        Hyperlink registerLink = new Hyperlink("Register an account here");
        registerLink.setStyle("-fx-text-fill: #1565c0; -fx-underline: true;");
        registerLinkRow.getChildren().addAll(newBuyerLabel, registerLink);

        loginBox.getChildren().addAll(loginTitle, new Label("Email:"), loginEmailField, 
                                      new Label("Password:"), loginPasswordField, loginSubmitBtn, registerLinkRow);

        // ----------------- REGISTER DETAILS CONTAINER -----------------
        VBox registerBox = new VBox(12);
        registerBox.setMaxWidth(320);
        registerBox.setAlignment(Pos.CENTER_LEFT);
        registerBox.setPadding(new Insets(20));
        registerBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8;");
        registerBox.setVisible(false); 

        Label registerTitle = new Label("Register Information Details");
        registerTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1565c0;");
        
        TextField regNameField = new TextField();
        regNameField.setPromptText("Enter full name");
        
        TextField regEmailField = new TextField();
        regEmailField.setPromptText("Enter email address");
        
        PasswordField regPasswordField = new PasswordField();
        regPasswordField.setPromptText("Create secure password");
        
        //TextField regUserIDField = new TextField();
        
        //TextField regMembershipTierField = new TextField();

        Button registerSubmitBtn = new Button("Register & Join Membership");
        registerSubmitBtn.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold;");
        registerSubmitBtn.setMaxWidth(Double.MAX_VALUE);

        Hyperlink backToLoginLink = new Hyperlink("← Back to Login");
        backToLoginLink.setStyle("-fx-text-fill: #555555;");
        
        registerBox.getChildren().addAll(registerTitle, new Label("Full Name:"), regNameField, 
                                         new Label("Email Address:"), regEmailField, 
                                         new Label("Password:"), regPasswordField, registerSubmitBtn, backToLoginLink);

        rootStack.getChildren().addAll(loginBox, registerBox);

        // --- INTERACTION SWITCHING LOGIC ---
        // registration details
        registerLink.setOnAction(e -> {
            loginBox.setVisible(false);
            registerBox.setVisible(true);
        });

        //back to login
        backToLoginLink.setOnAction(e -> {
            registerBox.setVisible(false);
            loginBox.setVisible(true);
        });

        //login verification
        loginSubmitBtn.setOnAction(e -> {
            String email = loginEmailField.getText().trim();
            String password = loginPasswordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                Alert warnAlert = new Alert(Alert.AlertType.WARNING, "Sila masukkan email dan password.");
                warnAlert.showAndWait();
                return;
            }

            boolean isAuthenticated = false;
            File userFile = new File("customer_database.txt");
            
            if (userFile.exists()) {
                try (Scanner fileScanner = new Scanner(userFile)) {
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();
                        if (line.trim().isEmpty()) continue;
                        
                        String[] data = line.split("\\|");
                        
                        // To avoid ArrayIndexOutOfBoundsException
                        if (data.length >= 5) {
                            // data[2] = Email, data[3] = Password
                            if (data[2].equalsIgnoreCase(email) && data[3].equals(password)) {
                                loggedInCustomer = new Customer(data[0], data[1], data[2], data[3], data[4]);
                                isAuthenticated = true;
                                break; 
                            }
                        }
                    }
                } catch (FileNotFoundException ex) {
                    System.out.println("Customer database file not found.");
                }
            } else {
                System.out.println("Fail customer_database.txt does not exist.");
            }
            
            if (isAuthenticated) {
                proceedToMainStorefront(stage); 
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Invalid email or password credential match.");
                errorAlert.showAndWait();
            }
        });

        //New member registration
        registerSubmitBtn.setOnAction(e -> {
            String name = regNameField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = regPasswordField.getText();
            //String userID = regUserIDField.getText().trim();
            //String membershipTier = regMembershipTierField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Alert warnAlert = new Alert(Alert.AlertType.WARNING, "Please complete all registration entry fields.");
                warnAlert.showAndWait();
                return;
            }

            loggedInCustomer = new Customer("U", name, email, password, "Silver");
            
            saveCustomerToFile(loggedInCustomer);
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Registration Successful");
            successAlert.setHeaderText("Welcome to NoorBook Store Membership!");
            successAlert.setContentText("Account created successfully for " + name + ".\nYour membership tier: Silver Member.");
            successAlert.showAndWait();

            regNameField.clear(); regEmailField.clear(); regPasswordField.clear();
            loginEmailField.setText(email); 
            
            registerBox.setVisible(false);
            loginBox.setVisible(true);
        });

        stage.setScene(new Scene(rootStack, 420, 420));
        stage.show();
    }

    // =========================================================================
    // STOREFRONT LAYOUT - AMIRAH ARISSA (2517166)
    // =========================================================================
    private void proceedToMainStorefront(Stage primaryStage) {
        VBox rootContainer = new VBox(15);
        rootContainer.setPadding(new Insets(15));
        rootContainer.setStyle("-fx-background-color: #f7f9fa;");

        Label headerTitle = new Label("NoorBook Store - Islamic Knowledge Hub");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1b5e20;");
        
        Label customerGreeting = new Label("Logged in as: " + loggedInCustomer.getName() + " (" + loggedInCustomer.getMembershipTier() + " Member)");
        customerGreeting.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
        rootContainer.getChildren().addAll(headerTitle, customerGreeting);

    // =========================================================================
    // CONTROL PANEL - SYAHIRAH (2516300)
    // =========================================================================
   
        HBox controlPanelRow = new HBox(12);
        controlPanelRow.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField(); 
        searchField.setPromptText("Search book or author...");
        searchField.setPrefWidth(220);

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

        controlPanelRow.getChildren().addAll(new Label("Filter Title:"), searchField, new Label("Category:"), categoryFilter);
        rootContainer.getChildren().add(controlPanelRow);

        // --- Body Split Layout ---
        HBox bodySplitViewLayout = new HBox(20);
        HBox.setHgrow(bodySplitViewLayout, Priority.ALWAYS);

        // =========================================================================
        // GRIDPANE MATRIX SHELF - AMIRAH ARISSA (2517166)
        // =========================================================================
        VBox galleryWrapperFrame = new VBox(6);
        Label shelfLabel = new Label("Available Books on Shelves:");
        shelfLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        bookGalleryGrid = new GridPane(); 
        bookGalleryGrid.setHgap(10);
        bookGalleryGrid.setVgap(10);
        bookGalleryGrid.setPadding(new Insets(10));
        bookGalleryGrid.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 6;");

        refreshDynamicStorefrontCatalog("", categoryFilter.getValue());
        galleryWrapperFrame.getChildren().addAll(shelfLabel, bookGalleryGrid);

        // =========================================================================
        // SHOPPING BASKET - SYAHIRAH (2516300)
        // =========================================================================
        VBox basketControlWrapper = new VBox(6);
        basketControlWrapper.setPrefWidth(240);
        Label cartLabel = new Label("Shopping Basket:");
        cartLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        cartListView = new ListView<>(cartItems);
        cartListView.setPrefHeight(220);

        HBox actionAdjustmentButtonsRow = new HBox(8);
        addToCartButton = new Button("Add Item"); 
        addToCartButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");

        removeFromCartButton = new Button("Remove"); 
        removeFromCartButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        actionAdjustmentButtonsRow.getChildren().addAll(addToCartButton, removeFromCartButton);

        checkoutButton = new Button("Confirm Checkout & Log Order"); 
        checkoutButton.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold;");
        checkoutButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(checkoutButton, Priority.ALWAYS);

        basketControlWrapper.getChildren().addAll(cartLabel, cartListView, actionAdjustmentButtonsRow, checkoutButton);
        bodySplitViewLayout.getChildren().addAll(galleryWrapperFrame, basketControlWrapper);
        rootContainer.getChildren().add(bodySplitViewLayout);

        // --- EVENT HANDLERS ---
        searchField.textProperty().addListener((observable, oldVal, newVal) -> {
            refreshDynamicStorefrontCatalog(newVal, categoryFilter.getValue());
        });

        categoryFilter.setOnAction(e -> {
            refreshDynamicStorefrontCatalog(searchField.getText(), categoryFilter.getValue());
        });

        addToCartButton.setOnAction(e -> {
            if (selectedProductFromGrid != null) {
                int quantityInCart = 0;
                for (Product item : cartItems) {
                    if (item.getProductID().equals(selectedProductFromGrid.getProductID())) {
                        quantityInCart++;
                    }
                }
                try {
                    if (quantityInCart >= selectedProductFromGrid.getStock()) {
                        throw new BookOutOfStockException("Only " + selectedProductFromGrid.getStock() + " unit(s) available.");
                    }
                    cartItems.add(selectedProductFromGrid);
                } catch (BookOutOfStockException ex) {
                    Alert outOfStockAlert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    outOfStockAlert.showAndWait();
                }
            } else {
                Alert emptySelectAlert = new Alert(Alert.AlertType.WARNING, "Please pick an item from the shelf first.");
                emptySelectAlert.showAndWait();
            }
        });

        removeFromCartButton.setOnAction(e -> {
            Product highlyTargetedItem = cartListView.getSelectionModel().getSelectedItem();
            if (highlyTargetedItem != null) {
                cartItems.remove(highlyTargetedItem);
            }
        });
        
        checkoutButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Your active basket remains empty.").showAndWait();
            } else {
                StringBuilder invoiceBreakdownBuffer = new StringBuilder("Items Checked Out:\n");
                double structuralAggregateCost = 0.0;

                for (Product item : cartItems) {
                    invoiceBreakdownBuffer.append("- ").append(item.getTitle()).append(" (RM").append(item.getPrice()).append(")\n");
                    structuralAggregateCost += item.getPrice();
                    item.setStock(item.getStock() - 1); 
                }

                saveOrderToHistoryFile(loggedInCustomer.getName(), loggedInCustomer.getEmail(), invoiceBreakdownBuffer.toString(), structuralAggregateCost);
                loggedInCustomer.addOrder("Total Cost: RM" + structuralAggregateCost + " | Units: " + cartItems.size());

                Alert orderSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                orderSuccessAlert.setTitle("Transaction Approved");
                orderSuccessAlert.setContentText(invoiceBreakdownBuffer.toString() + "\nTotal Price: RM" + String.format("%.2f", structuralAggregateCost));
                orderSuccessAlert.showAndWait();

                cartItems.clear();
                saveInventoryToFile(); 
                refreshDynamicStorefrontCatalog(searchField.getText(), categoryFilter.getValue());
            }
        });

        primaryStage.setScene(new Scene(rootContainer, 640, 480));
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
                                                "\nRM" + targetProduct.getPrice() + " (" + (targetProduct.isAvailable() ? "In Stock" : "OUT") + ")";

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

    // =========================================================================
    // FILE HANDLING -- AMIRAH ARISSA (2517166)
    // =========================================================================
    private void saveInventoryToFile() {
        try (PrintWriter writer = new PrintWriter("inventory_database.txt")) { 
            for (Product targetProduct : availableProducts) {
                writer.println(targetProduct.getProductID() + "|" + targetProduct.getTitle() + "|" + targetProduct.getAuthor() + "|" + targetProduct.getPrice() + "|" + targetProduct.getStock() + "|" + targetProduct.getCategory());
            }
    // =========================================================================
    // EXCEPTION HANDLING -- SOFIYA (2516342)
    // =========================================================================
        } catch (IOException e) {
            System.out.println("Error saving inventory file.");
        }
    }

    // =========================================================================
    // FILE HANDLING -- AMIRAH ARISSA (2517166)
    // =========================================================================
    private void loadInventory() {
        availableProducts.clear();
        loggedInCustomer = new Customer("U2026", "Ahmad Fauzi", "fauzi@iium.edu.my", "noorPass99", "Gold");
        try (Scanner fileScanner = new Scanner(new File("inventory_database.txt"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\\|");
                availableProducts.add(new Product(data[0], data[1], data[2], Double.parseDouble(data[3]), Integer.parseInt(data[4]), data[5]));
            }
        // =========================================================================
        // EXCEPTION HANDLING -- SOFIYA (2516342)
        // =========================================================================
        } catch (FileNotFoundException e) {
            System.out.println("inventory_database.txt not found.");
        }
    }

    // =========================================================================
    // FILE HANDLING -- AMIRAH ARISSA (2517166)
    // =========================================================================
    private void saveOrderToHistoryFile(String buyerName, String buyerEmail, String itemsSummary, double totalCost) {
        String fileName = "order_history_database.txt";
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = currentDateTime.format(formatter);

        try (PrintWriter writer = new PrintWriter(new java.io.FileWriter(fileName, true))) {
            writer.println(formattedTimestamp + "|" + buyerName + " (" + buyerEmail + ")|" + itemsSummary.trim() + "|" + "RM " + String.format("%.2f", totalCost));
        // =========================================================================
        // EXCEPTION HANDLING -- SOFIYA (2516342)
        // =========================================================================
        } catch (IOException e) {
            System.out.println("Error appending transaction to file.");
        }
    }
    
    private void initializeMockDatabase() {
        File dbFile = new File("inventory_database.txt");
        if (dbFile.exists()) {
            loadInventory();
        } else {
            availableProducts.add(new Product("B01", "Al-Mu'allim Quran", "Sheikh Mahmoud", 45.00, 12, "Quran"));
            availableProducts.add(new Product("B02", "Sahih al-Bukhari Vol 1", "Imam Al-Bukhari", 70.00, 4, "Hadith"));
            availableProducts.add(new Product("B03", "Tafsir Ibn Kathir Vol 1", "Imam Ibn Kathir", 60.00, 0, "Tafsir"));
            availableProducts.add(new Product("B04", "Riyadhus Saliheen", "Imam Al-Nawawi", 35.00, 8, "Hadith"));
            availableProducts.add(new Product("B05", "Atomic Habits", "James Clear", 42.00, 10, "Motivational Books"));
            availableProducts.add(new Product("B06", "The Secret", "Rhonda Byrne", 48.00, 7, "Motivational Books"));
            availableProducts.add(new Product("B07", "Athkar Dua Book", "Jannat Al Quran", 30.00, 12, "Children's Islamic Learning"));
            availableProducts.add(new Product("B08", "Islam For Younger Children", "Ghulam Sarwar", 28.00, 15, "Children's Islamic Learning"));
            
            saveInventoryToFile(); 
           
        }
        
        File userFile = new File("customer_database.txt");
        if (!userFile.exists()) {
            Customer mockUser = new Customer("U2026", "Ahmad Fauzi", "fauzi@iium.edu.my", "noorPass99", "Gold");
            saveCustomerToFile(mockUser);
    }
    
    
}
    private void saveCustomerToFile(Customer customer) {
    String fileName = "customer_database.txt";
    try (PrintWriter writer = new PrintWriter(new java.io.FileWriter(fileName, true))) {
        writer.println(customer.getUserID() + "|" + customer.getName() + "|" + customer.getEmail() + "|" + customer.getPassword() + "|" + customer.getMembershipTier());
    } catch (IOException e) {
        System.out.println("Error saving customer data to file.");
    }
    }
}
