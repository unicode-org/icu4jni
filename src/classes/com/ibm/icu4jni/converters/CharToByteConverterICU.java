/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/converters/CharToByteConverterICU.java,v $ 
* $Date: 2001/09/18 00:33:49 $ 
* $Revision: 1.6 $
*
*******************************************************************************
*/ 
 /**
  * A JNI interface for ICU converters
  * @author Ram Viswanadha, IBM
  */
 package com.ibm.icu4jni.converters;
 
 import java.io.UnsupportedEncodingException;
 import sun.io.*;
 import com.ibm.icu4jni.common.*;
 
 public class CharToByteConverterICU extends CharToByteConverter{
   
    
    /* data is 2 element array where
     * data[0] = inputOffset
     * data[1] = outputOffset
     */
    private int[] data = new int[2];
    
    /* handle to the ICU converter that is opened */
    private final long converterHandle;
    
    /* encoding of this converter object */
    private final String encoding;
    
    /* max bytes per char */
    private final int maxBytes;
    
    /**
     * Create an instance of CharToByteConverterICU with the specified encoding.
     *
     * @param string representing encoding
     * @exception UnsupportedEncodingException if the converter could not be opened
     */
    public CharToByteConverterICU(String enc)
            throws UnsupportedEncodingException{
         
         /* initialize data */
         encoding = enc;
         data[0] = 0;
         data[1] = 0;
         long[] converterHandleArr = new long[1];
         if(ErrorCode.LIBRARY_LOADED==false){
            ErrorCode.LIBRARY_LOADED=true;
         }
         
         /* open the converter and get the handle 
          * if there is an error throw Unsupported encoding exception 
          */
         if(NativeConverter.openConverter(converterHandleArr,encoding)
            > ErrorCode.U_ZERO_ERROR){
            throw new UnsupportedEncodingException();
         }
         converterHandle=converterHandleArr[0];
         maxBytes = NativeConverter.getMaxBytesPerChar(converterHandle);
    }
    /** 
     * Conversion through the JNI interface for ICU.
     *
     * Converts an array of Unicode characters into an array of bytes
     * in the target character encoding.  This method allows a buffer by
     * buffer conversion of a data stream.  The state of the conversion is
     * saved between calls to convert.  If a call to convert results in
     * an exception, the conversion may be continued by calling convert again
     * with suitably modified parameters.  All conversions should be finished
     * with a call to the flush method.
     * 
     * @return the number of bytes written to output.
     * @param input array containing Unicode characters to be converted.
     * @param inStart begin conversion at this offset in input array.
     * @param inEnd stop conversion at this offset in input array (exclusive).
     * @param output byte array to receive conversion result.
     * @param outStart start writing to output array at this offset.
     * @param outEnd stop writing to output array at this offset (exclusive).
     * @exception MalformedInputException if the input buffer contains any
     * sequence of chars that is illegal in Unicode (principally unpaired
     * surrogates and \uFFFF or \uFFFE). After this exception is thrown,
     * the method nextCharIndex can be called to obtain the index of the
     * first invalid input character.  The MalformedInputException can
     * be queried for the length of the invalid input.
     * @exception UnknownCharacterException for any character that
     * that cannot be converted to the external character encoding. Thrown
     * only when converter is not in substitution mode.
     * @exception ConversionBufferFullException if output array is filled prior
     * to converting all the input.
     */
    public int convert( char[] input, int inOff, int inEnd,
		                byte[] output, int outOff, int outEnd)
                        throws ConversionBufferFullException, 
                        UnknownCharacterException, 
                        MalformedInputException,
                        IllegalArgumentException{
                            
        /*Aguments check*/
        if(input==null||output==null){
            throw new IllegalArgumentException();
        }
        
        data[0] = inOff;  // input offset 
        data[1] = outOff; // output offset 

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
        
        /* save state */
        charOff += data[0]; /* input offset */
		byteOff += data[1]; /* output offset */
        
        /* If we don't have room for the output, throw an exception*/
        if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		        throw new ConversionBufferFullException();
		}
        else if(err>ErrorCode.U_ZERO_ERROR){
		        throw new MalformedInputException();
		}
	    /* ICU has been set to UCNV_STOP_CALLBACK so it returns
	     * at first untranslatable character and does not return
	     * an error. We need to test for that situation and throw
	     * an exception
	     */
   		if(data[0]!=inEnd && !subMode){
		    throw new UnknownCharacterException();
		}
		/*return the number of bytes written to the output*/
        return data[1];
    }
    
    /**
     * Writes any remaining output to the output buffer and resets the
     * converter to its initial state. 
     *
     * @param output byte array to receive flushed output.
     * @param outStart start writing to output array at this offset.
     * @param outEnd stop writing to output array at this offset (exclusive).
     * @exception ConversionBufferFullException if output array is filled 
     * before all the output can be flushed. flush will write what it can
     * to the output buffer and remember its state.  An additional call to
     * flush with a new output buffer will conclude the operation.
     */ 
    public final int flush(byte[] output, int outStart, int outEnd)
                    throws IllegalArgumentException, 
                    ConversionBufferFullException{
        
        /* argument check */
        if(output==null){
            throw new IllegalArgumentException();
        }
        /*set inputStart to 0 */ 
        data[0] = 0; 
        
        int oldOutputStart = data[1];
        int result = 0;
        data[1] = outStart;
        
        
        /* assume that output buffer is big enough since error is not handled*/
        int err= NativeConverter.flushCharToByte(
                                            converterHandle,  /* Handle to ICU Converter */
                                            output,           /* output array of chars */
                                            outEnd,           /* output index+1 to be written */
                                            data              /* contains data, inOff,outOff */
                                            );
        /* save state */
        charOff = data[0]; /* input offset */
		byteOff = data[1]; /* output offset */
		
        /* if we donot have room for output throw an exception*/
        if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		        throw new ConversionBufferFullException();
		}                          

        /* return the number of bytes written to ouput buffer */
	    result = data[1]-oldOutputStart;
	    reset();
	    return result;
    }
    
    /** 
     * Return the character set id
     */
    public final String getCharacterEncoding()
    {
        return encoding;
    }
    
    /**
     * Sets the substitution bytes 
     *
     * @param array of bytes to used for substitution
     */
    public final void setSubstitutionBytes(byte[] c) 
        throws IllegalArgumentException
    {
        if( c.length > getMaxBytesPerChar() ) {
            throw new IllegalArgumentException();
        }
        
        if(NativeConverter.setSubstitutionBytes(converterHandle,c,c.length)
                > ErrorCode.U_ZERO_ERROR){
            throw new IllegalArgumentException();
        }
    }
    
    /**
     *  Reset the state of the converter
     */
    public final void reset() {
	    byteOff = charOff = 0;
	    NativeConverter.resetCharToByte(converterHandle);
    }
    /** 
     * Returns the max number of bytes needed for converting
     * a Unicode character to target encoding
     *
     * @return maximum number of bytes as an int
     */
    public final int getMaxBytesPerChar(){
        return maxBytes;
    };   
    
    /**
     * Ascertains if a given Unicode character can 
     * be converted to the target encoding
     *
     * @param  the character to be converted
     * @return true if a character can be converted
     * 
     */
    public boolean canConvert(char c){
        return canConvert((int) c);
    }
    
    /**
     * Ascertains if a given Unicode codeunit (32bit value for handling surrogates)
     * can be converted to the target encoding. If the caller wants to test if a
     * surrogate pair can be converted to target encoding then the
     * responsibility of assembling the int value lies with the caller.
     * For assembling a codeunit the caller has to do something like:
     *
     * while(i<mySource.length){
     *           if(isFirstSurrogate(mySource[i])&& i+1< mySource.length){
     *               if(isSecondSurrogate(mySource[i+1])){
     *                   temp = (((mySource[i])<<(long)10)+(mySource[i+1])-((0xd800<<(long)10)+0xdc00-0x10000));
     *                   if(!((CharToByteConverterICU) myConv).canConvert(temp)){
     *                       passed=false;
     *                   }
     *                   i++;
     *                   i++;
     *               }
     * }
     * 
     * @param Unicode codeunit as int value
     * @return true if a character can be converted
     * 
     */
    public boolean canConvert(int codeUnit){
        return NativeConverter.canConvert(converterHandle, codeUnit);
    }
    
    /**
     * Returns the length, in chars, of the input which caused a
     * MalformedInputException.  Always refers to the last
     * MalformedInputException thrown by the converter.  If none have
     * ever been thrown, returns 0.
     */
    public final int getBadInputLength(){
        int[] length = new int[1];
        NativeConverter.countInvalidChars(converterHandle,length);
        return length[0];
    }
    
    /**
     * Sets converter into substitution mode.  In substitution mode,
     * the converter will replace untranslatable characters in the source
     * encoding with the substitution character set by setSubstitutionBytes.
     * When not in substitution mode, the converter will throw an
     * UnknownCharacterException when it encounters untranslatable input.
     *
     * @param doSub if true, enable substitution mode.
     * @see #setSubstitutionBytes
     */
    public final void setSubstitutionMode(boolean doSub) {
        /* set the substitution mode in ICU */ 
        NativeConverter.setSubstitutionModeCharToByte(converterHandle, doSub);
        /* save the mode*/
        subMode = doSub;
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
        
    /**
     * Creates an ICU Converter of the specified encoding
     *
     * @param  encoding string
     * @return CharToByteConverter object
     */
    public static final CharToByteConverter createConverter (String enc)
                    throws UnsupportedEncodingException{
            return (CharToByteConverter)(new CharToByteConverterICU(enc));
    }


}