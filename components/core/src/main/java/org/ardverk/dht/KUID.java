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

package org.ardverk.dht;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;

import org.ardverk.coding.CodingUtils;
import org.ardverk.collection.Key;
import org.ardverk.collection.KeyAnalyzer;
import org.ardverk.dht.lang.Identifier;
import org.ardverk.dht.lang.Negation;
import org.ardverk.dht.lang.Xor;
import org.ardverk.dht.security.SecurityUtils;
import org.ardverk.io.Writable;
import org.ardverk.lang.Bytes;


/**
 * Kademlia Unique Identifier ({@link KUID}) 
 */
public class KUID implements Identifier, Key<KUID>, Xor<KUID>, Negation<KUID>, 
        Writable, Serializable, Comparable<KUID>, Cloneable {

    private static final long serialVersionUID = -4611363711131603626L;
    
    private static final Random GENERATOR 
        = SecurityUtils.createSecureRandom();
    
    private static final int MSB = 1 << Byte.SIZE-1;
    
    /**
     * Creates and returns a random {@link KUID} of the given length in bytes.
     */
    public static KUID createRandom(int length) {
        byte[] key = new byte[length];
        GENERATOR.nextBytes(key);
        return new KUID(key);
    }
    
    /**
     * Creates and returns a random {@link KUID} of the same 
     * length as the given {@link KUID}.
     */
    public static KUID createRandom(KUID otherId) {
        return createRandom(otherId.length());
    }
    
    /**
     * Creates and returns a {@link KUID} from the given bytes.
     */
    public static KUID create(byte[] key) {
        return new KUID(key);
    }
    
    /**
     * Creates and returns a {@link KUID} from the given bytes.
     */
    public static KUID create(byte[] key, int offset, int length) {
        byte[] copy = new byte[length];
        System.arraycopy(key, 0, copy, 0, copy.length);
        return new KUID(copy);
    }
    
    /**
     * Creates and returns a {@link KUID} from a string encoded key.
     */
    public static KUID create(String key, int radix) {
        switch (radix) {
            case 16:
                byte[] data = CodingUtils.decodeBase16(key);
                return create(data);
            default:
                throw new IllegalArgumentException("radix=" + radix);
        }
    }
    
    /**
     * Creates and returns a random {@link KUID} that has the same
     * prefix as the given {@link KUID}.
     * 
     * NOTE: The bitIndex is counted 0 through n and is inclusive.
     * If you want the first three (3) bits to be the same then
     * pass two (2) as an argument (0 through 2 inclusive is 3).
     */
    public static KUID createWithPrefix(KUID prefix, int bitIndex) {
        // 1) Create a random KUID of the same length
        byte[] dst = new byte[prefix.length()];
        GENERATOR.nextBytes(dst);
        
        // 2) Overwrite the prefix bytes
        ++bitIndex;
        int length = bitIndex/8;
        System.arraycopy(prefix.key, 0, dst, 0, length);
        
        // 3) Overwrite the remaining bits
        int bitsToCopy = bitIndex % 8;
        if (bitsToCopy != 0) {
            // Mask has the low-order (8-bits) bits set
            int mask = (1 << (8-bitsToCopy)) - 1;
            int prefixByte = prefix.key[length];
            int randByte   = dst[length];
            dst[length] = (byte) ((prefixByte & ~mask) | (randByte & mask));
        }
        
        return create(dst);
    }
    
    /**
     * Returns a minimum {@link KUID} of the given length.
     */
    public static KUID min(int length) {
        return new KUID(new byte[length]);
    }
    
    /**
     * Returns a maximum {@link KUID} of the given length.
     */
    public static KUID max(int length) {
        byte[] maxKey = new byte[length];
        Arrays.fill(maxKey, (byte)0xFF);
        return new KUID(maxKey);
    }
    
    private final byte[] key;
    
    private final int hashCode;
    
    private KUID(byte[] key) {
        if (key.length == 0) {
            throw new IllegalArgumentException(
                    "key.length=" + key.length);
        }
        
        this.key = key;
        this.hashCode = Arrays.hashCode(key);
    }
    
    @Override
    public KUID getId() {
        return this;
    }

    /**
     * Returns {@code true} if the given {@link KUID} is compatible with
     * this {@link KUID}.
     */
    public boolean isCompatible(KUID otherId) {
        return otherId != null && length() == otherId.length();
    }
    
    /**
     * Returns the {@link KUID}'s bytes.
     */
    public byte[] getBytes() {
        return key.clone();
    }
    
    /**
     * Copies the {@link KUID}'s bytes into the given byte array.
     */
    public byte[] getBytes(byte[] dst, int destPos) {
        System.arraycopy(key, 0, dst, destPos, key.length);
        return dst;
    }
    
    /**
     * Calls {@link MessageDigest#update(byte[])} with the {@link KUID}'s bytes.
     */
    public void update(MessageDigest md) {
        md.update(key);
    }
    
    /**
     * Returns the length of the {@link KUID} in bytes.
     */
    public int length() {
        return key.length;
    }
    
    @Override
    public int lengthInBits() {
        return key.length * Byte.SIZE;
    }
    
    @Override
    public boolean isBitSet(int bitIndex) {
        int index = (int)(bitIndex / Byte.SIZE);
        int bit = (int)(bitIndex % Byte.SIZE);
        return (key[index] & mask(bit)) != 0;
    }
    
    @Override
    public int bitIndex(KUID otherId) {
        if (!isCompatible(otherId)) {
            throw new IllegalArgumentException("otherKey=" + otherId);            
        }
        
        boolean allNull = true;
        for (int i = 0; i < key.length; i++) {
            byte b1 = key[i];
            byte b2 = otherId.key[i];
            
            if (b1 != b2) {
                int xor = b1 ^ b2;
                for (int j = 0; j < Byte.SIZE; j++) {
                    if ((xor & mask(j)) != 0) {
                        return (i * Byte.SIZE) + j;
                    }
                }
            }
            
            if (b1 != 0) {
                allNull = false;
            }
        }
        
        if (allNull) {
            return KeyAnalyzer.NULL_BIT_KEY;
        }
        
        return KeyAnalyzer.EQUAL_BIT_KEY;
    }

    @Override
    public boolean isPrefixedBy(KUID prefix) {
        if (key.length < prefix.key.length) {
            return false;
        }
        
        for (int i = 0; i < prefix.key.length; i++) {
            if (key[i] != prefix.key[i]) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public KUID xor(KUID otherId) {
        if (!isCompatible(otherId)) {
            throw new IllegalArgumentException("otherId=" + otherId);
        }

        byte[] data = new byte[length()];
        for (int i = 0; i < key.length; i++) {
            data[i] = (byte) (key[i] ^ otherId.key[i]);
        }

        return new KUID(data);
    }

    @Override
    public KUID negate() {
        byte[] data = new byte[length()];
        for (int i = 0; i < key.length; i++) {
            data[i] = (byte)(~key[i]);
        }
        
        return new KUID(data);
    }
    
    /**
     * Returns the minimum {@link KUID}.
     */
    public KUID min() {
        return min(length());
    }
    
    /**
     * Returns the maximum {@link KUID}.
     */
    public KUID max() {
        return max(length());
    }
    
    /**
     * Sets the bit at the given bitIndex position to true (one, 1) 
     * and returns the {@link KUID}.
     */
    public KUID set(int bitIndex) {
        return set(bitIndex, true);
    }
    
    /**
     * Sets the bit at the given bitIndex position to false (zero, 0) 
     * and returns the {@link KUID}.
     */
    public KUID unset(int bitIndex) {
        return set(bitIndex, false);
    }
    
    /**
     * Flips the bit at the given bitIndex position and returns the 
     * {@link KUID}.
     */
    public KUID flip(int bitIndex) {
        return set(bitIndex, !isBitSet(bitIndex));
    }
    
    private KUID set(int bitIndex, boolean on) {
        int lengthInBits = lengthInBits();
        
        if (bitIndex < 0 || lengthInBits < bitIndex) {
            throw new IllegalArgumentException("bitIndex=" + bitIndex);
        }
        
        int index = (int)(bitIndex / Byte.SIZE);
        int bit = (int)(bitIndex % Byte.SIZE);
        
        int mask = mask(bit);
        int value = (int)(key[index] & 0xFF);
        
        if (on != ((value & mask) != 0x00)) {
            byte[] copy = getBytes();
            
            if (on) {
                copy[index] = (byte)(value | mask);
            } else {
                copy[index] = (byte)(value & ~mask);
            }
            return new KUID(copy);
        }
        
        return this;
    }
    
    /**
     * Returns the number of bits the two {@link KUID}s have in common.
     */
    public int commonPrefix(KUID otherId) {
        int bitIndex = bitIndex(otherId);
        if (bitIndex < 0) {
            switch (bitIndex) {
                case KeyAnalyzer.EQUAL_BIT_KEY:
                case KeyAnalyzer.NULL_BIT_KEY:
                    return lengthInBits();
                default:
                    throw new IllegalStateException("bitIndex=" + bitIndex);
            }
        }
        
        return bitIndex;
    }
    
    /**
     * Returns {@code true} if all bits of the {@link KUID} are zero
     */
    public boolean isMin() {
        return compare((byte)0x00);
    }
    
    /**
     * Returns {@code true} if all bits of the {@link KUID} are one
     */
    public boolean isMax() {
        return compare((byte)0xFF);
    }
    
    /**
     * Returns {@code true} if the {@link KUID}'s bytes have 
     * all the given expected value.
     */
    private boolean compare(byte expected) {
        for (int i = 0; i < key.length; i++) {
            if (expected != key[i]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns {@code true} if this {@link KUID} is closer in terms of XOR 
     * distance to the given key than the other {@link KUID} is to the key.
     */
    public boolean isCloserTo(KUID key, KUID otherId) {
        return compareTo(key, otherId) < 0;
    }
    
    /**
     * Compares the {@link KUID}s by their XOR distance.
     */
    public int compareTo(KUID key, KUID otherId) {
        return xor(key).compareTo(otherId.xor(key));
    }
    
    @Override
    public int compareTo(KUID otherId) {
        if (!isCompatible(otherId)) {
            throw new IllegalArgumentException("otherId=" + otherId);
        }
        
        int length = length();
        for (int i = 0; i < length; i++) {
            int diff = Bytes.compareUnsigned(key[i], otherId.key[i]);
            if (diff != 0) {
                return diff;
            }
        }
        
        return 0;
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof KUID)) {
            return false;
        }

        KUID otherId = (KUID) obj;
        return Arrays.equals(key, otherId.key);
    }

    @Override
    public KUID clone() {
        return this;
    }
    
    @Override
    public int write(OutputStream out) throws IOException {
        out.write(key);
        return length();
    }

    /**
     * Returns the {@link KUID}'s value as an {@link BigInteger}
     */
    public BigInteger toBigInteger() {
        return new BigInteger(1 /* unsigned */, key);
    }
    
    /**
     * Returns the {@link KUID}'s value as a Base 16 (hex) encoded String.
     */
    public String toHexString() {
        return CodingUtils.encodeBase16(key);
    }
    
    /**
     * Returns the {@link KUID}'s value as a Base 2 (bin) encoded String.
     */
    public String toBinString() {
        return CodingUtils.encodeBase2(key);
    }
    
    @Override
    public String toString() {
        return toHexString();
        //return toBinString();
    }
    
    /**
     * Returns a bit mask where the given bit is set
     */
    private static int mask(int bit) {
        return MSB >>> bit;
    }
}