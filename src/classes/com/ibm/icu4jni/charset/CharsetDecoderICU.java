/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetDecoderICU.java,v $ 
* $Date: 2001/11/03 03:25:11 $ 
* $Revision: 1.5 $
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

public final class CharsetDecoderICU extends CharsetDecoder{ 
        
    /* data is 2 element array where
     * data[INPUT_CONSUMED] = number of input bytes consumed
     * data[OUTPUT_WRITTEN] = number of chars written to output
     * data[INVALID_BYTES]  = number of invalid bytes
     */
    private int[] data = new int[3];
    
    /* handle to the ICU converter that is opened */
    private final long converterHandle;
      
    private static final int INPUT_CONSUMED = 0;
    private static final int OUTPUT_WRITTEN = 1;
    private static final int INVALID_BYTES  = 2;
    
    private  byte[] input = null;
    private  char[] output= null;
    
    // These instance variables are
    // always assigned in the methods
    // before being used. This class
    // inhrently multithread unsafe
    // so we dont have to worry about
    // synchronization
    private int inEnd;
    private int outEnd;
    private int save;
    private int ec;
    private int icuAction;
    
    /** 
     * Construcs a new decoder for the given charset
     * @param charset for which the decoder is created
     * @param cHandle the address of ICU converter
     * @exception UnsupportedCharsetException
     */
    public CharsetDecoderICU(Charset cs,long cHandle){
         super(cs,
               NativeConverter.getAveCharsPerByte(cHandle),
               NativeConverter.getMaxCharsPerByte(cHandle)
               );
                       
         // The default callback action on unmappable input 
         // or malformed input is to report so we set ICU converter
         // callback to stop
         ec = NativeConverter.setCallbackDecode(cHandle,
                                                NativeConverter.STOP_CALLBACK,
                                                false);
         if(ErrorCode.isFailure(ec)){
            throw ErrorCode.getException(ec);
         }
         // store the converter handle
         converterHandle=cHandle;

    }
    
    /**
     * Sets this decoders replacement string. Substitutes the string in input if an
     * umappable or illegal sequence is encountered
     * @param string to replace the error bytes with
     */    
    protected void implReplaceWith(String newReplacement) {
        if(converterHandle > 0){
            if( newReplacement.length() > NativeConverter.getMaxBytesPerChar(converterHandle)) {
                    throw new IllegalArgumentException();
            }           
            ec =NativeConverter.setSubstitutionChars(converterHandle,
                                                    newReplacement.toCharArray(),
                                                    newReplacement.length()
                                                    );
            if(ErrorCode.isFailure(ec)){
                throw ErrorCode.getException(ec);
            }
        }
     }
    
    /**
     * Sets the action to be taken if an illegal sequence is encountered
     * @param new action to be taken
     * @exception IllegalArgumentException
     */
    protected final void implOnMalformedInput(CodingErrorAction newAction) {
        icuAction = NativeConverter.STOP_CALLBACK;
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
 
        ec =NativeConverter.setCallbackDecode(converterHandle,icuAction,false);
        if(ErrorCode.isFailure(ec)){
            throw ErrorCode.getException(ec);
        } 
    }
    
    /**
     * Sets the action to be taken if an illegal sequence is encountered
     * @param new action to be taken
     * @exception IllegalArgumentException
     */
    protected final void implOnUnmappableCharacter(CodingErrorAction newAction) {
        icuAction = NativeConverter.STOP_CALLBACK;
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        ec = NativeConverter.setCallbackDecode(converterHandle,icuAction,true);
        if(ErrorCode.isFailure(ec)){
            throw ErrorCode.getException(ec);
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
       try{
            getArray(out);
            
            ec=NativeConverter.flushByteToChar(
                                            converterHandle,  /* Handle to ICU Converter */
                                            output,           /* input array of chars */
                                            outEnd,           /* input index+1 to be written */
                                            data              /* contains data, inOff,outOff */
                                            );
                                      
            
            /* if we donot have room for input throw an error*/
            if(ec == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		        return CoderResult.OVERFLOW;
		    }
	        implReset();
	        return CoderResult.UNDERFLOW;
	   }finally{
            /* save the flushed data */
            setPosition(out);
	   }
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
     * @param input buffer to populate with decoded result
     * @return result of decoding action. Returns CoderResult.UNDERFLOW if the decoding
     *         action succeeds or more input is needed for completing the decoding action.
     */
    protected CoderResult decodeLoop(ByteBuffer in,CharBuffer out){


        if(!in.hasRemaining()){
            return CoderResult.UNDERFLOW;
        }

        getArray(in);
        getArray(out);
        try{
            /* do the conversion */
            ec=NativeConverter.decode(
                                converterHandle,  /* Handle to ICU Converter */
                                input,            /* input array of bytes */
                                inEnd,            /* last index+1 to be converted */
                                output,           /* input array of chars */
                                outEnd,           /* input index+1 to be written */
                                data,             /* contains data, inOff,outOff */
                                false             /* donot flush the data */
                                );
            

            /* return an error*/
            if(ec == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		        return CoderResult.OVERFLOW;
		    }else if(ec==ErrorCode.U_INVALID_CHAR_FOUND){
		        return CoderResult.unmappableForLength(data[INVALID_BYTES]);
		    }else if(ec==ErrorCode.U_ILLEGAL_CHAR_FOUND){
                return CoderResult.malformedForLength(data[INVALID_BYTES]);
            }
            /* decoding action succeded */
            return CoderResult.UNDERFLOW;
        }finally{
            setPosition(in);
            setPosition(out);
        }
	}
	
	/**
     * Releases the system resources by cleanly closing ICU converter opened
     */
    protected void finalize()throws Throwable{
        NativeConverter.closeConverter(converterHandle);
        super.finalize();
    }
    
    //------------------------------------------
    // private utility methods
    //------------------------------------------

    private final void getArray(CharBuffer out){
        if(out.hasArray()){
            output = out.array();
            outEnd = out.arrayOffset() + out.limit();
            data[OUTPUT_WRITTEN] = (out.arrayOffset()+out.position());
        }else{
            outEnd = out.remaining();
            if(output==null || outEnd > output.length){
                output = new char[outEnd];
            }
            //since the new 
            // buffer start position 
            // is 0
            data[OUTPUT_WRITTEN] = 0;
        }
        
    }
    private  final void getArray(ByteBuffer in){
        if(in.hasArray()){
            input = in.array();
            inEnd = in.arrayOffset() + in.limit();
            data[INPUT_CONSUMED] = (in.arrayOffset()+in.position());
        }else{
            inEnd = in.remaining();
            if(input==null|| inEnd > input.length){ 
                input = new byte[inEnd];
            }
            // save the current position
            int pos = in.position();
            in.get(input,0,inEnd);
            // reset the position
            in.position(pos);
            // the start position  
            // of the new buffer  
            // is 0
            data[INPUT_CONSUMED] = 0;
        }
       
    }
    private final void setPosition(CharBuffer out){
        if(out.hasArray()){
		    out.position(out.position() + data[OUTPUT_WRITTEN] - out.arrayOffset());
        }else{
            out.put(output,0,data[OUTPUT_WRITTEN]);
        }
    }
    private final void setPosition(ByteBuffer in){   
        if(in.hasArray()){
		    in.position(in.position()+data[INPUT_CONSUMED] - in.arrayOffset());
        }else{
            in.position(in.position()+data[INPUT_CONSUMED]);
        }     
    }
}