/**
*******************************************************************************
* Copyright (C) 1996-2004, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetICU.java,v $ 
* $Date: 2004/12/30 21:17:38 $ 
* $Revision: 1.11 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.charset;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import com.ibm.icu4jni.common.ErrorCode;
import com.ibm.icu4jni.converters.NativeConverter;



public final class CharsetICU extends Charset{
    private String icuCanonicalName;
    /**
     * Constructor to create a the CharsetICU object
     * @param canonicalName the canonical name as a string
     * @param aliases the alias set as an array of strings
     * @stable ICU 2.4
     */
    protected CharsetICU(String canonicalName, String icuCanonName, String[] aliases) {
	     super(canonicalName,aliases);
         icuCanonicalName = icuCanonName;
        
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
        int ec = NativeConverter.openConverter(converterHandle, icuCanonicalName);
        if(ErrorCode.isSuccess(ec)){
            return new CharsetDecoderICU(this,converterHandle[0]);
        }else{
            throw ErrorCode.getException(ec);
        }
            
    };
    
    // hardCoded list of replacement bytes
    private static final Map subByteMap = new HashMap();
    static{
        subByteMap.put("UTF-32",new byte[]{0x00, 0x00, (byte)0xfe, (byte)0xff});
        subByteMap.put("ibm-16684_P110-2003",new byte[]{0x40, 0x40}); // make \u3000 the sub char
        subByteMap.put("ibm-971_P100-1995",new byte[]{(byte)0xa1, (byte)0xa1}); // make \u3000 the sub char
    }
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
        int ec = NativeConverter.openConverter(converterHandle, icuCanonicalName);
        
        //According to the contract all converters should have non-empty replacement
        byte[] replacement = NativeConverter.getSubstitutionBytes(converterHandle[0]);

        if(ErrorCode.isSuccess(ec)){
            try{
                return new CharsetEncoderICU(this,converterHandle[0], replacement);
            }catch(IllegalArgumentException ex){
                // work around for the non-sensical check in the nio API that
                // a substitution character must be mappable while decoding!!
                replacement = (byte[])subByteMap.get(icuCanonicalName);
                if(replacement==null){
                    replacement = new byte[NativeConverter.getMinBytesPerChar(converterHandle[0])];
                    for(int i=0; i<replacement.length; i++){
                        replacement[i]= 0x3f;
                    }
                }
                NativeConverter.setSubstitutionBytes(converterHandle[0], replacement, replacement.length);;
                return new CharsetEncoderICU(this,converterHandle[0], replacement);
            }
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