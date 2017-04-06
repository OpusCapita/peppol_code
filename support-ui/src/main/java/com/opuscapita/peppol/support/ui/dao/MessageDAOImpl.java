package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.*;
import com.opuscapita.peppol.support.ui.dto.MessageDTO;
import com.sun.istack.Nullable;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.26.11
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class MessageDAOImpl implements MessageDAO {
    private static final Logger logger = Logger.getLogger(MessageDAOImpl.class);
    private final static List<String> SEARCHEABLE_FIELDS = new ArrayList<>(Arrays.asList("sender.identifier", "sender.name", "sender.contactPerson",
            "sender.responsiblePerson", "recipientId", "invoiceNumber", "resolvedComment", "file.filename", "failed.errorMessage"));
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void add(Message message) {
        sessionFactory.getCurrentSession().save(message);
    }

    @Override
    public List<Message> getAll() {
        Query query = sessionFactory.getCurrentSession().createQuery("from Message");
        return query.list();
    }

    @Override
    public Message getById(Integer id) {
        return (Message) sessionFactory.getCurrentSession().get(Message.class, id);
    }

    @Override
    public
    @Nullable
    Message getByDocument(Customer sender, String invoiceNumber) throws HibernateException {
        Message message = (Message) sessionFactory.getCurrentSession().createQuery("from Message " +
                "where sender = :sender and invoiceNumber = :invoiceNumber")
                .setParameter("sender", sender)
                .setString("invoiceNumber", invoiceNumber)
                .uniqueResult();
        return message;
    }

    @Override
    public
    @Nullable
    Message getByFilename(String filename) throws HibernateException {
       /* Message message = (Message) sessionFactory.getCurrentSession()
                .createQuery("from Message " +
                    "left outer join FileInfo as file " +
                    "where file.filename = :filename")
                .setParameter("filename", filename)
                .uniqueResult();*/
        Message message = (Message) sessionFactory.getCurrentSession()
                .createCriteria(Message.class)
                .createAlias("files", "file", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("file.filename", filename))
                .uniqueResult();
        return message;
    }

    @Override
    public void update(Message message) {
        sessionFactory.getCurrentSession().update(message);
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    public void delete(Message message) {
        sessionFactory.getCurrentSession().delete(message);
    }

    @Override
    public void delete(Integer id) {
        Message message = (Message) sessionFactory.getCurrentSession().get(Message.class, id);
        sessionFactory.getCurrentSession().delete(message);
    }

    @Override
    public List<MessageDTO> getCustomerMessages(Integer customerId, TableParameters tableParameters) throws HibernateException {
        Criteria criteria = HibernateUtil.createCriteria(tableParameters, Message.class, sessionFactory.getCurrentSession());
        criteria.createAlias("files", "file", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("file.failedInfo", "failed", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("sender", "sender", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("file.sentInfo", "sent", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = createMessageProjection();
        projectionList.add(Projections.max("sent.timestamp"), "deliveredTimeStamp");
        criteria.setProjection(projectionList);

        criteria.add(Restrictions.eq("sender.id", customerId));

        if (tableParameters.getSearch() != null) {
            addSearchCriteria(criteria, tableParameters.getSearch());
        }
        criteria.setResultTransformer(new AliasToBeanResultTransformer(MessageDTO.class));
        return criteria.list();
    }

    @Override
    public List<MessageDTO> getAllOutboundMessages(final TableParameters tableParameters) throws HibernateException {
        return getAllMessages(tableParameters, Direction.OUT);
    }

    @Override
    public List<MessageDTO> getAllInboundMessages(TableParameters tableParameters) throws HibernateException {
        return getAllMessages(tableParameters, Direction.IN);
    }

    private List<MessageDTO> getAllMessages(TableParameters tableParameters, Direction direction) throws HibernateException {
        Session session = sessionFactory.getCurrentSession();
        session.setCacheMode(CacheMode.GET);

        Criteria criteria = HibernateUtil.createCriteria(tableParameters, Message.class, session);
        criteria.add(Restrictions.eq("direction", direction));
        criteria.createAlias("files", "file", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("sender", "sender", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("file.sentInfo", "sent", JoinType.LEFT_OUTER_JOIN);
        ProjectionList projectionList = createMessageProjection();
        projectionList.add(Projections.max("sent.timestamp"), "deliveredTimeStamp");
        criteria.setProjection(projectionList);

        if (tableParameters.getSearch() != null) {
            criteria.createAlias("file.failedInfo", "failed", JoinType.LEFT_OUTER_JOIN);
            addSearchCriteria(criteria, tableParameters.getSearch());
        }

        criteria.setResultTransformer(new AliasToBeanResultTransformer(MessageDTO.class));
        criteria.setCacheable(true);
        List<MessageDTO> messages = new ArrayList<>();
        try {
            messages = criteria.list();
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public List<MessageDTO> getInvalidMessages(TableParameters tableParameters) throws HibernateException {
        return getErrorMessages(tableParameters, true, Direction.OUT);
    }

    @Override
    public List<MessageDTO> getFailedMessages(TableParameters tableParameters) throws HibernateException {
        return getErrorMessages(tableParameters, false, Direction.OUT);
    }

    @Override
    public List<MessageDTO> getInvalidInboundMessages(TableParameters tableParameters) throws HibernateException {
        return getErrorMessages(tableParameters, true, Direction.IN);
    }

    private List<MessageDTO> getErrorMessages(TableParameters tableParameters, boolean invalid, Direction direction) {
        Criteria criteria = HibernateUtil.createCriteria(tableParameters, Message.class, sessionFactory.getCurrentSession());
        criteria.add(Restrictions.eq("direction", direction));
        if (invalid) {
            setErrorCriteriaParameters(criteria, MessageStatus.invalid);
        } else {
            setErrorCriteriaParameters(criteria, MessageStatus.failed);
        }
        ProjectionList projectionList = createMessageProjection();
        //ERROR - Expression #17 of SELECT list is not in GROUP BY clause and contains nonaggregated column 'peppol.failed
        projectionList.add(Projections.max("failed.errorMessage"), "errorMessage");
        criteria.setProjection(projectionList);
        if (tableParameters.getSearch() != null) {
            criteria.createAlias("file.sentInfo", "sent", JoinType.LEFT_OUTER_JOIN);
            addSearchCriteria(criteria, tableParameters.getSearch());
        }
        criteria.setResultTransformer(new AliasToBeanResultTransformer(MessageDTO.class));
        List<MessageDTO> messageDTOs = criteria.list();
        /*projectionList.add(Projections.rowCount());
        criteria.setProjection(projectionList);
        long count = (Long) criteria.uniqueResult();*/
        return messageDTOs;
    }

    private void setErrorCriteriaParameters(Criteria criteria, MessageStatus status) {
        criteria.add(Restrictions.eq("resolvedManually", false));
        criteria.add(Restrictions.eq("status", status));
        criteria.createAlias("files", "file", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("sender", "sender", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("file.failedInfo", "failed", JoinType.LEFT_OUTER_JOIN);//.add(Restrictions.eq("failed.invalid", invalid));
    }

    @Override
    public List<MessageDTO> getProcessingMessages(TableParameters tableParameters) throws HibernateException {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = HibernateUtil.createCriteria(tableParameters, Message.class, session);
        criteria.add(Restrictions.eq("direction", Direction.OUT));
        criteria.add(Restrictions.eq("status", MessageStatus.processing));
        criteria.createAlias("files", "file", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("sender", "sender", JoinType.LEFT_OUTER_JOIN);
        //criteria.createAlias("file.failedInfo", "failed", JoinType.LEFT_OUTER_JOIN).add(Restrictions.isNull("failed.timestamp"));
        //criteria.createAlias("file.sentInfo", "sent", JoinType.LEFT_OUTER_JOIN).add(Restrictions.isNull("sent.timestamp"));

        ProjectionList projectionList = createMessageProjection();
        criteria.setProjection(projectionList);

        if (tableParameters.getSearch() != null) {
            criteria.createAlias("file.failedInfo", "failed", JoinType.LEFT_OUTER_JOIN);
            addSearchCriteria(criteria, tableParameters.getSearch());
        }

        criteria.setResultTransformer(new AliasToBeanNestedResultTransformer(MessageDTO.class));
        return criteria.list();
    }

    @Override
    public List<MessageDTO> getSentMessages(TableParameters tableParameters) throws HibernateException {
        Criteria criteria = HibernateUtil.createCriteria(tableParameters, Message.class, sessionFactory.getCurrentSession());
        criteria.add(Restrictions.eq("direction", Direction.OUT));

        criteria.createAlias("files", "file", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("sender", "sender", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("file.sentInfo", "sent", JoinType.LEFT_OUTER_JOIN).add(Restrictions.isNotNull("sent.timestamp"));
        ProjectionList projectionList = createMessageProjection();
        projectionList.add(Projections.max("sent.timestamp"), "deliveredTimeStamp");
        criteria.setProjection(projectionList);

        if (tableParameters.getSearch() != null) {
            criteria.createAlias("file.failedInfo", "failed", JoinType.LEFT_OUTER_JOIN);
            addSearchCriteria(criteria, tableParameters.getSearch());
        }

        criteria.setResultTransformer(new AliasToBeanNestedResultTransformer(MessageDTO.class));
        return criteria.list();
    }

    @Override
    public List<MessageDTO> getReprocessedMessages(TableParameters tableParameters) throws HibernateException {
        Criteria criteria = HibernateUtil.createCriteria(tableParameters, Message.class, sessionFactory.getCurrentSession());
        criteria.add(Restrictions.eq("direction", Direction.OUT));

        criteria.createAlias("files", "file", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("sender", "sender", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("status", MessageStatus.reprocessed));
        ProjectionList projectionList = createMessageProjection();
        criteria.setProjection(projectionList);

        if (tableParameters.getSearch() != null) {
            criteria.createAlias("file.sentInfo", "sent", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("file.failedInfo", "failed", JoinType.LEFT_OUTER_JOIN);
            addSearchCriteria(criteria, tableParameters.getSearch());
        }

        criteria.setResultTransformer(new AliasToBeanNestedResultTransformer(MessageDTO.class));
        return criteria.list();
    }

    private Criteria addSearchCriteria(Criteria criteria, String searchCriteria) {
        Disjunction disjunction = Restrictions.disjunction();
        for (String field : SEARCHEABLE_FIELDS) {
            disjunction.add(Restrictions.like(field, "%" + searchCriteria + "%").ignoreCase());
        }
        criteria.add(disjunction);
        return criteria;
    }

    private ProjectionList createMessageProjection() {
        return Projections.projectionList()
                .add(Projections.groupProperty("id"), "id")
                .add(Projections.groupProperty("file.arrivedTimeStamp"), "arrivedTimeStamp")
                .add(Projections.property("direction"), "direction")
                .add(Projections.property("documentType"), "documentType")
                .add(Projections.property("recipientId"), "recipientId")
                .add(Projections.property("invoiceNumber"), "invoiceNumber")
                .add(Projections.property("invoiceDate"), "invoiceDate")
                .add(Projections.property("dueDate"), "dueDate")
                .add(Projections.property("resolvedManually"), "resolvedManually")
                .add(Projections.property("resolvedComment"), "resolvedComment")
                .add(Projections.property("status"), "status")
                .add(Projections.property("sender.identifier"), "senderIdentifier")
                .add(Projections.property("sender.name"), "senderName")
                .add(Projections.max("file.arrivedTimeStamp"), "arrivedTimeStamp")
                .add(Projections.max("file.id"), "fileId")
                .add(Projections.max("file.fileSize"), "fileSize")
                .add(Projections.max("file.filename"), "fileName");
    }

    @Override
    public int getCustomerMessageCount(Customer customer) throws HibernateException {
        return ((Long) sessionFactory.getCurrentSession()
                .createCriteria(Message.class)
                .add(Restrictions.eq("sender", customer))
                .setProjection(Projections.rowCount())
                .uniqueResult()).intValue();
    }

    @Override
    public int getAllMessageCount() throws HibernateException {
        return ((Long) sessionFactory.getCurrentSession().createQuery("select count(*) from Message").uniqueResult()).intValue();
    }

    @Override
    public int getInvalidMessageCount() throws HibernateException {
        return getMessageCount(MessageStatus.invalid, Direction.OUT);
    }

    @Override
    public int getFailedMessageCount() throws HibernateException {
        return getMessageCount(MessageStatus.failed, Direction.OUT);
    }

    @Override
    public int getSentMessageCount() throws HibernateException {
        return getMessageCount(MessageStatus.sent, Direction.OUT);
    }

    @Override
    public int getReprocessedMessageCount() throws HibernateException {
        return getMessageCount(MessageStatus.reprocessed, Direction.OUT);
    }

    @Override
    public int getProcessingMessageCount() throws HibernateException {
        return getMessageCount(MessageStatus.processing, Direction.OUT);
    }

    @Override
    public int getInvalidInboundMessageCount() throws HibernateException {
        return getMessageCount(MessageStatus.invalid, Direction.IN);
    }

    @Override
    public int getAllOutboundMessageCount() throws HibernateException {
        return getMessageCount(null, Direction.OUT);
    }

    @Override
    public int getAllInboundMessageCount() throws HibernateException {
        return getMessageCount(null, Direction.IN);
    }

    private int getMessageCount(MessageStatus messageStatus, Direction direction) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Message.class);
        criteria.add(Restrictions.eq("direction", direction));
        if (messageStatus != null) {
            criteria.add(Restrictions.eq("status", messageStatus));
        }
        return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    @Override
    public void resolveManually(Message message, String comment) throws HibernateException {
        message.setResolvedManually(true);
        message.setResolvedComment(comment);
        message.setStatus(MessageStatus.resolved);
        update(message);
    }

    @Override
    public void resolveManually(Integer messageId, String comment) throws HibernateException {
        Message message = getById(messageId);
        resolveManually(message, comment);
    }

}
