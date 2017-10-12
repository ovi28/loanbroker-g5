package main;

import com.rabbitmq.client.GetResponse;
import models.LoanRequest;
import services.CreditScoreService;
import workers.Consumer;
import workers.Publisher;

public class main {

    public static void main(String[] args) {
        CreditScoreService cs = new CreditScoreService();
        System.out.println("T1");
        System.out.println("Credit for user is: " + cs.getCreditScore("010180-5100"));
        
        LoanRequest request = new LoanRequest(1,2);
        
        Runnable r = new Publisher(request);
        r.run();
        
        GetResponse response = new Consumer().call();
        byte[] msg = response.getBody();
//        
        System.out.println("Message is: " + msg);
    }
}
