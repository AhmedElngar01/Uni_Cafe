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
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private final LocalDate today = LocalDate.now();
    private final LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
    private final LocalDate endOfWeek = startOfWeek.plusDays(6);

    private final IMenuManager menuManager = new MenuManager();
    private final INotificationSystem notificationSystem = new NotificationSystem();
    private final ILoyaltyProgram loyaltyProgram = new LoyaltyProgram();
    private final StudentOrderList studentOrderList = new StudentOrderList();
    private final IOrderProcessor orderProcessor = new OrderProcessor(loyaltyProgram, studentOrderList);
    private final IStaff staff = new Staff(orderProcessor, notificationSystem);
    private final IReportManager reportManager = new ReportManager(orderProcessor, loyaltyProgram);

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().toString()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        table.setItems(getMenuItems());
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private ObservableList<MenuItem> getMenuItems() {
        return FXCollections.observableArrayList(
                menuManager.getTheMenu().stream()
                        .filter(MenuItem.class::isInstance)
                        .map(MenuItem.class::cast)
                        .collect(Collectors.toList())
        );
    }

    @FXML
    private void addItem(ActionEvent event) {
        Dialog<MenuItem> dialog = createMenuItemDialog("Add Menu Item", null);
        dialog.showAndWait().ifPresent(item -> {
            menuManager.addMenuItem(item);
            table.getItems().add(item);
        });
    }

    @FXML
    private void editItem(ActionEvent event) {
        MenuItem selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "Edit Menu Item", "You need to select an item to edit.");
            return;
        }
        Dialog<MenuItem> dialog = createMenuItemDialog("Edit Menu Item", selectedItem);
        dialog.showAndWait().ifPresent(item -> {
            menuManager.editMenuItem(selectedItem.getName(), item);
            table.setItems(getMenuItems());
        });
    }

    @FXML
    private void deleteData(ActionEvent event) {
        TableViewSelectionModel<MenuItem> selectionModel = table.getSelectionModel();
        if (selectionModel.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Delete Menu Item", "You need to select a data before deleting.");
            return;
        }
        ObservableList<MenuItem> selectedItems = selectionModel.getSelectedItems();
        selectedItems.forEach(item -> menuManager.removeMenuItem(item.getName()));
        menuManager.getTheMenu().removeAll(selectedItems);
        table.getItems().removeAll(selectedItems);
    }

    public void orderPreparing() {
        List<IOrder> pendingOrders = staff.getPendingOrders();
        if (pendingOrders.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Orders", "No orders available!");
            return;
        }
        int orderId = promptOrderId("Mark Order as Preparing", "Current Orders:", pendingOrders);
        if (orderId == -1) return;
        IOrder order = staff.getOrderById(orderId);
        if (order == null) {
            showAlert(Alert.AlertType.WARNING, "Not Found", "Order not found.");
        } else if (!order.isPaid()) {
            showAlert(Alert.AlertType.WARNING, "Not Paid", "Order not paid yet!");
        } else {
            staff.markOrderPreparing(orderId);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order " + orderId + " marked as PREPARING.");
        }
    }

    public void orderReady() {
        List<IOrder> preparingOrders = staff.getPreparingOrders();
        if (preparingOrders.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Orders", "No orders available!");
            return;
        }
        int orderId = promptOrderId("Mark Order as Ready", "Preparing Orders:", preparingOrders);
        if (orderId == -1) return;
        IOrder order = staff.getOrderById(orderId);
        if (order == null) {
            showAlert(Alert.AlertType.WARNING, "Not Found", "Order not found.");
        } else if (!order.isPaid()) {
            showAlert(Alert.AlertType.WARNING, "Not Paid", "Order not paid yet!");
        } else if (order.getStatus() != com.example.cafe.order.OrderState.PREPARING) {
            showAlert(Alert.AlertType.WARNING, "Wrong Status", "Order must be in PREPARING status before marking as READY FOR PICKUP.");
        } else {
            staff.markOrderReady(orderId);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order " + orderId + " marked as READY FOR PICKUP.");
        }
    }

    public void showTotalSale() {
        double totalSales = reportManager.getTotalSales();
        double weeklySales = reportManager.getWeeklySales(today);
        double dailySales = reportManager.getDailySales(today);
        String content = String.format(
                "\n╔════════════════════════════════════╗" +
                        "\n║         DAILY SALES REPORT         ║" +
                        "\n╠════════════════════════════════════╣\n" +
                        "║   Sales for %s : %.2f EGP   ║\n" +
                        "╚════════════════════════════════════╝\n" +
                        "\n╔════════════════════════════════════╗" +
                        "\n║         WEEKLY SALES REPORT        ║" +
                        "\n╠════════════════════════════════════╣\n" +
                        "║   Sales for %s to %s : %.2f EGP   ║\n" +
                        "╚════════════════════════════════════╝\n",
                today, dailySales, startOfWeek, endOfWeek, weeklySales
        );
        showAlert(Alert.AlertType.INFORMATION, "Total Sales: " + totalSales + " EGP", content);
    }

    public void showPendingOrders() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════╗\n");
        sb.append("║       PENDING ORDERS         ║\n");
        sb.append("╚══════════════════════════════╝\n");
        staff.getPendingOrders().stream()
                .sorted((a, b) -> Double.compare(b.getTotalPrice(), a.getTotalPrice()))
                .forEach(o -> sb.append(String.format(
                        "Order ID: %-5d | Price: %6.2f EGP | Status: %-18s\n",
                        o.getOrderId(), o.getTotalPrice(), o.getStatus())));
        showAlert(Alert.AlertType.INFORMATION, "Pending Orders", sb.toString());
    }

    public void showLoyaltyReport() {
        int dailyRedemptions = reportManager.getDailyRedemptions(today);
        int dailyRedeemedPoints = reportManager.getDailyRedeemedPoints(today);
        int weeklyRedeemedPoints = reportManager.getWeeklyRedeemedPoints(today);
        int weeklyRedemptions = reportManager.getWeeklyRedemptions(today);

        String content = String.format(
                "\n╔══════════════════════════════╗" +
                        "\n║   DAILY LOYALTY REDEMPTIONS  ║" +
                        "\n╚══════════════════════════════╝" +
                        "\nPoints redeemed for %s: %d" +
                        "\nRedemptions for %s: %d" +
                        "\n╔══════════════════════════════╗" +
                        "\n║   WEEKLY LOYALTY REDEMPTIONS ║" +
                        "\n╚══════════════════════════════╝" +
                        "\nPoints redeemed for week %s to %s: %d" +
                        "\nRedemptions for week %s to %s: %d",
                today, dailyRedeemedPoints, today, dailyRedemptions,
                startOfWeek, endOfWeek, weeklyRedeemedPoints,
                startOfWeek, endOfWeek, weeklyRedemptions
        );
        showAlert(Alert.AlertType.INFORMATION, "Loyalty Redemptions", content);
    }

    private Dialog<MenuItem> createMenuItemDialog(String title, MenuItem existing) {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Enter item details");

        ButtonType okButtonType = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(existing != null ? existing.getName() : "");
        nameField.setPromptText("Name");
        TextField priceField = new TextField(existing != null ? String.valueOf(existing.getPrice()) : "");
        priceField.setPromptText("Price");
        TextField descriptionField = new TextField(existing != null ? existing.getDescription() : "");
        descriptionField.setPromptText("Description");
        TextField categoryField = new TextField(existing != null ? existing.getCategory().toString() : "");
        categoryField.setPromptText("Category (Ex: PIZZA)");

        VBox vbox = new VBox(nameField, priceField, descriptionField, categoryField);
        vbox.setSpacing(10);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String desc = descriptionField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    MenuItemCategory category = MenuItemCategory.valueOf(categoryField.getText().trim().toUpperCase());
                    if (name.isEmpty() || desc.isEmpty()) throw new IllegalArgumentException();
                    return new MenuItem(name, desc, price, category);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.WARNING, "Invalid input!", "Verify the validity of the data and classification.");
                }
            }
            return null;
        });
        return dialog;
    }

    private int promptOrderId(String title, String label, List<IOrder> orders) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-12s%n", "Order ID", "Status"));
        sb.append("--------------------------------\n");
        for (IOrder o : orders) {
            sb.append(String.format("%-10d %-12s%n", o.getOrderId(), o.getStatus()));
        }
        TextArea ordersArea = new TextArea(sb.toString());
        ordersArea.setEditable(false);
        ordersArea.setWrapText(false);
        ordersArea.setPrefRowCount(Math.min(16, orders.size() + 4));
        ordersArea.setPrefColumnCount(40);
        ordersArea.setStyle("-fx-font-family: 'monospace'; -fx-font-size: 12px;");

        TextField inputField = new TextField();
        inputField.setPromptText("Enter Order ID");

        VBox vbox = new VBox(new Label(label), ordersArea, new Label("Order ID:"), inputField);
        vbox.setSpacing(10);
        vbox.setPadding(new javafx.geometry.Insets(10));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            try {
                return Integer.parseInt(inputField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid Order ID.");
            }
        }
        return -1;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
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