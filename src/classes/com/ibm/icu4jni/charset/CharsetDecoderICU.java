/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetDecoderICU.java,v $ 
* $Date: 2001/10/16 17:23:40 $ 
* $Revision: 1.2 $
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
    
    public CharsetDecoderICU(Charset cs,long cHandle){
         super(cs,(float)NativeConverter.getMaxBytesPerChar(cHandle),
               (float)NativeConverter.getMaxBytesPerChar(cHandle));      
         data[0] = 0;
         data[1] = 0;
         converterHandle = cHandle;
         implReplaceWith(replacement);
    }

    protected void implReplaceWith(String newReplacement) {
        if(converterHandle!=0){           
            if( newReplacement.length() > NativeConverter.getMaxBytesPerChar(converterHandle)) {
                System.out.println(newReplacement + " length: " +newReplacement.length());
                throw new IllegalArgumentException();
            }           
            int ec =NativeConverter.setSubstitutionChars(converterHandle,
                                                    newReplacement.toCharArray(),
                                                    newReplacement.length()
                                                    );
            if( ec > ErrorCode.U_ZERO_ERROR){
                System.out.println(ErrorCode.getErrorName(ec));
                throw new IllegalArgumentException();
            }
        }
        replacement = newReplacement;
     }
    

    //TODO
    protected void implOnMalformedInput(CodingErrorAction newAction) {
        int icuAction = NativeConverter.STOP_CALLBACK;
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        if(converterHandle!=0){   
            if(NativeConverter.setCallbackDecode(converterHandle,icuAction,false)
                    > ErrorCode.U_ZERO_ERROR){
                throw new IllegalArgumentException();
            } 
        }
    }
    
    //TODO
    protected void implOnUnmappableCharacter(CodingErrorAction newAction) {
        int icuAction = NativeConverter.STOP_CALLBACK;
        if(newAction.equals(CodingErrorAction.IGNORE)){
            icuAction = NativeConverter.SKIP_CALLBACK;
        }else if(newAction.equals(CodingErrorAction.REPLACE)){
            icuAction = NativeConverter.SUBSTITUTE_CALLBACK;
        }
        if(converterHandle!=0){   
            if(NativeConverter.setCallbackDecode(converterHandle,icuAction,true)
                    > ErrorCode.U_ZERO_ERROR){
                throw new IllegalArgumentException();
            } 
        }
    }

    protected CoderResult implFlush(CharBuffer out) {
	    /* argument check */
        if(out==null){
            throw new IllegalArgumentException();
        }
        
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
                                  
        
        
        /* if we donot have room for output throw an exception*/
        if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		    return CoderResult.OVERFLOW;
		}
		out.put(output,0,data[1]);
	    implReset();
	    return CoderResult.UNDERFLOW;
    }

    protected void implReset() {
        NativeConverter.resetByteToChar(converterHandle);
    }
    
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
        //System.out.println("inEnd : " + inEnd  + " inPos: " + in.position() +" inLimit: " + in.limit());
        //System.out.println("outEnd : " + outEnd  + " outPos: " + out.position() +" outLimit: " + out.limit());
        
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
            /* If we don't have room for the output, throw an exception*/
            if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		        return CoderResult.OVERFLOW;
		    }else if(err==ErrorCode.U_INVALID_CHAR_FOUND){
                NativeConverter.countInvalidChars(converterHandle, retVal);
		        return CoderResult.unmappableForLength(retVal[0]);
		    }else if(err==ErrorCode.U_ILLEGAL_CHAR_FOUND){
                NativeConverter.countInvalidChars(converterHandle, retVal);	
                return CoderResult.malformedForLength(retVal[0]);
            }
            return CoderResult.UNDERFLOW;
        }finally{
            /* save state */
            //System.out.println("in position: " + in.position()+" data[0]: " +data[0]+ " limit: " +in.limit());
            if(data[0]>0){
                in.position(in.position()+data[0]);
            }
            if(data[1]>0){
		        out.put(output,0,data[1]);        /* output offset */
		    }
		    //System.out.println("Num chars converted: " +data[1] +" output: " + new String(output));
		   
        }

	}
	
}