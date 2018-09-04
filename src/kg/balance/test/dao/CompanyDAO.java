package kg.balance.test.dao;

import kg.balance.test.inputs.UpdateUser;
import kg.balance.test.models.Company;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CompanyDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public CompanyDAO (SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public Company create () {
        return null;
    }

    @Transactional(readOnly = true)
    public Company get (Long id) {
        return null;
    }

    @Transactional(readOnly = true)
    public List<Company> getAll () {
        return null;
    }

    @Transactional
    public Company update (Long id, UpdateUser userData) {
        return null;
    }

    @Transactional
    public void delete (Long id) {}
}
