/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/converters/NativeConverter.java,v $ 
* $Date: 2001/10/27 00:34:55 $ 
* $Revision: 1.5 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.converters;

import java.util.*;
import com.ibm.icu4jni.common.ICU4JNILoader;

public final class NativeConverter{
  
    // library loading ----------------------------------------------
    static {
        ICU4JNILoader.loadLibrary();
    }
      
    //Native methods
    
    /**
     * Converts an array of bytes containing characters in an external
     * encoding into an array of Unicode characters.  This  method allows
     * a buffer by buffer conversion of a data stream.  The state of the
     * conversion is saved between calls to convert.  Among other things,
     * this means multibyte input sequences can be split between calls.
     * If a call to convert results in an Error, the conversion may be
     * continued by calling convert again with suitably modified parameters.
     * All conversions should be finished with a call to the flush method.
     *
     * @param converterHandle Address of converter object created by C code
     * @param input byte array containing text to be converted.
     * @param inEnd stop conversion at this offset in input array (exclusive).
     * @param output character array to receive conversion result.
     * @param outEnd stop writing to output array at this offset (exclusive).
     * @param data integer array containing the following data    
     *        data[0] = inputOffset
     *        data[1] = outputOffset
     * @return int error code returned by ICU
     */
     
    public static final native int convertByteToChar( long converterHandle,
                                   byte[] input, int inEnd,
		                           char[] output, int outEnd,
		                           int[] data,
		                           boolean flush);
	public static final native int decode( long converterHandle,
                                   byte[] input, int inEnd,
		                           char[] output, int outEnd,
		                           int[] data,
		                           boolean flush);
	/**
     * Converts an array of Unicode chars containing characters in an 
     * external encoding into an array of bytes.  This  method allows
     * a buffer by buffer conversion of a data stream.  The state of the
     * conversion is saved between calls to convert.  Among other things,
     * this means multibyte input sequences can be split between calls.
     * If a call to convert results in an Error, the conversion may be
     * continued by calling convert again with suitably modified parameters.
     * All conversions should be finished with a call to the flush method.
     *
     * @param converterHandle Address of converter object created by C code
     * @param input char array containing text to be converted.
     * @param inEnd stop conversion at this offset in input array (exclusive).
     * @param output byte array to receive conversion result.
     * @param outEnd stop writing to output array at this offset (exclusive).
     * @param data integer array containing the following data    
     *        data[0] = inputOffset
     *        data[1] = outputOffset
     * @return int error code returned by ICU
     */	                         
    public static final native int convertCharToByte(long converterHandle,
                                   char[] input, int inEnd,
		                           byte[] output, int outEnd,
		                           int[] data,
		                           boolean flush); 
		                           
	public static final native int encode(long converterHandle,
                                   char[] input, int inEnd,
		                           byte[] output, int outEnd,
		                           int[] data,
		                           boolean flush);
	/**
     * Writes any remaining output to the output buffer and resets the
     * converter to its initial state. 
     *
     * @param converterHandle Address of converter object created by C code
     * @param output byte array to receive flushed output.
     * @param outEnd stop writing to output array at this offset (exclusive).
     * @return int error code returned by ICU
     * @param data integer array containing the following data    
     *        data[0] = inputOffset
     *        data[1] = outputOffset
     */ 
	public static final native int flushCharToByte(long converterHandle,
	                               byte[] output, 
	                               int outEnd, 
	                               int[] data);
	/**
     * Writes any remaining output to the output buffer and resets the
     * converter to its initial state. 
     *
     * @param converterHandle Address of converter object created by the native code
     * @param output char array to receive flushed output.
     * @param outEnd stop writing to output array at this offset (exclusive).
     * @return int error code returned by ICU
     * @param data integer array containing the following data    
     *        data[0] = inputOffset
     *        data[1] = outputOffset
     */ 	
	public static final native int flushByteToChar(long converterHandle,
	                               char[] output,  
	                               int outEnd, 
	                               int[] data);
	
	/**
	 * Open the converter with the specified encoding
	 *
	 * @param converterHandle long array for recieving the adress of converter object
	 *        created by the native code
	 * @param encoding string representing encoding
	 * @return int error code returned by ICU
	 */
	public static final native int openConverter(long[] converterHandle,
	                               String encoding);
	/**
	 * Resets the ByteToChar (toUnicode) state of specified converter 
	 *
	 * @param converterHandle Address of converter object created by the native code
     */
	public static final native void resetByteToChar(long  converterHandle);
    
    /**
	 * Resets the CharToByte (fromUnicode) state of specified converter 
	 *
	 * @param converterHandle Address of converter object created by the native code
     */
	public static final native void resetCharToByte(long  converterHandle);
	
	/**
	 * Closes the specified converter and releases the resources
	 *
	 * @param converterHandle Address of converter object created by the native code
	 */
	public static final native void closeConverter(long converterHandle);
    
