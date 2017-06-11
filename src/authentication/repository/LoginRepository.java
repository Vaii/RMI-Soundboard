package authentication.repository;

import Domain.User;

/**
 * Created by Vai on 6/11/17.
 */
public class LoginRepository {

    private ILoginContext loginContext;
    public LoginRepository(ILoginContext loginContext){
        this.loginContext = loginContext;
    }

    public User loginUser(String userName, String password){
        return loginContext.loginUser(userName, password);
    }

    public boolean registerUser(String userName, String password){
        return loginContext.registerUser(userName, password);
    }
}
