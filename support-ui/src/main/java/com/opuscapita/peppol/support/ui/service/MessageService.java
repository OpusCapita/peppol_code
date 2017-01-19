package com.opuscapita.peppol.support.ui.service;


import com.opuscapita.peppol.support.ui.domain.Customer;
import com.opuscapita.peppol.support.ui.domain.FileInfo;
import com.opuscapita.peppol.support.ui.domain.Message;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import com.opuscapita.peppol.support.ui.dto.MessageDTO;
import com.opuscapita.peppol.support.ui.dto.PeppolEvent;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.27.11
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */

public interface MessageService extends Service<Message> {

    public Message createMessage(PeppolEvent peppolEvent);

    @Deprecated
    public Message createMessage(PeppolEvent peppolEvent, FileInfo fileInfo);

    public Message getByPeppolEvent(PeppolEvent peppolEvent);

    /**
     * START of Outbound API
     */

    public List<MessageDTO> getAllMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getInvalidMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getFailedMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getSentMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getProcessingMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getReprocessedMessages(TableParameters tableParameters) throws HibernateException;

    public int getAllMessageCount() throws HibernateException;

    public int getInvalidMessageCount() throws HibernateException;

    public int getFailedMessageCount() throws HibernateException;

    public int getSentMessageCount() throws HibernateException;

    public int getProcessingMessageCount() throws HibernateException;

    public int getReprocessedMessageCount() throws HibernateException;

    /**
     *  END of Outbound API
     */

    /**
     * START of Inbound API
     */

    public List<MessageDTO> getAllInboundMessages(TableParameters tableParameters) throws HibernateException;

    public List<MessageDTO> getInvalidInboundMessages(TableParameters tableParameters) throws HibernateException;

    public int getAllInboundMessageCount() throws HibernateException;

    public int getInvalidInboundMessageCount() throws HibernateException;

    /**
     * END of Inbound API
     */

    public String getErrorMessage(String filePath);

    public List<MessageDTO> getCustomerMessages(Integer customerId, TableParameters tableParameters);

    public int getCustomerMessageCount(Integer customerId);

    public int getCustomerMessageCount(Customer customer);

    public void resolveManually(Integer messageId, String comment);

    public int  getAllOutboundMessageCount() throws HibernateException;
}
