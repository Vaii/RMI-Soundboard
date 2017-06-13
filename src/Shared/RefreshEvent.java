package Shared;

import java.io.Serializable;

/**
 * Created by Vai on 6/12/17.
 */
public class RefreshEvent implements Serializable{

    private boolean newItem;

    public RefreshEvent(Boolean newItem){
        this.newItem = newItem;

    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }
}
