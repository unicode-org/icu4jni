/**
*******************************************************************************
* Copyright (C) 1996-2005, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
*******************************************************************************
*/
/*
 *  @(#) NormalizationInterface.c
 *
 * (C) Copyright IBM Corp. 2001 - All Rights Reserved
 *  A JNI wrapper to ICU native Normalization Interface
 * @author: Ram Viswanadha
 */

#include "ErrorCode.h"
#include "unicode/unorm.h"
#include <stdlib.h>

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_text_NativeNormalizer_normalize___3CI_3CII_3I(  JNIEnv *env, 
                                                                     jclass jClass, 
                                                                     jcharArray source, 
                                                                     jint sourceLength, 
                                                                     jcharArray target, 
                                                                     jint targetLength,
                                                                     jint mode, 
                                                                     jintArray requiredLength){

    UErrorCode errorCode =U_ZERO_ERROR;

    const jchar* uSource =(jchar*) (*env)->GetPrimitiveArrayCritical(env,source, NULL);
    if(uSource){
        jchar* uTarget=(jchar*) (*env)->GetPrimitiveArrayCritical(env,target,NULL);
        if(uTarget){
            jint* reqLength = (jint*) (*env)->GetPrimitiveArrayCritical(env,requiredLength,NULL);
            if(reqLength){
                int retVal=unorm_normalize(uSource,sourceLength,(UNormalizationMode)mode,
                                0 /* Ignore the option IGNORE_HANGUL */,
                                uTarget,targetLength,&errorCode);
                
                *reqLength=retVal;
                if(U_FAILURE(errorCode)){
                    (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,0);
                    (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,0);
                    (*env)->ReleasePrimitiveArrayCritical(env,requiredLength,reqLength,0);
                    return errorCode;
                }
            }else{
                errorCode = U_ILLEGAL_ARGUMENT_ERROR;
            }
            (*env)->ReleasePrimitiveArrayCritical(env,requiredLength,reqLength,0);
        }else{
            errorCode = U_ILLEGAL_ARGUMENT_ERROR;
        }
        (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,0);
    }else{
        errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    }
    (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,0); 

    return errorCode;

}


JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_text_NativeNormalizer_quickCheck___3CII_3I (JNIEnv *env, 
                                                       jclass jClass, 
                                                       jcharArray source, 
                                                       jint sourceLength, 
                                                       jint mode,
                                                       jintArray qcReturn){
    UErrorCode errorCode =U_ZERO_ERROR;

    const jchar* uSource =(jchar*) (*env)->GetPrimitiveArrayCritical(env,source, NULL);
    if(uSource){
        jint*  qcRetVal= (jint*) (*env)->GetPrimitiveArrayCritical(env,qcReturn,NULL);
        if(qcRetVal){
            *qcRetVal= (jint) unorm_quickCheck( uSource, sourceLength,
                                                (UNormalizationMode) mode , 
                                                &errorCode);
         
            if(U_FAILURE(errorCode)){
                (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,0);
                (*env)->ReleasePrimitiveArrayCritical(env,qcReturn,qcRetVal,0);
                return errorCode;
            }
        }else{
            errorCode = U_ILLEGAL_ARGUMENT_ERROR;
        }
        (*env)->ReleasePrimitiveArrayCritical(env,qcReturn,qcRetVal,0);
    }else{
        errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    }
    (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,0); 
    
    return errorCode;
}

#define MAX_LENGTH 1000

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_text_NativeNormalizer_normalize__Ljava_lang_String_2I_3Ljava_lang_String_2(JNIEnv *env, 
                                                                                                jclass jClass, 
                                                                                                jstring source, 
                                                                                                jint mode, 
                                                                                                jobjectArray retStr){
    UErrorCode errorCode = U_ZERO_ERROR;
    const jchar* uSource= (jchar*) (*env)->GetStringChars(env, source,NULL);
    jint sourceLength = (*env)->GetStringLength(env,source);
    UChar* target = NULL;    
    int32_t targetLength =0;
    UChar dst[MAX_LENGTH]={0};
    jstring retString;
    int retVal =0;

    if(uSource){
       target =  dst;
       targetLength = MAX_LENGTH;
       
       retVal=unorm_normalize(uSource,sourceLength,(UNormalizationMode)mode,
                0 /* Ignore the option IGNORE_HANGUL */,
                target,targetLength,&errorCode);

       if(retVal>MAX_LENGTH){
           UChar* target = (UChar*)malloc(sizeof(UChar) * retVal);
           targetLength = retVal;
           retVal=unorm_normalize(uSource,sourceLength,(UNormalizationMode)mode,
                0 /* Ignore the option IGNORE_HANGUL */,
                target,targetLength,&errorCode);
           retString = (*env)->NewString(env,target,retVal);
           free(target);
       }else{
           target[retVal]=0;
           retString = (*env)->NewString(env,target,retVal);
       }
       
       if(U_FAILURE(errorCode)){
           (*env)->ReleaseStringChars(env,source,NULL);
           return errorCode;
       }
        /* succeeded without error set the object array */
       (*env)->SetObjectArrayElement(env,retStr,0,retString);

    }else{
        errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    }
    (*env)->ReleaseStringChars(env,source,NULL);
    return errorCode;           
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_text_NativeNormalizer_quickCheck__Ljava_lang_String_2I_3I(JNIEnv *env, 
                                                                               jclass jClass, 
                                                                               jstring source, 
                                                                               jint mode, 
                                                                               jintArray qcReturn){
    UErrorCode errorCode =U_ZERO_ERROR;
    const jchar* uSource =NULL;
    jint sourceLength = 0;
    uSource = (*env)->GetStringCritical(env,source,NULL);
    sourceLength = (*env)->GetStringLength(env,source);
    if(uSource){
        jint* qcRetVal = (*env)->GetPrimitiveArrayCritical(env,qcReturn,NULL);
        if(qcRetVal){
            *qcRetVal= (jint) unorm_quickCheck(uSource, sourceLength,
                                        (UNormalizationMode) mode , 
                                        &errorCode);
            if(U_FAILURE(errorCode)){
                (*env)->ReleaseStringCritical(env,source,NULL);
                (*env)->ReleasePrimitiveArrayCritical(env,qcReturn,qcRetVal,0);
                return errorCode;
            }
        }else{
            errorCode = U_ILLEGAL_ARGUMENT_ERROR;
        }
        (*env)->ReleasePrimitiveArrayCritical(env,qcReturn,qcRetVal,0);
    }else{
        errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    }
    (*env)->ReleaseStringCritical(env,source,NULL);
    return errorCode;
}
