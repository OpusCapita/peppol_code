package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.Customer;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.26.11
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class CustomerDAOImpl implements CustomerDAO {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public void add(Customer customer) {
        sessionFactory.getCurrentSession().save(customer);
    }

    @Override
    public List<Customer> getAll() {
        Query query = sessionFactory.getCurrentSession().createQuery("from Customer");
        return query.list();
    }

    @Override
    public Customer getById(Integer id) {
        return (Customer) sessionFactory.getCurrentSession().get(Customer.class, id);
    }

    @Override
    public void update(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("update Customer set name = :name, outboundEmails = :outboundEmails, inboundEmails = :inboundEmails," +
                " contactPerson = :contactPerson, responsiblePerson = :responsiblePerson where id = :id");
        query
                .setParameter("id", customer.getId())
                .setParameter("name", customer.getName())
                .setParameter("outboundEmails", customer.getOutboundEmails())
                .setParameter("inboundEmails", customer.getInboundEmails())
                .setParameter("contactPerson", customer.getContactPerson())
                .setParameter("responsiblePerson", customer.getResponsiblePerson());
        query.executeUpdate();
    }

    @Override
    public void delete(Customer customer) {
        sessionFactory.getCurrentSession().delete(customer);
    }

    @Override
    public void delete(Integer id) {
        Customer customer = (Customer) sessionFactory.getCurrentSession().get(Customer.class, id);
        sessionFactory.getCurrentSession().delete(customer);
    }

    @Override
    public Customer getByCustomerId(String customerId) throws HibernateException {
        Customer customer = (Customer) sessionFactory.getCurrentSession().createQuery("from Customer where identifier = :customerId")
                .setString("customerId", customerId)
                .uniqueResult();
        return customer;
    }


    @Override
    public List<Customer> getAll(TableParameters tableParameters) throws HibernateException {
        Criteria criteria = HibernateUtil.createCriteria(tableParameters, Customer.class, sessionFactory.getCurrentSession());

        List<Customer> customers = criteria.list();
        return customers;
    }

    @Override
    public int getCustomerCount() throws HibernateException {
        return ((Long) sessionFactory.getCurrentSession().createQuery("select count(*) from Customer").uniqueResult()).intValue();
    }


}
