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

//Primary Application Launcher & Storefront UI Engine

public class App extends Application {

// ==========================================
// NAME: AMIRAH ARISSA (2517166) 
// ==========================================

    //UI Layout Nodes
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
        primaryStage.setTitle("NoorBook Store - Interactive Storefront");

        // Bootstrap data models 
        initializeMockDatabase();

        // Layout using VBox with 15px padding/spacing 
        VBox rootContainer = new VBox(15);
        rootContainer.setPadding(new Insets(15));
        rootContainer.setStyle("-fx-background-color: #f7f9fa;");

        // Header identity display leveraging encapsulated data fields via getters 
        Label headerTitle = new Label("NoorBook Store - Islamic Knowledge Hub");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1b5e20;");

        
        Label customerGreeting = new Label("Logged in as: " + loggedInCustomer.getName() + " (" + loggedInCustomer.getMembershipTier() + " Member)");
        customerGreeting.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
        rootContainer.getChildren().addAll(headerTitle, customerGreeting);

        // --- FILTER & SEARCH CONTROL BAR (HBox Alignment) ---
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
            new BookCategories("CAT3", "Tafsir", "Exegesis collections")

        );

        //TO FILTER ITEMS
        categoryFilter.setValue(categoryFilter.getItems().get(0)); 

        controlPanelRow.getChildren().addAll(new Label("Filter Title:"), searchField, new Label("Category:"), categoryFilter);
        rootContainer.getChildren().add(controlPanelRow);

        // --- SPLIT INTERFACE VIEW FRAMEWORK ---
        HBox bodySplitViewLayout = new HBox(20);
        HBox.setHgrow(bodySplitViewLayout, Priority.ALWAYS);

        // Left Component Box: Visual Gallery Layout Frame 
        VBox galleryWrapperFrame = new VBox(6);
        Label shelfLabel = new Label("Available Books on Shelves:");
        shelfLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        bookGalleryGrid = new GridPane(); // Responsive grid for rendering product layouts
        bookGalleryGrid.setHgap(10);
        bookGalleryGrid.setVgap(10);
        bookGalleryGrid.setPadding(new Insets(10));
        bookGalleryGrid.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 6;");

        // Initial catalog array display generation pass
        refreshDynamicStorefrontCatalog("", categoryFilter.getValue());
        galleryWrapperFrame.getChildren().addAll(shelfLabel, bookGalleryGrid);

        // Right Component Box: Shopping Basket UI Layout 
        VBox basketControlWrapper = new VBox(6);
        basketControlWrapper.setPrefWidth(240);
        Label cartLabel = new Label("Shopping Basket:");
        cartLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        // Cart items view displaying models through Product's native toString() overrides
        cartListView = new ListView<>(cartItems);
        cartListView.setPrefHeight(220);

        // Management operational controls panel row
        HBox actionAdjustmentButtonsRow = new HBox(8);
        addToCartButton = new Button("Add Item"); // Cart append button
        addToCartButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");

        removeFromCartButton = new Button("Remove"); // Cart item drop button
        removeFromCartButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        
        actionAdjustmentButtonsRow.getChildren().addAll(addToCartButton, removeFromCartButton);

        checkoutButton = new Button("Confirm Checkout & Log Order"); // Invoicing processing node
        checkoutButton.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold;");
        checkoutButton.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(checkoutButton, Priority.ALWAYS);

        basketControlWrapper.getChildren().addAll(cartLabel, cartListView, actionAdjustmentButtonsRow, checkoutButton);
        bodySplitViewLayout.getChildren().addAll(galleryWrapperFrame, basketControlWrapper);
        rootContainer.getChildren().add(bodySplitViewLayout);

        // --- EVENT HANDLERS POWERED BY LAMBDA EXPRESSIONS ---
        
        // Real-time tracking of keys typed into the search box 
        searchField.textProperty().addListener((observable, oldVal, newVal) -> {
            refreshDynamicStorefrontCatalog(newVal, categoryFilter.getValue());
        });

        // Track updates to the chosen category filters dropdown 
        categoryFilter.setOnAction(e -> {
            refreshDynamicStorefrontCatalog(searchField.getText(), categoryFilter.getValue());
        });

        // Appends checked display grids directly to inventory models 
        addToCartButton.setOnAction(e -> {
            if (selectedProductFromGrid != null) {
                if (selectedProductFromGrid.isAvailable()) { // Checks against Product's available boolean state
                    cartItems.add(selectedProductFromGrid);
                    System.out.println("[Member 3 UI Action] Added item to local staging list: " + selectedProductFromGrid.getTitle());
                } else {
                    Alert outOfStockAlert = new Alert(Alert.AlertType.ERROR, "Selected text title is depleted from shelves.");
                    outOfStockAlert.showAndWait();
                }
            } else {
                Alert emptySelectAlert = new Alert(Alert.AlertType.WARNING, "Please pick an item from the layout matrix shelf first.");
                emptySelectAlert.showAndWait();
            }
        });

        // Target indexed item drop operations 
        removeFromCartButton.setOnAction(e -> {
            Product highlyTargetedItem = cartListView.getSelectionModel().getSelectedItem();
            if (highlyTargetedItem != null) {
                cartItems.remove(highlyTargetedItem);
            }
        });
        
        // Processes complete checkouts and returns parameters 
        checkoutButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                Alert noticeAlert = new Alert(Alert.AlertType.INFORMATION, "Your active basket remains empty.");
                noticeAlert.showAndWait();
            } else {
                StringBuilder invoiceBreakdownBuffer = new StringBuilder("Items Checked Out:\n");
                double structuralAggregateCost = 0.0;

                
                for (Product item : cartItems) {
                    invoiceBreakdownBuffer.append("- ").append(item.getTitle()).append(" (RM").append(item.getPrice()).append(")\n");
                    structuralAggregateCost += item.getPrice();
                    item.setStock(item.getStock() - 1); 
                }

                String receiptLogSummaryText = "Total Cost: RM" + structuralAggregateCost + " | Units: " + cartItems.size();

                // CRITICAL STRUCTURAL INTERACTION: Directly fires addOrder() inside Customer.java
                loggedInCustomer.addOrder(receiptLogSummaryText);

                // Inform user with feedback dialog alerts 
                Alert orderSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                orderSuccessAlert.setTitle("Transaction Approved");
                orderSuccessAlert.setHeaderText("NoorBook Order Processed Successfully!");
                orderSuccessAlert.setContentText(invoiceBreakdownBuffer.toString() + "\nLogged under Customer Profile context successfully.");
                orderSuccessAlert.showAndWait();

                // Clean out active cart fields
                cartItems.clear();
                refreshDynamicStorefrontCatalog(searchField.getText(), categoryFilter.getValue());
            }
        });

        // Scene layout render engine activation 
        primaryStage.setScene(new Scene(rootContainer, 640, 480));
        primaryStage.show();
    }

    //Refreshes and structurally renders items inside the responsive JavaFX GridPane matrix 
    private void refreshDynamicStorefrontCatalog(String textKeyword, BookCategories activeCategoryChoice) {
        bookGalleryGrid.getChildren().clear(); 
        int columnPosIndexTracker = 0;
        int rowPosIndexTracker = 0;


        for (Product targetProduct : availableProducts) {
            // Evaluates matches against search expressions and current dropdown values
            boolean standardNameMatch = targetProduct.getTitle().toLowerCase().contains(textKeyword.toLowerCase()) ||
                                        targetProduct.getAuthor().toLowerCase().contains(textKeyword.toLowerCase());

            boolean activeCategoryMatch = activeCategoryChoice.getCategoryName().equalsIgnoreCase("All Categories") ||
                                          targetProduct.getCategory().equalsIgnoreCase(activeCategoryChoice.getCategoryName());

            if (standardNameMatch && activeCategoryMatch) {
                // Compiles display labels using custom data structures
                String gridTileTextDescriptor = targetProduct.getTitle() + "\nBy: " + targetProduct.getAuthor() + 
                                                "\nRM" + targetProduct.getPrice() + " (" + (targetProduct.isAvailable() ? "In Stock" : "OUT") + ")";


                Button itemCardTileButton = new Button(gridTileTextDescriptor);
                itemCardTileButton.setPrefSize(160, 85);
                itemCardTileButton.setStyle("-fx-text-alignment: center; -fx-background-color: #e8f5e9; -fx-border-color: #81c784; -fx-border-radius: 4;");

                // Highlight out-of-stock variations cleanly
                if (!targetProduct.isAvailable()) {
                    itemCardTileButton.setStyle("-fx-text-alignment: center; -fx-background-color: #ffebee; -fx-border-color: #ef9a9a; -fx-text-fill: #7d7d7d;");
                }

                // Lambda function linking individual tile actions to focus parameters
                itemCardTileButton.setOnAction(evt -> {
                    if (targetProduct.isAvailable()) {
                        selectedProductFromGrid = targetProduct;

                        // Clear active accent colors 
                        bookGalleryGrid.getChildren().forEach(node -> {

                            if (node instanceof Button) node.setStyle("-fx-text-alignment: center; -fx-background-color: #e8f5e9; -fx-border-color: #81c784;");
                        });

                        // Set focus background highlighting on chosen node choices
                        itemCardTileButton.setStyle("-fx-text-alignment: center; -fx-background-color: #a5d6a7; -fx-border-color: #2e7d32; -fx-border-width: 2;");
                    }
                });

                // Inject component nodes systematically into structured row blocks
                bookGalleryGrid.add(itemCardTileButton, columnPosIndexTracker, rowPosIndexTracker);
                columnPosIndexTracker++;

                if (columnPosIndexTracker > 1) { 
                    columnPosIndexTracker = 0;
                    rowPosIndexTracker++;
                }
            }
        }
    }

    

