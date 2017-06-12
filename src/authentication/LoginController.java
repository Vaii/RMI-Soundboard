package authentication;

import Controller.ControllerController;
import Domain.Crypt;
import Domain.User;
import authentication.repository.ILoginContext;
import authentication.repository.LoginMongoContext;
import authentication.repository.LoginRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class LoginController implements Initializable {

    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    Button login;
    @FXML
    RadioButton client;
    @FXML
    RadioButton controller;
    @FXML
    Label noAccount;

    private LoginRepository loginRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginRepository = new LoginRepository(new LoginMongoContext());
        login.setOnAction(this::tryLogin);
        noAccount.setOnMouseClicked(this::registerAccount);
    }

    private void registerAccount(MouseEvent mouseEvent) {
        try{
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            Parent root = loader.load();
            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    private void tryLogin(ActionEvent actionEvent) {

        if(!username.getText().isEmpty() && !password.getText().isEmpty()){
            String currentUsername = username.getText();
            String currentPassword = Crypt.hashPassword(password.getText());

            User currentUser = loginRepository.loginUser(currentUsername, currentPassword);

            if(currentUser != null){
                if(client.isSelected() && controller.isSelected()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Both functions selected");
                    alert.setHeaderText("Both functions are selected");
                    alert.setContentText("In order to use the application please select only one function");
                    alert.showAndWait();
                }
                else if(client.isSelected()){
                    currentUser.setController(false);

                    try {
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Client/Client.fxml"));
                        Parent root = loader.load();
                        stage.setTitle("Client");
                        stage.setScene(new Scene(root));
                        stage.show();
                        Stage thisStage = (Stage)login.getScene().getWindow();
                        thisStage.close();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
                else if(controller.isSelected()){
                    currentUser.setController(true);

                    try{
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Controller/Controller.fxml"));
                        Parent root = loader.load();
                        stage.setTitle("Controller");
                        stage.setScene(new Scene(root));
                        stage.show();
                        Stage thisStage = (Stage)login.getScene().getWindow();
                        thisStage.close();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
                else if(!client.isSelected() && !controller.isSelected()){
                    Alert alert =  new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("please select a single function");
                    alert.setTitle("Something went wrong");
                    alert.setContentText("Please select 1 function");
                    alert.showAndWait();
                }
                else{
                    Alert alert =  new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("please select a function");
                    alert.setTitle("Something went wrong");
                    alert.setContentText("Please select the desired function");
                    alert.showAndWait();
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Account unknown");
                alert.setHeaderText("No such login found");
                alert.setContentText("Please enter your login details again");
                alert.showAndWait();
                client.setSelected(false);
                controller.setSelected(false);
                username.clear();
                password.clear();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error logging in");
            alert.setHeaderText("Encountered an error logging in");
            alert.setContentText("Please fill in all the information");
            alert.showAndWait();
        }


    }
}
