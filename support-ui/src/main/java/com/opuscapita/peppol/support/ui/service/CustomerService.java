package com.opuscapita.peppol.support.ui.service;


import com.opuscapita.peppol.support.ui.domain.Customer;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import com.opuscapita.peppol.support.ui.dto.PeppolEvent;
import com.opuscapita.peppol.support.ui.transport.TransportType;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.27.11
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public interface CustomerService extends Service<Customer> {

    void add(PeppolEvent peppolEvent);

    Customer getByCustomerId(String customerId) throws HibernateException;

    void updateSmtpList() throws HibernateException;

    List<Customer> getAll(TableParameters tableParameters) throws HibernateException;

    int getCustomerCount() throws HibernateException;

    String getCustomerEmail(String customerId);

    String getCustomerEmail(String customerId, TransportType direction);
}
