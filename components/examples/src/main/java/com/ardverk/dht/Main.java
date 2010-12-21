/*
 * Copyright 2009-2010 Roger Kapsi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ardverk.dht;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.ardverk.io.IoUtils;
import org.ardverk.utils.DeadlockScanner;

import bsh.Interpreter;

public class Main {
    
    public static void main(String[] args) throws Exception {
        
        DeadlockScanner.start();
        
        Interpreter bsh 
            = InterpreterFactory.create();
        
        Reader example = loadExample();
        if (example != null) {
            try {
                bsh.eval(example);
            } finally {
                IoUtils.close(example);
            }
        }
        
        bsh.run();
    }
    
    private static Reader loadExample() {
        InputStream in = Main.class.getResourceAsStream("example.bsh");
        if (in != null) {
            return new BufferedReader(new InputStreamReader(in));
        }
        return null;
    }
}