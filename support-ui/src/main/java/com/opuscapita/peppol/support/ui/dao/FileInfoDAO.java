package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.FailedFileInfo;
import com.opuscapita.peppol.support.ui.domain.FileInfo;
import com.opuscapita.peppol.support.ui.domain.ReprocessFileInfo;
import com.opuscapita.peppol.support.ui.domain.SentFileInfo;
import com.sun.istack.Nullable;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.2.12
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public interface FileInfoDAO extends DAO<FileInfo> {
    public
    @Nullable
    FileInfo getByFileName(String filename) throws HibernateException;

    public void saveSentFile(SentFileInfo sentFileInfo) throws HibernateException;

    public void saveErrorFile(FailedFileInfo failedFileInfo) throws HibernateException;

    public void saveReprocessFile(ReprocessFileInfo reprocessFileInfo) throws HibernateException;

    public List<FileInfo> getFileInfos(int messageId) throws HibernateException;

}
