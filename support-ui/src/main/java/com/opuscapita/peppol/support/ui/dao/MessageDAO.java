package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.Customer;
import com.opuscapita.peppol.support.ui.domain.Message;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import com.opuscapita.peppol.support.ui.dto.MessageDTO;
import com.sun.istack.Nullable;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.26.11
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public interface MessageDAO extends DAO<Message> {
    public
    @Nullable
    Message getByDocument(Customer sender, String invoiceNumber) throws HibernateException;

    public
    @Nullable
    Message getByFilename(String filename) throws HibernateException;

    public List<MessageDTO> getAllOutboundMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getInvalidMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getFailedMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getSentMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getProcessingMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getReprocessedMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getCustomerMessages(Integer customerId, TableParameters tableParameters) throws HibernateException;

    public int getCustomerMessageCount(Customer customer) throws HibernateException;

    public int getAllMessageCount() throws HibernateException;

    public int getInvalidMessageCount() throws HibernateException;

    public int getFailedMessageCount() throws HibernateException;

    public int getSentMessageCount() throws HibernateException;

    public int getProcessingMessageCount() throws HibernateException;

    public int getReprocessedMessageCount() throws HibernateException;

    public void resolveManually(Message message, String comment) throws HibernateException;

    public void resolveManually(Integer messageId, String comment) throws HibernateException;


    public List<MessageDTO> getAllInboundMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getInvalidInboundMessages(TableParameters tableParameters) throws HibernateException;

    public int getAllInboundMessageCount() throws HibernateException;

    public int getInvalidInboundMessageCount() throws HibernateException;

}
