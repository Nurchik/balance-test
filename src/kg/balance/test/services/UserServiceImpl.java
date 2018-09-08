package kg.balance.test.services;


import com.sun.org.apache.xpath.internal.operations.Bool;
import kg.balance.test.dao.BalanceDAO;
import kg.balance.test.dao.BalanceDAOImpl;
import kg.balance.test.exceptions.UniqueConstraintViolation;
import kg.balance.test.exceptions.UserNotFound;
import kg.balance.test.models.User;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    private BalanceDAOImpl<User> userRepository;

    @Autowired
    public void setUserRepository(BalanceDAOImpl<User> userRepository) {
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
    public User createUser(User user) throws UniqueConstraintViolation {
        if (user.getIsAdmin() == null) {
            user.setIsAdmin(false); // по-умолчанию, ставим false
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // хэшируем пароль
        try {
            return userRepository.add(user);
        } catch (PersistenceException ex) {
            // Это, конечно, слишком неправильно все ConstraintViolations приводить к UniqueConstraintViolation. Но, в этой демке, будем париться только при уникальность ;-)
            if (ex.getCause().getClass() == ConstraintViolationException.class) {
                throw new UniqueConstraintViolation("name");
            }
            throw ex;
        }
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
        if (userData.getFullName() != null) {
            user.setFullName(userData.getFullName());
        }
        if (userData.getPhoneNumber() != null) {
            user.setPhoneNumber(userData.getPhoneNumber());
        }
        if (userData.getIsAdmin() != null) {
            user.setIsAdmin(userData.getIsAdmin());
        }
        /*
        *  Здесь мы вызываем метод update для persisted-сущности user, хотя наши изменения будут внесены в БД, когда завершится @Transactional метод
        *  Но, т.к. во время тестов у нас есть @Transactional-аннотированный тестовый метод, то Транзакция данного метода будет выполняться в рамках транзакции Тестового метода (из-за propagation=REQUIRED), и мы не сможем в тестовом методе получать изменения сразу после завершения этого метода
        *  userRepository.update пытается сделать merge persisted-сущности (что ничего не меняет) и, самое главное, делает flush сессии
        * */
        userRepository.update(user);
        return user;
    }

    @Transactional
    public void deleteUser(Long id) throws UserNotFound {
        User user = userRepository.get(id).orElseThrow(UserNotFound::new);
        userRepository.delete(user);
    }
}
