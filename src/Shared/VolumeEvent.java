package Shared;

import java.io.Serializable;

/**
 * Created by Vai on 6/13/17.
 */
public class VolumeEvent implements Serializable {

    private float newVolume;

    public VolumeEvent(float newVolume){
        this.newVolume = newVolume;
    }

    public float getNewVolume() {
        return newVolume;
    }
}
