package com.example.cafe;

import com.example.cafe.student.IStudent;
import com.example.cafe.student.StudentManager;
import com.example.cafe.userManager.IUserManager;
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

    IUserManager<IStudent> studentManager = new StudentManager();
    IStudent loggedin;

    String id;
    String name;
    String password;

    public void login(ActionEvent event) {

        try {
            id = idFiled.getText();
            password= passwordFild.getText();

            if(id.equals("admin") && password.equals("admin")) {
                switchTo(event,"Admin.fxml");
            }
            else if(studentManager.login(id,password)) {
                switchToHome(event);
            }
            else {
                warning.setText("User not found");
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
            name = nameField.getText();
            id = idFiled.getText();
            password= passwordFild.getText();
            if (studentManager.register(name,id,password)){
                login(event);

            }


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

    private void switchToHome (ActionEvent event) throws IOException {
        loggedin= studentManager.getLoggedInUser();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        root = loader.load();

        HomeController homeController = loader.getController();
        homeController.currentStudent(loggedin);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void switchTo (ActionEvent event,String destination) throws IOException {
        root = FXMLLoader.load(getClass().getResource(destination));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

