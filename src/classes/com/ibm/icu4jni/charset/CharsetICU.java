/**
*******************************************************************************
* Copyright (C) 1996-2004, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetICU.java,v $ 
* $Date: 2004/06/17 20:52:12 $ 
* $Revision: 1.9 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.charset;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import com.ibm.icu4jni.common.ErrorCode;
import com.ibm.icu4jni.converters.NativeConverter;



public final class CharsetICU extends Charset{
    /**
     * Constructor to create a the CharsetICU object
     * @param canonicalName the canonical name as a string
     * @param aliases the alias set as an array of strings
     * @stable ICU 2.4
     */
    protected CharsetICU(String canonicalName, String[] aliases) {
	     super(canonicalName,aliases);
    }
    /**
     * Returns a new decoder instance of this charset object
     * @return a new decoder object
     * @stable ICU 2.4
     */
    public CharsetDecoder newDecoder(){
        // the arrays are locals and not
        // instance variables since the
        // methods on this class need to 
        // be thread safe
        long[] converterHandle = new long[1];
        int ec = NativeConverter.openConverter(converterHandle, toString());
        if(ErrorCode.isSuccess(ec)){
            return new CharsetDecoderICU(this,converterHandle[0]);
        }else{
            throw ErrorCode.getException(ec);
        }
            
    };
    
    /**
     * Returns a new encoder object of the charset
     * @return a new encoder
     * @stable ICU 2.4
     */
    public CharsetEncoder newEncoder(){
        // the arrays are locals and not
        // instance variables since the
        // methods on this class need to 
        // be thread safe
        long[] converterHandle = new long[1];
        int ec = NativeConverter.openConverter(converterHandle, toString());
        if(ErrorCode.isSuccess(ec)){
            return new CharsetEncoderICU(this,converterHandle[0]);
        }else{
            throw ErrorCode.getException(ec);
        }
    } 
    
    /**
     * Ascertains if a charset is a sub set of this charset
     * @param cs charset to test
     * @return true if the given charset is a subset of this charset
     * @stable ICU 2.4
     */
    public boolean contains(Charset cs){
        return false;
    }
}