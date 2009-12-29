package com.ardverk.dht.message;

import java.net.SocketAddress;

import com.ardverk.dht.KUID;
import com.ardverk.dht.routing.Contact;

public class DefaultNodeRequest extends AbstractLookupRequest 
        implements NodeRequest {
    
    public DefaultNodeRequest(MessageId messageId, 
            Contact contact, Contact destination, KUID key) {
        super(messageId, contact, destination, key);
    }

    public DefaultNodeRequest(MessageId messageId, 
            Contact contact, SocketAddress address, KUID key) {
        super(messageId, contact, address, key);
    }
}