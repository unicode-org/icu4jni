/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*/
 /*
 * Normalizer.java
 *
 * Created on September 14, 2001, 3:49 PM
 */

package com.ibm.icu4jni.text;

import java.lang.String;
import com.ibm.icu4jni.common.*;

/**
 *
 * @author Ram Viswanadha
 * @version 
 */
 /**
 * <tt>Normalizer</tt> transforms Unicode text into an equivalent composed or
 * decomposed form, allowing for easier sorting and searching of text.
 * <tt>Normalizer</tt> supports the standard normalization forms described in
 * <a href="http://www.unicode.org/unicode/reports/tr15/" target="unicode">
 * Unicode Technical Report #15</a>.
 * <p>
 * Characters with accents or other adornments can be encoded in
 * several different ways in Unicode.  For example, take the character "Â"
 * (A-acute).   In Unicode, this can be encoded as a single character (the
 * "composed" form):
 * <pre>
 *      00C1    LATIN CAPITAL LETTER A WITH ACUTE</pre>
 * or as two separate characters (the "decomposed" form):
 * <pre>
 *      0041    LATIN CAPITAL LETTER A
 *      0301    COMBINING ACUTE ACCENT</pre>
 * <p>
 * To a user of your program, however, both of these sequences should be
 * treated as the same "user-level" character "Â".  When you are searching or
 * comparing text, you must ensure that these two sequences are treated
 * equivalently.  In addition, you must handle characters with more than one
 * accent.  Sometimes the order of a character's combining accents is
 * significant, while in other cases accent sequences in different orders are
 * really equivalent.
 * <p>
 * Similarly, the string "ffi" can be encoded as three separate letters:
 * <pre>
 *      0066    LATIN SMALL LETTER F
 *      0066    LATIN SMALL LETTER F
 *      0069    LATIN SMALL LETTER I</pre>
 * or as the single character
 * <pre>
 *      FB03    LATIN SMALL LIGATURE FFI</pre>
 * <p>
 * The ffi ligature is not a distinct semantic character, and strictly speaking
 * it shouldn't be in Unicode at all, but it was included for compatibility
 * with existing character sets that already provided it.  The Unicode standard
 * identifies such characters by giving them "compatibility" decompositions
 * into the corresponding semantic characters.  When sorting and searching, you
 * will often want to use these mappings.
 * <p>
 * <tt>Normalizer</tt> helps solve these problems by transforming text into the
 * canonical composed and decomposed forms as shown in the first example above.
 * In addition, you can have it perform compatibility decompositions so that
 * you can treat compatibility characters the same as their equivalents.
 * Finally, <tt>Normalizer</tt> rearranges accents into the proper canonical
 * order, so that you do not have to worry about accent rearrangement on your
 * own.
 * Currently the only usage model for <tt>Normalizer</tt> is to use the 
 * the static {@link #normalize normalize()} method is used to process an
 * entire input string at once. 
 * <p>
 * <b>Note:</b> <tt>Normalizer</tt> objects behave like iterators and have
 * methods such as <tt>setIndex</tt>, <tt>next</tt>, <tt>previous</tt>, etc.
 * You should note that while the <tt>setIndex</tt> and <tt>getIndex</tt> refer
 * to indices in the underlying <em>input</em> text being processed, the
 * <tt>next</tt> and <tt>previous</tt> methods it iterate through characters
 * in the normalized <em>output</em>.  This means that there is not
 * necessarily a one-to-one correspondence between characters returned
 * by <tt>next</tt> and <tt>previous</tt> and the indices passed to and
 * returned from <tt>setIndex</tt> and <tt>getIndex</tt>.  It is for this
 * reason that <tt>Normalizer</tt> does not implement the
 * {@link CharacterIterator} interface.
 * <p>
 * <b>Note:</b> <tt>Normalizer</tt> is currently based on version 3.1.1
 * of the <a href="http://www.unicode.org" target="unicode">Unicode Standard</a>.
 * It will be updated as later versions of Unicode are released.  If you are
 * using this class on a JDK that supports an earlier version of Unicode, it
 * is possible that <tt>Normalizer</tt> may generate composed or dedecomposed
 * characters for which your JDK's {@link java.lang.Character} class does not
 * have any data.
 * <p>
 */
