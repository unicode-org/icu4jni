/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/converters/ByteToCharConverterICU.java,v $ 
* $Date: 2001/10/27 00:34:55 $ 
* $Revision: 1.7 $
*
*******************************************************************************
*/ 
 /** 
  * A JNI interface for ICU converters.
  *
  * 
  * @author Ram Viswanadha, IBM
  */
  package com.ibm.icu4jni.converters;
  
  import java.io.UnsupportedEncodingException;
  import sun.io.*;
  import com.ibm.icu4jni.common.*;
  public class ByteToCharConverterICU extends ByteToCharConverter{
    
    /* data is 2 element array where
     * data[0] = inputOffset
     * data[1] = outputOffset
     */
    private int[] data = new int[2];
    
    /* handle to the ICU converter that is opened */
    private final long converterHandle;
    
    /* encoding of the converter object*/
    private final String encoding;
    
    /**
     * Create an instance of ByteToCharConverterICU with the specified encoding.
     *
     * @param string representing encoding
     * @exception UnsupportedEncodingException if the converter could not be opened
     */
    public ByteToCharConverterICU(String enc)
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
         if(NativeConverter.openConverter(converterHandleArr,enc) >
                ErrorCode.U_ZERO_ERROR){
            throw new UnsupportedEncodingException();
         }
         converterHandle = converterHandleArr[0];
    }
    
    

    /**
     * Conversion through the JNI interface for ICU.
     *
     * Converts an array of bytes containing characters in an external
     * encoding into an array of Unicode characters.  This  method allows
     * a buffer by buffer conversion of a data stream.  The state of the
     * conversion is saved between calls to convert.  Among other things,
     * this means multibyte input sequences can be split between calls.
     * If a call to convert results in an exception, the conversion may be
     * continued by calling convert again with suitably modified parameters.
     * All conversions should be finished with a call to the flush method.
     *
     * @return the number of bytes written to output.
     * @param input byte array containing text to be converted.
     * @param inStart begin conversion at this offset in input array.
     * @param inEnd stop conversion at this offset in input array (exclusive).
     * @param output character array to receive conversion result.
     * @param outStart start writing to output array at this offset.
     * @param outEnd stop writing to output array at this offset (exclusive).
     * @exception MalformedInputException if the input buffer contains any
     * sequence of bytes that is illegal for the input character set.
     * @exception UnknownCharacterException for any character that
     * that cannot be converted to Unicode. Thrown only when converter 
     * is not in substitution mode.
     * @exception ConversionBufferFullException if output array is filled prior
     * to converting all the input.
     * @exception IllegalArgumentException is thrown if any of the arrays
     * passed are null
     */
    public int convert( byte[] input, int inOff, int inEnd,
		                char[] output, int outOff, int outEnd)
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
        int err=NativeConverter.convertByteToChar(
                            converterHandle,  /* Handle to ICU Converter */
                            input,            /* input array of bytes */
                            inEnd,            /* last index+1 to be converted */
                            output,           /* output array of chars */
                            outEnd,           /* output index+1 to be written */
                            data,             /* contains data, inOff,outOff */
                            false             /* donot flush the data */
                            );
        
        /* save state */
        byteOff += data[0]; /* input offset */
		charOff += data[1]; /* output offset */
            
        /* If we don't have room for the output, throw an exception */
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
		/*return the number of chars written to the output*/    
        return data[1];
    }
    
    /**
     * Return the character set id
     */
    public final String getCharacterEncoding()
    {
        return encoding;
    }
    
    /**
     * Writes any remaining output to the output buffer and resets the
     * converter to its initial state.  
     *
     * @param output char array to receive flushed output.
     * @param outStart start writing to output array at this offset.
     * @param outEnd stop writing to output array at this offset (exclusive).
     * @exception ConversionBufferFullException if output array is filled 
     * before all the output can be flushed. flush will write what it can
     * to the output buffer and remember its state.  An additional call to
     * flush with a new output buffer will conclude the operation.
     */
    public final int flush(char[] output, int outStart, int outEnd) 
                        throws IllegalArgumentException,
                        ConversionBufferFullException {
        
        /* argument check */
        if(output==null){
            throw new IllegalArgumentException();
        }
        /*set inputStart to 0 */ 
        
        data[0] = 0; 
        int result=0;
        int oldOutputStart = data[1];
        data[1] = outStart;
        
        /* assume that output buffer is big enough since error is not handled*/
        int err=NativeConverter.flushByteToChar(
                                        converterHandle,  /* Handle to ICU Converter */
                                        output,           /* output array of chars */
                                        outEnd,           /* output index+1 to be written */
                                        data              /* contains data, inOff,outOff */
                                        );
                                  
        /* save state */
        byteOff  = data[0]; /* input offset */
		charOff = data[1];  /* output offset */
        
        /* if we donot have room for output throw an exception*/
        if(err == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
		        throw new ConversionBufferFullException();
		}
        
        /* return the number of bytes written to ouput buffer */
	    reset();
	    return data[1];
    }
    
    /**
     * Sets the substitution characters to use when the converter is in
     * substitution mode.  The given chars must not be
     * longer than the value returned by getMaxCharsPerByte for this
     * converter.
     *
     * @param newSubBytes the substitution bytes
     * @exception IllegalArgumentException if given byte array is longer than
     *    the value returned by the method getMaxBytesPerChar or if the 
     *    JNI interface has returned an error code
     * @see #setSubstitutionMode
     * @see #getMaxCharsPerByte
     */
    public final void setSubstitutionChars(char[] c) 
        throws IllegalArgumentException{
        
        if( c.length > getMaxCharsPerByte() ) {
            throw new IllegalArgumentException();
        }
        subChars= c;
        
        if( NativeConverter.setSubstitutionChars(converterHandle,subChars,subChars.length)
                > ErrorCode.U_ZERO_ERROR){
            throw new IllegalArgumentException();
        }
    }
    
    /*
     *   Reset the state of the converter
     */
    public final void reset() {
	    byteOff = charOff = 0;
	    NativeConverter.resetByteToChar(converterHandle);
    }
    
    /**
     * Returns the maximum number of characters needed to convert a byte. Useful
     * for calculating the maximum output buffer size needed for a particular
     * input buffer. Returns 2 since surrogate support is included
     * 
     * @return maximum number of chars need for converting a char
     */
    public final int getMaxCharsPerByte(){
        return 2;
    }
    
    /**
     * Sets converter into substitution mode.  In substitution mode,
     * the converter will replace untranslatable characters in the source
     * encoding with the substitution character set by setSubstitionChars.
     * When not in substitution mode, the converter will throw an
     * UnknownCharacterException when it encounters untranslatable input.
     *
     * @param doSub if true, enable substitution mode.
     * @see #setSubstitutionChars
     */
    public final void setSubstitutionMode(boolean doSub) {
        if(doSub){
            NativeConverter.setSubstitutionChars(converterHandle,subChars,subChars.length);
        }else{
            NativeConverter.setSubstitutionChars(converterHandle,null,0);
        }
    }
    
    /**
     * Returns the length, in bytes, of the input which caused a
     * MalformedInputException.  Always refers to the last
     * MalformedInputException thrown by the converter.  If none have
     * ever been thrown, returns 0.
     */
    public final int getBadInputLength(){
        int[] length = new int[1];
        NativeConverter.countInvalidBytes(converterHandle,length);
        return length[0];
    }
    
    /**
     * Returns the substitution characters as an array of chars
     *
     */
    public final char[] getSubChars(){
        return subChars;
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
     * @return ByteToCharConverter object
     */
    public static final ByteToCharConverter createConverter (String enc)
                    throws UnsupportedEncodingException{
            return (ByteToCharConverter)(new ByteToCharConverterICU(enc));
    }

  }