package Controller;

import Controller.repository.ControllerMongoContext;
import Controller.repository.ControllerRepository;
import Domain.Sound;
import Domain.SoundboardCommunicator;
import Shared.SoundEvent;
import com.sun.deploy.cache.Cache;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private ControllerRepository cRepo;

    private ArrayList<Sound> soundList = new ArrayList<>();

    private ObservableList<Sound> comboboxSounds = FXCollections.observableArrayList();

    private SoundboardCommunicator communicator;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cRepo = new ControllerRepository(new ControllerMongoContext());

        local.setOnAction(this::playLocalSound);
        browse.setOnAction(this::uploadFile);
        remote.setOnAction(this::remoteSound);

        soundList.addAll(cRepo.getAllSounds());
        comboboxSounds.addAll(soundList);

        sounds.getItems().addAll(comboboxSounds);
        sounds.getSelectionModel().selectFirst();

        try{
            this.communicator = new SoundboardCommunicator(this);
        }
        catch(RemoteException ex){
            Logger.getLogger(ControllerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        connectToPublisher();
    }

    private void remoteSound(ActionEvent actionEvent) {
        Sound soundToPlay = soundList.get(sounds.getSelectionModel().getSelectedIndex());

        SoundEvent soundEvent = new SoundEvent(soundToPlay);

        communicator.broadcast("Sound", soundEvent);
    }

    private void connectToPublisher() {

        communicator.connectToPublisher();
        communicator.register("Sound");
        communicator.register("Volume");
    }

    private void uploadFile(ActionEvent actionEvent) {

        FileChooser fChooser = new FileChooser();
        fChooser.setTitle("Select a sound file");
        fChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Sound files", "*.mp3", ".wav")
        );

        File file = fChooser.showOpenDialog(local.getScene().getWindow());
        try{
            FileInputStream fileInput = new FileInputStream(file);
            byte[] soundByte = inputStreamToByteArray(fileInput);

            Sound sound = new Sound(file.getName(), soundByte);

            cRepo.uploadSound(sound);

        }
        catch(IOException ex){
            Logger.getLogger(ControllerController.class.getName()).log(Level.SEVERE, null, ex);
        }




    }

    public byte[] inputStreamToByteArray(InputStream inputStream) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while((bytesRead = inputStream.read(buffer)) > 0){
            baos.write(buffer,0,bytesRead);
        }
        return baos.toByteArray();
    }


    private void playLocalSound(ActionEvent actionEvent) {

        Sound soundToPlay = soundList.get(sounds.getSelectionModel().getSelectedIndex());

        try{
            File tempMp3 = File.createTempFile("sound", "mp3", Cache.getActiveCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(soundToPlay.getFile());
            fos.close();

            Media media = new Media(tempMp3.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();

        }
        catch(IOException ex){
            Logger.getLogger(ControllerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
