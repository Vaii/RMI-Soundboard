package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Vai on 6/11/17.
 */
public class ControllerController implements Initializable {

    @FXML
    Button remote, local, browse, upload, connect;

    @FXML
    ComboBox sounds;

    @FXML
    TextField ipField, portField;

    @FXML
    Slider volumeSlider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
