package com.opuscapita.peppol.support.ui.service;


import com.opuscapita.peppol.support.ui.dao.CustomerDAO;
import com.opuscapita.peppol.support.ui.domain.Customer;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import com.opuscapita.peppol.support.ui.dto.PeppolEvent;
import com.opuscapita.peppol.support.ui.transport.TransportType;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.4.12
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private SmtpService smtpService;


    @Override
    public void add(Customer customer) throws HibernateException {
        customerDAO.add(customer);
    }

    @Override
    public void add(PeppolEvent peppolEvent) {
        Customer customer = new Customer();

        customer.setName(peppolEvent.getSenderName());
        customer.setIdentifier(peppolEvent.getSenderId());

        customerDAO.add(customer);
    }

    @Override
    public List<Customer> getAll() throws HibernateException {
        List<Customer> list = customerDAO.getAll();
        return list;
    }

    @Override
    public Customer getById(Integer id) throws HibernateException {
        Customer customer = customerDAO.getById(id);
        return customer;
    }

    @Override
    public Customer getByCustomerId(String customerId) throws HibernateException {
        Customer customer = customerDAO.getByCustomerId(customerId);
        return customer;
    }

    @Override
    public void update(Customer customer) throws HibernateException {
        customerDAO.update(customer);
    }

    @Override
    public void delete(Customer customer) throws HibernateException {
        customerDAO.delete(customer);
    }

    @Override
    public void delete(Integer id) throws HibernateException {
        customerDAO.delete(id);
    }

    @Override
    public void updateSmtpList() throws HibernateException {
        List<Customer> customers = customerDAO.getAll();
        for (Customer customer : customers) {
            customerDAO.update(customer);
        }
    }

    @Override
    public List<Customer> getAll(TableParameters tableParameters) throws HibernateException {
        List<Customer> customers = customerDAO.getAll(tableParameters);
        return customers;
    }

    @Override
    public int getCustomerCount() throws HibernateException {
        return customerDAO.getCustomerCount();
    }

    @Override
    public String getCustomerEmail(String customerId) {
        Customer customer = customerDAO.getByCustomerId(customerId);
        if (customer == null) {
            return "";
        }
        return customer.getOutboundEmails();
    }

    @Override
    public String getCustomerEmail(String customerId, TransportType direction) {
        Customer customer = customerDAO.getByCustomerId(customerId);
        if (customer == null) {
            return "";
        }
        return TransportType.isInbound(direction) ? customer.getInboundEmails() : customer.getOutboundEmails();
    }
}