public class Normalizer{

    private static int MAX_BUFFER_SIZE = 1000;
    
   /**
    * Compose a string.
    * The string will be composed to according the the specified mode.
    * @param source     The string to compose.
    * @param compat     If true the char array will be decomposed accoding to NFKC rules
    *                   and if false will be decomposed according to NFC rules.
    * @return String    The composed string   
    */            
    public static String compose(String str, boolean compat){
        char[] target = new char[MAX_BUFFER_SIZE];
        char[] source = str.toCharArray();
        int requiredLength =  compose(source,target,compat);
        if (requiredLength < MAX_BUFFER_SIZE){
            target = new char[requiredLength];
            compose(source,target,compat); 
        }
        return new String(target);
    }
   /**
    * Compose a string.
    * The string will be composed to according the the specified mode.
    * @param source The char array to compose.
    * @param result A char buffer to receive the normalized text.
    * @param compat If true the char array will be decomposed accoding to NFKC rules
    *               and if false will be decomposed according to NFC rules.
    * @return int   The total buffer size needed;if greater than length of result,
    *               the output was truncated.
    *   
    */         
    public static int compose(char[] source,char[] target, boolean compat){
                                          
        int[] requiredLength = new int[1];
        int errorCode = NativeNormalizer.normalize(source,source.length,
                                                    target,target.length,
                                                    (compat)? UNORM_NFKC : UNORM_NFC,
                                                    requiredLength);
        if(errorCode > ErrorCode.U_ZERO_ERROR){
            throw ErrorCode.getException(errorCode);
        }
        return requiredLength[0];
    }
   /**
    * Decompose a string.
    * The string will be decomposed to according the the specified mode.
    * @param source     The string to decompose.
    * @param compat     If true the char array will be decomposed accoding to NFKD rules
    *                   and if false will be decomposed according to NFD rules.
    * @return String    The decomposed string   
    */         
    public static String decompose(String str,boolean compat){
        char[] target = new char[MAX_BUFFER_SIZE];
        char[] source = str.toCharArray();
        int requiredLength =  decompose(source,target,compat);
        if (requiredLength < MAX_BUFFER_SIZE){
            target = new char[requiredLength];
            decompose(source,target,compat); 
        }
        return new String(target);
    }
    /**
    * Decompose a string.
    * The string will be decomposed to according the the specified mode.
    * @param source The char array to decompose.
    * @param result A char buffer to receive the normalized text.
    * @param compat If true the char array will be decomposed accoding to NFKD rules
    *               and if false will be decomposed according to NFD rules.
    * @return int   The total buffer size needed;if greater than length of result,
    *               the output was truncated.
    *   
    */
    public static int decompose(char[] source,char[] target,boolean compat){
        int[] requiredLength = new int[1];
        int errorCode = NativeNormalizer.normalize(source,source.length, 
                                                    target,target.length,
                                                    (compat)? UNORM_NFKD :UNORM_NFD,
                                                    requiredLength);
        if(errorCode > ErrorCode.U_ZERO_ERROR){
            throw ErrorCode.getException(errorCode);
        }
        return requiredLength[0];
    }
    /**
    * Normalize a string.
    * The string will be normalized according the the specified normalization mode
    * and options.
    * @param source     The string to normalize.
    * @param mode       The normalization mode; one of Normalizer.UNORM_NONE, 
    *                   Normalizer.UNORM_NFD, Normalizer.UNORM_NFC, Normalizer.UNORM_NFKC, 
    *                   Normalizer.UNORM_NFKD, Normalizer.UNORM_DEFAULT
    * @return String    The normalized string
    *   
    */
    public static String normalize(String str, int normalizationMode){
        char[] target = new char[MAX_BUFFER_SIZE];
        char[] source = str.toCharArray();
        int requiredLength =  normalize(source,target,normalizationMode);
        if (requiredLength < MAX_BUFFER_SIZE){
            target = new char[requiredLength];
            normalize(source,target,normalizationMode); 
        }
        return new String(target);    
    }
    /**
    * Normalize a string.
    * The string will be normalized according the the specified normalization mode
    * and options.
    * @param source The char array to normalize.
    * @param result A char buffer to receive the normalized text.
    * @param mode   The normalization mode; one of Normalizer.UNORM_NONE, 
    *               Normalizer.UNORM_NFD, Normalizer.UNORM_NFC, Normalizer.UNORM_NFKC, 
    *               Normalizer.UNORM_NFKD, Normalizer.UNORM_DEFAULT
    * @return int   The total buffer size needed;if greater than length of result,
    *               the output was truncated.
    *   
    */
    public static int normalize(char[] source, char[] target, 
                                int  normalizationMode){
        int[] requiredLength = new int[1];
        if (!check(normalizationMode)){
                throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
        }
        int errorCode =NativeNormalizer.normalize(source,source.length,
                                                    target,target.length,
                                                    normalizationMode,
                                                    requiredLength);
        if(errorCode > ErrorCode.U_ZERO_ERROR){
            throw ErrorCode.getException(errorCode);
        }
        return requiredLength[0];
    }

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
    * @paran mode         normalization format (Normalizer.UNORM_NFC,Normalizer.UNORM_NFD,  
    *                     Normalizer.UNORM_NFKC,Normalizer.UNORM_NFKD)
    * @return             Return code to specify if the text is normalized or not 
    *                     (Normalizer.UNORM_YES, Normalizer.UNORM_NO or
    *                     Normalizer.UNORM_MAYBE)
    */
    public static int quickCheck( String source, int mode){
        if (!check(mode)){
                throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
        }
        int[] retVal = new int[1];
        int errorCode = NativeNormalizer.quickCheck(source.toCharArray(), 
                                                    source.length(),
                                                    mode,retVal);
                   
        if(errorCode > ErrorCode.U_ZERO_ERROR){
            throw ErrorCode.getException(errorCode);
        }
        return retVal[0];
    }
       
