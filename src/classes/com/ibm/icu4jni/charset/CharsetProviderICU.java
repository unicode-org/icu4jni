/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetProviderICU.java,v $ 
* $Date: 2001/10/27 00:34:55 $ 
* $Revision: 1.3 $
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
	    return lookup(charsetName);
    }
    
    /**
     * Adds an entry to the given map whose key is the charset's 
     * canonical name and whose value is the charset itself. 
     * @param map a map to receive charset objects and names
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
        if(canonicalName==null){
            // this would make the Charset API to throw 
            // unsupported encoding exception
            return null;
        }else{
	        return (new CharsetICU(canonicalName, aliases(canonicalName)));
	    }
    }
    /**
     * Stores the canonical names and aliases in the local cache
     */
    private void addMapping(String canonicalName, String[] aliases) {
        // store the canonical name as one of the aliases
	    put(alias,canonicalName,canonicalName);
	    for (int i = 0; i < aliases.length; i++){
	        put(alias, aliases[i], canonicalName);
	    }
	    put(aliasNames, canonicalName, aliases);
    }
    
    // A local cache of available converters and aliases
    // needs to be maintained since Charset class maintains
    // cache for the most recently accessed charset
    
    /**
     * Canonicalizes the charset name by looking up in aliasMap
     */
    private String canonicalize(String charsetName) {
	    // get the canonical name from the cache
	    String canonicalName = (String)alias.get(charsetName);
	    if(canonicalName ==null){
	        // lazy evaluate the available converters, aliases  
	        // and add to the local cache
            canonicalName = NativeConverter.getCanonicalName(charsetName);
            if(canonicalName!=null){
                String[] aliases = (String[])NativeConverter.getAliases(canonicalName);
                // store the charsets and aliases in a Map
                addMapping(canonicalName,aliases);
            }
	    }
	    return canonicalName;
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
    private Map alias = Collections.synchronizedMap(new TreeMap(String.CASE_INSENSITIVE_ORDER));
    /**
     * Stores alias names as keys and canonical names as value
     * as value and caches it
     */
    private  Map aliasNames = Collections.synchronizedMap(new TreeMap(String.CASE_INSENSITIVE_ORDER));
    /**
     * puts the values in the the given map
     */
    private static void put(Map map, String name, Object value) {
	    if (!map.containsKey(name)){
	        map.put(name, value);
	    }
    }
}