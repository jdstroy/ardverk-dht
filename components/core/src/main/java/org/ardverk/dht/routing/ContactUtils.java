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

package org.ardverk.dht.routing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import org.ardverk.lang.TimeStamp;
import org.ardverk.utils.ReverseComparator;

public class ContactUtils {

  private ContactUtils() {}
    
  private static final Comparator<ContactEntry> HEALTH_ASCENDING 
    = new Comparator<ContactEntry>() {
      @Override
      public int compare(ContactEntry o1, ContactEntry o2) {
        int e1 = o1.getErrorCount();
        int e2 = o2.getErrorCount();
        
        if (e1 == 0 && e2 == 0) {
          return LongevityUtils.TIMESTAMP_ASCENDING.compare(o1, o2);
        } else if (e1 == 0 && e2 != 0) {
          return -1;
        } else if (e1 != 0 && e2 == 0) {
          return 1;
        }
        
        // Sort everyone else from least recently failed to most recently failed.
        // TODO: Take the number of errors into account?
        TimeStamp t1 = o1.getErrorTimeStamp();
        TimeStamp t2 = o2.getErrorTimeStamp();
        if (t1 == null) {
          return (t2 != null) ? -1 : 0;
        } else if (t2 == null) {
          return 1;
        }
        return t1.compareTo(t2);
      }
    };
    
  private static final Comparator<ContactEntry> HEALTH_DESCENDING 
    = new ReverseComparator<ContactEntry>(HEALTH_ASCENDING);
  
  public static long getAdaptiveTimeout(DefaultContact contact, 
      long defaultValue, TimeUnit unit) {
    long rtt = contact.getRoundTripTime(unit);
    if (rtt < 0) {
      return defaultValue;
    }
    
    return Math.min((long)(rtt * 1.5f), defaultValue);
  }
  
  public static ContactEntry[] byHealth(ContactEntry[] entries) {
    return byHealth(entries, true);
  }
  
  public static ContactEntry[] byHealth(ContactEntry[] entries, boolean ascending) {
    Arrays.sort(entries, ascending ? HEALTH_ASCENDING : HEALTH_DESCENDING);
    return entries;
  }
  
  /**
   * Turns the given array of {@link ContactEntry}s into an array of {@link Contact}s.
   */
  public static Contact[] toContacts(ContactEntry[] entries) {
    return toContacts(entries, 0, entries.length);
  }
  
  /**
   * Turns the given array of {@link ContactEntry}s into an array of {@link Contact}s.
   */
  public static Contact[] toContacts(ContactEntry[] entries, int offset, int length) {
    Contact[] contacts = new Contact[length];
    for (int i = 0; i < length; i++) {
      contacts[i] = entries[offset + i].getContact();
    }
    return contacts;
  }
  
  /**
   * Returns the least recently seen {@link ContactEntry} in the 
   * given {@link Collection}.
   */
  public static ContactEntry getLeastRecentlySeen(
      Collection<? extends ContactEntry> entries) {
    ContactEntry lrs = null;
    for (ContactEntry entry : entries) {
      if (lrs == null || entry.getTimeStamp().compareTo(lrs.getTimeStamp()) < 0) {
        lrs = entry;
      }
    }
    return lrs;
  }
  
  /**
   * Returns the most recently seen {@link ContactEntry} in the 
   * given {@link Collection}.
   */
  public static ContactEntry getMostRecentlySeen(
      Collection<? extends ContactEntry> entries) {
    ContactEntry mrs = null;
    for (ContactEntry entry : entries) {
      if (mrs == null || entry.getTimeStamp().compareTo(mrs.getTimeStamp()) >= 0) {
        mrs = entry;
      }
    }
    return mrs;
  }
}