package com.example.cafe;

import com.example.cafe.loyalty.Discount;
import com.example.cafe.loyalty.FreeCoffee;
import com.example.cafe.loyalty.FreeDonates;
import com.example.cafe.loyalty.FreeIceCream;
import com.example.cafe.loyalty.FreePizza;
import com.example.cafe.loyalty.FreeWaffel;
import com.example.cafe.loyalty.ILoyaltyProgram;
import com.example.cafe.loyalty.IredeemPoints;
import com.example.cafe.loyalty.LoyaltyProgram;
import com.example.cafe.menu.IMenuItem;
import com.example.cafe.menu.IMenuManager;
import com.example.cafe.menu.MenuItem;
import com.example.cafe.menu.MenuManager;

import com.example.cafe.notification.INotificationSystem;
import com.example.cafe.notification.NotificationSystem;
import com.example.cafe.order.IOrder;
import com.example.cafe.order.IOrderProcessor;
import com.example.cafe.order.Order;
import com.example.cafe.order.OrderProcessor;
import com.example.cafe.payment.CashPayment;
import com.example.cafe.payment.CreditCardPayment;
import com.example.cafe.payment.MobilePayment;
import com.example.cafe.payment.PaymentSystem;
import com.example.cafe.payment.VisaPayment;
import com.example.cafe.report.IReportManager;
import com.example.cafe.report.ReportManager;
import com.example.cafe.staff.IStaff;
import com.example.cafe.staff.Staff;
import com.example.cafe.student.IStudent;
import com.example.cafe.student.StudentOrder;
import com.example.cafe.student.StudentOrderList;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HomeController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private IStudent student;

    private final IMenuManager menuManager = new MenuManager();
    INotificationSystem notificationSystem = new NotificationSystem();
    ILoyaltyProgram loyaltyProgram = new LoyaltyProgram();
    StudentOrderList studentOrderList = new StudentOrderList();
    IOrderProcessor orderProcessor = new OrderProcessor(loyaltyProgram, studentOrderList);
    IStaff staff = new Staff(orderProcessor, notificationSystem);
    IReportManager reportManager = new ReportManager(orderProcessor, loyaltyProgram);

    @FXML
    private Label helloLabel;

    @FXML
    private Label LoyaltyPoint;

    @FXML
    private TableView<MenuItem> table;

    @FXML
    private TableColumn<MenuItem, Integer> idColumn;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, String> priceColumn;

    @FXML
    private TableColumn<MenuItem, String> categoryColumn;

    @FXML
    private TableColumn<MenuItem, String> descriptionColumn;

    public void currentStudent(IStudent s) {
        student = s;
        helloLabel.setText("Hello " + s.getName());
        LoyaltyPoint.setText("" + loyaltyProgram.getLoyaltyPoints(s));
    }

    private ObservableList<MenuItem> initialData() {
        return FXCollections.observableArrayList(
                menuManager.getTheMenu().stream()
                        .filter(item -> item instanceof MenuItem)
                        .map(item -> (MenuItem) item)
                        .collect(Collectors.toList()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellFactory(col -> new TableCell<MenuItem, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        categoryColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCategory().toString()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.setItems(initialData());
    }

    @FXML
    public void makeOrder() {
        IOrder order = new Order(student.getStudentId());
        java.util.List<IMenuItem> menuItems = menuManager.getTheMenu();

        while (true) {

            StringBuilder menuText = new StringBuilder("Menu:\n");
            for (int i = 0; i < menuItems.size(); i++) {
                IMenuItem item = menuItems.get(i);
                menuText.append(String.format("%2d. %-20s | %6.2f EGP | %s\n", (i + 1), item.getName(), item.getPrice(),
                        item.getDescription()));
            }
            menuText.append("\nEnter the item number to add (or 0 to finish):");

            TextInputDialog itemDialog = new TextInputDialog();
            itemDialog.setTitle("Place Order");
            itemDialog.setHeaderText("Add Item to Order");
            itemDialog.setContentText(menuText.toString());

            Optional<String> itemResult = itemDialog.showAndWait();
            if (!itemResult.isPresent())
                break;

            int itemNumber;
            try {
                itemNumber = Integer.parseInt(itemResult.get().trim());
            } catch (Exception e) {
                showAlert("Invalid input", "Please enter a valid number for item.");
                continue;
            }
            if (itemNumber == 0)
                break;
            if (itemNumber < 1 || itemNumber > menuItems.size()) {
                showAlert("Invalid item", "Please select a valid item number.");
                continue;
            }
            IMenuItem selectedItem = menuItems.get(itemNumber - 1);

            TextInputDialog qtyDialog = new TextInputDialog();
            qtyDialog.setTitle("Quantity");
            qtyDialog.setHeaderText("Enter quantity for " + selectedItem.getName());
            qtyDialog.setContentText("Quantity:");

            Optional<String> qtyResult = qtyDialog.showAndWait();
            if (!qtyResult.isPresent())
                continue;

            int quantity;
            try {
                quantity = Integer.parseInt(qtyResult.get().trim());
            } catch (Exception e) {
                showAlert("Invalid input", "Please enter a valid number for quantity.");
                continue;
            }
            if (quantity < 1) {
                showAlert("Invalid quantity", "Quantity must be at least 1.");
                continue;
            }
            for (int i = 0; i < quantity; i++) {
                order.addItemToOrder(selectedItem);
            }
            showAlert("Item Added", "Added " + quantity + " x " + selectedItem.getName() + " to your order.");
        }

        if (order.getOrderMenuItemList().getOrderMenuItems().isEmpty()) {
            showAlert("Order Cancelled", "No items were added to your order.");
            return;
        }

        java.util.Map<String, Long> itemCount = order.getOrderMenuItemList().getOrderMenuItems().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        omi -> omi.getMenuItem().getName(), java.util.stream.Collectors.counting()));
        StringBuilder summary = new StringBuilder();
        summary.append("Order Summary:\n");
        itemCount.forEach((name, count) -> summary.append(String.format("- %-20s x %d\n", name, count)));
        summary.append(String.format("\nTotal price: %,.2f EGP\n", order.getTotalPrice()));
        summary.append("\nDo you want to confirm this order?");

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Order");
        confirmAlert.setHeaderText("Order Summary");
        confirmAlert.setContentText(summary.toString());

        Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
        if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
            orderProcessor.placeOrder(order, student);
            showAlert("Order Placed", "✔ Order placed successfully! Your Order ID is: " + order.getOrderId());
        } else {
            showAlert("Order Cancelled", "Order cancelled.");
        }
    }

    @FXML
    public void payForOrder() {
        TextInputDialog orderIdDialog = new TextInputDialog();
        orderIdDialog.setTitle("Payment");
        orderIdDialog.setHeaderText("Enter Order ID to Pay");
        orderIdDialog.setContentText("Order ID:");
        Optional<String> orderIdResult = orderIdDialog.showAndWait();
        if (!orderIdResult.isPresent())
            return;

        int payOrderId;
        try {
            payOrderId = Integer.parseInt(orderIdResult.get().trim());
        } catch (Exception e) {
            showAlert("Invalid Input", "Please enter a valid number for Order ID.");
            return;
        }

        IOrder payOrder = orderProcessor.getOrderById(payOrderId);
        if (payOrder == null) {
            showAlert("Order Not Found", "Order not found.");
            return;
        }
        if (payOrder.isPaid()) {
            showAlert("Already Paid", "Order is already paid. You cannot pay again.");
            return;
        }

        double originalTotal = payOrder.getTotalPrice();
        double discountedTotal = originalTotal;

        ILoyaltyProgram lp = loyaltyProgram;
        if (lp != null && student != null) {
            int currentPoints = lp.getLoyaltyPoints(student);
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
                ChoiceDialog<String> discountDialog = new ChoiceDialog<>("10 EGP (50 points)",
                        "10 EGP (50 points)", "20 EGP (100 points)", "40 EGP (200 points)");
                discountDialog.setTitle("Redeem Discount");
                discountDialog.setHeaderText("Choose discount option:");
                Optional<String> discountResult = discountDialog.showAndWait();
                int discountAmount = 0, pointsNeeded = 0;
                if (discountResult.isPresent()) {
                    switch (discountResult.get()) {
                        case "10 EGP (50 points)":
                            discountAmount = 10;
                            pointsNeeded = 50;
                            break;
                        case "20 EGP (100 points)":
                            discountAmount = 20;
                            pointsNeeded = 100;
                            break;
                        case "40 EGP (200 points)":
                            discountAmount = 40;
                            pointsNeeded = 200;
                            break;
                    }
                    if (discountAmount > 0) {
                        // Assume Discount class exists as in your code
                        Discount discountStrategy = new Discount(discountAmount, pointsNeeded);
                        if (discountStrategy.redeemPoints(student, lp)) {
                            discountedTotal = Math.max(0, originalTotal - discountStrategy.getDiscountAmount());
                            showAlert("Discount Applied", "Discount applied: -" + discountAmount + " EGP\nNew total: "
                                    + String.format("%,.2f EGP", discountedTotal));
                        } else {
                            showAlert("Not Enough Points",
                                    "Not enough points for selected discount. Skipping discount.");
                        }
                    }
                }
            }
        }

        if (discountedTotal == 0.0) {
            payOrder.setPaid(true);
            if (orderProcessor instanceof OrderProcessor op) {
                op.payOrder(payOrder.getOrderId());
            }
            reportManager.addToTotalSales(0.0);
            showAlert("Paid", "✔ Order fully paid by points!");
            try {
                if (loyaltyProgram != null && student != null) {
                    loyaltyProgram.awardPoints(student, 0.0);
                }
            } catch (Exception e) {
                showAlert("Error", "Error awarding loyalty points: " + e.getMessage());
            }
            return;
        }

        ChoiceDialog<String> payDialog = new ChoiceDialog<>("Cash", "Cash", "Credit Card", "Mobile", "Visa");
        payDialog.setTitle("Payment Method");
        payDialog.setHeaderText("Choose payment method:");
        Optional<String> payResult = payDialog.showAndWait();
        if (!payResult.isPresent())
            return;

        PaymentSystem paymentSystem = new PaymentSystem();
        boolean paid = false;
        switch (payResult.get()) {
            case "Cash":
                paid = paymentSystem.processPayment(new CashPayment(), discountedTotal, student);
                break;
            case "Credit Card":
                paid = paymentSystem.processPayment(new CreditCardPayment(), discountedTotal, student);
                break;
            case "Mobile":
                paid = paymentSystem.processPayment(new MobilePayment(), discountedTotal, student);
                break;
            case "Visa":
                paid = paymentSystem.processPayment(new VisaPayment(), discountedTotal, student);
                break;
            default:
                showAlert("Invalid", "Invalid payment method.");
                return;
        }

        if (paid) {
            if (!payOrder.isPaid()) {
                payOrder.setPaid(true);
                if (orderProcessor instanceof OrderProcessor op) {
                    op.payOrder(payOrder.getOrderId());
                }
                reportManager.addToTotalSales(discountedTotal);
                try {
                    if (loyaltyProgram != null && student != null) {
                        loyaltyProgram.awardPoints(student, discountedTotal);
                    }
                } catch (Exception e) {
                    showAlert("Error", "Error awarding loyalty points: " + e.getMessage());
                }
                showAlert("Success", "✔ Payment successful!");
            } else {
                showAlert("Already Paid", "Order is already paid. You cannot pay again.");
            }
        } else {
            showAlert("Failed", "✖ Payment failed.");
        }
    }

    @FXML
    public void redeemLoyaltyPoints() {
        if (student == null) {
            showAlert("Error", "No student logged in.");
            return;
        }

        String[] rewards = {
                "Free Coffee (10 points)",
                "Free Ice Cream (15 points)",
                "Free Pizza (40 points)",
                "Free Waffle (20 points)",
                "Free Donates (30 points)"
        };

        ChoiceDialog<String> rewardDialog = new ChoiceDialog<>(rewards[0], rewards);
        rewardDialog.setTitle("Redeem Loyalty Points");
        rewardDialog.setHeaderText("Choose a reward to redeem:");
        rewardDialog.setContentText("Reward:");

        Optional<String> result = rewardDialog.showAndWait();
        if (!result.isPresent())
            return;

        IredeemPoints redeemStrategy;
        int requiredPoints;
        switch (result.get()) {
            case "Free Coffee (10 points)":
                redeemStrategy = new FreeCoffee();
                requiredPoints = 10;
                break;
            case "Free Ice Cream (15 points)":
                redeemStrategy = new FreeIceCream();
                requiredPoints = 15;
                break;
            case "Free Pizza (40 points)":
                redeemStrategy = new FreePizza();
                requiredPoints = 40;
                break;
            case "Free Waffle (20 points)":
                redeemStrategy = new FreeWaffel();
                requiredPoints = 20;
                break;
            case "Free Donates (30 points)":
                redeemStrategy = new FreeDonates();
                requiredPoints = 30;
                break;
            default:
                showAlert("Invalid Option", "Invalid reward option.");
                return;
        }

        int currentPoints;
        try {
            currentPoints = loyaltyProgram.getLoyaltyPoints(student);
        } catch (Exception e) {
            showAlert("Error", "Error reading loyalty points: " + e.getMessage());
            return;
        }

        if (currentPoints < requiredPoints) {
            showAlert("Not Enough Points",
                    "You have " + currentPoints + " points. This reward requires " + requiredPoints + " points.");
            return;
        }

        loyaltyProgram.setRedeemStrategy(redeemStrategy);
        boolean redeemed = loyaltyProgram.redeemPoints(student);
        if (redeemed) {
            showAlert("Success", "✔ Points redeemed successfully!");
            LoyaltyPoint.setText("" + loyaltyProgram.getLoyaltyPoints(student));
        } else {
            showAlert("Failed", "✖ Redemption failed.");
        }
    }

    @FXML
    public void showMyOrders() {
        if (orderProcessor instanceof OrderProcessor op) {
            op.reloadOrdersFromFile();
        }
        java.util.List<IOrder> orders = orderProcessor.getOrdersByStudent(student);
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
                java.util.Map<String, Long> itemCount = order.getOrderMenuItemList().getOrderMenuItems().stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                omi -> omi.getMenuItem().getName(), java.util.stream.Collectors.counting()));
                itemCount.forEach((name, count) -> {
                    sb.append(String.format("- %-20s x %d\n", name, count));
                });
            }
            sb.append("------------------------------\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("My Orders");
        alert.setHeaderText("Order Details");
        alert.setContentText(sb.toString());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Ensures full text is visible
        alert.showAndWait();
    }

    @FXML
    public void showNotification() {
        StringBuilder sb = new StringBuilder();

        for (String note : notificationSystem.getNotifications()) {
            sb.append("- ").append(note).append("\n");
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

    @FXML
    public void Logout(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}