package Controller;

import Controller.repository.ControllerMongoContext;
import Controller.repository.ControllerRepository;
import Domain.Sound;
import Domain.SoundboardCommunicator;
import Shared.RefreshEvent;
import Shared.SoundEvent;
import com.sun.deploy.cache.Cache;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.Buffer;
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
    Button remote, local, browse, remove, connect;

    @FXML
    ComboBox<Sound> sounds;

    @FXML
    TextField ipField, portField;

    @FXML
    Slider volumeSlider;

    private ControllerRepository cRepo;

    private ArrayList<Sound> soundList;

    private ObservableList<Sound> observableList = FXCollections.observableArrayList();

    private SoundboardCommunicator communicator;

    public ObservableList<Sound> itemsToObserve(){
        return FXCollections.unmodifiableObservableList(observableList);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cRepo = new ControllerRepository(new ControllerMongoContext());

        soundList = new ArrayList<>();

        local.setOnAction(this::playLocalSound);
        browse.setOnAction(this::uploadFile);
        remote.setOnAction(this::remoteSound);
        remove.setOnAction(this::removeSound);
        volumeSlider.setOnMouseReleased(this::changeVolume);

        connect.setOnAction(this::connectToPublisher);

        soundList.addAll(cRepo.getAllSounds());

        observableList.addAll(soundList);
        sounds.setItems(observableList);
        sounds.getSelectionModel().selectFirst();

        try{
            this.communicator = new SoundboardCommunicator(this);
        }
        catch(RemoteException ex){
            Logger.getLogger(ControllerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void changeVolume(MouseEvent mouseEvent) {

        int value = (int)volumeSlider.getValue();
        double percentage = (double)value / 100;

        float systemVolume = 7.5f * (float)percentage;

        setOutputVolume(systemVolume);
    }

    private void removeSound(ActionEvent actionEvent) {
        cRepo.removeSound(soundList.get(sounds.getSelectionModel().getSelectedIndex()));

        if(communicator.isConnected()){
            RefreshEvent refreshEvent = new RefreshEvent(true);
            communicator.broadcast("Refresh", refreshEvent);
        }
    }

    public void refreshSoundList(String property, RefreshEvent refreshEvent){

        Platform.runLater(() -> {
            ArrayList<Sound> newSounds = new ArrayList<>();
            newSounds.addAll(cRepo.getAllSounds());

            sounds.getItems().clear();
            sounds.getItems().addAll(newSounds);
            soundList = newSounds;
        });

    }

    private void remoteSound(ActionEvent actionEvent) {
        Sound soundToPlay = soundList.get(sounds.getSelectionModel().getSelectedIndex());

        SoundEvent soundEvent = new SoundEvent(soundToPlay);

        communicator.broadcast("Sound", soundEvent);
    }

    private void connectToPublisher(ActionEvent actionEvent) {

        if(!ipField.getText().isEmpty() && !portField.getText().isEmpty()){
            communicator.connectToPublisher(ipField.getText(), Integer.parseInt(portField.getText()));

            communicator.register("Sound");
            communicator.register("Volume");
            communicator.register("Refresh");

            communicator.subscribe("Refresh");

            if(communicator.isConnected()){
                ipField.setDisable(true);
                portField.setDisable(true);
                connect.setDisable(true);
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information missing");
            alert.setHeaderText("Missing IP-Address or Port number");
            alert.setContentText("Please fill in the required fields");
            alert.showAndWait();
        }
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

            if(communicator.isConnected()){
                RefreshEvent refreshEvent = new RefreshEvent(true);
                communicator.broadcast("Refresh", refreshEvent);
            }
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

    public static void setOutputVolume(float value){

        String command = "set volume " + value;
        try{
            ProcessBuilder pb = new ProcessBuilder("osascript", "-e", command);
            pb.directory(new File("/usr/bin"));
            StringBuffer output = new StringBuffer();
            Process p = pb.start();
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;

            while((line = reader.readLine()) != null){
                output.append(line + "\n");
            }
            System.out.println("output");
        }
        catch(Exception e){
            Logger.getLogger(ControllerController.class.getName()).log(Level.SEVERE, null , e);
        }
    }

}
