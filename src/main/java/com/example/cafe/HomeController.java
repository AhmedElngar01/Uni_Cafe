package com.example.cafe;


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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TableView<TestItems> table;

    @FXML
    private TableColumn<TestItems, String> categoryColumn;

    @FXML
    private TableColumn<TestItems, String> descriptionColumn;

    @FXML
    private TableColumn<TestItems, Integer> idColumn;

    @FXML
    private TableColumn<TestItems, String> nameColumn;

    @FXML
    private TableColumn<TestItems, String> priceColumn;

    ObservableList<TestItems> initialData(){
        TestItems p1 = new TestItems(1,"Last Sample Name"," US","100","Pizza");
        TestItems p2 = new TestItems(2,"Last Name"," PH","200","Pizza2");
        return FXCollections.observableArrayList(p1, p2);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<TestItems, Integer>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<TestItems, String>("Name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<TestItems, String>("Price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<TestItems, String>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<TestItems, String>("description"));

        table.setItems(initialData());


    }


    public void makeOrder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("title");
        alert.setHeaderText("Enter the item id you want to add ");

        TextField inputField = new TextField();
        //inputField.setPromptText("prompt");

        VBox vbox = new VBox(inputField);
        vbox.setSpacing(10);
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            inputField.getText();
        }
    }

    public void showMyOrders(){
        displayInfo("My Order","11111");
    }
    public void showNotification(){
        displayInfo("Notification", "2222222");
    }

    private void displayInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void Logout (ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



}



