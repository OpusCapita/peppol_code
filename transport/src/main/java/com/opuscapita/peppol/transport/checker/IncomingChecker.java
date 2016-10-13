package com.opuscapita.peppol.transport.checker;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.storage.Storage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * Checks configured directory periodically and sends file to MQ.
 * Supports recursive walk through the directories.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class IncomingChecker {
    private static final Logger logger = LoggerFactory.getLogger(IncomingChecker.class);

    private final RabbitTemplate rabbitTemplate;
    private final Storage storage;

    @Value("${transport.file.age.seconds?:120}")
    private int age;
    @Value("${transport.input.directory}")
    private String directory;
    @Value("${transport.input.recursive?:false}")
    private boolean recursive;
    @Value("${transport.input.mask?:.*}")
    private String mask;
    @Value("${transport.input.queue?:internal_routing}")
    private String queue;

    @Autowired
    public IncomingChecker(@NotNull RabbitTemplate rabbitTemplate, @NotNull Storage storage) {
        this.rabbitTemplate = rabbitTemplate;
        this.storage = storage;
    }

    @Scheduled(fixedRate = 60_000) // 1 minute
    public void receive() throws IOException {
        receive(new File(directory));
    }

    private void receive(File directory) throws IOException {
        logger.debug("Checking directory " + directory.getAbsolutePath());

        Date earlier = DateUtils.addSeconds(new Date(), age);
        AgeFileFilter ageFileFilter = new AgeFileFilter(earlier);

        Iterator<File> files = FileUtils.iterateFiles(directory, ageFileFilter, null);
        while (files.hasNext()) {
            File file = files.next();

            if (file.isDirectory()) {
                if (recursive) {
                    receive(file);
                }
                continue;
            }

            if (file.getAbsolutePath().matches(mask)) {
                logger.info("Found outgoing file: " + file.getAbsolutePath());
                send(file);
            }
        }
    }

    private void send(File file) throws IOException {
        String fileName = storage.storeTemporary(file);

        ContainerMessage cm = new ContainerMessage("From " + file.getAbsolutePath(), fileName, Endpoint.GATEWAY);

        rabbitTemplate.convertAndSend(queue, cm);
        logger.info("File " + cm.getFileName() + " processed and sent to MQ");
    }


}
