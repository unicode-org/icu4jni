/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetEncoderICU.java,v $ 
* $Date: 2001/10/18 01:16:44 $ 
* $Revision: 1.3 $
*
*******************************************************************************
*/ 
 /** 
  * A JNI interface for ICU converters.
  *
  * 
  * @author Ram Viswanadha, IBM
  */
package com.ibm.icu4jni.charset;
import java.nio.charset.*;
import java.nio.charset.CharsetEncoder;
import java.nio.*;
import com.ibm.icu4jni.converters.NativeConverter;
import com.ibm.icu4jni.common.*;
import java.nio.charset.CodingErrorAction;

public class CharsetEncoderICU extends CharsetEncoder{
    /* data is 2 element array where
     * data[0] = inputOffset
     * data[1] = outputOffset
     */
    private int[] data = new int[2];
    
    /* handle to the ICU converter that is opened */
    private final long converterHandle;
    
    private byte[] replacement;
    
    static{
        // check if the converter is loaded
        if(ErrorCode.LIBRARY_LOADED==false){
            ErrorCode.LIBRARY_LOADED=true;
        }
    }
    
    /** 
     * Construcs a new encoder for the given charset
     * @param charset for which the decoder is created
     * @param cHandle the address of ICU converter
     * @param substitue error sequences in the input with 
     *        this string in output
     */
    public CharsetEncoderICU(Charset cs,String canonicalName,byte[] replacement){
         super(cs,
               (float)NativeConverter.aveBytesPerChar(canonicalName),
               (float)NativeConverter.maxBytesPerChar(canonicalName),
               replacement);    
               
         data[0] = 0;
         data[1] = 0;
	     /* initialize data */       
         long[] converterHandleArr = new long[1];
        
         // open the converter and get the handle 
         // if there is an error throw Unsupported encoding exception    
         int errorCode = NativeConverter.openConverter(converterHandleArr,canonicalName);
         if(errorCode > ErrorCode.U_ZERO_ERROR){
            throw new UnsupportedCharsetException(canonicalName  + 
                                                  " ErrorCode: " +
                                                  ErrorCode.getErrorName(errorCode));
         }
         
         // store the converter handle
         converterHandle=converterHandleArr[0];
         
         // The default callback action on unmappable input 
         // or malformed input is to report so we set ICU converter
         // callback to stop
         errorCode = NativeConverter.setCallbackEncode(converterHandle,
                                                       NativeConverter.STOP_CALLBACK,
                                                       false);
         implReplaceWith(replacement);
    }
  
    /**
     * Sets this encoders replacement string. Substitutes the string in output if an
     * umappable or illegal sequence is encountered
     * @param string to replace the error chars with
     */   
    protected void implReplaceWith(byte[] newReplacement){
        if(converterHandle > 0){
            if( newReplacement.length > NativeConverter.getMaxBytesPerChar(converterHandle) ) {
                throw new IllegalArgumentException();
            }
            int ec =NativeConverter.setSubstitutionBytes(converterHandle,
                                                            newReplacement,
                                                            newReplacement.length);
            if(ec > ErrorCode.U_ZERO_ERROR){
                throw new IllegalArgumentException(ErrorCode.getErrorName(ec));
            }
        }
        replacement = newReplacement;

    }

    /**
     * Sets the action to be taken if an illegal sequence is encountered
     * @param new action to be taken
     * @exception IllegalArgumentException
     */
    protected void implOnMalformedInput(CodingErrorAction newAction) {
        int icuAction = NativeConverter.STOP_CALLBACK;
        
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        
        if(NativeConverter.setCallbackEncode(converterHandle,icuAction,false)
                > ErrorCode.U_ZERO_ERROR){
            throw new IllegalArgumentException();
        } 
                
    }

    
    /**
     * Sets the action to be taken if an illegal sequence is encountered
     * @param new action to be taken
     * @exception IllegalArgumentException
     */
    protected void implOnUnmappableCharacter(CodingErrorAction newAction){
        int icuAction = NativeConverter.STOP_CALLBACK;
        
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        int ec = NativeConverter.setCallbackEncode(converterHandle,icuAction,true);
        if(ec > ErrorCode.U_ZERO_ERROR){
            throw new IllegalArgumentException(ErrorCode.getErrorName(ec));
        } 
    }  
    
