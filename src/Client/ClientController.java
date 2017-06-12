package Client;

import Domain.SoundboardCommunicator;
import Shared.SoundEvent;
import com.sun.deploy.cache.Cache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        communicator.connectToPublisher();

        communicator.subscribe("Sound");

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
