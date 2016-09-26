package com.opuscapita.peppol.support.ui.service;

import com.opuscapita.peppol.support.ui.components.PidUtil;
import com.opuscapita.peppol.support.ui.components.QuartzStatusUtil;
import com.opuscapita.peppol.support.ui.components.RabbitMqConnectionFactoryPool;
import com.opuscapita.peppol.support.ui.domain.AmqpStatus;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by KACENAR1 on 14.24.2.
 */
@Service
public class StatusServiceImpl implements StatusService {
    private static final Logger logger = Logger.getLogger(StatusService.class);

    @Autowired
    private RabbitMqConnectionFactoryPool rabbitMqConnectionFactoryPool;

    @Value("${peppol.amqp.queue.list}")
    private String queues;

    @Value("${peppol.amqp.host}")
    private String host;

    @Value("${peppol.amqp.port}")
    private int port;

    private List<String> queueList;
    private RabbitAdmin rabbitAdmin;

    @PostConstruct
    public void init() {
        queueList = new ArrayList<String>();
        for (String queue : queues.split(";")) {
            queueList.add(queue);
        }
        ConnectionFactory connectionFactory = rabbitMqConnectionFactoryPool.getConnectionFactory(host, port);
        rabbitAdmin = new RabbitAdmin(connectionFactory);
    }

    public List<AmqpStatus> getAmqpStatuses() {
        List<AmqpStatus> amqpStatuses = new ArrayList<AmqpStatus>();
        for (String queue : queueList) {
            Properties queueProperties = rabbitAdmin.getQueueProperties(queue);
            amqpStatuses.add(new AmqpStatus(
                    (Integer) queueProperties.get("QUEUE_MESSAGE_COUNT"),
                    queue,
                    (Integer) queueProperties.get("QUEUE_CONSUMER_COUNT")));
        }
        return amqpStatuses;
    }

    @Override
    public String getOutboundProcessId() {
        return PidUtil.getProcessPid();
    }

    @Override
    public String getQuartzStatus() {
        String status = QuartzStatusUtil.getQuartzStatus();
        status = status.replace("\n", "<br>");
        return status;
    }
}
