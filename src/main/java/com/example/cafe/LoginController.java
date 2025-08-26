package com.example.cafe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField nameField;

    @FXML
    private TextField idFiled;

    @FXML
    private PasswordField passwordFild;

    @FXML
    private Label warning;

    String id;
    String password;

    public void login(ActionEvent event) {

        try {
            id = idFiled.getText();
            password= passwordFild.getText();

            if(id.equals("admin") && password.equals("admin")) {
                switchTo(event,"Admin.fxml");
            }
            else if(id.equals("23") && password.equals("1111")) {
                switchTo(event,"Home.fxml");
            }
            else {
                warning.setText("You must be 18+");
            }
        }
        catch (NumberFormatException e){
            warning.setText("enter only numbers plz");
        }
        catch (Exception e) {
            warning.setText("error");
        }
    }

    public void Register(ActionEvent event) {

        try {
            id = idFiled.getText();
            password= passwordFild.getText();


        }
        catch (NumberFormatException e){
            warning.setText("enter only numbers plz");
        }
        catch (Exception e) {
            warning.setText("error");
        }
    }






    public void switchToRegister(ActionEvent event) throws IOException {switchTo(event,"Register.fxml");}

    public void switchToLogin(ActionEvent event) throws IOException {switchTo(event,"Login.fxml");}

    private void switchTo (ActionEvent event,String destination) throws IOException {
        root = FXMLLoader.load(getClass().getResource(destination));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

