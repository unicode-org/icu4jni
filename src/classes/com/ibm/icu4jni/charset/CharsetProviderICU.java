/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetProviderICU.java,v $ 
* $Date: 2001/10/18 01:16:44 $ 
* $Revision: 1.2 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.charset;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Map;
import java.lang.ref.SoftReference;
import java.util.TreeMap;
import java.util.Iterator;
import com.ibm.icu4jni.common.*;
import com.ibm.icu4jni.converters.NativeConverter;

public class CharsetProviderICU extends CharsetProvider{
    
    static{
        // check if the library is loaded
        // Accessing this static variable
        // executes the static block in ErrorCode
        // and loads the library
        if(ErrorCode.LIBRARY_LOADED==false){
            ErrorCode.LIBRARY_LOADED=true;
        }
    }
    
    /**
     * Constructs a CharsetProviderICU object 
     */
    public CharsetProviderICU(){
        // Get the available converter canonical names and aliases
        Object[] charsets = NativeConverter.getAvailable();
        for(int i=0; i<charsets.length;i++){
            // get the ICU aliases for a converter
            Object[] aliases = NativeConverter.getAliases((String)charsets[i]);
            // store the charsets and aliases in a Map
            charset((String)charsets[i],(String[])aliases);
        }
    }
    
    /**
     * Constructs a charset for the given charset name
     * @param charset name
     * @return charset objet for the given charset name
     */
    public final Charset charsetForName(String charsetName) {
	    return lookup(charsetName);
    }
    
    /**
     * Adds an entry to the given map whose key is the charset's 
     * canonical name and whose value is the charset itself. 
     */
    public final void putCharsets(Map map) {
        //get the iterator for the keys in aliases map
        Iterator iter = alias.keySet().iterator();
        for (; iter.hasNext();) {
	        String canonicalName = (String)iter.next();
	        put(map, canonicalName, lookup(canonicalName));
	    }
    }
    
    //--------------------------------------------------
    // Private utility methods
    //--------------------------------------------------
    
    /**
     * Looks up the charsetName in local cache, constructs a charset
     * object and returns it
     * @param charset name
     * @return charset objet for the given charset name
     */
    private Charset lookup(String charsetName) {
	    // get the canonical name
	    String canonicalName = canonicalize(charsetName);
        // create the converter object and return it
	    return (new CharsetICU(canonicalName, aliases(canonicalName)));
    }
    /**
     * Stores the canonical names and aliases in the local cache
     */
    private void charset(String canonicalName, String[] aliases) {
        // store the canonical name as one of the aliases
	    put(alias,canonicalName,canonicalName);
	    for (int i = 0; i < aliases.length; i++){
	        put(alias, aliases[i], canonicalName);
	    }
	    put(aliasNames, canonicalName, aliases);
    }
    /**
     * Canonicalizes the charset name by looking up in aliasMap
     */
    private String canonicalize(String charsetName) {
	    // get the canonical name
	    String canonicalName = (String)alias.get(charsetName);
	    return (canonicalName != null) ? canonicalName : charsetName;
    }
    
    /**
     * Retrieves aliases for a given charset from
     * the alias map
     */
    private final String[] aliases(String canonicalName) {
	    return (String[])aliasNames.get(canonicalName);
    }
    /**
     * Stores canonical names as keys and string array aliases
     * as value and caches it
     */
    private Map alias =new TreeMap(String.CASE_INSENSITIVE_ORDER);
    /**
     * Stores alias names as keys and canonical names as value
     * as value and caches it
     */
    private  Map aliasNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    /**
     * puts the values in the the given map
     */
    private static void put(Map map, String name, Object value) {
	    if (!map.containsKey(name)){
	        map.put(name, value);
	    }
    }
}