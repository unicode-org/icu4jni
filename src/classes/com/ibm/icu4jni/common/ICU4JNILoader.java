/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/common/ICU4JNILoader.java,v $ 
* $Date: 2002/04/09 05:59:44 $ 
* $Revision: 1.3 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.common;

public final class ICU4JNILoader {
    
    public static boolean LIBRARY_LOADED = false;
    
    public static final void loadLibrary() 
            throws UnsatisfiedLinkError{
        try{
            System.loadLibrary("ICUInterface21");
            ErrorCode.LIBRARY_LOADED = true;  
        }
        catch(UnsatisfiedLinkError e){
            System.loadLibrary("ICUInterface21d");
        } 
    }
   
}  

