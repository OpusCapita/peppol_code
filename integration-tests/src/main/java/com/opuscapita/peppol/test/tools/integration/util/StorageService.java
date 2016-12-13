package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.test.tools.integration.model.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gamanse1 on 2016.12.09..
 */
@Component
public class StorageService {
    Logger logger = LoggerFactory.getLogger(StorageService.class);
    @Autowired
    MessageRepository messageRepository;
}
