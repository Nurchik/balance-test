package kg.balance.test.security;

import kg.balance.test.dao.BalanceDAO;
import kg.balance.test.exceptions.UserNotFound;
import kg.balance.test.models.User;
import kg.balance.test.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class BalanceUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    /*
    BalanceDAO<User> userRepository;

    @Autowired
    public void setUserRepository(BalanceDAO<User> userRepository) {
        this.userRepository = userRepository;
        userRepository.setEntityClass(User.class);
    }
    */

    public UserDetails loadUserById (Long id) throws UserNotFound {
        //User user = userRepository.get(id);
        User user = userService.getUser(id);
        UserPrincipal up = new UserPrincipal(user);
        return up;
    }

    public UserDetails loadUserByUsername(String username) {
        //User user = userRepository.getByName(username);
        try {
            User user = userService.getUser(username);
            UserPrincipal up = new UserPrincipal(user);
            return up;
        } catch (UserNotFound ex) {
            return null;
        }
    }
}
