/**
*******************************************************************************
* Copyright (C) 1996-2006, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.common;
/**
 * Class for loading the JNI library
 * @internal ICU 2.4
 */
public final class ICU4JNILoader {
    private static final String VERSION_STRING = "36";
    private static final String VERSION_STRING_DEBUG = VERSION_STRING + "d";
    /**
     * @internal ICU 2.4
     */    
    public static boolean LIBRARY_LOADED = false;
    /**
     * Loads the JNI library
     * @internal ICU 2.4
     */
    public static final void loadLibrary() 
            throws UnsatisfiedLinkError{
        try{
            System.loadLibrary("ICUInterface"+VERSION_STRING);
            ErrorCode.LIBRARY_LOADED = true;  
        }
        catch(UnsatisfiedLinkError e){
            System.loadLibrary("ICUInterface"+ VERSION_STRING_DEBUG);
        } 
    }
   
}  

