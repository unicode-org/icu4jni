/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetEncoderICU.java,v $ 
* $Date: 2001/11/21 22:12:09 $ 
* $Revision: 1.6 $
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

public final class CharsetEncoderICU extends CharsetEncoder{
    /* data is 3 element array where
     * data[INPUT_CONSUMED] = number of input chars consumed
     * data[OUTPUT_WRITTEN] = number of output bytes written
     * data[INVALID_CHARS]  = number of invalid chars
     */
    private int[] data = new int[3];
    
    private static final int INPUT_CONSUMED  = 0;
    private static final int OUTPUT_WRITTEN = 1;
    private static final int INVALID_CHARS = 2;
    
    /* handle to the ICU converter that is opened */
    private final long converterHandle;
    
    private  char[] input = null;
    private  byte[] output= null;
    
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
     * Construcs a new encoder for the given charset
     * @param charset for which the decoder is created
     * @param cHandle the address of ICU converter
     */
    public CharsetEncoderICU(Charset cs,long cHandle){
         super(cs,
               (float)NativeConverter.getAveBytesPerChar(cHandle),
               (float)NativeConverter.getMaxBytesPerChar(cHandle),
               NativeConverter.getSubstitutionBytes(cHandle).getBytes()
              );    
      
         // The default callback action on unmappable input 
         // or malformed input is to ignore so we set ICU converter
         // callback to stop and report the error
         ec = NativeConverter.setCallbackEncode(cHandle,
                                                NativeConverter.STOP_CALLBACK,
                                                false);
         converterHandle = cHandle;
         if(ErrorCode.isFailure(ec)){
            throw ErrorCode.getException(ec);
         }   
    }
  
