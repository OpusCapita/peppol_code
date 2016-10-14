package com.opuscapita.peppol.tests.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by BOGDAAN1 on 2016.09.05..
 */
public class RabbitConnector {

    private final static Logger logger = LogManager.getLogger(RabbitConnector.class);

    @JsonProperty("uri")
    private String uri;
    @JsonProperty("exchange")
    private String exchange;
    @JsonProperty("alternate_exchange")
    private String alternateExchange;
    @JsonProperty("exchange_type")
    private String exchangeType;

    private ConnectionFactory factory = new ConnectionFactory();
    private Connection connection;
    private Channel channel;

    public RabbitConnector(){
        logger.info("New instance of RabbitConnector created");
    }

    public void setConnection() throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {

        factory.setUri(uri);
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        logger.info("Connected to rabbit instance");


        if (this.alternateExchange.length() > 0){
            Map<String, Object> alternateExchange = new HashMap<String, Object>();
            alternateExchange.put("alternate-exchange", this.alternateExchange);
            channel.exchangeDeclare(this.exchange, this.exchangeType, true, false, alternateExchange);
            channel.exchangeDeclare(this.alternateExchange, this.exchangeType, true);
        } else {
            channel.exchangeDeclare(this.exchange, this.exchangeType, true);
        }

        //this.channel.exchangeDeclare(this.exchange, this.exchangeType, true);
        logger.info("Exchange "+this.exchange+ " declared");
    }

    public void publishMessage(byte[] message, String routingKey) throws IOException, TimeoutException{
        //Send message to the exchange, using routing key
        if(message.length > 0) {
            this.channel.basicPublish(this.exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
            logger.info("[x] Sent '" + message.toString() + "' to " + this.exchange + " with routing key " + routingKey);
        }else{
            logger.warn("Trying to send an empty message. No can do!");
        }
    }

    public void closeConnections() throws IOException, TimeoutException{
        //closes connection to the Rabbit server.
        if(this.channel.isOpen()) {
            this.channel.close();
        }
        if (this.connection.isOpen()){
            this.connection.close();
        }
        logger.info("All Rabbit connections closed.");
    }

}
