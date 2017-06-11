package Domain;

/**
 * Created by Vai on 6/11/17.
 */
public class Config {

    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Config.user = user;
    }
}
