/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetDecoderICU.java,v $ 
* $Date: 2001/10/18 01:16:44 $ 
* $Revision: 1.3 $
*
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

public class CharsetDecoderICU extends CharsetDecoder{ 
        
    /* data is 2 element array where
     * data[0] = inputOffset
     * data[1] = outputOffset
     */
    private int[] data = new int[2];
    
    /* handle to the ICU converter that is opened */
    private final long converterHandle;
      
    private String replacement;
    
    static{
        // check if the converter is loaded
        if(ErrorCode.LIBRARY_LOADED==false){
            ErrorCode.LIBRARY_LOADED=true;
        }
    }
    
    /** 
     * Construcs a new decoder for the given charset
     * @param charset for which the decoder is created
     * @param cHandle the address of ICU converter
     * @exception UnsupportedCharsetException
     */
    public CharsetDecoderICU(Charset cs,String canonicalName){
         super(cs,
               1.0f,/* AverageCharsPerByte */
               2.0f /* maxCharsPerByte     */);        
               
         data[0] = 0;
         data[1] = 0;
	     /* initialize data */       
         long[] converterHandleArr = new long[1];
        
         // open the converter and get the handle 
         // if there is an error throw Unsupported encoding exception    
         int errorCode = NativeConverter.openConverter(converterHandleArr,canonicalName);
         
         if(errorCode > ErrorCode.U_ZERO_ERROR){
            throw new UnsupportedCharsetException(canonicalName + 
                                                  " ErrorCode: "+
                                                  ErrorCode.getErrorName(errorCode));
         }
         
         // store the converter handle
         converterHandle=converterHandleArr[0];
         
         // The default callback action on unmappable input 
         // or malformed input is to report so we set ICU converter
         // callback to stop
         errorCode = NativeConverter.setCallbackDecode(converterHandle,
                                                       NativeConverter.STOP_CALLBACK,
                                                       false);
         implReplaceWith(replacement);
    }
    
    /**
     * Sets this decoders replacement string. Substitutes the string in output if an
     * umappable or illegal sequence is encountered
     * @param string to replace the error bytes with
     */    
    protected void implReplaceWith(String newReplacement) {
        if(converterHandle > 0){
            if( newReplacement.length() > NativeConverter.getMaxBytesPerChar(converterHandle)) {
                    throw new IllegalArgumentException();
            }           
            int ec =NativeConverter.setSubstitutionChars(converterHandle,
                                                    newReplacement.toCharArray(),
                                                    newReplacement.length()
                                                    );
            if( ec > ErrorCode.U_ZERO_ERROR){
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
    protected final void implOnMalformedInput(CodingErrorAction newAction) {
        int icuAction = NativeConverter.STOP_CALLBACK;
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        if(converterHandle!=0){   
            int ec =NativeConverter.setCallbackDecode(converterHandle,icuAction,false);
            if(ec > ErrorCode.U_ZERO_ERROR){
                throw new IllegalArgumentException(ErrorCode.getErrorName(ec));
            } 
        }
    }
    
    /**
     * Sets the action to be taken if an illegal sequence is encountered
     * @param new action to be taken
     * @exception IllegalArgumentException
     */
    protected final void implOnUnmappableCharacter(CodingErrorAction newAction) {
        int icuAction = NativeConverter.STOP_CALLBACK;
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        if(converterHandle!=0){   
            int ec = NativeConverter.setCallbackDecode(converterHandle,icuAction,true);
            if(ec > ErrorCode.U_ZERO_ERROR){
                throw new IllegalArgumentException(ErrorCode.getErrorName(ec));
            } 
        }
    }
    
    /**
     * Flushes any characters saved in the converter's internal buffer and
     * resets the converter.
     * @param new action to be taken
     * @return result of flushing action and completes the decoding all input. 
     *         Returns CoderResult.UNDERFLOW if the action succeeds.
     */
    protected final CoderResult implFlush(CharBuffer out) {
       
        /*set inputStart to 0 */ 
        data[0] = 0; 
        data[1] = 0;
        
        int outEnd = out.remaining();
        char[] output = new char[outEnd];

        /* assume that output buffer is big enough since error is not handled*/
        int err=NativeConverter.flushByteToChar(
                                        converterHandle,  /* Handle to ICU Converter */
                                        output,           /* output array of chars */
                                        outEnd,           /* output index+1 to be written */
                                        data              /* contains data, inOff,outOff */
                                        );
                                  
        
        
        /* if we donot have room for output throw an error*/
        if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		    return CoderResult.OVERFLOW;
		}
		out.put(output,0,data[1]);
	    implReset();
	    return CoderResult.UNDERFLOW;
    }
    
    /**
     * Resets the to Unicode mode of converter
     */
    protected void implReset() {
        NativeConverter.resetByteToChar(converterHandle);
    }
    
    /**
     * Decodes one or more bytes. The default behaviour of the converter
     * is stop and report if an error in input stream is encountered. 
     * To set different behaviour use @see CharsetDecoder.onMalformedInput()
     * This  method allows a buffer by buffer conversion of a data stream.  
     * The state of the conversion is saved between calls to convert.  
     * Among other things, this means multibyte input sequences can be 
     * split between calls. If a call to convert results in an Error, the 
     * conversion may be continued by calling convert again with suitably 
     * modified parameters.All conversions should be finished with a call to 
     * the flush method.
     * @param input buffer to decode
     * @param output buffer to populate with decoded result
     * @return result of decoding action. Returns CoderResult.UNDERFLOW if the decoding
     *         action succeeds or more input is needed for completing the decoding action.
     */
    protected CoderResult decodeLoop(ByteBuffer in,CharBuffer out){

	    int inEnd = in.remaining();
        int outEnd = out.remaining();
        byte[] input = new byte[inEnd];
        char[] output= new char[outEnd];
        if(!in.hasRemaining()){
            return CoderResult.UNDERFLOW;
        }
        int pos = in.position();
        in.get(input,0,inEnd);
        /* reset the position */
        in.position(pos);
                
        data[0] = 0;  // input offset 
        data[1] = 0;  // output offset 
        try{
            /* do the conversion */
            int err=NativeConverter.convertByteToChar(
                                converterHandle,  /* Handle to ICU Converter */
                                input,            /* input array of bytes */
                                inEnd,            /* last index+1 to be converted */
                                output,           /* output array of chars */
                                outEnd,           /* output index+1 to be written */
                                data,             /* contains data, inOff,outOff */
                                false             /* donot flush the data */
                                );
            

            int[] retVal = new int[1];
            /* return an error*/
            if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		        return CoderResult.OVERFLOW;
		    }else if(err==ErrorCode.U_INVALID_CHAR_FOUND){
                NativeConverter.countInvalidChars(converterHandle, retVal);
		        return CoderResult.unmappableForLength(retVal[0]);
		    }else if(err==ErrorCode.U_ILLEGAL_CHAR_FOUND){
                NativeConverter.countInvalidChars(converterHandle, retVal);	
                return CoderResult.malformedForLength(retVal[0]);
            }
            /* decoding action succeded */
            return CoderResult.UNDERFLOW;
        }finally{
            
           /* save state */
            if(data[0]>0){
                in.position(in.position()+data[0]);
            }
            if(data[1]>0){
		        out.put(output,0,data[1]);        /* output offset */
		    }
		   
        }

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