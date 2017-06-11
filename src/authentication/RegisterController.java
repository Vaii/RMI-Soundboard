package authentication;

import Domain.Crypt;
import authentication.repository.LoginMongoContext;
import authentication.repository.LoginRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Vai on 6/11/17.
 */
public class RegisterController implements Initializable {

    private LoginRepository registerRepo;
    @FXML
    Button register;
    @FXML
    TextField username;
    @FXML
    TextField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        register.setOnAction(this::registerAccount);
        registerRepo = new LoginRepository(new LoginMongoContext());

    }

    private void registerAccount(ActionEvent actionEvent) {

        if(!username.getText().isEmpty() && !password.getText().isEmpty()){
            String newUserUsername = username.getText();
            String newUserPassword = Crypt.hashPassword(password.getText());

            if(registerRepo.registerUser(newUserUsername, newUserPassword)){
                Stage stage = (Stage)register.getScene().getWindow();
                stage.close();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error registering");
                alert.setHeaderText("Something went wrong trying to register");
                alert.setContentText("This account already exists, please pick another username");
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please fill out every field");
            alert.setContentText("Please re-enter your details");
            alert.showAndWait();
        }

    }
}