    // public static data members -----------------------------------

    public static final int NO_NORMALIZATION = 1;
    /** 
    * Canonical decomposition 
    */
    public static final int DECOMP_CAN = 2;
    /** 
    * Compatibility decomposition 
    */
    public static final int DECOMP_COMPAT = 3;
    /** 
    * Default normalization 
    */
    public static final int DEFAULT_NORMALIZATION = DECOMP_COMPAT;
    /** 
    * Canonical decomposition followed by canonical composition 
    */
    public static final int DECOMP_CAN_COMP_COMPAT = 4;
    /** 
    * Compatibility decomposition followed by canonical composition 
    */
    public static final int DECOMP_COMPAT_COMP_CAN = 5;
          
    /** No decomposition/composition */
    public static final int UNORM_NONE = 1; 
    /** Canonical decomposition */
    public static final int UNORM_NFD = 2;
    /** Compatibility decomposition */
    public static final int UNORM_NFKD = 3;
    /** Canonical decomposition followed by canonical composition */
    public static final int UNORM_NFC = 4;
    /** Default normalization */
    public static final int UNORM_DEFAULT = UNORM_NFC; 
    /** Compatibility decomposition followed by canonical composition */
    public static final int UNORM_NFKC =5;
    /** "Fast C or D" form */
    public static final int UNORM_FCD = 6;
    /** Indicates that string is not in the normalized format*/
    public static final int UNORM_NO=0;
    /** Indicates that string is in the normalized format*/
    public static final int UNORM_YES=1;
    /** Indicates that string cannot be determined if it is in the normalized 
    * format without further thorough checks*/
    public static final int UNORM_MAYBE=2;
          
    // public methods ------------------------------------------------------

    /**
    * Checks if argument is a valid normalization format for use
    * @param normalization format
    * @return true if strength is a valid collation strength, false otherwise
    */
    static boolean check(int normalization)
    {
    if (normalization < UNORM_NONE || 
        (normalization > UNORM_NFKC))
        return false;
    return true;
    }

}