    /**
     * Sets this encoders replacement string. Substitutes the string in output if an
     * umappable or illegal sequence is encountered
     * @param string to replace the error chars with
     */   
    protected void implReplaceWith(byte[] newReplacement){
        if(converterHandle != 0 ){
            if( newReplacement.length > NativeConverter.getMaxBytesPerChar(converterHandle) ) {
                System.out.println(converterHandle);
                throw new IllegalArgumentException("Number of replacement Bytes are greater than max bytes per char" );
            }
            ec =NativeConverter.setSubstitutionBytes(converterHandle,
                                                    newReplacement,
                                                    newReplacement.length);
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
    protected void implOnMalformedInput(CodingErrorAction newAction) {
        icuAction = NativeConverter.STOP_CALLBACK;
        
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        ec = NativeConverter.setCallbackEncode(converterHandle,icuAction,false);
        if( ErrorCode.isFailure(ec)){
            throw ErrorCode.getException(ec);
        } 
                
    }

    
    /**
     * Sets the action to be taken if an illegal sequence is encountered
     * @param new action to be taken
     * @exception IllegalArgumentException
     */
    protected void implOnUnmappableCharacter(CodingErrorAction newAction){
        icuAction = NativeConverter.STOP_CALLBACK;
        
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        ec = NativeConverter.setCallbackEncode(converterHandle,icuAction,true);
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
    protected CoderResult implFlush(ByteBuffer out) {       
       try{
            getArray(out);          
            ec=NativeConverter.flushCharToByte(
                                            converterHandle,  /* Handle to ICU Converter */
                                            output,           /* output array of chars */
                                            outEnd,           /* output index+1 to be written */
                                            data              /* contains data, inOff,outOff */
                                            );
                                                     
            /* If we don't have room for the output, throw an exception*/
            if(ErrorCode.isFailure(ec)){
                if(ec == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		            return CoderResult.OVERFLOW;
		        }else{
		            ErrorCode.getException(ec);
		        }
		    }
	        implReset();
	        return  CoderResult.UNDERFLOW;
	    }finally{     
            setPosition(out);
        }
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
               
        if(!in.hasRemaining()){
            return CoderResult.UNDERFLOW;
        }

        getArray(in);
        getArray(out);      
        try{
            /* do the conversion */
            ec=NativeConverter.encode(
                                converterHandle,  /* Handle to ICU Converter */
                                input,            /* input array of bytes */
                                inEnd,            /* last index+1 to be converted */
                                output,           /* output array of chars */
                                outEnd,           /* output index+1 to be written */
                                data,             /* contains data, inOff,outOff */
                                false             /* donot flush the data */
                                );
            if(ErrorCode.isFailure(ec)){            
                /* If we don't have room for the output return error */
                if(ec == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		            return CoderResult.OVERFLOW;
		        }
                else if(ec==ErrorCode.U_INVALID_CHAR_FOUND){
		            return CoderResult.unmappableForLength(data[INVALID_CHARS]);
		        }else if(ec==ErrorCode.U_ILLEGAL_CHAR_FOUND){
                    return CoderResult.malformedForLength(data[INVALID_CHARS]);
                }
            }
            return CoderResult.UNDERFLOW;
        }finally{
            /* save state */
            setPosition(in);
            setPosition(out);         
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
     * Ascertains if a given Unicode code point (32bit value for handling surrogates)
     * can be converted to the target encoding. If the caller wants to test if a
     * surrogate pair can be converted to target encoding then the
     * responsibility of assembling the int value lies with the caller.
     * For assembling a code point the caller can use UTF16 class of ICU4J and do something like:
     * <pre>
     * while(i<mySource.length){
     *        if(UTF16.isLeadSurrogate(mySource[i])&& i+1< mySource.length){
     *            if(UTF16.isTrailSurrogate(mySource[i+1])){
     *                int temp = UTF16.charAt(mySource,i,i+1,0);
     *                if(!((CharsetEncoderICU) myConv).canEncode(temp)){
     *                    passed=false;
     *                }
     *                i++;
     *                i++;
     *            }
     *       }
     * }
     * </pre>
     * or
     * <pre>
     * String src = new String(mySource);
     * int i,codepoint;
     * boolean passed = false;
     * while(i<src.length()){
     *      codepoint = UTF16.charAt(src,i);
     *      i+= (codepoint>0xfff)? 2:1;
     *      if(!(CharsetEncoderICU) myConv).canEncode(codepoint)){
     *          passed = false;
     *      }
     * }
     * </pre>
     *
     * @param Unicode code point as int value
     * @return true if a character can be converted
     * 
     */
    public boolean canEncode(int codepoint){
        return NativeConverter.canEncode(converterHandle, codepoint);
    }

    /**
     * Releases the system resources by cleanly closing ICU converter opened
     * @exception Throwable exception thrown by super class' finalize method
     */
    protected void finalize() throws Throwable{
        NativeConverter.closeConverter(converterHandle);
        super.finalize();
    }
    
    //------------------------------------------
    // private utility methods
    //------------------------------------------
    private  final void getArray(ByteBuffer out){
        if(out.hasArray()){
            output = out.array();
            outEnd = out.arrayOffset() + out.limit();
            data[OUTPUT_WRITTEN] = (out.arrayOffset()+out.position());
        }else{
            outEnd = out.remaining();
            if(output==null || (outEnd > output.length)){
                output = new byte[outEnd];
`‰
            }
            //since the new 
            // buffer start position 
            // is 0
            data[OUTPUT_WRITTEN] = 0;
        }
    }

    private  final void getArray(CharBuffer in){
        if(in.hasArray()){
            input = in.array();
            inEnd = in.arrayOffset() + in.limit();
            data[INPUT_CONSUMED] = (in.arrayOffset()+in.position());
        }else{
            inEnd = in.remaining();
            if(input==null ||( inEnd > input.length)){
                input = new char[inEnd];
            }
            int pos = in.position();
            in.get(input,0,inEnd);
            in.position(pos);
            // return 0 since the new 
            // buffer start position 
            // is 0
            data[INPUT_CONSUMED] = 0;
        }
       
    }
    private final void setPosition(ByteBuffer out){
        if(out.hasArray()){
		    out.position(out.position() + data[OUTPUT_WRITTEN] - out.arrayOffset());
        }else{
            out.put(output,0,data[OUTPUT_WRITTEN]);
        }
    }
    private final void setPosition(CharBuffer in){          
        if(in.hasArray()){
		    in.position(in.position()+data[INPUT_CONSUMED] - in.arrayOffset());
        }else{
            in.position(in.position()+data[INPUT_CONSUMED]);
        }
    }
}