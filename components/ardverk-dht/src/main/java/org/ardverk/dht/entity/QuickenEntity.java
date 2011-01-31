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

package org.ardverk.dht.entity;

import org.ardverk.dht.concurrent.DHTFuture;
import org.ardverk.dht.message.MessageType;

/**
 * The result of a quicken operation. It's a combination of multiple
 * operations.
 * 
 * @see PingEntity
 * @see NodeEntity
 */
public interface QuickenEntity extends Entity {

    /**
     * Returns all {@link MessageType#PING} {@link DHTFuture}s.
     */
    public DHTFuture<PingEntity>[] getPingFutures();
    
    /**
     * Returns all {@link MessageType#FIND_NODE} {@link DHTFuture}s.
     */
    public DHTFuture<NodeEntity>[] getLookupFutures();
}