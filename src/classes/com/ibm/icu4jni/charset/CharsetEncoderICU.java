/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/charset/CharsetEncoderICU.java,v $ 
* $Date: 2001/10/12 01:30:56 $ 
* $Revision: 1.1 $
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

public class CharsetEncoderICU extends CharsetEncoder{
    /* data is 2 element array where
     * data[0] = inputOffset
     * data[1] = outputOffset
     */
    private int[] data = new int[2];
    
    /* handle to the ICU converter that is opened */
    private final long converterHandle;
    private byte[] replacement; 
    public CharsetEncoderICU(Charset cs,long cHandle,byte[] replacement){
         super(cs,(float)NativeConverter.getMaxBytesPerChar(cHandle),
                (float)NativeConverter.getMaxBytesPerChar(cHandle),replacement);      
         data[0] = 0;
         data[1] = 0;
         converterHandle = cHandle;
         implReplaceWith(replacement);
    }
    
    protected void implReplaceWith(byte[] newReplacement){
        if(converterHandle!=0){
            if( newReplacement.length > NativeConverter.getMaxBytesPerChar(converterHandle) ) {
                throw new IllegalArgumentException();
            }
            
            if(NativeConverter.setSubstitutionBytes(converterHandle,newReplacement,newReplacement.length)
                    > ErrorCode.U_ZERO_ERROR){
                throw new IllegalArgumentException();
            }
        }
        replacement = newReplacement;
    }
    
    //TODO
    protected void implOnMalformedInput(CodingErrorAction newAction) { }

    
    //TODO
    protected void implOnUnmappableCharacter(CodingErrorAction newAction){   }  

    protected CoderResult implFlush(ByteBuffer out) {
	    /* argument check */
        if(out==null){
            throw new IllegalArgumentException();
        }
        
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
        //System.out.println(outEnd + "  num written " + data[1]);
        if(data[1]>0){
		    out.put(output,0,data[1]);       // output offset
        }
	    implReset();
	    return  CoderResult.UNDERFLOW;
    }

    protected void implReset() { 

	    NativeConverter.resetCharToByte(converterHandle);
    }
    

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
		   // if(data[1]==0){
		   //      System.out.println("Num written by encode : " + data[1]);
		   //     System.out.println("length of input : " + input.length); 
		   // }
		    
		}
	}

    public boolean canEncode(char c) {
        return canEncode((int) c);
    }

   /* public boolean canEncode(CharSequence source) {
        CharBuffer buffer;
	    if (source instanceof CharBuffer){
	        buffer = ((CharBuffer))source.duplicate();
	    }else{
	        bufer = CharBuffer.wrap(source.toString());
	    }
	    ByteBuffer byteBuf = ByteBuffer.allocate(buffer.remaining()* 
	                                        NativeConverter.getMaxBytesPerChar(converterHandle));
	    CoderResult result= encodeLoop(buffer,byteBuf);
	    //System.out.println(result.toString());
	    return result.isError();
    }	
    */
    public boolean canEncode(int codeUnit){
        return NativeConverter.canEncode(converterHandle, codeUnit);
    }
}