
package Aggregator;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Alex
 */
public class Aggregator {

    private static final String PUBLISH_QUEUE_NAME = "Aggregator_Queue";
    private static final String CONSUME_QUEUE_NAME = "Normalizer_Queue";
    private static final String HOST_NAME = "localhost";

    public static void main(String[] args) throws Exception {
        ConnectionFactory hostConnectionFactory = new ConnectionFactory();
        hostConnectionFactory.setHost(HOST_NAME);
        Connection hostConnection = hostConnectionFactory.newConnection();
        Channel hostChannel = hostConnection.createChannel();
        List<String> loanRequests = receiveMessages(hostChannel);
        String bestInterestRate = calculateBestLoan(loanRequests);
        sendMessage(bestInterestRate, hostChannel);
    }

    private static void sendMessage(String message, Channel channel) throws IOException, TimeoutException {
        try {
            channel.queueDeclare(PUBLISH_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", PUBLISH_QUEUE_NAME, null, message.getBytes());
            System.out.println("[x] sent '" + message + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        channel.close();
        channel.getConnection().close();

    }

    private static List<String> receiveMessages(Channel channel) throws IOException, TimeoutException {
        channel.queueDeclare(CONSUME_QUEUE_NAME, false, false, false, null);
        System.out.println("[*] Waiting for messages...");
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(CONSUME_QUEUE_NAME, false, consumer);

        List<String> loanResponses = new ArrayList<>();
        String response;
        try {
            do {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery(1000);
                try {
                    response = new String(delivery.getBody());
                    loanResponses.add(response);
                } catch (NullPointerException ex) {
                    break;
                }
            } while (!response.equals(""));
        } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
            ex.printStackTrace();
        }

        return loanResponses;

    }

    private static String calculateBestLoan(List<String> loanResponses) {
        double interestRate = 0;
        for (String loanResponse : loanResponses) {
            try {

                double responseInterestRate = Double.parseDouble(getNodeValue(loanResponse, "interestRate"));
                if (responseInterestRate > interestRate) {
                    interestRate = responseInterestRate;
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return String.format("%s Best Rate: %s", loanResponses.size(), interestRate);

    }

    private static String getNodeValue(String xml, String node) {
        String nodeValue = "";
        try {
            Document document = loadXMLFromString(xml);
            Node wantedNote = document.getElementsByTagName(node).item(0);
            nodeValue = wantedNote.getTextContent();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return nodeValue;

    }

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes()));

    }

}
