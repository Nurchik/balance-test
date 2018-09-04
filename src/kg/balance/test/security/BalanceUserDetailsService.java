package kg.balance.test.security;

import kg.balance.test.dao.UserDAO;
import kg.balance.test.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class BalanceUserDetailsService implements UserDetailsService {

    @Autowired
    UserDAO userRepository;

    public UserDetails loadUserById (Long id) {
        User user = userRepository.getUser(id);
        if (user == null) {
            return null;
        }
        UserPrincipal up = new UserPrincipal(user);
        return up;
    }

    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.getUser(username);
        if (user == null) {
            return null;
        }
        UserPrincipal up = new UserPrincipal(user);
        return up;
    }
}
