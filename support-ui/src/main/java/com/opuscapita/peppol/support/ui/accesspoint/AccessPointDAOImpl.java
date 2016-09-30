package com.opuscapita.peppol.support.ui.accesspoint;

import com.opuscapita.peppol.support.ui.dao.HibernateUtil;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by KACENAR1 on 22.12.2015
 */
@Component
public class AccessPointDAOImpl implements AccessPointDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void delete(AccessPoint accessPoint) throws HibernateException {
        sessionFactory.getCurrentSession().delete(accessPoint);
    }

    @Override
    public AccessPoint getByApId(String accessPointId) throws HibernateException {
        return (AccessPoint) sessionFactory.getCurrentSession().createQuery("from AccessPoint where accessPointId = :accessPointId")
                .setString("accessPointId", accessPointId)
                .uniqueResult();
    }

    @Override
    public List<AccessPoint> getAll(TableParameters tableParameters) throws HibernateException {
        Criteria criteria = HibernateUtil.createCriteria(tableParameters, AccessPoint.class, sessionFactory.getCurrentSession());
        return criteria.list();
    }

    @Override
    public void add(AccessPoint accessPoint) throws HibernateException {
        sessionFactory.getCurrentSession().save(accessPoint);
    }

    @Override
    public List<AccessPoint> getAll() throws HibernateException {
        return sessionFactory.getCurrentSession().createQuery("from AccessPoint").list();
    }

    @Override
    public AccessPoint getById(Integer id) throws HibernateException {
        return (AccessPoint) sessionFactory.getCurrentSession().get(AccessPoint.class, id);
    }

    @Override
    public void update(AccessPoint accessPoint) throws HibernateException {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("update AccessPoint set accessPointName = :name, emailList = :emails, contactPerson = :contactPerson " +
                "where id = :id");
        query
                .setParameter("id", accessPoint.getId())
                .setParameter("name", accessPoint.getAccessPointName())
                .setParameter("emails", accessPoint.getEmailList())
                .setParameter("contactPerson", accessPoint.getContactPerson());
        query.executeUpdate();
    }

    @Override
    public void delete(Integer id) throws HibernateException {
        AccessPoint accessPoint = (AccessPoint) sessionFactory.getCurrentSession().get(AccessPoint.class, id);
        sessionFactory.getCurrentSession().delete(accessPoint);
    }

    @Override
    public int getCount() throws HibernateException {
        return ((Long) sessionFactory.getCurrentSession().createQuery("select count(*) from AccessPoint").uniqueResult()).intValue();
    }
}
