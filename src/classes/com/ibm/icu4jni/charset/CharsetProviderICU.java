/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetProviderICU.java,v $ 
* $Date: 2001/11/02 02:28:25 $ 
* $Revision: 1.4 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.charset;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Iterator;
import com.ibm.icu4jni.common.*;
import com.ibm.icu4jni.converters.NativeConverter;

public final class CharsetProviderICU extends CharsetProvider{
    
    /**
     * Constructs a CharsetProviderICU object 
     */
    public CharsetProviderICU(){
    }
    
    /**
     * Constructs a charset for the given charset name
     * @param charset name
     * @return charset objet for the given charset name
     */
    public final Charset charsetForName(String charsetName) {
	    // get the canonical name	 
        String canonicalName = NativeConverter.getCanonicalName(charsetName);            
        // create the converter object and return it
        if(canonicalName==null){
            // this would make the Charset API to throw 
            // unsupported encoding exception
            return null;
        }else{
            String[] aliases = (String[])NativeConverter.getAliases(charsetName);            
	        return (new CharsetICU(canonicalName, aliases));
	    }
    }
    
    /**
     * Adds an entry to the given map whose key is the charset's 
     * canonical name and whose value is the charset itself. 
     * @param map a map to receive charset objects and names
     */
    public final void putCharsets(Map map) {
        // Get the available converter canonical names and aliases        
        String[] charsets = NativeConverter.getAvailable();        
        for(int i=0; i<charsets.length;i++){
            // get the ICU aliases for a converter            
            String[] aliases = NativeConverter.getAliases(charsets[i]);            
            // store the charsets and aliases in a Map    
	        if (!map.containsKey(charsets[i])){
	            map.put(charsets[i], aliases);
	        }
        }
    }    
}