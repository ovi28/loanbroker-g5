/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JsonTranslator;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author user
 */
public class JsonTranslator {
    
    
    private static final String CONSUME_QUEUE_NAME = "Json_Bank_Translator_Queue";
    private static final String BANK_HOST = "datdb.cphbusiness.dk";
    private static final String PUBLISH_EXCHANGE_NAME = "cphbusiness.bankJSON";
    private static final String REPLY_QUEUE_NAME = "Bank_Response_Queue";
    private static final String HOST_NAME = "localhost";

    public static void main(String[] args) throws TimeoutException, IOException, Exception {
        ConnectionFactory bankConnectionFactory = new ConnectionFactory();
        bankConnectionFactory.setHost(BANK_HOST);
        Connection bankConnection = bankConnectionFactory.newConnection();

        Channel bankPublishChannel = bankConnection.createChannel();

        ConnectionFactory hostConnectionFactory = new ConnectionFactory();
        hostConnectionFactory.setHost(HOST_NAME);
        Connection hostConnection = hostConnectionFactory.newConnection();

        Channel hostConsumeChannel = hostConnection.createChannel();

//        String xmlRequest = "<LoanRequest>    <ssn>12345678</ssn>    
//        <creditScore>685</creditScore>    <loanAmount>1000.0</loanAmount>    
//                <loanDuration>1973-01-01 01:00:00.0 CET</loanDuration> 
//                        </LoanRequest>";
        String xmlRequest = receiveMessage(hostConsumeChannel);
        String jsonRequest = xmlToJson(xmlRequest);
        getBankJsonResponseAndForward(jsonRequest, bankPublishChannel);

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

        System.out.println("[*] Consumed JSON message from queue:");
        return response;

    }

    private static String xmlToJson(String xmlToConvert) {
        String asJson = "";
        try {
            JSONObject jsonObject = XML.toJSONObject(xmlToConvert);

            String jsonString = jsonObject.toString();

            int startInd = jsonString.indexOf("{", 1);
            int endInd = jsonString.lastIndexOf("}");
            jsonString = jsonString.substring(startInd, endInd);
            //removing paranteses

            //Transforming and taking loanDuration
            jsonObject = new JSONObject(jsonString);
            String loanDurationString = jsonObject.get("loanDuration").toString();
            long loanDurationDays = getAmountOfDays(loanDurationString);

            jsonObject.put("loanDuration", loanDurationDays);

            asJson = jsonObject.toString();


        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return asJson;


    }

    private static long getAmountOfDays(String loanDuration) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long diff = 0;
        try {
            //Given date format
            Date fromDate = dateFormat.parse("1970-01-01");
            Date toDate = dateFormat.parse(loanDuration);
               //converting 
            diff = toDate.getTime() - fromDate.getTime();
            diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }


        return diff;

    }


    private static void getBankJsonResponseAndForward(String jsonRequest, Channel channel) throws Exception {
        channel.queueDeclare(REPLY_QUEUE_NAME, false, false, false, null);

        String replyKey = "json";

        System.out.println("Waiting for response...");

        //Sending request and routing the request using a builder.
        AMQP.BasicProperties builder = new AMQP.BasicProperties
                .Builder()
                .contentType("application/json")
                .deliveryMode(1)
                .replyTo(REPLY_QUEUE_NAME)
                .build();

        //Publish and route message
        channel.basicPublish(PUBLISH_EXCHANGE_NAME, replyKey, builder, jsonRequest.getBytes());

        channel.close();
        channel.getConnection().close();
        System.out.println("[x] forwarded response successfully!");

    }
    
}
