/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetProviderICU.java,v $ 
* $Date: 2001/10/12 01:30:56 $ 
* $Revision: 1.1 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.charset;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Map;
import java.lang.ref.SoftReference;
import java.util.TreeMap;
import com.ibm.icu4jni.common.*;
import com.ibm.icu4jni.converters.NativeConverter;

public class CharsetProviderICU extends CharsetProvider{
    
    protected void charset(String canonicalName, String[] aliases) {
        // store the canonical name as one of the aliases
	    put(alias,canonicalName,canonicalName);
	    for (int i = 0; i < aliases.length; i++){
	        put(alias, aliases[i], canonicalName);
	    }
	    put(aliasNames, canonicalName, aliases);
    }

    private String canonicalize(String charsetName) {
	    // get the canonical name
	    String canonicalName = (String)alias.get(charsetName);
	    return (canonicalName != null) ? canonicalName : charsetName;
    }
    
    public CharsetProviderICU(){
        // check if the library is loaded
        // Accessing this static variable
        // executes the static block in ErrorCode
        // and loads the library
        if(ErrorCode.LIBRARY_LOADED==false){
            ErrorCode.LIBRARY_LOADED=true;
        }
        // Get the available converter canonical names and aliases
        Object[] charsets = NativeConverter.getAvailable();
        for(int i=0; i<charsets.length;i++){
            // get the ICU aliases for a converter
            Object[] aliases = NativeConverter.getAliases((String)charsets[i]);
            // store the charsets and aliases in a Map
            charset((String)charsets[i],(String[])aliases);
        }
    }
    private Charset lookup(String charsetName) {
	    // get the canonical name
	    String canonicalName = canonicalize(charsetName);
        // create the converter object and return it
	    return (new CharsetICU(canonicalName, aliases(canonicalName)));
    }

    public final Charset charsetForName(String charsetName) {
	    return lookup(charsetName);
    }

    public final void putCharsets(Map m) {
        // This method is of no use for ICU4JNI 
        // The library is statically loaded and
        // the charset names and aliases are obtained
        // from the library. There is no way a client
        // can write his own converter, plug it into this
        // provider and make use of it.
        //
    }

    private final String[] aliases(String charsetName) {
	    return (String[])aliasNames.get(charsetName);
    }
    private  Map alias =new TreeMap(String.CASE_INSENSITIVE_ORDER);

    private  Map aliasNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);

    private static void put(Map map, String name, Object value) {
	    if (!map.containsKey(name)){
	        map.put(name, value);
	    }
    }
}