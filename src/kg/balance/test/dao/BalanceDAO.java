package kg.balance.test.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BalanceDAO<T> {
    public Optional<T> get (Long id);
    public Optional<T> getByName (String name);
    public Optional<List<T>> list ();
    public T add (T entity);
    public void update (T entity);
    public void delete (T entity);
}