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
 * 
 * <t>Unicode Normalization</t> 
 *
 * <h2>Unicode normalization API</h2>
 *
 * <code>unorm_normalize</code> transforms Unicode text into an equivalent composed or
 * decomposed form, allowing for easier sorting and searching of text.
 * <code>unorm_normalize</code> supports the standard normalization forms described in
 * <a href="http://www.unicode.org/unicode/reports/tr15/" target="unicode">
 * Unicode Standard Annex #15 &mdash; Unicode Normalization Forms</a>.
 *
 * Characters with accents or other adornments can be encoded in
 * several different ways in Unicode.  For example, take the character A-acute.
 * In Unicode, this can be encoded as a single character (the
 * "composed" form):
 *
 * <pre>
 *      00C1    LATIN CAPITAL LETTER A WITH ACUTE
 * </pre>
 *
 * or as two separate characters (the "decomposed" form):
 *
 * <pre>
 *      0041    LATIN CAPITAL LETTER A
 *      0301    COMBINING ACUTE ACCENT
 * </pre>
 *
 * To a user of your program, however, both of these sequences should be
 * treated as the same "user-level" character "A with acute accent".  When you are searching or
 * comparing text, you must ensure that these two sequences are treated 
 * equivalently.  In addition, you must handle characters with more than one
 * accent.  Sometimes the order of a character's combining accents is
 * significant, while in other cases accent sequences in different orders are
 * really equivalent.
 *
 * Similarly, the string "ffi" can be encoded as three separate letters:
 *
 * <pre>
 *      0066    LATIN SMALL LETTER F
 *      0066    LATIN SMALL LETTER F
 *      0069    LATIN SMALL LETTER I
 * </pre>
 *
 * or as the single character
 *
 * <pre>
 *      FB03    LATIN SMALL LIGATURE FFI
 * </pre>
 *
 * The ffi ligature is not a distinct semantic character, and strictly speaking
 * it shouldn't be in Unicode at all, but it was included for compatibility
 * with existing character sets that already provided it.  The Unicode standard
 * identifies such characters by giving them "compatibility" decompositions
 * into the corresponding semantic characters.  When sorting and searching, you
 * will often want to use these mappings.
 *
 * <code>unorm_normalize</code> helps solve these problems by transforming text into the
 * canonical composed and decomposed forms as shown in the first example above.  
 * In addition, you can have it perform compatibility decompositions so that 
 * you can treat compatibility characters the same as their equivalents.
 * Finally, <code>unorm_normalize</code> rearranges accents into the proper canonical
 * order, so that you do not have to worry about accent rearrangement on your
 * own.
 *
 * Form FCD, "Fast C or D", is also designed for collation.
 * It allows to work on strings that are not necessarily normalized
 * with an algorithm (like in collation) that works under "canonical closure", i.e., it treats precomposed
 * characters and their decomposed equivalents the same.
 *
 * It is not a normalization form because it does not provide for uniqueness of representation. Multiple strings
 * may be canonically equivalent (their NFDs are identical) and may all conform to FCD without being identical
 * themselves.
 *
 * The form is defined such that the "raw decomposition", the recursive canonical decomposition of each character,
 * results in a string that is canonically ordered. This means that precomposed characters are allowed for as long
 * as their decompositions do not need canonical reordering.
 *
 * Its advantage for a process like collation is that all NFD and most NFC texts - and many unnormalized texts -
 * already conform to FCD and do not need to be normalized (NFD) for such a process. The FCD quick check will
 * return UNORM_YES for most strings in practice.
 *
 * normalize(UNORM_FCD) may be implemented with UNORM_NFD.
 *
 * For more details on FCD see the collation design document:
 * http://oss.software.ibm.com/cvs/icu/~checkout~/icuhtml/design/collation/ICU_collation_design.htm
 *
 * ICU collation performs either NFD or FCD normalization automatically if normalization
 * is turned on for the collator object.
 * Beyond collation and string search, normalized strings may be useful for string equivalence comparisons,
 * transliteration/transcription, unique representations, etc.
 *
 * The W3C generally recommends to exchange texts in NFC.
 * Note also that most legacy character encodings use only precomposed forms and often do not
 * encode any combining marks by themselves. For conversion to such character encodings the
 * Unicode text needs to be normalized to NFC.
 * For more usage examples, see the Unicode Standard Annex.
 */

