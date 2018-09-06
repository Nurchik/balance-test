package kg.balance.test.services;

import kg.balance.test.dao.BalanceDAO;
import kg.balance.test.exceptions.UserNotFound;
import kg.balance.test.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public interface UserService {
    public User getUser (Long id) throws UserNotFound;
    public User getUser (String name) throws UserNotFound;
    public List<User> getUsers ();
    public User createUser (User user);
    public User updateUser (Long userId, User userData) throws UserNotFound;
    public void deleteUser (Long id) throws UserNotFound;

}
