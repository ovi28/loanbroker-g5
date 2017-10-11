package org.bank.credit.web.service;

public class CreditScoreServiceProxy implements org.bank.credit.web.service.CreditScoreService_PortType {
  private String _endpoint = null;
  private org.bank.credit.web.service.CreditScoreService_PortType creditScoreService_PortType = null;
  
  public CreditScoreServiceProxy() {
    _initCreditScoreServiceProxy();
  }
  
  public CreditScoreServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initCreditScoreServiceProxy();
  }
  
  private void _initCreditScoreServiceProxy() {
    try {
      creditScoreService_PortType = (new org.bank.credit.web.service.CreditScoreService_ServiceLocator()).getCreditScoreServicePort();
      if (creditScoreService_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)creditScoreService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)creditScoreService_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (creditScoreService_PortType != null)
      ((javax.xml.rpc.Stub)creditScoreService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.bank.credit.web.service.CreditScoreService_PortType getCreditScoreService_PortType() {
    if (creditScoreService_PortType == null)
      _initCreditScoreServiceProxy();
    return creditScoreService_PortType;
  }
  
  public int creditScore(java.lang.String ssn) throws java.rmi.RemoteException{
    if (creditScoreService_PortType == null)
      _initCreditScoreServiceProxy();
    return creditScoreService_PortType.creditScore(ssn);
  }
  
  
}