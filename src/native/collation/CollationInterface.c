/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/native/collation/CollationInterface.c,v $ 
* $Date: 2001/09/19 02:47:18 $ 
* $Revision: 1.9 $
*
*******************************************************************************
*/

#include "CollationInterface.h"
#include "ErrorCode.h"
#include "unicode/ucol.h"
#include "unicode/ucoleitr.h"

#ifdef DEBUG
#include <stdio.h>
#endif

/**
* ICU constant values and methods
*/
#define UCOL_MAX_BUFFER 64



/**
* Closing a C UCollator with the argument locale rules.
* Note determining if a collator currently exist for the caller is to be handled
* by the caller. Hence if the caller has a existing collator, it is his 
* responsibility to delete first before calling this method.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address of the C UCollator
*/
JNIEXPORT void JNICALL Java_com_ibm_icu4jni_text_NativeCollation_closeCollator
  (JNIEnv *env, jclass obj, jlong address)
{
  UCollator *collator = (UCollator *)address;
  ucol_close(collator);
}


/**
* Close a C collation element iterator.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address of C collation element iterator to close.
*/
JNIEXPORT void JNICALL Java_com_ibm_icu4jni_text_NativeCollation_closeElements
  (JNIEnv *env, jclass obj, jlong address)
{
  UCollationElements *iterator = (UCollationElements *)address;
  ucol_closeElements(iterator);
}

/**
* Compare two strings.
* The strings will be compared using the normalization mode and options
* specified in openCollator or openCollatorFromRules
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address address of the c collator
* @param source The source string.
* @param target The target string.
* @return result of the comparison, UCOL_EQUAL, UCOL_GREATER or UCOL_LESS
*/
JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_text_NativeCollation_compare
  (JNIEnv *env, jclass obj, jlong address, jstring source, jstring target)
{
  const UCollator *collator  = (const UCollator *)address;
        jsize      srclength = (*env)->GetStringLength(env, source);
        jsize      tgtlength = (*env)->GetStringLength(env, target);
  const UChar     *srcstr    = (const UChar *)(*env)->GetStringCritical(env, 
                                                                       source, 
                                                                       0);
  const UChar     *tgtstr    = (const UChar *)(*env)->GetStringCritical(env, 
                                                                       target, 
                                                                       0);
  
  jint result = -2; 
  
  result = ucol_strcoll(collator, srcstr, srclength, tgtstr, tgtlength);
  (*env)->ReleaseStringCritical(env, source, srcstr);
  (*env)->ReleaseStringCritical(env, target, tgtstr);
  return result;
}

/**
* Universal attribute getter
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address address of the C collator
* @param type type of attribute to be set
* @return attribute value
* @exception thrown when error occurs while getting attribute value
*/
JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_text_NativeCollation_getAttribute
  (JNIEnv *env, jclass obj, jlong address, jint type)
{
  const UCollator *collator = (const UCollator *)address;
  UErrorCode status = U_ZERO_ERROR;
  jint result = (jint)ucol_getAttribute(collator, (UColAttribute)type, 
                                        &status);
  if ( error(env, status) != FALSE) {
    return (jint)UCOL_DEFAULT;
  }
  return result;
}

/** 
* Create a CollationElementIterator object that will iterator over the elements 
* in a string, using the collation rules defined in this RuleBasedCollatorJNI
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address address of C collator
* @param source string to iterate over
* @return address of C collationelement
*/
JNIEXPORT jlong JNICALL Java_com_ibm_icu4jni_text_NativeCollation_getCollationElementIterator
  (JNIEnv *env, jclass obj, jlong address, jstring source)
{
        UErrorCode status    = U_ZERO_ERROR;
        UCollator *collator  = (UCollator *)address;
        jlong       result;
        jsize      srclength = (*env)->GetStringLength(env, source);

  const UChar     *srcstr    = (const UChar *)(*env)->GetStringCritical(env, 
                                                                       source, 
                                                                       0);

  result = (jlong)(ucol_openElements(collator, srcstr, srclength, &status));

  (*env)->ReleaseStringCritical(env, source, srcstr);
   error(env, status);
    
  return result;
}

/**
* Get the maximum length of any expansion sequences that end with the specified 
* comparison order.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address of the C collation element iterator containing the text.
* @param order collation order returned by previous or next.
* @return maximum length of any expansion sequences ending with the specified 
*         order or 1 if collation order does not occur at the end of any 
*         expansion sequence.
*/
JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_text_NativeCollation_getMaxExpansion
  (JNIEnv *env, jclass obj, jlong address, jint order)
{
  UCollationElements *iterator = (UCollationElements *)address;
  return ucol_getMaxExpansion(iterator, order);
}

/**
* Get the normalization mode for this object.
* The normalization mode influences how strings are compared.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address of C collator
* @return normalization mode; one of the values from NormalizerEnum
*/
JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_text_NativeCollation_getNormalization
  (JNIEnv *env, jclass obj, jlong address)
{
  const UCollator *collator = (const UCollator *)address;
  return (jint)ucol_getNormalization(collator);
}

