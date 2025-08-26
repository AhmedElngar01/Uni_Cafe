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
import java.util.Arrays;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

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
    @FXML
    private void addItem(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("title");
        alert.setHeaderText("header");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("description");

        TextField categoryField = new TextField();
        categoryField.setPromptText("category");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        VBox vbox = new VBox(nameField,priceField,descriptionField,categoryField);
        vbox.setSpacing(10);
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            if(!nameField.getText().isEmpty() || !priceField.getText().isEmpty() || !categoryField.getText().isEmpty()|| !descriptionField.getText().isEmpty()){
                TestItems newData = new TestItems(0,descriptionField.getText(), categoryField.getText(), priceField.getText(),nameField.getText());
                table.getItems().add(newData);
            }else{
                Alert w = new Alert(Alert.AlertType.WARNING);
                w.showAndWait();
            }
        }
    }

    @FXML
    private void deleteData(ActionEvent event){
        TableView.TableViewSelectionModel<TestItems> selectionModel = table.getSelectionModel();
        if(selectionModel.isEmpty()){
            Alert w = new Alert(Alert.AlertType.WARNING);
            w.setHeaderText("You need select a data before deleting.");
            w.showAndWait();
        }

        ObservableList<Integer> list = selectionModel.getSelectedIndices();
        Integer[] selectedIndices = new Integer[list.size()];
        selectedIndices = list.toArray(selectedIndices);

        Arrays.sort(selectedIndices);

        for(int i = selectedIndices.length - 1; i >= 0; i--){
            selectionModel.clearSelection(selectedIndices[i].intValue());
            table.getItems().remove(selectedIndices[i].intValue());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<TestItems, Integer>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<TestItems, String>("Name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<TestItems, String>("Price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<TestItems, String>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<TestItems, String>("description"));

        table.setItems(initialData());

        //editData();
    }

    public void orderPreparing() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("preparing");
        alert.setContentText("Enter the item id you want to add ");

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
    public void orderReady() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ready");
        alert.setContentText("Enter the item id you want to add ");

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



    public void showTotalSale() {
        displayInfo("Total Sale","aaaaaaaaaa");
    }

    public void ShowPendingOrders() {
        displayInfo("Pending Orders","aaaaaaaaaa");
    }

    public void ShowLoyaltyReport() {
        displayInfo("loyalty redemptions","aaaaaaaaaa");
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

//    private void editData(){
//        firstName.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
//        firstName.setOnEditCommit(event ->{
//            Person person = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            person.setFirstName(event.getNewValue());
//            System.out.println(person.getLastName() + "'s Name was updated to "+ event.getNewValue() +" at row "+ (event.getTablePosition().getRow() + 1));
//        });
//
//        lastName.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
//        lastName.setOnEditCommit(event ->{
//            Person person = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            person.setLastName(event.getNewValue());
//            System.out.println(person.getFirstName() + "'s Last Name was updated to "+ event.getNewValue() +" at row "+ (event.getTablePosition().getRow() + 1));
//        });
//
//        origin.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
//        origin.setOnEditCommit(event ->{
//            Person person = event.getTableView().getItems().get(event.getTablePosition().getRow());
//            person.setOrigin(event.getNewValue());
//            System.out.println("Origin was updated to "+ event.getNewValue() +" at row "+ (event.getTablePosition().getRow() + 1));
//        });
//    }
}

