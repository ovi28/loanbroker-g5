/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XMLTranslator;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author user
 */
public class XmlTranslator {
     
    private static final String CONSUME_QUEUE_NAME = "Xml_Bank_Translator_Queue";
    private static final String BANK_HOST = "datdb.cphbusiness.dk";
    private static final String PUBLISH_EXCHANGE_NAME = "cphbusiness.bankXML";
    private static final String REPLY_QUEUE_NAME = "Bank_Response_Queue";
    //private static final String HOST_NAME = "hostAdrs";

    public static void main(String[] args) throws Exception {

        ConnectionFactory bankConnectionFactory = new ConnectionFactory();
        bankConnectionFactory.setHost(BANK_HOST);
        Connection bankConnection = bankConnectionFactory.newConnection();

        Channel bankPublishChannel = bankConnection.createChannel();

        ConnectionFactory hostConnectionFactory = new ConnectionFactory();
        hostConnectionFactory.setHost(HOST_NAME);
        Connection hostConnection = hostConnectionFactory.newConnection();

        Channel hostConsumeChannel = hostConnection.createChannel();

        String xmlRequest = receiveMessage(hostConsumeChannel);
//        String xmlRequest = "<LoanRequest>    <ssn>12345678</ssn>   
//        <creditScore>690</creditScore>    <loanAmount>1000.0</loanAmount>   
//                <loanDuration>1973-01-01 01:00:00.0 CET</loanDuration> 
//                        </LoanRequest>";
        getBankXmlResponseAndForward(xmlRequest, bankPublishChannel);
    }

    private static String receiveMessage(Channel channel) throws IOException, TimeoutException {
        channel.queueDeclare(CONSUME_QUEUE_NAME, false, false, false, null);
        System.out.println("[*] Waiting for messages...");

        QueueingConsumer consumer = new QueueingConsumer(channel);

        channel.basicConsume(CONSUME_QUEUE_NAME, true, consumer);

        String response = "";
        try {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            response = new String(delivery.getBody());

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        System.out.println("[*] Consumed message from queue:");
        return response;

    }


    private static void getBankXmlResponseAndForward(String xmlRequest, Channel channel) throws Exception{
        channel.queueDeclare(REPLY_QUEUE_NAME, false, false, false, null);

        String replyKey = "xml";

        System.out.println("Waiting for response...");

        //Sending request and routing the request using a builder.
        AMQP.BasicProperties builder = new AMQP.BasicProperties
                .Builder()
                .contentType("application/xml")
                .deliveryMode(1)
                .replyTo(REPLY_QUEUE_NAME)
                .build();

        //Publish and route message
        channel.basicPublish(PUBLISH_EXCHANGE_NAME, replyKey, builder, xmlRequest.getBytes());

        channel.close();
        channel.getConnection().close();
        System.out.println("[x] forwarded response successfully!");

    }
    
}
