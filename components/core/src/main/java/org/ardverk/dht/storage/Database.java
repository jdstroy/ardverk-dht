/*
 * Copyright 2009-2011 Roger Kapsi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ardverk.dht.storage;

import org.ardverk.dht.KUID;
import org.ardverk.dht.lang.IntegerValue;
import org.ardverk.dht.lang.StringValue;

/**
 * A simple and minimum interface of a {@link Database} 
 * that is needed by the DHT.
 */
public interface Database {
    
    /**
     * Returned by {@link Database#store(ValueTuple)}.
     */
    public static interface Condition extends IntegerValue, StringValue {
    }
    
    /**
     * Returns the {@link DatabaseConfig}.
     */
    public DatabaseConfig getDatabaseConfig();
    
    /**
     * Stores the given {@link ValueTuple} and returns a {@link Condition}.
     */
    public Condition store(ValueTuple tuple);
    
    /**
     * Returns a {@link ValueTuple} for the given {@link Resource}.
     */
    public ValueTuple get(Resource resource);
    
    /**
     * Returns all {@link ValueTuple}s.
     */
    public Iterable<ValueTuple> values();
    
    /**
     * Returns all {@link ValueTuple}s that are close to the lookup 
     * {@link KUID} but not any further than the last {@link KUID}.
     */
    public Iterable<ValueTuple> values(KUID lookupId, KUID lastId);
    
    /**
     * Returns the size of the {@link Database}.
     */
    public int size();
    
    /**
     * Returns {@code true} if the {@link Database} is empty.
     */
    public boolean isEmpty();
}