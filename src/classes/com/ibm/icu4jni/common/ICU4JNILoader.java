/**
*******************************************************************************
* Copyright (C) 1996-2005, International Business Machines Corporation and    *
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
            System.loadLibrary("ICUInterface32");
            ErrorCode.LIBRARY_LOADED = true;  
        }
        catch(UnsatisfiedLinkError e){
            System.loadLibrary("ICUInterface32d");
        } 
    }
   
}  