    /**
     * Flushes any characters saved in the converter's internal buffer and
     * resets the converter.
     * @param new action to be taken
     * @return result of flushing action and completes the decoding all input. 
     *         Returns CoderResult.UNDERFLOW if the action succeeds.
     */
    protected CoderResult implFlush(ByteBuffer out) {       
        /*set inputStart to 0 */ 
        data[0] = 0; 
        data[1] = 0;
        
        int outEnd = out.remaining();
        byte[] output = new byte[outEnd];
        
        /* assume that output buffer is big enough since error is not handled*/
        int err=NativeConverter.flushCharToByte(
                                        converterHandle,  /* Handle to ICU Converter */
                                        output,           /* output array of chars */
                                        outEnd,           /* output index+1 to be written */
                                        data              /* contains data, inOff,outOff */
                                        );
                                  
        
        int[] retVal = new int[1];
            
        /* If we don't have room for the output, throw an exception*/
        if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		    return CoderResult.OVERFLOW;
		}
        if(data[1]>0){
		    out.put(output,0,data[1]);       // output offset
        }
	    implReset();
	    return  CoderResult.UNDERFLOW;
    }
    
    /**
     * Resets the from Unicode mode of converter
     */
    protected void implReset() { 
	    NativeConverter.resetCharToByte(converterHandle);
    }
    
    /**
     * Encodes one or more chars. The default behaviour of the
     * converter is stop and report if an error in input stream is encountered.
     * To set different behaviour use @see CharsetEncoder.onMalformedInput()
     * @param input buffer to decode
     * @param output buffer to populate with decoded result
     * @return result of decoding action. Returns CoderResult.UNDERFLOW if the decoding
     *         action succeeds or more input is needed for completing the decoding action.
     */
    protected CoderResult encodeLoop(CharBuffer in,ByteBuffer out){
        
        int inEnd = in.remaining();
        int outEnd = out.remaining();
        char[] input = new char[inEnd];
        byte[] output=new byte[outEnd];
        // save the current position 
        int pos = in.position();
        in.get(input,0,inEnd);
        // reset 
        in.position(pos);
     
        data[0] = 0;  // input offset 
        data[1] = 0;  // output offset 
        try{
            /* do the conversion */
            int err=NativeConverter.convertCharToByte(
                                converterHandle,  /* Handle to ICU Converter */
                                input,            /* input array of bytes */
                                inEnd,            /* last index+1 to be converted */
                                output,           /* output array of chars */
                                outEnd,           /* output index+1 to be written */
                                data,             /* contains data, inOff,outOff */
                                false             /* donot flush the data */
                                );
            
            
            int[] retVal = new int[1];
            
            /* If we don't have room for the output, throw an exception*/
            if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		        return CoderResult.OVERFLOW;
		    }
            else if(err==ErrorCode.U_INVALID_CHAR_FOUND){
                NativeConverter.countInvalidBytes(converterHandle, retVal);
		        return CoderResult.unmappableForLength(retVal[0]);
		    }else if(err==ErrorCode.U_ILLEGAL_CHAR_FOUND){
                NativeConverter.countInvalidBytes(converterHandle, retVal);	
                return CoderResult.malformedForLength(retVal[0]);
            }
            return CoderResult.UNDERFLOW;
        }finally{
            if(data[0]>0){
                in.position(in.position()+data[0]); // input offset
            }
            if(data[1]>0){
		        out.put(output,0,data[1]);       // output offset 
		    }		    
		}
	}
	
    /**
     * Ascertains if a given Unicode character can 
     * be converted to the target encoding
     *
     * @param  the character to be converted
     * @return true if a character can be converted
     * 
     */
    public boolean canEncode(char c) {
        return canEncode((int) c);
    }
    
    /**
     * Ascertains if a given Unicode codeunit (32bit value for handling surrogates)
     * can be converted to the target encoding. If the caller wants to test if a
     * surrogate pair can be converted to target encoding then the
     * responsibility of assembling the int value lies with the caller.
     * For assembling a codeunit the caller has to do something like:
     *
     * while(i<mySource.length){
     *        if(isFirstSurrogate(mySource[i])&& i+1< mySource.length){
     *            if(isSecondSurrogate(mySource[i+1])){
     *                temp = (((mySource[i])<<(long)10)+(mySource[i+1])-((0xd800<<(long)10)+0xdc00-0x10000));
     *                if(!((CharsetEncoderICU) myConv).canEncode(temp)){
     *                    passed=false;
     *                }
     *                i++;
     *                i++;
     *            }
     *       }
     * }
     * 
     * @param Unicode codeunit as int value
     * @return true if a character can be converted
     * 
     */
    public boolean canEncode(int codeUnit){
        return NativeConverter.canEncode(converterHandle, codeUnit);
    }
    
    /**
     * Releases the system resources by cleanly closing ICU converter opened
     * @exception Throwable exception thrown by super class' finalize method
     */
    protected void finalize() throws Throwable{
        try{
            NativeConverter.closeConverter(converterHandle);
        }
        finally{
            super.finalize();
        }
    }
}