package kg.balance.test.dao;

import kg.balance.test.inputs.UpdateUser;
import kg.balance.test.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

interface UserDAOInterface {
    public User addUser (User user);
    public User updateUser (User user);
    public void deleteUser (Long id);
    public User getUser (Long id);
    public User getUser (String username);
    public List<User> listUsers ();
}

@Repository
public class UserDAO implements UserDAOInterface {

    @Autowired
    private SessionFactory sessionFactory;

    public User addUser (User user) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
        return user;
    }

    public User getUser (Long id) {
        return null;
    }

    public User getUser (String username) {
        return null;
    }

    public List<User> listUsers () {
        return null;
    }

    public User updateUser (User user) {
        return null;
    }

    public void deleteUser (Long id) {}
}
