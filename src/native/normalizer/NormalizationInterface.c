/*
 *  @(#) NormalizationInterface.c
 *
 * (C) Copyright IBM Corp. 2001 - All Rights Reserved
 *  A JNI wrapper to ICU native Normalization Interface
 * @author: Ram Viswanadha
 */

#include "ErrorCode.h"
#include "unicode/unorm.h"


JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_text_NativeNormalizer_normalize(JNIEnv *env, 
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

                if(U_FAILURE(errorCode)){
                    (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
                    (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT);
                    (*env)->ReleasePrimitiveArrayCritical(env,requiredLength,reqLength,JNI_COMMIT);
                    return errorCode;
                }
                *reqLength=retVal;
            }
            (*env)->ReleasePrimitiveArrayCritical(env,requiredLength,reqLength,JNI_COMMIT);
        }
        (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
    }
    (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT); 

    return errorCode;

}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_text_NativeNormalizer_quickCheck (JNIEnv *env, 
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
            int retVal= (int) unorm_quickCheck( uSource, sourceLength,
                                                (UNormalizationMode) mode , 
                                                &errorCode);
            *qcRetVal = retVal; 
         
            if(U_FAILURE(errorCode)){
                (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT);
                return errorCode;
            }
        }
        (*env)->ReleasePrimitiveArrayCritical(env,qcReturn,qcRetVal,JNI_COMMIT);
    }
    (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT); 
    
    return errorCode;
}


