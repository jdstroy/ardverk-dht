package com.ardverk.dht.message;

import org.ardverk.lang.NullArgumentException;

import com.ardverk.dht.KUID;
import com.ardverk.dht.routing.Contact2;

abstract class AbstractLookupRequest extends AbstractRequestMessage 
        implements LookupRequest {
    
    private final KUID key;
    
    public AbstractLookupRequest(MessageId messageId, 
            Contact2 contact, KUID key) {
        super(messageId, contact);
        
        if (key == null) {
            throw new NullArgumentException("key");
        }
        
        this.key = key;
    }

    @Override
    public KUID getKey() {
        return key;
    }
}
