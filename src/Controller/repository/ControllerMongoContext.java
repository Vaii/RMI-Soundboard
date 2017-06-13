package Controller.repository;

import Domain.DataSource;
import Domain.Sound;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;

/**
 * Created by Vai on 6/12/17.
 */
public class ControllerMongoContext implements IControllerContext {

    @Override
    public boolean uploadSound(Sound sound) {

        MongoCollection sounds = DataSource.connect().getCollection("Sounds");

        Sound doesExist = sounds.findOne("{name:#}", sound.getName()).as(Sound.class);

        if(doesExist != null){
            return false;
        }
        else{
            sounds.save(sound);
            return true;
        }
    }

    @Override
    public boolean removeSound(Sound sound) {
        MongoCollection sounds = DataSource.connect().getCollection("Sounds");

        sounds.remove("{name:#}", sound.getName());

        return true;
    }

    @Override
    public ArrayList<Sound> loadAllSounds() {

        MongoCollection sounds = DataSource.connect().getCollection("Sounds");
        MongoCursor<Sound> allSounds = sounds.find().as(Sound.class);

        ArrayList<Sound> currentSounds = new ArrayList<>();

        while(allSounds.hasNext()){
            currentSounds.add(allSounds.next());
        }

        return currentSounds;
    }
}
