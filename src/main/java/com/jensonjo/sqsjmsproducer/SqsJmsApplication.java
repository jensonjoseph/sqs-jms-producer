package com.jensonjo.sqsjmsproducer;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import lombok.extern.java.Log;

@SpringBootApplication
@Log
public class SqsJmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqsJmsApplication.class, args);

//        log.info("Create a new connection factory with all defaults (credentials and region) set automatically");
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.defaultClient()
        );

        try {
//            log.info("Create the connection");
            SQSConnection connection = connectionFactory.createConnection();

//            log.info("Get the wrapped client");
            AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();

//            log.info("Create an SQS queue named MyQueue, if it doesn't already exist");
            if (!client.queueExists("MyQueue")) {
                client.createQueue("MyQueue");
            }

//            log.info("Create the nontransacted session with AUTO_ACKNOWLEDGE mode");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

//            log.info("Create a queue identity and specify the queue name to the session");
            Queue queue = session.createQueue("MyQueue");

//            log.info("Create a producer for the 'MyQueue'");
            MessageProducer producer = session.createProducer(queue);

//            log.info("Create the text message");
            TextMessage message = session.createTextMessage("Hello World");

//            log.info("Send the message");
            producer.send(message);
            log.info("JMS Message " + message.getJMSMessageID());

        } catch (JMSException e) {
            log.severe(e.getLocalizedMessage());
        }


    }

}
