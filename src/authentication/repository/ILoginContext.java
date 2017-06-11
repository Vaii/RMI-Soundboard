package authentication.repository;

import Domain.User;

/**
 * Created by Vai on 6/11/17.
 */
public interface ILoginContext {

    User loginUser(String userName, String Password);
    boolean registerUser(String userName, String Password);
}