/**
* Get the offset of the current source character.
* This is an offset into the text of the character containing the current
* collation elements.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param addresss of the C collation elements iterator to query.
* @return offset of the current source character.
*/
JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_text_NativeCollation_getOffset
  (JNIEnv *env, jclass obj, jlong address)
{
  UCollationElements *iterator = (UCollationElements *)address;
  return ucol_getOffset(iterator);
}

/**
* Get the collation rules from a UCollator.
* The rules will follow the rule syntax.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address the address of the C collator
* @return collation rules.
*/
JNIEXPORT jstring JNICALL Java_com_ibm_icu4jni_text_NativeCollation_getRules
  (JNIEnv *env, jclass obj, jlong address)
{
  const UCollator *collator = (const UCollator *)address;
  int32_t length=0;
  const UChar *rules = ucol_getRules(collator, &length);
  return (*env)->NewString(env, rules, length);
}

/**
* Get a sort key for the argument string
* Sort keys may be compared using java.util.Arrays.equals
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address address of the C collator
* @param source string for key to be generated
* @return sort key
*/
JNIEXPORT jbyteArray JNICALL Java_com_ibm_icu4jni_text_NativeCollation_getSortKey
  (JNIEnv *env, jclass obj, jlong address, jstring source)
{
  const UCollator *collator  = (const UCollator *)address;
        jsize      srclength = (*env)->GetStringLength(env, source);
  const UChar     *srcstr    = (const UChar *)(*env)->GetStringCritical(env, 
                                                                   source, 0);

  uint8_t bytearray[UCOL_MAX_BUFFER];
  
  jint bytearraysize = ucol_getSortKey(collator, srcstr, srclength, bytearray, 
                                       UCOL_MAX_BUFFER);

  jbyteArray result;
  
  (*env)->ReleaseStringCritical(env, source, srcstr);

  if (bytearraysize == 0) {
    return NULL;
  }
  
  /* no problem converting uint8_t to int8_t, gives back the correct value
   * tried and tested
   */
  result = (*env)->NewByteArray(env, bytearraysize);
  (*env)->SetByteArrayRegion(env, result, 0, bytearraysize, bytearray);

  return result;
}

/**
* Returns a hash of this collation object
* Note this method is not complete, it only returns 0 at the moment.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address address of C collator
* @return hash of this collation object
*/
JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_text_NativeCollation_hashCode
  (JNIEnv *env, jclass obj, jlong address)
{
  UCollator *collator = (UCollator *)address;
  int32_t length=0;
  const UChar *rules = ucol_getRules(collator, &length);
  /* temporary commented out
   * return uhash_hashUCharsN(rules, length);
   */
  return 0;
}

/**
* Get the ordering priority of the next collation element in the text.
* A single character may contain more than one collation element.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address if C collation elements containing the text.
* @return next collation elements ordering, otherwise returns NULLORDER if an 
*         error has occured or if the end of string has been reached
*/
JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_text_NativeCollation_next
  (JNIEnv *env, jclass obj, jlong address)
{
  UCollationElements *iterator = (UCollationElements *)address;
  UErrorCode status = U_ZERO_ERROR;
  jint result = ucol_next(iterator, &status);

   error(env, status);
  return result;
}

/**
* Opening a new C UCollator with the default locale.
* Note determining if a collator currently exist for the caller is to be handled
* by the caller. Hence if the caller has a existing collator, it is his 
* responsibility to delete first before calling this method.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @return address of the new C UCollator
* @exception thrown if creation of the UCollator fails
*/
JNIEXPORT jlong JNICALL Java_com_ibm_icu4jni_text_NativeCollation_openCollator__
  (JNIEnv *env, jclass obj)
{
  jlong result;
  UErrorCode status = U_ZERO_ERROR;

  result = (jlong)ucol_open(NULL, &status);
  if ( error(env, status) != FALSE)
    return 0;
 
  return result;
}


/**
* Opening a new C UCollator with the argument locale rules.
* Note determining if a collator currently exist for the caller is to be handled
* by the caller. Hence if the caller has a existing collator, it is his 
* responsibility to delete first before calling this method.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param locale name
* @return address of the new C UCollator
* @exception thrown if creation of the UCollator fails
*/
JNIEXPORT jlong JNICALL Java_com_ibm_icu4jni_text_NativeCollation_openCollator__Ljava_lang_String_2
  (JNIEnv *env, jclass obj, jstring locale)
{
  /* this will be null terminated */
  const char *localestr = (*env)->GetStringUTFChars(env, locale, 0);
  
  jlong result;
  UErrorCode status = U_ZERO_ERROR;

  result = (jlong)ucol_open(localestr, &status);
  (*env)->ReleaseStringUTFChars(env, locale, localestr);
  
  if ( error(env, status) != FALSE)
    return 0;
 
  return result;
}

