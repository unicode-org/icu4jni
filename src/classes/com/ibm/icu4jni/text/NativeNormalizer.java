/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/text/NativeNormalizer.java,v $ 
* $Date: 2001/11/21 22:09:52 $ 
* $Revision: 
*
*******************************************************************************
*/ 
/*
 * NativeNormalizer.java
 *
 * Created on September 14, 2001, 5:29 PM
 */

package com.ibm.icu4jni.text;
import com.ibm.icu4jni.common.ErrorCode;
import com.ibm.icu4jni.text.Normalizer;
import com.ibm.icu4jni.common.ICU4JNILoader;
/**
 *
 * @author  Ram Viswanadha
 * @version 
 */
 
final class NativeNormalizer {
    
    /*
     * Static block to load the library if not loaded
     * this required since all the methods in Normalizer
     * are static
     */
    static{
      ICU4JNILoader.loadLibrary();
    }
    
    /**
    * Normalize a string.
    * The string will be normalized according the the specified normalization mode
    * and options.
    * @param source The string to normalize.
    * @param sourceLength The length of source.
    * @param result A buffer to receive the normalized text.
    * @param resultLength The maximum size of result.
    * @param mode The normalization mode; one of Normalizer.UNORM_NONE, 
    * Normalizer.UNORM_NFD, Normalizer.UNORM_NFC, Normalizer.UNORM_NFKC, 
    * Normalizer.UNORM_NFKD, Normalizer.UNORM_DEFAULT
    * @param requiredLength A array to receive the total buffer size needed; 
    *                       if greater than resultLength,the output was truncated.
    * @return int error code returned by ICU  
    */
    static native final int normalize(char[] source,
                                      int sourceLength,  
                                      char[] result,
                                      int resultLength,
                                      int normalizationMode,
                                      int[] requiredLength);   
   /**
    * Normalize a string.
    * The string will be normalized according the the specified normalization mode
    * and options.
    * @param source The string to normalize.
    * @param mode The normalization mode; one of Normalizer.UNORM_NONE, 
    * Normalizer.UNORM_NFD, Normalizer.UNORM_NFC, Normalizer.UNORM_NFKC, 
    * Normalizer.UNORM_NFKD, Normalizer.UNORM_DEFAULT
    * @param requiredLength A array to receive the total buffer size needed; 
    *                       if greater than resultLength,the output was truncated.
    * @param errorCode an array to receive the error code returned by ICU  
    * @return int error code returned by ICU
    */
    static native final int normalize(String source, 
                                      int normalizationMode, 
                                      String[] target);
    /**
    * Performing quick check on a string, to quickly determine if the string is 
    * in a particular normalization format.
    * Three types of result can be returned Normalizer.UNORM_YES, Normalizer.UNORM_NO or
    * Normalizer.UNORM_MAYBE. Result Normalizer.UNORM_YES indicates that the argument
    * string is in the desired normalized format, Normalizer.UNORM_NO determines that
    * argument string is not in the desired normalized format. A Normalizer.UNORM_MAYBE
    * result indicates that a more thorough check is required, the user may have to
    * put the string in its normalized form and compare the results.
    *
    * @param source       string for determining if it is in a normalized format
    * @param sourcelength length of source to test
    * @paran mode         normalization format from the enum UNormalizationMode
    * @param qcReturn     An array to receive quick check output which is  
    *                     Normalizer.UNORM_YES, Normalizer.UNORM_NO or
    *                     Normalizer.UNORM_MAYBE
    * @return int error code returned by ICU          
    */
    static native final int quickCheck(char[] source, 
                                       int sourceLength,
                                       int normalizationMode,
                                       int[] qcReturn);
    /**
    * Performing quick check on a string, to quickly determine if the string is 
    * in a particular normalization format.
    * Three types of result can be returned Normalizer.UNORM_YES, Normalizer.UNORM_NO or
    * Normalizer.UNORM_MAYBE. Result Normalizer.UNORM_YES indicates that the argument
    * string is in the desired normalized format, Normalizer.UNORM_NO determines that
    * argument string is not in the desired normalized format. A Normalizer.UNORM_MAYBE
    * result indicates that a more thorough check is required, the user may have to
    * put the string in its normalized form and compare the results.
    *
    * @param source       string for determining if it is in a normalized format
    * @param sourcelength length of source to test
    * @paran mode         normalization format from the enum UNormalizationMode
    * @param qcReturn     An array to receive quick check output which is  
    *                     Normalizer.UNORM_YES, Normalizer.UNORM_NO or
    *                     Normalizer.UNORM_MAYBE
    * @return int error code returned by ICU          
    */
    static native final int quickCheck(String source,
                                       int normalizationMode,
                                       int[] qcReturn);
}