//Initializes mock objects 
    private void initializeMockDatabase() {

        // Instantiate our Customer profile class 
        loggedInCustomer = new Customer("U2026", "Ahmad Fauzi", "fauzi@iium.edu.my", "noorPass99", "Gold");

        // Populate standard book item profiles 
        availableProducts.add(new Product("B01", "Al-Mu'allim Quran", "Sheikh Mahmoud", 45.00, 12, "Quran"));
        availableProducts.add(new Product("B02", "Sahih al-Bukhari Vol 1", "Imam Al-Bukhari", 70.00, 4, "Hadith"));
        availableProducts.add(new Product("B03", "Tafsir Ibn Kathir Vol 1", "Imam Ibn Kathir", 60.00, 0, "Tafsir")); // Out of stock sample
        availableProducts.add(new Product("B04", "Riyadhus Saliheen", "Imam Al-Nawawi", 35.00, 8, "Hadith"));
    }
}

// =========================================================================
    // AUTHOR: SYAHIRAH  (MEMBER 3 - CUSTOMER STOREFRONT UI)
    // =========================================================================
    private void showCustomerDashboard(Stage stage, VBox rootContainer) {
        VBox customerBox = new VBox(15);
        customerBox.setPadding(new Insets(25));
        customerBox.setStyle("-fx-background-color: #ffffff;");

        // Matches Amirah's loggedInCustomer variable
        Label welcomeLabel = new Label("Welcome to Noor Book Store, " + loggedInCustomer.getName() + "! Assalamu Alaikum.");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e5e43;");

        HBox searchPanel = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search literature titles...");
        searchField.setPrefWidth(220);
        
        ComboBox<String> filterBox = new ComboBox<>(FXCollections.observableArrayList("All Categories", "Quran", "Hadith", "Tafsir"));
        filterBox.setValue("All Categories");
        searchPanel.getChildren().addAll(searchField, filterBox);

        ListView<Product> catalogDisplay = new ListView<>();
        catalogDisplay.setItems(availableProducts); // Matches Amirah's book list

        // Dynamic search listeners linking to your search method
        searchField.textProperty().addListener((obs, oldVal, newVal) -> performSearch(searchField.getText(), filterBox.getValue(), catalogDisplay));
        filterBox.setOnAction(e -> performSearch(searchField.getText(), filterBox.getValue(), catalogDisplay));

        ListView<Product> cartView = new ListView<>(cartItems); // Matches Amirah's cart items list
        cartView.setPrefHeight(120);

        Button addToCartBtn = new Button("Add Selected Book to Cart");
        Button removeCartBtn = new Button("Remove Item from Cart");
        Button checkoutBtn = new Button("Finalize Order & Print Receipt");
        Button logoutBtn = new Button("Logout");
        checkoutBtn.setStyle("-fx-background-color: #1e5e43; -fx-text-fill: white; -fx-font-weight: bold;");

        // Cart addition logic wrapping your teammate's Exception class
        addToCartBtn.setOnAction(e -> {
            Product selected = catalogDisplay.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    if (!selected.isAvailable()) {
                        throw new BookOutOfStockException("The title '" + selected.getTitle() + "' is completely out of stock!");
                    }
                    cartItems.add(selected);
                    System.out.println("[Storefront UI] Successfully staged: " + selected.getTitle());
                } catch (BookOutOfStockException ex) {
                    Alert outOfStockAlert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    outOfStockAlert.setTitle("Inventory Exception");
                    outOfStockAlert.showAndWait();
                }
            } else {
                Alert noSelectAlert = new Alert(Alert.AlertType.WARNING, "Please select a book from the catalog first.");
                noSelectAlert.showAndWait();
            }
        });

        removeCartBtn.setOnAction(e -> {
            Product selected = cartView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cartItems.remove(selected);
            }
        });

        // Checkout execution node connecting directly to Customer order histories
        checkoutBtn.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                Alert noticeAlert = new Alert(Alert.AlertType.INFORMATION, "Your active basket remains empty.");
                noticeAlert.showAndWait();
            } else {
                StringBuilder invoiceBreakdownBuffer = new StringBuilder("Items Checked Out:\n");
                double totalCost = 0.0;

                for (Product item : cartItems) {
                    invoiceBreakdownBuffer.append("- ").append(item.getTitle()).append(" (RM").append(item.getPrice()).append(")\n");
                    totalCost += item.getPrice();
                    item.setStock(item.getStock() - 1); 
                }

                String receiptLogSummaryText = "Total Cost: RM" + totalCost + " | Units: " + cartItems.size();

                // Links directly to the addOrder method your partner made
                loggedInCustomer.addOrder(receiptLogSummaryText);

                Alert orderSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                orderSuccessAlert.setTitle("Transaction Approved");
                orderSuccessAlert.setHeaderText("Order Processed Successfully!");
                orderSuccessAlert.setContentText(invoiceBreakdownBuffer.toString() + "\nLogged under Customer Profile context successfully.");
                orderSuccessAlert.showAndWait();

                cartItems.clear();
                performSearch(searchField.getText(), filterBox.getValue(), catalogDisplay); 
            }
        });

        logoutBtn.setOnAction(e -> {
            System.out.println("Logging out customer context safely.");
            stage.close(); 
        });

        HBox cartControls = new HBox(10, addToCartBtn, removeCartBtn, checkoutBtn, logoutBtn);
        customerBox.getChildren().addAll(welcomeLabel, searchPanel, new Label("Available Islamic Literature Catalog:"), catalogDisplay, new Label("Your Shopping Cart:"), cartView, cartControls);
        
        // Swaps the interface layout dynamically inside Amirah's main root container
        rootContainer.getChildren().clear();
        rootContainer.getChildren().add(customerBox);
    }

    // =========================================================================
    // DYNAMIC SEARCH CALCULATION METHOD
    // =========================================================================
    private void performSearch(String text, String category, ListView<Product> display) {
        ObservableList<Product> filtered = FXCollections.observableArrayList();
        for (Product p : availableProducts) {
            boolean matchText = p.getTitle().toLowerCase().contains(text.toLowerCase()) || 
                               p.getAuthor().toLowerCase().contains(text.toLowerCase());
            
            boolean matchCat = category.equals("All Categories") || p.getCategory().equalsIgnoreCase(category);
            if (matchText && matchCat) {
                filtered.add(p);
            }
        }
        display.setItems(filtered);
    }
