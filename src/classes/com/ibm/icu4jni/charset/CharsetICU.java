/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetICU.java,v $ 
* $Date: 2001/10/12 01:30:56 $ 
* $Revision: 1.1 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.nio.charset.spi.CharsetProvider;
import com.ibm.icu4jni.charset.*;
import com.ibm.icu4jni.converters.*;
import com.ibm.icu4jni.common.*;

public class CharsetICU extends Charset{
    
    protected CharsetICU(String canonicalName, String[] aliases) {
	     super(canonicalName,aliases);
	     
	     /* initialize data */       
         long[] converterHandleArr = new long[1];
         // check if the converter is loaded
         if(ErrorCode.LIBRARY_LOADED==false){
            ErrorCode.LIBRARY_LOADED=true;
         }
         //System.out.println("This is the canonicalName " +canonicalName);
         
         // open the converter and get the handle 
         // if there is an error throw Unsupported encoding exception       
         if(NativeConverter.openConverter(converterHandleArr,canonicalName)
            > ErrorCode.U_ZERO_ERROR){
            throw new UnsupportedCharsetException(canonicalName);
         }
         // store the converter handle
         converterHandle=converterHandleArr[0];
    }

    private long converterHandle = 0;

    public CharsetDecoder newDecoder(){
        return new CharsetDecoderICU((Charset)this,converterHandle);
    };

    public CharsetEncoder newEncoder(){
        byte[] replacement = { 0x001a };
        return new CharsetEncoderICU((Charset)this,converterHandle,replacement);
    }; 
    
    
    public boolean contains(Charset cs){
        return false;
    }
   
    /**
     * Releases the system resources by cleanly closing ICU converter opened
     * @exception Throwable exception thrown by super class' finalize method
     */
    protected void finalize() throws Throwable{
        try{
            System.out.println("finalize called");
            NativeConverter.closeConverter(converterHandle);
        }
        finally{
            super.finalize();
        }
    }
}