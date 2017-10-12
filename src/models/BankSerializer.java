/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author user
 */
public class BankSerializer {
    
 
    private static final String ALIAS_REQUEST = "BankQuoteRequest"; // tag name for BankRequest
    private static final String ALIAS_REPLY = "BankQuoteReply"; // tag name for BankReply
    private XSerial xstream; // class for serialization

    public BankSerializer() {
        super();
        xstream = new XStream();
        // register aliases (i.e., tag names)
        xstream.alias(ALIAS_REQUEST, BankQuoteRequest.class);
        xstream.alias(ALIAS_REPLY, BankQuoteReply.class);
    }

    /**
     * This method parses a bankRequest from an XML string.
     *
     * @param str is the string containing the XML
     * @return the BankRequest containng the same information like the given XML
     * (str)
     */
    public BankQuoteRequest requestFromString(String str) {
        return (BankQuoteRequest) xstream.fromXML(str);
    }

    /**
     * This method parses a BankReply from an XML string.
     *
     * @param str is the string containing the XML
     * @return the BankReply containng the same information like the given XML
     * (str)
     */
    public BankQuoteReply replyFromString(String str) {
        return (BankQuoteReply) xstream.fromXML(str);
    }

    /**
     * Serializes a BankRequest into an XML string.
     *
     * @param request is the BankRequest to be serialized into XML
     * @return the string containing XML with information about the request
     */
    public String requestToString(BankQuoteRequest request) {
        return xstream.toXML(request);
    }

    /**
     * Serializes a BankReply into XML string.
     *
     * @param request is the BankReply to be serialized into XML
     * @return the string containing XML with information about the reply
     */
    public String replyToString(BankQuoteReply reply) {
        return xstream.toXML(reply);
    }
}



