package com.ardverk.dht.message;

import org.ardverk.lang.NullArgumentException;

import com.ardverk.dht.KUID;
import com.ardverk.dht.routing.Contact2;

public class DefaultStoreRequest extends AbstractRequestMessage 
        implements StoreRequest {

    private final KUID key;
    
    private final byte[] value;
    
    public DefaultStoreRequest(MessageId messageId, 
            Contact2 contact, KUID key, byte[] value) {
        super(messageId, contact);
        
        if (key == null) {
            throw new NullArgumentException("key");
        }
        
        if (value == null) {
            throw new NullArgumentException("value");
        }
        
        this.key = key;
        this.value = value;
    }

    @Override
    public KUID getKey() {
        return key;
    }

    @Override
    public byte[] getValue() {
        return value;
    }
}