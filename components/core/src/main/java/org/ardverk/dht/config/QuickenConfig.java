/*
 * Copyright 2009-2012 Roger Kapsi
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

package org.ardverk.dht.config;

import java.util.concurrent.TimeUnit;

import org.ardverk.dht.concurrent.ExecutorKey;
import org.ardverk.utils.TimeUtils;


public class QuickenConfig extends Config {

  private volatile PingConfig pingConfig = new PingConfig();

  private volatile NodeConfig lookupConfig = new NodeConfig();

  private volatile float pingCount = 1.0f;
  
  private volatile long contactTimeoutInMillis 
    = TimeUtils.convert(5L*60L, TimeUnit.SECONDS, TimeUnit.MILLISECONDS);
  
  private volatile long bucketTimeoutInMillis 
    = TimeUtils.convert(5L*60L, TimeUnit.SECONDS, TimeUnit.MILLISECONDS);
  
  @Override
  public void setExecutorKey(ExecutorKey executorKey) {
    super.setExecutorKey(executorKey);
    pingConfig.setExecutorKey(executorKey);
    lookupConfig.setExecutorKey(executorKey);
  }
  
  public PingConfig getPingConfig() {
    return pingConfig;
  }
  
  public void setPingConfig(PingConfig pingConfig) {
    this.pingConfig = pingConfig;
  }
  
  public float getPingCount() {
    return pingCount;
  }
  
  public void setPingCount(float pingCount) {
    this.pingCount = pingCount;
  }

  public long getContactTimeout(TimeUnit unit) {
    return unit.convert(contactTimeoutInMillis, TimeUnit.MILLISECONDS);
  }

  public long getContactTimeoutInMillis() {
    return getContactTimeout(TimeUnit.MILLISECONDS);
  }
  
  public void setContactTimeout(long timeout, TimeUnit unit) {
    this.contactTimeoutInMillis = unit.toMillis(timeout);
  }

  public NodeConfig getLookupConfig() {
    return lookupConfig;
  }
  
  public void setLookupConfig(NodeConfig lookupConfig) {
    this.lookupConfig = lookupConfig;
  }

  public long getBucketTimeout(TimeUnit unit) {
    return unit.convert(bucketTimeoutInMillis, TimeUnit.MILLISECONDS);
  }

  public long getBucketTimeoutInMillis() {
    return getBucketTimeout(TimeUnit.MILLISECONDS);
  }
  
  public void setBucketTimeout(long timeout, TimeUnit unit) {
    this.bucketTimeoutInMillis = unit.toMillis(timeout);
  }

  @Override
  public void setOperationTimeout(long timeout, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getOperationTimeout(TimeUnit unit) {
    throw new UnsupportedOperationException();
  }
}