public final class Normalizer{
   
    private static final int[] requiredLength = new int[1];
    private static final int[] quickCheckRet = new int[1];
    private static final int[] errCode        = new int[1];
   
   /**
    * Compose a string.
    * The string will be composed to according the the specified mode.
    * @param source     The string to compose.
    * @param compat     If true the char array will be decomposed accoding to NFKC rules
    *                   and if false will be decomposed according to NFC rules.
    * @return String    The composed string   
    */            
    public static String compose(String str, boolean compat)
                                 throws Exception{
        return normalize(str,(compat)? UNORM_NFKC : UNORM_NFC);
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
    public static int compose(char[] source,char[] target, boolean compat)
                              throws Exception{
        return normalize(source,target,(compat)? UNORM_NFKC : UNORM_NFC);
    }
   
   /**
    * Decompose a string.
    * The string will be decomposed to according the the specified mode.
    * @param source     The string to decompose.
    * @param compat     If true the char array will be decomposed accoding to NFKD rules
    *                   and if false will be decomposed according to NFD rules.
    * @return String    The decomposed string   
    */         
    public static String decompose(String str, boolean compat)
                                   throws Exception{
        return normalize(str, (compat)? UNORM_NFKD :UNORM_NFD);                   
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
    public static int decompose(char[] source,char[] target, boolean compat)
                                throws Exception{
        return normalize( source, target,(compat)? UNORM_NFKD :UNORM_NFD);
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
    public static String normalize( String str, 
                                    int normalizationMode)
                                    throws Exception{
         synchronized(errCode){
            if(!check(normalizationMode)){
                throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
            }
            String retStr =  NativeNormalizer.normalize(str, 
                                                        normalizationMode, 
                                                        errCode);
            if(ErrorCode.isFailure(errCode[0])){
                throw ErrorCode.getException(errCode[0]);
            }
            return retStr;
         }      
            
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
    public static int normalize(char[] source, 
                                char[] target, 
                                int  normalizationMode) 
                                throws Exception{
        synchronized(requiredLength){
            if (!check(normalizationMode)){
                    throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
            }
            int errorCode =NativeNormalizer.normalize(source,source.length,
                                                      target,target.length,
                                                      normalizationMode,
                                                      requiredLength); 
            if(errorCode == ErrorCode.U_BUFFER_OVERFLOW_ERROR){
                return requiredLength[0];
            }
            if(ErrorCode.isFailure(errorCode)){
                throw ErrorCode.getException(errorCode);
            }
            return requiredLength[0];
        }
    }

   /**
    * Conveinience method.
    *
    * @param source       string for determining if it is in a normalized format
    * @paran mode         normalization format (Normalizer.UNORM_NFC,Normalizer.UNORM_NFD,  
    *                     Normalizer.UNORM_NFKC,Normalizer.UNORM_NFKD)
    * @return             Return code to specify if the text is normalized or not 
    *                     (Normalizer.UNORM_YES, Normalizer.UNORM_NO or
    *                     Normalizer.UNORM_MAYBE)
    */
    public static int quickCheck( String source, int mode)
                            throws Exception{
        synchronized(quickCheckRet){
            if (!check(mode)){
                    throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
            }
            int errorCode = NativeNormalizer.quickCheck(source, 
                                                        mode,
                                                        quickCheckRet);
                           
            if(ErrorCode.isFailure(errorCode)){
                throw ErrorCode.getException(errorCode);
            }
            return quickCheckRet[0];
        }
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

    public static int quickCheck(char[] source, int mode){
        synchronized(quickCheckRet){
            if (!check(mode)){
                    throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
            }
            int errorCode = NativeNormalizer.quickCheck(source, 
                                                        source.length,
                                                        mode,
                                                        quickCheckRet);
                           
            if(ErrorCode.isFailure(errorCode)){
                throw ErrorCode.getException(errorCode);
            }
            return quickCheckRet[0];
        }
    }
    // public static data members -----------------------------------
          
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
          

    /**
    * Checks if argument is a valid normalization format for use
    * @param normalization format
    * @return true if strength is a valid collation strength, false otherwise
    */
    public static boolean check(int normalization){
        
        if ( normalization < UNORM_NONE || 
             (normalization > UNORM_NFKC)
            ){
            return false;
        }
        return true;
    }

}
