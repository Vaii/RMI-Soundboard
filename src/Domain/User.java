package Domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.jongo.marshall.jackson.oid.MongoObjectId;

/**
 * Created by Vai on 6/11/17.
 */
public class User {

    private static final String NAME = "name";
    private static final String PASSWORD = "password";

    @MongoObjectId
    private String _id;

    public User(){

    }

    @JsonCreator
    public User(@JsonProperty(NAME) String name,
                @JsonProperty(PASSWORD) String password){
        this.name = name;
        this.password = password;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(PASSWORD)
    public String getPassword() {
        return password;
    }

    private String name;
    private String password;

    private String IP;

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public boolean isController() {
        return isController;
    }

    public void setController(boolean controller) {
        isController = controller;
    }

    private boolean isController;


}
