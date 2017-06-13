package Controller.repository;

import Domain.Sound;

import java.util.ArrayList;

/**
 * Created by Vai on 6/12/17.
 */
public interface IControllerContext {

    boolean uploadSound(Sound sound);
    boolean removeSound(Sound sound);
    ArrayList<Sound> loadAllSounds();
}
