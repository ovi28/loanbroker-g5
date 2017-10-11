package workers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import services.RabbitMQService;

/**
 *
 * @author Group 6
 */
public class Consumer implements Callable<GetResponse>{

    @Override
    public GetResponse call() {
        RabbitMQService service = new RabbitMQService();
        GetResponse response = null;
        
        try (Connection conn = service.getRabbitMQConnection("localhost")) {
            Channel channel = conn.createChannel();
            service.createQueue("creditScore", true, false, false, null, channel);
            service.createExchange("creditScore", "direct", true, channel);
            service.bindExchangeQueue("creditScore", "creditScore", "", channel);
            response = service.getResponseFromQueue("creditScore", "", true, channel);
            channel.close();
            return response;
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
}
