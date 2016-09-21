package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.SmtpListTmp;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class SmtpDAOImpl implements SmtpDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void add(SmtpListTmp smtp) throws HibernateException {
        sessionFactory.getCurrentSession().save(smtp);
    }

    @Override
    public List<SmtpListTmp> getAll() throws HibernateException {
        Query query = sessionFactory.getCurrentSession().createQuery("from SmtpListTmp");
        return query.list();
    }

    @Override
    public SmtpListTmp getById(Integer id) throws HibernateException {
        return (SmtpListTmp) sessionFactory.getCurrentSession().get(SmtpListTmp.class, id);
    }

    @Override
    public SmtpListTmp getBySender(String senderId) throws HibernateException {
        Query query = sessionFactory.getCurrentSession().createQuery("from SmtpListTmp where senderId = :senderId");
        query.setParameter("senderId", senderId);
        List<SmtpListTmp> result = query.list();
        if (result.size() > 0) {
            return (SmtpListTmp) query.list().get(0);
        } else {
            return null;
        }
    }

    @Override
    public void update(SmtpListTmp smtp) throws HibernateException {
        sessionFactory.getCurrentSession().update(smtp);
    }

    @Override
    public void delete(Integer id) throws HibernateException {
        SmtpListTmp smtp = (SmtpListTmp) sessionFactory.getCurrentSession().get(SmtpListTmp.class, id);
        sessionFactory.getCurrentSession().delete(smtp);
    }

    @Override
    public void delete(SmtpListTmp smtp) throws HibernateException {
        sessionFactory.getCurrentSession().delete(smtp);
    }
}
