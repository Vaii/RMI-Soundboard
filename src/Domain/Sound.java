package Domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.io.Serializable;

/**
 * Created by Vai on 6/12/17.
 */
public class Sound implements Serializable {


    @MongoObjectId
    private String _id;


    private static final String NAME = "name";
    private static final String FILE = "file";

    private String name;
    private byte[] file;

    public Sound(){

    }

    @JsonCreator
    public Sound(@JsonProperty(NAME) String name,
                 @JsonProperty(FILE) byte[] file){
        this.name = name;
        this.file = file;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public byte[] getFile() {
        return file;
    }

    @Override
    public String toString() {
        return name;
    }
}
