package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.Customer;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.26.11
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public interface CustomerDAO extends DAO<Customer> {
    public Customer getByCustomerId(String customerId) throws HibernateException;

    public List<Customer> getAll(TableParameters tableParameters) throws HibernateException;

    public int getCustomerCount() throws HibernateException;
}