    /**
     * Sets the substitution Unicode chars of the specified converter used
     * by encoder
	 * @param converterHandle Address of converter object created by the native code
     * @param subChars array of chars to used for substitution
     * @param length length of the array 
     * @return int error code returned by ICU
     */    
    public static final native int setSubstitutionChars( long converterHandle,
                                   char[] subChars,int length); 
    /**
     * Sets the substitution bytes of the specified converter used by decoder
     *
	 * @param converterHandle Address of converter object created by the native code
     * @param subChars array of bytes to used for substitution
     * @param length length of the array 
     * @return int error code returned by ICU
     */    
    public static final native int setSubstitutionBytes( long converterHandle,
                                   byte[] subChars,int length);
    /**
     * Sets the substitution mode of CharToByte(fromUnicode) for the specified converter 
     *
	 * @param converterHandle Address of converter object created by the native code
     * @param mode to set the true/false
     * @return int error code returned by ICU
     */  
    public static final native int setSubstitutionModeCharToByte(long converterHandle, 
                                   boolean mode);
    /**
     * Gets the numnber of invalid bytes in the specified converter object 
     * for the last error that has occured
     *
	 * @param converterHandle Address of converter object created by the native code
     * @param length array of int to recieve length of the array 
     * @return int error code returned by ICU
     */
    public static final native int countInvalidBytes(long converterHandle, int[] length);
    
    /**
     * Gets the numnber of invalid chars in the specified converter object 
     * for the last error that has occured
     *
	 * @param converterHandle Address of converter object created by the native code
     * @param length array of int to recieve length of the array 
     * @return int error code returned by ICU
     */   
    public static final native int countInvalidChars(long converterHandle, int[] length);
    
    /**
     * Gets the number of bytes needed for converting a char
     *
	 * @param converterHandle Address of converter object created by the native code
     * @return number of bytes needed
     */ 
    public static final native int getMaxBytesPerChar(long converterHandle);
   
    /**
     * Gets the average numnber of bytes needed for converting a char
     *
	 * @param converterHandle Address of converter object created by the native code
     * @return number of bytes needed
     */ 
    public static final native float getAveBytesPerChar(long converterHandle);
   
    /**
     * Gets the number of chars needed for converting a byte
     *
	 * @param converterHandle Address of converter object created by the native code
     * @return number of bytes needed
     */ 
    public static final native int getMaxCharsPerByte(long converterHandle);
   
    /**
     * Gets the average numnber of chars needed for converting a byte
     *
	 * @param converterHandle Address of converter object created by the native code
     * @return number of bytes needed
     */ 
    public static final native float getAveCharsPerByte(long converterHandle);
    
    
    public static final native String getSubstitutionBytes(long converterHandle);
    /**
     * Ascertains if a given Unicode code unit can 
     * be converted to the target encoding
	 * @param converterHandle Address of converter object created by the native code
     * @param  the character to be converted
     * @return true if a character can be converted
     * 
     */
    public static final native boolean canEncode(long converterHandle,int codeUnit);
    
    /**
     * Ascertains if a given a byte sequence can be converted to Unicode
	 * @param converterHandle Address of converter object created by the native code
     * @param  the bytes to be converted
     * @return true if a character can be converted
     * 
     */
    public static final native boolean canDecode(long converterHandle,byte[] bytes);
    
    /**
     * Gets the number of converters installed in the current installation of ICU
     * @return int number of converters installed
     */
    public static final native int countAvailable();
    
    /**
     * Gets the canonical names of available converters 
     * @return Object[] names as an object array
     */
    public static final native Object[] getAvailable();
    
    /**
     * Gets the number of aliases for a converter name
     * @param encoding name
     * @return number of aliases for the converter
     */
    public static final native int countAliases(String enc);
    
    /** 
     * Gets the aliases associated with the converter name
     * @param converter name
     * @return converter names as elements in an object array
     */
    public static final native Object[] getAliases(String enc);
    
    /**
     * Gets the canonical name of the converter
     * @param converter name
     * @return canonical name of the converter
     */
    public static final native String getCanonicalName(String enc);
    
    /**
     * Sets the callback to Unicode for ICU conveter. The default behaviour of ICU callback
     * is to call the specified callback function for both illegal and unmapped sequences.
     * @param converterHandle Adress of the converter object created by native code
     * @param mode call back mode to set. This is either STOP_CALLBACK, SKIP_CALLBACK or SUBSTITUE_CALLBACK
     *        The converter performs the specified callback when an error occurs
     * @param stopOnIllegal If true sets the alerts the converter callback to stop on an illegal sequence
     * @return int error code returned by ICU
     */
    public static final native int setCallbackDecode(long converterHandle, int mode, boolean stopOnIllegal);
   
    /**
     * Sets the callback from Unicode for ICU conveter. The default behaviour of ICU callback
     * is to call the specified callback function for both illegal and unmapped sequences.
     * @param converterHandle Adress of the converter object created by native code
     * @param mode call back mode to set. This is either STOP_CALLBACK, SKIP_CALLBACK or SUBSTITUE_CALLBACK
     *        The converter performs the specified callback when an error occurs
     * @param stopOnIllegal If true sets the alerts the converter callback to stop on an illegal sequence
     * @return int error code returned by ICU
     */
    public static final native int setCallbackEncode(long converterHandle, int mode, boolean stopOnIllegal);

    public static final int STOP_CALLBACK = 0;
    public static final int SKIP_CALLBACK = 1;
    public static final int SUBSTITUTE_CALLBACK = 3;

}