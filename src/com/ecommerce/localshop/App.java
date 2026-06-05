import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;

// RENAME REQUIREMENT: Main Application Entry Point named 'App'
public class App extends Application {

    // Central Data States
    private ArrayList<Product> inventory = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<String> orderHistory = new ArrayList<>();
    private ArrayList<Product> cart = new ArrayList<>();
    private User currentUser = null;

    private Stage primaryStage;
    private BorderPane mainRoot;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainRoot = new BorderPane();

        // Load database persistence logs
        loadUsers();
        loadInventory();

        // Boot system to entry portal gateway
        showLoginGateway();

        Scene scene = new Scene(mainRoot, 850, 600);
        // APPLICATION NAME: Formally titled "Noor Book Store"
        primaryStage.setTitle("Noor Book Store - Islamic eCommerce Platform");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

  // =========================================================================
    // AUTHOR: MEMBER 3 (SYAHIRAH - CUSTOMER STOREFRONT UI)
    // =========================================================================
    private void showCustomerDashboard() {
        VBox customerBox = new VBox(15);
        customerBox.setPadding(new Insets(25));
        customerBox.setStyle("-fx-background-color: #ffffff;");

        Label welcomeLabel = new Label("Welcome to Noor Book Store, " + currentUser.getUsername() + "! Assalamu Alaikum.");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e5e43;");

        HBox searchPanel = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search literature titles...");
        ComboBox<String> filterBox = new ComboBox<>(FXCollections.observableArrayList("All Categories", "Quran & Tafsir", "Hadith Collections", "Children Learning"));
        filterBox.setValue("All Categories");
        searchPanel.getChildren().addAll(searchField, filterBox);

        ListView<Product> catalogDisplay = new ListView<>();
        catalogDisplay.setItems(FXCollections.observableArrayList(inventory));

        // LAMBDA event listeners linking criteria fields dynamically to search dispatch calculations
        searchField.textProperty().addListener((obs, oldVal, newVal) -> performSearch(searchField.getText(), filterBox.getValue(), catalogDisplay));
        filterBox.setOnAction(e -> performSearch(searchField.getText(), filterBox.getValue(), catalogDisplay));

        ListView<Product> cartView = new ListView<>();
        cartView.setPrefHeight(120);

        Button addToCartBtn = new Button("Add Selected Book to Cart");
        Button removeCartBtn = new Button("Remove Item from Cart");
        Button checkoutBtn = new Button("Finalize Order & Print Receipt");
        Button logoutBtn = new Button("Logout");
        checkoutBtn.setStyle("-fx-background-color: #1e5e43; -fx-text-fill: white; -fx-font-weight: bold;");

        addToCartBtn.setOnAction(e -> {
            Product selected = catalogDisplay.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cart.add(selected);
                cartView.setItems(FXCollections.observableArrayList(cart));
            }
        });

        removeCartBtn.setOnAction(e -> {
            Product selected = cartView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cart.remove(selected);
                cartView.setItems(FXCollections.observableArrayList(cart));
            }
        });

        checkoutBtn.setOnAction(e -> processCheckout(cartView));
        logoutBtn.setOnAction(e -> showLoginGateway());

        HBox cartControls = new HBox(10, addToCartBtn, removeCartBtn, checkoutBtn, logoutBtn);
        customerBox.getChildren().addAll(welcomeLabel, searchPanel, new Label("Available Islamic Literature Catalog:"), catalogDisplay, new Label("Your Shopping Cart:"), cartView, cartControls);
        mainRoot.setCenter(customerBox);
    }

    private void performSearch(String text, String category, ListView<Product> display) {
        ObservableList<Product> filtered = FXCollections.observableArrayList();
        for (Product p : inventory) {
            boolean matchText = p.getTitle().toLowerCase().contains(text.toLowerCase());
            boolean matchCat = category.equals("All Categories") || p.getCategory().equalsIgnoreCase(category);
            if (matchText && matchCat) {
                filtered.add(p);
            }
        }
        display.setItems(filtered);
    }
