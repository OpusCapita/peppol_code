package com.opuscapita.peppol.support.ui.service;


import com.opuscapita.peppol.support.ui.common.Util;
import com.opuscapita.peppol.support.ui.dao.FileInfoDAO;
import com.opuscapita.peppol.support.ui.domain.FileInfo;
import com.opuscapita.peppol.support.ui.domain.Message;
import com.opuscapita.peppol.support.ui.domain.MessageStatus;
import com.opuscapita.peppol.support.ui.domain.ReprocessFileInfo;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.4.12
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {
    private static final Logger logger = Logger.getLogger(FileInfoService.class);

    @Autowired
    private FileInfoDAO fileInfoDAO;

    @Autowired
    private MessageService messageService;

    @Autowired
    private Util util;

    @Value("${reprocess.outbound.dir}")
    private String reprocessOutboundDir;

    @Value("${reprocess.inbound.dir}")
    private String reprocessInboundDir;


    @Override
    public void add(FileInfo file) throws HibernateException {
        fileInfoDAO.add(file);
    }

    @Override
    public List<FileInfo> getAll() {
        List<FileInfo> list = fileInfoDAO.getAll();
        return list;
    }

    @Override
    public FileInfo getById(Integer id) {
        FileInfo fileInfo = fileInfoDAO.getById(id);
        return fileInfo;
    }

    @Override
    public void update(FileInfo fileInfo) {
        fileInfoDAO.update(fileInfo);
    }

    @Override
    public void delete(FileInfo FileInfo) {
        fileInfoDAO.delete(FileInfo);
    }

    @Override
    public void delete(Integer id) {
        fileInfoDAO.delete(id);
    }

    @Override
    public FileInfo getByFileName(String filename) {
        FileInfo fileInfo = fileInfoDAO.getByFileName(filename);
        return fileInfo;
    }

    @Override
    public void reprocessFile(Integer fileId, boolean outbound) throws Exception {
        FileInfo fileInfo = fileInfoDAO.getById(fileId);
        byte[] fileData = util.findMessage(fileInfo.getFilename());
        Message message = fileInfo.getMessage();
        message.setStatus(MessageStatus.reprocessed);
        messageService.update(message);
        addReprocessInfo(fileInfo);
        fileInfoDAO.update(fileInfo);
        final Path filePath = Paths.get(outbound ? reprocessOutboundDir : reprocessInboundDir, fileInfo.getFilename());
        Files.write(filePath, fileData);
    }

    private void addReprocessInfo(FileInfo fileInfo) {
        ReprocessFileInfo reprocessFileInfo = new ReprocessFileInfo();
        reprocessFileInfo.setReprocessedFile(fileInfo);
        if (fileInfo.getReprocessInfo() == null) {
            fileInfo.setReprocessInfo(new TreeSet<ReprocessFileInfo>());
        }
        fileInfo.getReprocessInfo().add(reprocessFileInfo);
    }

    @Override
    public List<FileInfo> getMessageFileInfos(int messageId) throws HibernateException {

        List<FileInfo> result = null;
        try {
            result = fileInfoDAO.getFileInfos(messageId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (result == null) {
            result = new ArrayList<>(0);
        }
        return result;
    }

}
