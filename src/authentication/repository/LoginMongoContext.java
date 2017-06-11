package authentication.repository;

import Domain.DataSource;
import Domain.User;
import org.jongo.MongoCollection;

/**
 * Created by Vai on 6/11/17.
 */
public class LoginMongoContext implements ILoginContext {
    @Override
    public User loginUser(String userName, String password) {

        MongoCollection users = DataSource.connect().getCollection("Users");

        User currentUser = users.findOne("{ name:#, password:#}", userName, password).as(User.class);

        if(currentUser != null){
            return currentUser;
        }

        return null;
    }

    @Override
    public boolean registerUser(String userName, String password) {

        MongoCollection users = DataSource.connect().getCollection("Users");

        User takenUsername = users.findOne("{ name:# }", userName).as(User.class);

        if(takenUsername != null){
            return false;
        }
        else{
            users.insert("{ name:#, password:#}", userName, password);
            return true;
        }
    }
}
