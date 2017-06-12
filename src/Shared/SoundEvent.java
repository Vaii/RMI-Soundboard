package Shared;

import Domain.Sound;

import java.io.Serializable;

/**
 * Created by Vai on 6/12/17.
 */
public class SoundEvent implements Serializable {

    private final Sound sound;

    public SoundEvent(Sound sound){
        this.sound = sound;
    }

    public Sound getSound() {
        return sound;
    }
}
