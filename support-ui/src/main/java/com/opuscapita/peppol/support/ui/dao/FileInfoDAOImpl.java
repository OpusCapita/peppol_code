package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.FailedFileInfo;
import com.opuscapita.peppol.support.ui.domain.FileInfo;
import com.opuscapita.peppol.support.ui.domain.ReprocessFileInfo;
import com.opuscapita.peppol.support.ui.domain.SentFileInfo;
import com.sun.istack.Nullable;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.2.12
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class FileInfoDAOImpl implements FileInfoDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void add(FileInfo file) {
        sessionFactory.getCurrentSession().save(file);
        updateFileStatus(file);
    }

    @Override
    public List<FileInfo> getAll() {
        Query query = sessionFactory.getCurrentSession().createQuery("from FileInfo");
        return query.list();
    }

    @Override
    public FileInfo getById(Integer id) {
        return (FileInfo) sessionFactory.getCurrentSession().get(FileInfo.class, id);
    }

    @Override
    public void update(FileInfo file) {
        sessionFactory.getCurrentSession().update(file);
        updateFileStatus(file);
    }

    @Override
    public void delete(FileInfo file) {
        sessionFactory.getCurrentSession().delete(file);
    }

    @Override
    public void delete(Integer id) {
        FileInfo file = (FileInfo) sessionFactory.getCurrentSession().get(FileInfo.class, id);
        sessionFactory.getCurrentSession().delete(file);
    }

    @Override
    public
    @Nullable
    FileInfo getByFileName(String filename) throws HibernateException {
        /*FileInfo fileInfo = (FileInfo) sessionFactory.getCurrentSession()
                .createQuery("from FileInfo where filename = :filename")
                .setString("filename", filename)
                .uniqueResult();*/
        FileInfo fileInfo = (FileInfo) sessionFactory.getCurrentSession()
                .createCriteria(FileInfo.class)
                .setFetchMode("sentInfo", FetchMode.JOIN)
                .setFetchMode("failedInfo", FetchMode.JOIN)
                .setFetchMode("reprocessInfo", FetchMode.JOIN)
                .add(Restrictions.eq("filename", filename))
                .uniqueResult();
        return fileInfo;
    }

    @Override
    public void saveSentFile(SentFileInfo sentFileInfo) throws HibernateException {
        sessionFactory.getCurrentSession().save(sentFileInfo);
    }

    @Override
    public void saveErrorFile(FailedFileInfo failedFileInfo) throws HibernateException {
        sessionFactory.getCurrentSession().save(failedFileInfo);
    }

    @Override
    public void saveReprocessFile(ReprocessFileInfo reprocessFileInfo) throws HibernateException {
        sessionFactory.getCurrentSession().save(reprocessFileInfo);
    }

    private void updateFileStatus(FileInfo fileInfo) {
        if (fileInfo.getSentInfo() != null) {
            for (SentFileInfo sentFileInfo : fileInfo.getSentInfo()) {
                if (sentFileInfo.getId() == null) {
                    saveSentFile(sentFileInfo);
                }
            }
        }
        if (fileInfo.getFailedInfo() != null) {
            for (FailedFileInfo failedFileInfo : fileInfo.getFailedInfo()) {
                if (failedFileInfo.getId() == null) {
                    saveErrorFile(failedFileInfo);
                }
            }
        }
        if (fileInfo.getReprocessInfo() != null) {
            for (ReprocessFileInfo reprocessFileInfo : fileInfo.getReprocessInfo()) {
                if (reprocessFileInfo.getId() == null) {
                    saveReprocessFile(reprocessFileInfo);
                }
            }
        }
    }

    @Override
    public List<FileInfo> getFileInfos(int messageId) throws HibernateException {
        Query query = sessionFactory.getCurrentSession().createQuery("from FileInfo where message.id = :messageId order by arrivedTimeStamp desc ");
        query.setParameter("messageId", messageId);
        return query.list();
    }

}
