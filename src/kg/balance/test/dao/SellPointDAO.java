package kg.balance.test.dao;

import kg.balance.test.inputs.UpdateUser;
import kg.balance.test.models.SellPoint;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class SellPointDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public SellPointDAO (SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public SellPoint create () {
        return null;
    }

    @Transactional(readOnly = true)
    public SellPoint get (Long id) {
        return null;
    }

    @Transactional(readOnly = true)
    public List<SellPoint> getAll () {
        return null;
    }

    @Transactional
    public SellPoint update (Long id, UpdateUser userData) {
        return null;
    }

    @Transactional
    public void delete (Long id) {}
}
