/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Normalizer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author Alex
 */
public class Normalizer {
    private static final String BANK_HOST = "datdb.cphbusiness.dk";
    private static final String HOST_NAME = "localhost";
    private static final String PUBLISH_QUEUE_NAME = "Normalizer_Queue";
    private static final String CONSUME_QUEUE_NAME = "Bank_Response_Queue";
    

    public static void main(String[] args) throws Exception {

        ConnectionFactory bankConnectionFactory = new ConnectionFactory();
        bankConnectionFactory.setHost(BANK_HOST);
        Connection bankConnection = bankConnectionFactory.newConnection();

        Channel bankConsumeChannel = bankConnection.createChannel();

        ConnectionFactory hostConnectionFactory = new ConnectionFactory();
        hostConnectionFactory.setHost(HOST_NAME);
        Connection hostConnection = hostConnectionFactory.newConnection();

        Channel hostPublishChannel = hostConnection.createChannel();

        List<String> loanResponses = receiveMessages(bankConsumeChannel);
        bankConsumeChannel.close();
        bankConsumeChannel.getConnection().close();

        for (String loan : loanResponses) {
            String identifier = identifyMessage(loan);
            switch (identifier) {
                case "JSON":
                    loan = jsonToXml(loan);
                    break;
                case "XML":
                    break;
                case "unknown":
                    System.out.println("[ ] Error - Not recognized.");
                    continue;
            }

            sendMessage(loan, hostPublishChannel);
        }

        hostPublishChannel.close();
        hostPublishChannel.getConnection().close();


    }
    private static void sendMessage(String message, Channel channel) throws IOException, TimeoutException {
        try {
            channel.queueDeclare(PUBLISH_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", PUBLISH_QUEUE_NAME, null, message.getBytes());
            System.out.println("[x] sent '" + message + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    private static String identifyMessage(String message) {
        String identifier = "";
        if (message.startsWith("{")) identifier = "JSON";
        else if (message.startsWith("<")) identifier = "XML";
        else identifier = "unknown";
        return identifier;
          }

    private static String jsonToXml(String jsonToConvert) {
        String asXML = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonToConvert);
            asXML = XML.toString(jsonObject);
            StringBuilder formatted = new StringBuilder(asXML);
            formatted.insert(0, "<LoanResponse>");
            formatted.append("</LoanResponse>");
            asXML = formatted.toString();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }


        return asXML;
    }

   
    private static List<String> receiveMessages(Channel channel) throws IOException, TimeoutException {
        channel.queueDeclare(CONSUME_QUEUE_NAME, false, false, false, null);
        System.out.println("[*] Waiting for messages...");

        QueueingConsumer consumer = new QueueingConsumer(channel);

        channel.basicConsume(CONSUME_QUEUE_NAME, false, consumer); 
        List<String> loanResponses = new ArrayList<>();
        String response = "";
        try {
            do {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery(1000); 
                try {
                    response = new String(delivery.getBody());
                    loanResponses.add(response);
                } catch (NullPointerException ex) {
                    break; 
                }
            }
            while (!response.equals(""));


        } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
            ex.printStackTrace();
        }

        return loanResponses;

    }

    
    
}
