package Controller.repository;

import Domain.Sound;

import java.util.ArrayList;

/**
 * Created by Vai on 6/12/17.
 */
public class ControllerRepository {

    private  IControllerContext controllerContext;

    public ControllerRepository(IControllerContext controllerContext){
        this.controllerContext = controllerContext;
    }

    public boolean uploadSound(Sound sound){
        if(controllerContext.uploadSound(sound)){
            return true;
        }
        else{
            return false;
        }
    }

    public ArrayList<Sound> getAllSounds(){
        return controllerContext.loadAllSounds();
    }
}
