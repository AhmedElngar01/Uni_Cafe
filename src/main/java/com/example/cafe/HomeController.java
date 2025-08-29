package com.example.cafe;

import com.example.cafe.loyalty.*;
import com.example.cafe.menu.*;
import com.example.cafe.menu.MenuItem;
import com.example.cafe.notification.*;
import com.example.cafe.order.*;
import com.example.cafe.payment.*;
import com.example.cafe.recommendation.RecommendationSystem;
import com.example.cafe.report.*;
import com.example.cafe.staff.*;
import com.example.cafe.student.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HomeController implements Initializable {

    @FXML
    private Label helloLabel;
    @FXML
    private Label LoyaltyPoint;
    @FXML
    private TableView<MenuItem> table;
    @FXML
    private TableColumn<MenuItem, String> nameColumn;
    @FXML
    private TableColumn<MenuItem, String> priceColumn;
    @FXML
    private TableColumn<MenuItem, String> categoryColumn;
    @FXML
    private TableColumn<MenuItem, String> descriptionColumn;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private IStudent student;

    private final IMenuManager menuManager = new MenuManager();
    private final INotificationSystem notificationSystem = new NotificationSystem();
    private final ILoyaltyProgram loyaltyProgram = new LoyaltyProgram();
    private final StudentOrderList studentOrderList = new StudentOrderList();
    private final IOrderProcessor orderProcessor = new OrderProcessor(loyaltyProgram, studentOrderList);
    private final IStaff staff = new Staff(orderProcessor, notificationSystem);
    private final IReportManager reportManager = new ReportManager(orderProcessor, loyaltyProgram);
    private final RecommendationSystem recommender =
    new RecommendationSystem(
        // Provide the menu items as List<MenuItem>
        new MenuManager().getTheMenu().stream()
            .filter(mi -> mi instanceof MenuItem)
            .map(mi -> (MenuItem) mi)
            .collect(Collectors.toList())
    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupMenuTable();
        table.setItems(getMenuItems());
    }

    public void currentStudent(IStudent s) {
        this.student = s;
        helloLabel.setText("Hello " + s.getName());
        LoyaltyPoint.setText(String.valueOf(loyaltyProgram.getLoyaltyPoints(s)));
    }

    private ObservableList<MenuItem> getMenuItems() {
        return FXCollections.observableArrayList(
                menuManager.getTheMenu().stream()
                        .filter(item -> item instanceof MenuItem)
                        .map(item -> (MenuItem) item)
                        .collect(Collectors.toList()));
    }

    private void setupMenuTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        categoryColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCategory().toString()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    @FXML
    public void makeOrder() {
        if (student == null) {
            showAlert("Error", "No student logged in.");
            return;
        }
        IOrder order = new Order(student.getStudentId());
        List<IMenuItem> menuItems = menuManager.getTheMenu();

        // You need to have a RecommendationSystem instance available

        while (true) {
            Optional<Integer> itemNumberOpt = promptForMenuItem(menuItems);
            if (!itemNumberOpt.isPresent() || itemNumberOpt.get() == 0)
                break;

            int itemNumber = itemNumberOpt.get();
            if (itemNumber < 1 || itemNumber > menuItems.size()) {
                showAlert("Invalid item", "Please select a valid item number.");
                continue;
            }
            IMenuItem selectedItem = menuItems.get(itemNumber - 1);

            Optional<Integer> quantityOpt = promptForQuantity(selectedItem.getName());
            if (!quantityOpt.isPresent() || quantityOpt.get() < 1)
                continue;

            int quantity = quantityOpt.get();
            for (int i = 0; i < quantity; i++)
                order.addItemToOrder(selectedItem);

            showAlert("Item Added", "Added " + quantity + " x " + selectedItem.getName() + " to your order.");

            // --- Recommendation logic ---
            List<MenuItem> availableAsMenuItem = menuManager.getTheMenu().stream()
                    .filter(mi -> mi instanceof MenuItem)
                    .map(mi -> (MenuItem) mi)
                    .collect(Collectors.toList());

            MenuItem recommended = recommender.recommend(availableAsMenuItem);
            if (recommended != null) {
                Alert recAlert = new Alert(Alert.AlertType.CONFIRMATION);
                recAlert.setTitle("Recommendation");
                recAlert.setHeaderText("Would you like to add " + recommended.getName() + " to your order?");
                recAlert.setContentText("Recommended item: " + recommended.getName());
                ButtonType yesBtn = new ButtonType("Yes");
                ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                recAlert.getButtonTypes().setAll(yesBtn, noBtn);

                Optional<ButtonType> recResult = recAlert.showAndWait();
                if (recResult.isPresent() && recResult.get() == yesBtn) {
                    order.addItemToOrder(recommended);
                    recommender.update(selectedItem.getName(), recommended.getName(), 1.0); // positive reward
                    showAlert("Added", "Added " + recommended.getName() + "!");
                } else {
                    recommender.update(selectedItem.getName(), recommended.getName(), 0.0); // no reward
                }
            }
            // ---
        }

        if (order.getOrderMenuItemList().getOrderMenuItems().isEmpty()) {
            showAlert("Order Cancelled", "No items were added to your order.");
            return;
        }

        if (confirmOrder(order)) {
            orderProcessor.placeOrder(order, student);
            showAlert("Order Placed", "✔ Order placed successfully! Your Order ID is: " + order.getOrderId());
        } else {
            showAlert("Order Cancelled", "Order cancelled.");
        }
    }

    private Optional<Integer> promptForMenuItem(List<IMenuItem> menuItems) {
        StringBuilder menuText = new StringBuilder("Menu:\n");
        for (int i = 0; i < menuItems.size(); i++) {
            IMenuItem item = menuItems.get(i);
            menuText.append(String.format("%2d. %-20s | %6.2f EGP | %s\n", (i + 1), item.getName(), item.getPrice(),
                    item.getDescription()));
        }
        menuText.append("\nEnter the item number to add (or 0 to finish):");

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Place Order");
        dialog.setHeaderText("Add Item to Order");
        dialog.setContentText(menuText.toString());

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent())
            return Optional.empty();

        try {
            return Optional.of(Integer.parseInt(result.get().trim()));
        } catch (Exception e) {
            showAlert("Invalid input", "Please enter a valid number for item.");
            return Optional.empty();
        }
    }

    private Optional<Integer> promptForQuantity(String itemName) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Quantity");
        dialog.setHeaderText("Enter quantity for " + itemName);
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent())
            return Optional.empty();

        try {
            int qty = Integer.parseInt(result.get().trim());
            if (qty < 1)
                throw new NumberFormatException();
            return Optional.of(qty);
        } catch (Exception e) {
            showAlert("Invalid input", "Please enter a valid number for quantity.");
            return Optional.empty();
        }
    }

    private boolean confirmOrder(IOrder order) {
        Map<String, Long> itemCount = order.getOrderMenuItemList().getOrderMenuItems().stream()
                .collect(Collectors.groupingBy(omi -> omi.getMenuItem().getName(), Collectors.counting()));

        StringBuilder summary = new StringBuilder("Order Summary:\n");
        itemCount.forEach((name, count) -> summary.append(String.format("- %-20s x %d\n", name, count)));
        summary.append(String.format("\nTotal price: %,.2f EGP\n", order.getTotalPrice()));
        summary.append("\nDo you want to confirm this order?");

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Order");
        confirmAlert.setHeaderText("Order Summary");
        confirmAlert.setContentText(summary.toString());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @FXML
    public void payForOrder() {
        if (student == null) {
            showAlert("Error", "No student logged in.");
            return;
        }
        Optional<Integer> orderIdOpt = promptForOrderId();
        if (!orderIdOpt.isPresent())
            return;

        int orderId = orderIdOpt.get();
        IOrder payOrder = orderProcessor.getOrderById(orderId);
        if (payOrder == null) {
            showAlert("Order Not Found", "Order not found.");
            return;
        }
        if (payOrder.isPaid()) {
            showAlert("Already Paid", "Order is already paid. You cannot pay again.");
            return;
        }

        double originalTotal = payOrder.getTotalPrice();
        double discountedTotal = handleLoyaltyDiscount(originalTotal, payOrder);

        if (discountedTotal == 0.0) {
            finalizePayment(payOrder, 0.0);
            showAlert("Paid", "✔ Order fully paid by points!");
            return;
        }

        Optional<String> paymentMethodOpt = promptForPaymentMethod();
        if (!paymentMethodOpt.isPresent())
            return;

        boolean paid = processPayment(paymentMethodOpt.get(), discountedTotal);
        if (paid) {
            finalizePayment(payOrder, discountedTotal);
            showAlert("Success", "✔ Payment successful!");
        } else {
            showAlert("Failed", "✖ Payment failed.");
        }
    }

    private Optional<Integer> promptForOrderId() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Payment");
        dialog.setHeaderText("Enter Order ID to Pay");
        dialog.setContentText("Order ID:");
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent())
            return Optional.empty();

        try {
            return Optional.of(Integer.parseInt(result.get().trim()));
        } catch (Exception e) {
            showAlert("Invalid Input", "Please enter a valid number for Order ID.");
            return Optional.empty();
        }
    }

    private double handleLoyaltyDiscount(double originalTotal, IOrder payOrder) {
        double discountedTotal = originalTotal;
        int currentPoints = loyaltyProgram.getLoyaltyPoints(student);

        Alert pointsAlert = new Alert(Alert.AlertType.CONFIRMATION);
        pointsAlert.setTitle("Loyalty Points");
        pointsAlert.setHeaderText("Order total: " + String.format("%,.2f EGP", originalTotal) +
                "\nYou have " + currentPoints + " loyalty points.");
        pointsAlert.setContentText("Do you want to redeem points for a discount?");
        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        pointsAlert.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> redeemResult = pointsAlert.showAndWait();
        if (redeemResult.isPresent() && redeemResult.get() == yesBtn) {
            Optional<Discount> discountOpt = promptForDiscount();
            if (discountOpt.isPresent()) {
                Discount discountStrategy = discountOpt.get();
                if (discountStrategy.redeemPoints(student, loyaltyProgram)) {
                    discountedTotal = Math.max(0, originalTotal - discountStrategy.getDiscountAmount());
                    showAlert("Discount Applied", "Discount applied: -" + discountStrategy.getDiscountAmount() +
                            " EGP\nNew total: " + String.format("%,.2f EGP", discountedTotal));
                } else {
                    showAlert("Not Enough Points", "Not enough points for selected discount. Skipping discount.");
                }
            }
        }
        return discountedTotal;
    }

    private Optional<Discount> promptForDiscount() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("10 EGP (50 points)",
                "10 EGP (50 points)", "20 EGP (100 points)", "40 EGP (200 points)");
        dialog.setTitle("Redeem Discount");
        dialog.setHeaderText("Choose discount option:");
        Optional<String> result = dialog.showAndWait();

        if (!result.isPresent())
            return Optional.empty();

        switch (result.get()) {
            case "10 EGP (50 points)":
                return Optional.of(new Discount(10, 50));
            case "20 EGP (100 points)":
                return Optional.of(new Discount(20, 100));
            case "40 EGP (200 points)":
                return Optional.of(new Discount(40, 200));
            default:
                return Optional.empty();
        }
    }

    private Optional<String> promptForPaymentMethod() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Cash", "Cash", "Credit Card", "Mobile", "Visa");
        dialog.setTitle("Payment Method");
        dialog.setHeaderText("Choose payment method:");
        return dialog.showAndWait();
    }

    private boolean processPayment(String method, double amount) {
        PaymentSystem paymentSystem = new PaymentSystem();
        switch (method) {
            case "Cash":
                return paymentSystem.processPayment(new CashPayment(), amount, student);
            case "Credit Card":
                return paymentSystem.processPayment(new CreditCardPayment(), amount, student);
            case "Mobile":
                return paymentSystem.processPayment(new MobilePayment(), amount, student);
            case "Visa":
                return paymentSystem.processPayment(new VisaPayment(), amount, student);
            default:
                showAlert("Invalid", "Invalid payment method.");
                return false;
        }
    }

    private void finalizePayment(IOrder payOrder, double paidAmount) {
        if (!payOrder.isPaid()) {
            payOrder.setPaid(true);
            if (orderProcessor instanceof OrderProcessor op)
                op.payOrder(payOrder.getOrderId());
            reportManager.addToTotalSales(paidAmount);
            try {
                if (loyaltyProgram != null && student != null) {
                    loyaltyProgram.awardPoints(student, paidAmount);
                    LoyaltyPoint.setText(String.valueOf(loyaltyProgram.getLoyaltyPoints(student))); // Update label here
                }
            } catch (Exception e) {
                showAlert("Error", "Error awarding loyalty points: " + e.getMessage());
            }
        }
    }

    @FXML
    public void redeemLoyaltyPoints() {
        if (student == null) {
            showAlert("Error", "No student logged in.");
            return;
        }

        Map<String, IredeemPoints> rewards = new LinkedHashMap<>();
        rewards.put("Free Coffee (10 points)", new FreeCoffee());
        rewards.put("Free Ice Cream (15 points)", new FreeIceCream());
        rewards.put("Free Pizza (40 points)", new FreePizza());
        rewards.put("Free Waffle (20 points)", new FreeWaffel());
        rewards.put("Free Donates (30 points)", new FreeDonates());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(rewards.keySet().iterator().next(), rewards.keySet());
        dialog.setTitle("Redeem Loyalty Points");
        dialog.setHeaderText("Choose a reward to redeem:");
        dialog.setContentText("Reward:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent())
            return;

        String selectedReward = result.get();
        IredeemPoints redeemStrategy = rewards.get(selectedReward);
        int requiredPoints = extractPointsFromReward(selectedReward);

        int currentPoints = loyaltyProgram.getLoyaltyPoints(student);
        if (currentPoints < requiredPoints) {
            showAlert("Not Enough Points",
                    "You have " + currentPoints + " points. This reward requires " + requiredPoints + " points.");
            return;
        }

        loyaltyProgram.setRedeemStrategy(redeemStrategy);
        boolean redeemed = loyaltyProgram.redeemPoints(student);
        if (redeemed) {
            showAlert("Success", "✔ Points redeemed successfully!");
            LoyaltyPoint.setText(String.valueOf(loyaltyProgram.getLoyaltyPoints(student)));
        } else {
            showAlert("Failed", "✖ Redemption failed.");
        }
    }

    private int extractPointsFromReward(String reward) {
        int start = reward.indexOf('(');
        int end = reward.indexOf(' ', start + 1);
        if (start != -1 && end != -1) {
            try {
                return Integer.parseInt(reward.substring(start + 1, end));
            } catch (NumberFormatException ignored) {
            }
        }
        return Integer.MAX_VALUE;
    }

    @FXML
    public void showMyOrders() {
        if (orderProcessor instanceof OrderProcessor op)
            op.reloadOrdersFromFile();
        List<IOrder> orders = orderProcessor.getOrdersByStudent(student);

        StringBuilder sb = new StringBuilder();
        if (orders.isEmpty()) {
            sb.append("You have no orders.");
        } else {
            sb.append("YOUR ORDERS:\n");
            for (IOrder order : orders) {
                sb.append("------------------------------\n");
                sb.append(String.format("Order ID: %-5d | Status: %-18s\n", order.getOrderId(), order.getStatus()));
                sb.append(String.format("Total Price: %,.2f EGP\n", order.getTotalPrice()));
                sb.append("Items:\n");
                Map<String, Long> itemCount = order.getOrderMenuItemList().getOrderMenuItems().stream()
                        .collect(Collectors.groupingBy(omi -> omi.getMenuItem().getName(), Collectors.counting()));
                itemCount.forEach((name, count) -> sb.append(String.format("- %-20s x %d\n", name, count)));
            }
            sb.append("------------------------------\n");
        }
        showInfoAlert("My Orders", "Order Details", sb.toString());
    }

    @FXML
    public void showNotification() {
        StringBuilder sb = new StringBuilder();
        if (student != null) {
            java.util.List<String> notes = ((NotificationSystem)notificationSystem).getNotifications(student.getStudentId());
            for (String note : notes) {
                sb.append("- ").append(note).append("\n");
            }
        }
        sb.append("=========================================\n");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Your Notifications");
        alert.setContentText(sb.toString());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    public void Logout(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}