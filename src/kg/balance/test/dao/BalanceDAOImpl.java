package kg.balance.test.dao;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE) // Чтобы каждый раз создавался новый инстанс (т.к. это дженерик класс)
public class BalanceDAOImpl<T> implements BalanceDAO<T> {

    @Autowired
    SessionFactory sessionFactory;

    private Class<T> entityClass;

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Optional<T> get(Long id) {
        Session session = sessionFactory.getCurrentSession();
        T entity = session.get(entityClass, id);
        // Переводим сущность в состояние detached, чтобы при изменении данных, было необходимо делать merge (explicit merge and session flush)
        //session.evict(entity);
        return Optional.<T>ofNullable(entity);
    }

    public Optional<T> getByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.<T>ofNullable(session.byNaturalId(entityClass).using("name", name).load());
    }

    public Optional<List<T>> list() {
        Session session = sessionFactory.getCurrentSession();
        return Optional.<List<T>>ofNullable(session.createQuery("from " + entityClass.getName(), entityClass).getResultList());
    }

    public T add(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(entity);
        session.flush();
        return entity;
    }

    public void update(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(entity);
        session.flush();
    }

    public void delete(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(entity);// на всякий случай, переводим в persisted state.
        session.remove(entity);
        session.flush();
    }
}