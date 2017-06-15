package Client;

import Controller.ControllerController;
import Domain.SoundboardCommunicator;
import Shared.SoundEvent;
import Shared.VolumeEvent;
import com.sun.deploy.cache.Cache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Vai on 6/12/17.
 */
public class ClientController implements Initializable {

    @FXML
    Label labelView;

    private SoundboardCommunicator communicator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            this.communicator = new SoundboardCommunicator(this);
        }
        catch(RemoteException ex){
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }

        connectToPublisher();
    }

    private void connectToPublisher() {

        communicator.connectToPublisher("localhost", 1099);

        communicator.subscribe("Sound");
        communicator.subscribe("Volume");

    }

    public void requestVolumeChange(String property, VolumeEvent volumeEvent){

        setOutputVolume(volumeEvent.getNewVolume());

        Platform.runLater(() -> labelView.setText("Someone changed your volume!"));
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

    public void requestPlaySound(String property, SoundEvent soundEvent){

        try{
            File tempMp3 = File.createTempFile("Sound", "mp3", Cache.getActiveCacheDir());
            tempMp3.deleteOnExit();;
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(soundEvent.getSound().getFile());
            fos.close();

            Media media = new Media(tempMp3.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);

            player.play();

            Platform.runLater(() ->
                    labelView.setText("Someone played " + soundEvent.getSound().getName() + " on your pc"));

        }
        catch(IOException ex){
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
