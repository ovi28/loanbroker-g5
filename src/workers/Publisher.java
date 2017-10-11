package workers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.LoanRequest;
import org.json.JSONObject;
import services.RabbitMQService;

/**
 *
 * @author Group 6
 */
public class Publisher implements Runnable {
    
    private LoanRequest loanRequest;
    
    public Publisher(LoanRequest loanRequest) {
        this.loanRequest = loanRequest;
    }

    @Override
    public void run() {
        RabbitMQService service = new RabbitMQService();
        
        try (Connection conn = service.getRabbitMQConnection("localhost")) {
            Channel channel = conn.createChannel();
            service.createQueue("creditScore", true, false, false, null, channel);
            service.createExchange("creditScore", "direct", true, channel);
            service.bindExchangeQueue("creditScore", "creditScore", "", channel);
            
            String message = new JSONObject(loanRequest).toString();
            service.postToQueue(message, "creditScore", "", null, channel);
            channel.close();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
