/**
*******************************************************************************
* Copyright (C) 1996-2003, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/common/ICU4JNILoader.java,v $ 
* $Date: 2003/12/09 23:51:08 $ 
* $Revision: 1.9 $
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
            System.loadLibrary("ICUInterface28");
            ErrorCode.LIBRARY_LOADED = true;  
        }
        catch(UnsatisfiedLinkError e){
            System.loadLibrary("ICUInterface28d");
        } 
    }
   
}  

