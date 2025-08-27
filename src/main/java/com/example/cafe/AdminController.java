package com.example.cafe;

import com.example.cafe.loyalty.ILoyaltyProgram;
import com.example.cafe.loyalty.LoyaltyProgram;
import com.example.cafe.menu.*;
import com.example.cafe.menu.MenuItem;

import com.example.cafe.notification.INotificationSystem;
import com.example.cafe.notification.NotificationSystem;
import com.example.cafe.order.IOrder;
import com.example.cafe.order.IOrderProcessor;
import com.example.cafe.order.OrderProcessor;
import com.example.cafe.report.IReportManager;
import com.example.cafe.report.ReportManager;
import com.example.cafe.staff.IStaff;
import com.example.cafe.staff.Staff;
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
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
    java.time.LocalDate endOfWeek = startOfWeek.plusDays(6);

    IMenuManager menuManager = new MenuManager();
    INotificationSystem notificationSystem = new NotificationSystem();
    ILoyaltyProgram loyaltyProgram = new LoyaltyProgram();
    StudentOrderList studentOrderList = new StudentOrderList();
    IOrderProcessor orderProcessor = new OrderProcessor(loyaltyProgram, studentOrderList);
    IStaff staff = new Staff(orderProcessor, notificationSystem);
    IReportManager reportManager = new ReportManager(orderProcessor, loyaltyProgram);

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

    ObservableList<MenuItem> initialData() {
        ObservableList<MenuItem> items = FXCollections.observableArrayList(
                menuManager.getTheMenu().stream()
                        .filter(item -> item instanceof MenuItem)
                        .map(item -> (MenuItem) item)
                        .toList());
        return items;
    }

    @FXML
    private void addItem(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Add Menu Item");
        alert.setHeaderText("Enter item details");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category (Ex: PIZZA)");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        VBox vbox = new VBox(nameField, priceField, descriptionField, categoryField);
        vbox.setSpacing(10);
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            if (!nameField.getText().isEmpty() && !priceField.getText().isEmpty() && !categoryField.getText().isEmpty()
                    && !descriptionField.getText().isEmpty()) {
                try {
                    double price = Double.parseDouble(priceField.getText());
                    MenuItemCategory category = MenuItemCategory.valueOf(categoryField.getText().trim().toUpperCase());
                    MenuItem newItem = new MenuItem(
                            nameField.getText(),
                            descriptionField.getText(),
                            price,
                            category);
                    menuManager.getTheMenu().add(newItem);
                    table.getItems().add(newItem);

                    menuManager.addMenuItem(
                            new MenuItem(nameField.getText(), descriptionField.getText(), price, category));
                } catch (Exception e) {
                    Alert w = new Alert(Alert.AlertType.WARNING);
                    w.setHeaderText("Invalid input! Verify the validity of the data and classification.");
                    w.showAndWait();
                }
            } else {
                Alert w = new Alert(Alert.AlertType.WARNING);
                w.setHeaderText("All fields are required!");
                w.showAndWait();
            }
        }
    }

    @FXML
    private void editItem(ActionEvent event) {
        MenuItem selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert w = new Alert(Alert.AlertType.WARNING);
            w.setHeaderText("You need to select an item to edit.");
            w.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit Menu Item");
        alert.setHeaderText("Edit item details");

        TextField nameField = new TextField(selectedItem.getName());
        nameField.setPromptText("Name");

        TextField descriptionField = new TextField(selectedItem.getDescription());
        descriptionField.setPromptText("Description");

        TextField categoryField = new TextField(selectedItem.getCategory().toString());
        categoryField.setPromptText("Category (Ex: PIZZA)");

        TextField priceField = new TextField(String.valueOf(selectedItem.getPrice()));
        priceField.setPromptText("Price");

        VBox vbox = new VBox(nameField, priceField, descriptionField, categoryField);
        vbox.setSpacing(10);
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            if (!nameField.getText().isEmpty() && !priceField.getText().isEmpty() && !categoryField.getText().isEmpty()
                    && !descriptionField.getText().isEmpty()) {
                try {
                    double price = Double.parseDouble(priceField.getText());
                    MenuItemCategory category = MenuItemCategory.valueOf(categoryField.getText().trim().toUpperCase());

                    menuManager.editMenuItem(selectedItem.getName(),
                            new MenuItem(nameField.getText(), descriptionField.getText(), price, category));

                    table.refresh();
                    table.setItems(FXCollections.observableArrayList(
                            menuManager.getTheMenu().stream()
                                    .filter(item -> item instanceof MenuItem)
                                    .map(item -> (MenuItem) item)
                                    .toList()));

                } catch (Exception e) {
                    Alert w = new Alert(Alert.AlertType.WARNING);
                    w.setHeaderText("Invalid input! Verify the validity of the data and classification.");
                    w.showAndWait();
                }
            } else {
                Alert w = new Alert(Alert.AlertType.WARNING);
                w.setHeaderText("All fields are required!");
                w.showAndWait();
            }
        }
    }

    @FXML
    private void deleteData(ActionEvent event) {
        TableViewSelectionModel<MenuItem> selectionModel = table.getSelectionModel();
        if (selectionModel.isEmpty()) {
            Alert w = new Alert(Alert.AlertType.WARNING);
            w.setHeaderText("You need select a data before deleting.");
            w.showAndWait();
            return;
        }

        ObservableList<MenuItem> selectedItems = selectionModel.getSelectedItems();
        menuManager.removeMenuItem(selectionModel.getSelectedItem().getName());
        menuManager.getTheMenu().removeAll(selectedItems);
        table.getItems().removeAll(selectedItems);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        categoryColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCategory().toString()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.setItems(initialData());
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void orderPreparing() {
        java.util.List<IOrder> pendingOrders = staff.getPendingOrders();
        if (pendingOrders.isEmpty()) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("No Orders");
            info.setHeaderText(null);
            info.setContentText("No orders available!");
            info.showAndWait();
            return;
        }

        // Build a formatted string of pending orders
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-12s%n", "Order ID", "Status"));
        sb.append("--------------------------------\n");
        for (IOrder o : pendingOrders) {
            sb.append(String.format("%-10d %-12s%n", o.getOrderId(), o.getStatus()));
        }


        TextArea ordersArea = new TextArea(sb.toString());
        ordersArea.setEditable(false);
        ordersArea.setWrapText(false);
        ordersArea.setPrefRowCount(Math.min(16, pendingOrders.size() + 4)); // Bigger area
        ordersArea.setPrefColumnCount(40);
        ordersArea.setStyle("-fx-font-family: 'monospace'; -fx-font-size: 12px;");

        TextField inputField = new TextField();
        inputField.setPromptText("Enter Order ID");

        VBox vbox = new VBox(
                new Label("Current Orders:"),
                ordersArea,
                new Label("Order ID:"),
                inputField);
        vbox.setSpacing(10);
        vbox.setPadding(new javafx.geometry.Insets(10));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mark Order as Preparing");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            String input = inputField.getText();
            try {
                int preparingOrderId = Integer.parseInt(input.trim());
                IOrder preparingOrder = staff.getOrderById(preparingOrderId);
                if (preparingOrder != null) {
                    if (preparingOrder.isPaid()) {
                        staff.markOrderPreparing(preparingOrderId);
                        Alert done = new Alert(Alert.AlertType.INFORMATION);
                        done.setTitle("Success");
                        done.setHeaderText(null);
                        done.setContentText("Order " + preparingOrderId + " marked as PREPARING.");
                        done.showAndWait();
                    } else {
                        Alert warn = new Alert(Alert.AlertType.WARNING);
                        warn.setTitle("Not Paid");
                        warn.setHeaderText(null);
                        warn.setContentText("Order not paid yet!");
                        warn.showAndWait();
                    }
                } else {
                    Alert warn = new Alert(Alert.AlertType.WARNING);
                    warn.setTitle("Not Found");
                    warn.setHeaderText(null);
                    warn.setContentText("Order not found.");
                    warn.showAndWait();
                }
            } catch (NumberFormatException e) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setTitle("Invalid Input");
                warn.setHeaderText(null);
                warn.setContentText("Please enter a valid Order ID.");
                warn.showAndWait();
            }
        }
    }

    public void orderReady() {
        java.util.List<IOrder> preparingOrders = staff.getPreparingOrders();
        if (preparingOrders.isEmpty()) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("No Orders");
            info.setHeaderText(null);
            info.setContentText("No orders available!");
            info.showAndWait();
            return;
        }

        // Build a formatted string of preparing orders
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-12s%n", "Order ID", "Status"));
        sb.append("--------------------------------\n");
        for (IOrder o : preparingOrders) {
            sb.append(String.format("%-10d %-12s%n", o.getOrderId(), o.getStatus()));
        }

        TextArea ordersArea = new TextArea(sb.toString());
        ordersArea.setEditable(false);
        ordersArea.setWrapText(false);
        ordersArea.setPrefRowCount(Math.min(16, preparingOrders.size() + 4)); // Bigger area
        ordersArea.setPrefColumnCount(40); // Even wider area
        ordersArea.setStyle("-fx-font-family: 'monospace'; -fx-font-size: 12px;");

        TextField inputField = new TextField();
        inputField.setPromptText("Enter Order ID");

        VBox vbox = new VBox(
                new Label("Preparing Orders:"),
                ordersArea,
                new Label("Order ID:"),
                inputField);
        vbox.setSpacing(10);
        vbox.setPadding(new javafx.geometry.Insets(10));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mark Order as Ready");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            String input = inputField.getText();
            try {
                int readyOrderId = Integer.parseInt(input.trim());
                IOrder readyOrder = staff.getOrderById(readyOrderId);
                if (readyOrder != null) {
                    if (readyOrder.isPaid()) {
                        if (readyOrder.getStatus() == com.example.cafe.order.OrderState.PREPARING) {
                            staff.markOrderReady(readyOrderId);
                            Alert done = new Alert(Alert.AlertType.INFORMATION);
                            done.setTitle("Success");
                            done.setHeaderText(null);
                            done.setContentText("Order " + readyOrderId + " marked as READY FOR PICKUP.");
                            done.showAndWait();
                        } else {
                            Alert warn = new Alert(Alert.AlertType.WARNING);
                            warn.setTitle("Wrong Status");
                            warn.setHeaderText(null);
                            warn.setContentText(
                                    "Order must be in PREPARING status before marking as READY FOR PICKUP.");
                            warn.showAndWait();
                        }
                    } else {
                        Alert warn = new Alert(Alert.AlertType.WARNING);
                        warn.setTitle("Not Paid");
                        warn.setHeaderText(null);
                        warn.setContentText("Order not paid yet!");
                        warn.showAndWait();
                    }
                } else {
                    Alert warn = new Alert(Alert.AlertType.WARNING);
                    warn.setTitle("Not Found");
                    warn.setHeaderText(null);
                    warn.setContentText("Order not found.");
                    warn.showAndWait();
                }
            } catch (NumberFormatException e) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setTitle("Invalid Input");
                warn.setHeaderText(null);
                warn.setContentText("Please enter a valid Order ID.");
                warn.showAndWait();
            }
        }
    }

    public void showTotalSale() {

        double totalSales = reportManager.getTotalSales();
        double weeklySales = reportManager.getWeeklySales(today);
        double dailySales = reportManager.getDailySales(today);

        displayInfo("Total Sales: " + totalSales + " EGP",
                "\n╔════════════════════════════════════╗" +
                        "\n║         DAILY SALES REPORT         ║" +
                        "\n╠════════════════════════════════════╣\n" +
                        "║   Sales for " + today + " : " + dailySales + " EGP   ║\n" +
                        "╚════════════════════════════════════╝\n" +
                        "\n╔════════════════════════════════════╗" +
                        "\n║         WEEKLY SALES REPORT        ║" +
                        "\n╠════════════════════════════════════╣\n" +
                        "║   Sales for " + startOfWeek + " to " + endOfWeek + " : " + weeklySales + " EGP   ║\n" +
                        "╚════════════════════════════════════╝\n");
    }

    public void ShowPendingOrders() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════╗\n");
        sb.append("║       PENDING ORDERS         ║\n");
        sb.append("╚══════════════════════════════╝\n");

        staff.getPendingOrders().stream()
                .sorted(java.util.Comparator.comparingDouble(IOrder::getTotalPrice).reversed())
                .forEach(o -> sb.append(String.format(
                        "Order ID: %-5d | Price: %6.2f EGP | Status: %-18s\n",
                        o.getOrderId(), o.getTotalPrice(), o.getStatus())));

        displayInfo("Pending Orders", sb.toString());
    }

    public void ShowLoyaltyReport() {

        int dailyRedemptions = reportManager.getDailyRedemptions(today);
        int dailyRedeemedPoints = reportManager.getDailyRedeemedPoints(today);
        int weeklyRedeemedPoints = reportManager.getWeeklyRedeemedPoints(today);
        int weeklyRedemptions = reportManager.getWeeklyRedemptions(today);
        int totalRedemptions = reportManager.getTotalRedemptions();
        int totalRedeemedPoints = reportManager.getTotalRedeemedPoints();

        displayInfo("loyalty redemptions",
                "\n╔══════════════════════════════╗" +
                        "\n║   DAILY LOYALTY REDEMPTIONS  ║" +
                        "\n╚══════════════════════════════╝" +
                        "\nPoints redeemed for " + today + ": " + dailyRedeemedPoints +
                        "\nRedemptions for " + today + ": " + dailyRedemptions +
                        "\n╔══════════════════════════════╗" +
                        "\n║   WEEKLY LOYALTY REDEMPTIONS ║" +
                        "\n╚══════════════════════════════╝" +
                        "\nPoints redeemed for week " + startOfWeek + " to " + endOfWeek + ": " + weeklyRedeemedPoints +
                        "\nRedemptions for week " + startOfWeek + " to " + endOfWeek + ": " + weeklyRedemptions +
                        "\n╔══════════════════════════════╗" +
                        "\n║   LOYALTY REDEMPTIONS        ║" +
                        "\n╚══════════════════════════════╝" +
                        "\nTotal Redemptions: " + totalRedemptions +
                        "\nTotal Redeemed Points: " + totalRedeemedPoints);
    }

    private void displayInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void Logout(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
