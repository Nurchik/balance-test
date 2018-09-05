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
import java.util.Optional;

interface UserServiceInterface {
    public User getUser (Long id) throws UserNotFound;
    public User getUser (String name) throws UserNotFound;
    public List<User> getUsers ();
    public User createUser (User user);
    public User updateUser (Long userId, User userData) throws UserNotFound;
    public void deleteUser (Long id) throws UserNotFound;

}

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    PasswordEncoder passwordEncoder;

    private BalanceDAO<User> userRepository;

    @Autowired
    public void setUserRepository(BalanceDAO<User> userRepository) {
        this.userRepository = userRepository;
        userRepository.setEntityClass(User.class);
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) throws UserNotFound {
        return userRepository.get(id).orElseThrow(UserNotFound::new);
    }

    @Transactional(readOnly = true)
    public User getUser(String name) throws UserNotFound {
        return userRepository.getByName(name).orElseThrow(UserNotFound::new);
    }

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.list().orElse(new ArrayList<User>());
    }

    @Transactional
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // хэшируем пароль
        return userRepository.add(user);
    }

    /*
    * Т.к. в данном методе нам нужно изменить только 3 поля, нам нужно получить объект из БД, затем внести изменения в этот объект
    * Нам не нужно вызывать метод userRepository.update (который мержит объекты по id), т.к. полученный объект является persisted instance
    * т.е. этот объект сохранен в кэше сессии (Hibernate Session) и при изменении данных с помощью сеттеров, эти изменения сохранятся в БД
    * по окончании транзакции.
    * */
    @Transactional
    public User updateUser(Long userId, User userData) throws UserNotFound {
        User user = userRepository.get(userId).orElseThrow(UserNotFound::new);
        user.setFullName(userData.getFullName());
        user.setPhoneNumber(userData.getPhoneNumber());
        user.setIsAdmin(userData.getIsAdmin());
        return user;
    }

    @Transactional
    public void deleteUser(Long id) throws UserNotFound {
        User user = userRepository.get(id).orElseThrow(UserNotFound::new);
        userRepository.delete(user);
    }
}
