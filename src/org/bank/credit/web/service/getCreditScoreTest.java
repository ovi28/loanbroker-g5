/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bank.credit.web.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import services.CreditScoreService;

/**
 *
 * @author user
 */
public class getCreditScoreTest {
     private static final String HOST_NAME = "localhost";
    private static final String CONSUME_QUEUE_NAME = "Loan_Request_Queue";
    private static final String PUBLISH_QUEUE_NAME = "Get_Credit_Score_Queue";

    public static void main(String[] args) throws IOException, TimeoutException, ServiceException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String xmlMessage = receiveMessage(channel);
        String requestSsn = getSsn(xmlMessage);
        int creditScore = creditScore(requestSsn);
        byte[] updatedRequest = appendCreditScore(xmlMessage, String.valueOf(creditScore));
        sendMessage(updatedRequest, channel);


    }
    private static String getSsn(String xml) {
        String ssn = "";
        try {
            Document document = loadXMLFromString(xml);
            Node ssnNode = document.getElementsByTagName("ssn").item(0);
            ssn = ssnNode.getTextContent();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ssn;

    }
     private static byte[] appendCreditScore(String xmlToAppend, String creditScore) {
        byte[] xmlByteArray = {};
        try {
            Document document = loadXMLFromString(xmlToAppend);

            Node creditScoreNode = document.getElementsByTagName("creditScore").item(0);
            creditScoreNode.appendChild(document.createTextNode(creditScore));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);

            transformer.transform(source, result);

            xmlByteArray = bos.toByteArray();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return xmlByteArray;

    }

    private static int creditScore(java.lang.String ssn) throws ServiceException, RemoteException {
        CreditScoreService service = new CreditScoreService();   
                           
        return service.getCreditScore(ssn);
    }

    

    private static void sendMessage(byte[] message, Channel channel) throws IOException, TimeoutException {
        try {
            channel.queueDeclare(PUBLISH_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", PUBLISH_QUEUE_NAME, null, message);
            System.out.println("[x] sent '" + message + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        channel.close();
        channel.getConnection().close();

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

        return response;

    }  
  
   private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new ByteArrayInputStream(xml.getBytes()));


    }
    
}