/**
* Opening a new C UCollator with the argument locale rules.
* Note determining if a collator currently exist for the caller is to be 
* handled by the caller. Hence if the caller has a existing collator, it is his 
* responsibility to delete first before calling this method.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param rules set of collation rules
* @param normalizationmode normalization mode
* @param strength collation strength
* @return address of the new C UCollator
* @exception thrown if creation of the UCollator fails
*/
JNIEXPORT jlong JNICALL Java_com_ibm_icu4jni_text_NativeCollation_openCollatorFromRules
  (JNIEnv *env, jclass obj, jstring rules, jint normalizationmode, 
   jint strength)
{
        jsize  ruleslength = (*env)->GetStringLength(env, rules);
  const UChar *rulestr     = (const UChar *)(*env)->GetStringCritical(env, 
                                                                    rules, 0);
        UErrorCode status = U_ZERO_ERROR;
        jlong   result;
  
  result = (jlong)ucol_openRules(rulestr, ruleslength, 
                               (UNormalizationMode)normalizationmode,
                               (UCollationStrength)strength, NULL, &status);

  (*env)->ReleaseStringCritical(env, rules, rulestr);
  if ( error(env, status) != FALSE) {
    return 0;
  }

  return result;
}

/**
* Get the ordering priority of the previous collation element in the text.
* A single character may contain more than one collation element.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address of the C collation element iterator containing the text.
* @return previous collation element ordering, otherwise returns NULLORDER if 
*         an error has occured or if the start of string has been reached
* @exception thrown when retrieval of previous collation element fails.
*/
JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_text_NativeCollation_previous
  (JNIEnv *env, jclass obj, jlong address)
{
  UCollationElements *iterator = (UCollationElements *)address;
  UErrorCode status = U_ZERO_ERROR;
  jint result = ucol_previous(iterator, &status);

   error(env, status);
  return result;
}


/**
* Reset the collation elements to their initial state.
* This will move the 'cursor' to the beginning of the text.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address of C collation element iterator to reset.
*/
JNIEXPORT void JNICALL Java_com_ibm_icu4jni_text_NativeCollation_reset
  (JNIEnv *env, jclass obj, jlong address)
{
  UCollationElements *iterator = (UCollationElements *)address;
  ucol_reset(iterator);
}

/**
* Thread safe cloning operation
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address address of C collator to be cloned
* @return address of the new clone
* @exception thrown when error occurs while cloning
*/
JNIEXPORT jlong JNICALL Java_com_ibm_icu4jni_text_NativeCollation_safeClone
  (JNIEnv *env, jclass obj, jlong address)
{
  const UCollator *collator = (const UCollator *)address;
  UErrorCode status = U_ZERO_ERROR;
  jlong result;
  jint buffersize = U_COL_SAFECLONE_BUFFERSIZE;

  result = (jlong)ucol_safeClone(collator, NULL, &buffersize, &status);

  if ( error(env, status) != FALSE) {
    return 0;
  }
 
  return result;
}

/**
* Universal attribute setter.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address address of the C collator
* @param type type of attribute to be set
* @param value attribute value
* @exception thrown when error occurs while setting attribute value
*/
JNIEXPORT void JNICALL Java_com_ibm_icu4jni_text_NativeCollation_setAttribute
  (JNIEnv *env, jclass obj, jlong address, jint type, jint value)
{
  UCollator *collator = (UCollator *)address;
  UErrorCode status = U_ZERO_ERROR;
  ucol_setAttribute(collator, (UColAttribute)type, (UColAttributeValue)value, 
                    &status);
   error(env, status);
}

/**
* Set the normalization mode used int this object
* The normalization mode influences how strings are compared.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address the address of the C collator
* @param normalizationmode desired normalization mode; one of the values 
*        from NormalizerEnum
*/
JNIEXPORT void JNICALL Java_com_ibm_icu4jni_text_NativeCollation_setNormalization
  (JNIEnv *env, jclass obj, jlong address, jint normalizationmode)
{
  UCollator *collator = (UCollator *)address;
  
  if(collator){
  	ucol_setNormalization(collator, (UNormalizationMode)normalizationmode);
  }
}

/**
* Set the offset of the current source character.
* This is an offset into the text of the character to be processed.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address of the C collation element iterator to set.
* @param offset The desired character offset.
* @exception thrown when offset setting fails
*/
JNIEXPORT void JNICALL Java_com_ibm_icu4jni_text_NativeCollation_setOffset
  (JNIEnv *env, jclass obj, jlong address, jint offset)
{
  UCollationElements *iterator = (UCollationElements *)address;
  UErrorCode status = U_ZERO_ERROR;

  ucol_setOffset(iterator, offset, &status);
   error(env, status);
}

/**
* Set the text containing the collation elements.
* @param env JNI environment
* @param obj RuleBasedCollatorJNI object
* @param address of the C collation element iterator to be set
* @param source text containing the collation elements.
* @exception thrown when error occurs while setting offset
*/
JNIEXPORT void JNICALL Java_com_ibm_icu4jni_text_NativeCollation_setText
  (JNIEnv *env, jclass obj, jlong address, jstring source)
{
  UCollationElements *iterator = (UCollationElements *)address;
  UErrorCode status = U_ZERO_ERROR;
  int strlength = (*env)->GetStringLength(env, source);
  const UChar *str = (const UChar *)(*env)->GetStringCritical(env, source, 0);
  
  ucol_setText(iterator, str, strlength, &status);
  (*env)->ReleaseStringCritical(env, source, str);

   error(env, status);
}
