package services;

import org.bank.credit.web.service.CreditScoreService_PortType;

/***
 * Services to contact WSDL Credit Score SOAP Service
 * 
 * @author Group 5
 */
public class CreditScoreService implements ICreditScoreService{
    
    @Override
    public int getCreditScore(String ssn) {
        int result = 0;
        try { // Call Web Service Operation
            org.bank.credit.web.service.CreditScoreService_ServiceLocator service = new org.bank.credit.web.service.CreditScoreService_ServiceLocator();
            CreditScoreService_PortType port = service.getCreditScoreServicePort();
            // TODO initialize WS operation arguments here
            // TODO process result here
            result = port.creditScore(ssn);
        } catch (Exception ex) {
            
        }
        return result;
    }
}
