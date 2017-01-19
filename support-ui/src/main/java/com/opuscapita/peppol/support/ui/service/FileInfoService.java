package com.opuscapita.peppol.support.ui.service;

import com.opuscapita.peppol.support.ui.domain.FileInfo;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.2.12
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public interface FileInfoService extends Service<FileInfo> {

    public FileInfo getByFileName(String filename) throws HibernateException;

    public void reprocessFile(Integer fileId, boolean outbound) throws Exception;

    public List<FileInfo> getMessageFileInfos(int messageId) throws HibernateException;
}
