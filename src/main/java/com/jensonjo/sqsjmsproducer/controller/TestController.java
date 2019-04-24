package com.jensonjo.sqsjmsproducer.controller;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import lombok.extern.java.Log;

/**
 * Created by jensonkakkattil on Apr, 2019.
 */
@RestController
@Log
public class TestController {
    @GetMapping(value = "/publish/{queueName}/{messageTxt}")
    public String publishMessage(@PathVariable(value = "queueName") String queueName,
                                 @PathVariable(value = "messageTxt") String messageTxt) {
        //        log.info("Create a new connection factory with all defaults (credentials and region) set automatically");
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.defaultClient()
        );

        String returnMsg = "";

        try {
//            log.info("Create the connection");
            SQSConnection connection = connectionFactory.createConnection();

//            log.info("Get the wrapped client");
            AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();

//            log.info("Create an SQS queue named MyQueue, if it doesn't already exist");
            if (!client.queueExists(queueName)) {
                client.createQueue(queueName);
            }

//            log.info("Create the nontransacted session with AUTO_ACKNOWLEDGE mode");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

//            log.info("Create a queue identity and specify the queue name to the session");
            Queue queue = session.createQueue(queueName);

//            log.info("Create a producer for the 'MyQueue'");
            MessageProducer producer = session.createProducer(queue);

//            log.info("Create the text message");
            TextMessage message = session.createTextMessage(messageTxt);

//            log.info("Send the message");
            producer.send(message);
            returnMsg = "JMS Message " + message.getJMSMessageID();
            log.info(returnMsg);

        } catch (JMSException e) {
            log.severe(e.getLocalizedMessage());
        }

        return returnMsg;
    }
}
