/*
 *  @(#) icujniinterface.c	1.2 00/10/11
 *
 * (C) Copyright IBM Corp. 2000 - All Rights Reserved
 *  A JNI wrapper to ICU native converter Interface
 * @author: Ram Viswanadha
 */

#include "ConverterInterface.h"
#include "unicode/utypes.h"   /* Basic ICU data types */
#include "unicode/ucnv.h"     /* C   Converter API    */
#include "unicode/ustring.h"  /* some more string functions*/
#include "unicode/ucnv_cb.h"  /* for callback functions */
#include "ErrorCode.h"
#include <stdlib.h>
#include <string.h>
 
/* Prototype of callback for substituting user settable sub chars */
void  JNI_TO_U_CALLBACK_SUBSTITUTE
 (const void *,UConverterToUnicodeArgs *,const char* ,int32_t ,UConverterCallbackReason ,UErrorCode * );

/**
 * Opens the ICU converter
 * @param env environment handle for JNI 
 * @param jClass handle for the class
 * @param handle buffer to recieve ICU's converter address
 * @param converterName name of the ICU converter
 */
JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_openConverter (JNIEnv *env, 
                                                               jclass jClass, 
                                                               jlongArray handle, 
                                                               jstring converterName){
    
    UConverter* conv=NULL;
    char cnvName[100];
    UErrorCode errorCode = U_ZERO_ERROR;

    jlong* myHandle = (jlong*) (*env)->GetPrimitiveArrayCritical(env,handle, NULL);
    if(myHandle){
        const jchar* u_cnvName= (jchar*) (*env)->GetStringChars(env, converterName,NULL);
        if(u_cnvName){
            jsize count = (*env)->GetStringLength(env,converterName);
            if(count>0){
                if(count< 100){
                    u_UCharsToChars(u_cnvName,&cnvName[0],count);
                    /* Sun's java.exe is passing down 0x10 if the string 
                     * is of certain length so we need to null terminate 
                     */
                    cnvName[count] = '\0';
        
                    conv = ucnv_open(cnvName,&errorCode);

                    if(U_FAILURE(errorCode)){
                        (*env)->ReleaseStringChars(env, converterName,u_cnvName);
                        (*env)->ReleasePrimitiveArrayCritical(env,handle,(jlong*)myHandle,JNI_COMMIT);
                        conv=NULL;
                        return errorCode;
                    }
                }else{
                    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
                }

            }else{
                errorCode = U_ILLEGAL_ARGUMENT_ERROR;
            }
        }
        (*env)->ReleaseStringChars(env, converterName,u_cnvName);
        *myHandle =(jlong) conv;
    }
    (*env)->ReleasePrimitiveArrayCritical(env,handle,(jlong*)myHandle,JNI_COMMIT);
    return errorCode;
}

/**
 * Closes the ICU converter
 * @param env environment handle for JNI 
 * @param jClass handle for the class
 * @param handle address of ICU converter
 */
JNIEXPORT void JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_closeConverter (JNIEnv *env, 
                                                                jclass jClass, 
                                                                jlong handle){
     
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        ucnv_close(cnv);
    }
}

/**
 * Sets the substution mode for to Unicode conversion. Currently only 
 * two modes are supported: substitute or report
 * @param env environment handle for JNI 
 * @param jClass handle for the class
 * @param handle address of ICU converter
 * @param mode the mode to set 
 */
JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_setSubstitutionModeCharToByte (JNIEnv *env, 
                                                                               jclass jClass, 
                                                                               jlong handle, 
                                                                               jboolean mode){
    
    UConverter* conv = (UConverter*)handle;
    UErrorCode errorCode =U_ZERO_ERROR;

    if(conv){
        
        UConverterFromUCallback fromUOldAction ;
        void* fromUOldContext;
        void* fromUNewContext=NULL;
        if(mode){

            ucnv_setFromUCallBack(conv,
               UCNV_FROM_U_CALLBACK_SUBSTITUTE,
               fromUNewContext,
               &fromUOldAction,
               (const void**)&fromUOldContext,
               &errorCode);

        }
        else{

            ucnv_setFromUCallBack(conv,
               UCNV_FROM_U_CALLBACK_STOP,
               fromUNewContext,
               &fromUOldAction,
               (const void**)&fromUOldContext,
               &errorCode);
         
        }
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}

/**
 * Converts a buffer of Unicode code units to target encoding 
 * @param env environment handle for JNI 
 * @param jClass handle for the class
 * @param handle address of ICU converter
 * @param source buffer of Unicode chars to convert 
 * @param sourceEnd limit of the source buffer
 * @param target buffer to recieve the converted bytes
 * @param targetEnd the limit of the target buffer
 * @param data buffer to recieve state of the current conversion
 * @param flush boolean that specifies end of source input
 */
JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_convertCharToByte(JNIEnv *env, 
                                                                  jclass jClass, 
                                                                  jlong handle, 
                                                                  jcharArray source, 
                                                                  jint sourceEnd, 
                                                                  jbyteArray target, 
                                                                  jint targetEnd, 
                                                                  jintArray data, 
                                                                  jboolean flush){
    

    UErrorCode errorCode =U_ZERO_ERROR;
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        jint* myData = (jint*) (*env)->GetPrimitiveArrayCritical(env,data,NULL);
        if(myData){
            jint* sourceOffset = &myData[0];
            jint* targetOffset = &myData[1];
            const jchar* uSource =(jchar*) (*env)->GetPrimitiveArrayCritical(env,source, NULL);
            if(uSource){
                jbyte* uTarget=(jbyte*) (*env)->GetPrimitiveArrayCritical(env,target,NULL);
                if(uTarget){
                    const jchar* mySource = uSource+ *sourceOffset;
                    const UChar* mySourceLimit= uSource+sourceEnd;
                    char* cTarget=uTarget+ *targetOffset;
                    const char* cTargetLimit=uTarget+targetEnd;
                    
                    ucnv_fromUnicode( cnv , &cTarget, cTargetLimit,&mySource,
                                    mySourceLimit,NULL,(UBool) flush, &errorCode);

                    *sourceOffset = (jint) (mySource - uSource)-*sourceOffset;
                    *targetOffset = (jint) ((jbyte*)cTarget - uTarget)- *targetOffset;
                    if(U_FAILURE(errorCode)){
                        (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
                        (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT);
                        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
                        return errorCode;
                    }
                }else{
                    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
                }
                (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
            }else{
                    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
            }
            (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT); 
        }else{
                    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
        }
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_encode(JNIEnv *env, 
                                                      jclass jClass, 
                                                      jlong handle, 
                                                      jcharArray source, 
                                                      jint sourceEnd, 
                                                      jbyteArray target, 
                                                      jint targetEnd, 
                                                      jintArray data, 
                                                      jboolean flush){
   
    UErrorCode ec = Java_com_ibm_icu4jni_converters_NativeConverter_convertCharToByte(env,
                                                    jClass,handle,source,sourceEnd, 
                                                    target,targetEnd,data,flush);

    if(ec == U_ILLEGAL_CHAR_FOUND || ec == U_INVALID_CHAR_FOUND){
        jint* myData = (jint*) (*env)->GetPrimitiveArrayCritical(env,data,NULL);
        UConverter* cnv = (UConverter*)handle;
        jint count =0;
        UChar invalidUChars[32];
        ucnv_getInvalidUChars(cnv,invalidUChars,(int8_t*)&count,&ec);
        myData[2] = count;
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
    }
    return ec;
}

/**
 * Converts a buffer of encoded bytes to Unicode code units
 * @param env environment handle for JNI 
 * @param jClass handle for the class
 * @param handle address of ICU converter
 * @param source buffer of Unicode chars to convert 
 * @param sourceEnd limit of the source buffer
 * @param target buffer to recieve the converted bytes
 * @param targetEnd the limit of the target buffer
 * @param data buffer to recieve state of the current conversion
 * @param flush boolean that specifies end of source input
 */
JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_convertByteToChar(JNIEnv *env, 
                                                                  jclass jClass, 
                                                                  jlong handle, 
                                                                  jbyteArray source, 
                                                                  jint sourceEnd, 
                                                                  jcharArray target,
                                                                  jint targetEnd, 
                                                                  jintArray data,
                                                                  jboolean flush){

    UErrorCode errorCode =U_ZERO_ERROR;
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        jint* myData = (jint*) (*env)->GetPrimitiveArrayCritical(env,data,NULL);
        if(myData){
            jint* sourceOffset = &myData[0];
            jint* targetOffset = &myData[1];

            const jbyte* uSource =(jbyte*) (*env)->GetPrimitiveArrayCritical(env,source, NULL);
            if(uSource){
                jchar* uTarget=(jchar*) (*env)->GetPrimitiveArrayCritical(env,target,NULL);
                if(uTarget){
                    const jbyte* mySource = uSource+ *sourceOffset;
                    const char* mySourceLimit= uSource+sourceEnd;
                    UChar* cTarget=uTarget+ *targetOffset;
                    const UChar* cTargetLimit=uTarget+targetEnd;
                    
                    ucnv_toUnicode( cnv , &cTarget, cTargetLimit,(const char**)&mySource,
                                   mySourceLimit,NULL,(UBool) flush, &errorCode);
                
                    *sourceOffset = mySource - uSource - *sourceOffset  ;
                    *targetOffset = cTarget - uTarget - *targetOffset;
                    if(U_FAILURE(errorCode)){
                        (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
                        (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT);
                        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
                        return errorCode;
                    }
                }else{
                    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
                }
                (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
            }else{
                errorCode = U_ILLEGAL_ARGUMENT_ERROR;
            }
            (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT); 
        }else{
            errorCode = U_ILLEGAL_ARGUMENT_ERROR;
        }
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_decode(JNIEnv *env, 
                                                      jclass jClass, 
                                                      jlong handle, 
                                                      jbyteArray source, 
                                                      jint sourceEnd, 
                                                      jcharArray target,
                                                      jint targetEnd, 
                                                      jintArray data,
                                                      jboolean flush){

    jint ec = Java_com_ibm_icu4jni_converters_NativeConverter_convertByteToChar(env,
                                                    jClass,handle,source,sourceEnd, 
                                                    target,targetEnd,data,flush);

    if(ec == U_ILLEGAL_CHAR_FOUND || ec == U_INVALID_CHAR_FOUND){
        UConverter* cnv = (UConverter*)handle;
        UErrorCode errorCode = U_ZERO_ERROR;
        if(cnv){
            jint* myData = (jint*) (*env)->GetPrimitiveArrayCritical(env,data,NULL);
            UChar invalidUChars[32];
            jint len = 0;
            ucnv_getInvalidUChars(cnv,invalidUChars,(int8_t*)len,&errorCode);
            myData[2] = len;
            (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        }else{
            errorCode = U_ILLEGAL_ARGUMENT_ERROR;
        }
    }
    return ec;
}
JNIEXPORT void JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_resetByteToChar(JNIEnv *env, 
                                                                jclass jClass, 
                                                                jlong handle){

    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        ucnv_resetToUnicode(cnv);
    }
}

JNIEXPORT void JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_resetCharToByte(JNIEnv *env, 
                                                                jclass jClass, 
                                                                jlong handle){

    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        ucnv_resetFromUnicode(cnv);
    }

}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_countInvalidBytes (JNIEnv *env, 
                                                                   jclass jClass, 
                                                                   jlong handle, 
                                                                   jintArray length) {
    UConverter* cnv = (UConverter*)handle;
    UErrorCode errorCode = U_ZERO_ERROR;
    if(cnv){
        char invalidChars[32];

        jint* len = (jint*) (*env)->GetPrimitiveArrayCritical(env,length, NULL);
        if(len){
            ucnv_getInvalidChars(cnv,invalidChars,(int8_t*)len,&errorCode);
        }
        (*env)->ReleasePrimitiveArrayCritical(env,length,(jint*)len,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;

}


JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_countInvalidChars(JNIEnv *env, 
                                                                  jclass jClass, 
                                                                  jlong handle, 
                                                                  jintArray length) {

    UErrorCode errorCode =U_ZERO_ERROR;
    UConverter* cnv = (UConverter*)handle;
    UChar invalidUChars[32];
    if(cnv){
        jint* len = (jint*) (*env)->GetPrimitiveArrayCritical(env,length, NULL);
        if(len){
            ucnv_getInvalidUChars(cnv,invalidUChars,(int8_t*)len,&errorCode);
        }
        (*env)->ReleasePrimitiveArrayCritical(env,length,(jint*)len,0);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;

}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_getMaxBytesPerChar(JNIEnv *env, 
                                                                   jclass jClass, 
                                                                   jlong handle){
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        return (jint)ucnv_getMaxCharSize(cnv);
    }
    return -1;
}


JNIEXPORT jfloat JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_getAveBytesPerChar(JNIEnv *env, 
                                                                   jclass jClass, 
                                                                   jlong handle){
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
         jfloat max = (jfloat)ucnv_getMaxCharSize(cnv);
         jfloat min = (jfloat)ucnv_getMinCharSize(cnv);
         return (jfloat) ( (max+min)/2 );
    }
    return -1;
}
JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_flushByteToChar(JNIEnv *env, 
                                                                jclass jClass, 
                                                                jlong handle, 
                                                                jcharArray target, 
                                                                jint targetEnd, 
                                                                jintArray data){
    UErrorCode errorCode =U_ZERO_ERROR;
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        jbyte source ='\0';
        jint* myData = (jint*) (*env)->GetPrimitiveArrayCritical(env,data,NULL);
        if(myData){
            jint* targetOffset = &myData[1];
            jchar* uTarget=(jchar*) (*env)->GetPrimitiveArrayCritical(env,target,NULL);
            if(uTarget){
                const jbyte* mySource =&source;
                const char* mySourceLimit=&source;
                UChar* cTarget=uTarget+ *targetOffset;
                const UChar* cTargetLimit=uTarget+targetEnd;

                ucnv_toUnicode( cnv , &cTarget, cTargetLimit,(const char**)&mySource,
                               mySourceLimit,NULL,TRUE, &errorCode);


                *targetOffset = (jint) ((jchar*)cTarget - uTarget)- *targetOffset;
                if(U_FAILURE(errorCode)){
                    (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
                    (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
                    return errorCode;
                }
            }else{
                errorCode = U_ILLEGAL_ARGUMENT_ERROR;
            }
            (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);

        }else{
            errorCode = U_ILLEGAL_ARGUMENT_ERROR;
        }
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_flushCharToByte (JNIEnv *env, 
                                                                 jclass jClass, 
                                                                 jlong handle, 
                                                                 jbyteArray target, 
                                                                 jint targetEnd, 
                                                                 jintArray data){
          
    UErrorCode errorCode =U_ZERO_ERROR;
    UConverter* cnv = (UConverter*)handle;
    jchar source = '\0';
    if(cnv){
        jint* myData = (jint*) (*env)->GetPrimitiveArrayCritical(env,data,NULL);
        if(myData){
            jint* targetOffset = &myData[1];
            jbyte* uTarget=(jbyte*) (*env)->GetPrimitiveArrayCritical(env,target,NULL);
            if(uTarget){
                const jchar* mySource = &source;
                const UChar* mySourceLimit= &source;
                char* cTarget=uTarget+ *targetOffset;
                const char* cTargetLimit=uTarget+targetEnd;

                ucnv_fromUnicode( cnv , &cTarget, cTargetLimit,&mySource,
                                  mySourceLimit,NULL,TRUE, &errorCode);
            

                *targetOffset = (jint) ((jbyte*)cTarget - uTarget)- *targetOffset;
                if(U_FAILURE(errorCode)){
                    (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
                
                    (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
                    return errorCode;
                }
            }else{
                errorCode = U_ILLEGAL_ARGUMENT_ERROR;
            }
            (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
        }else{
            errorCode = U_ILLEGAL_ARGUMENT_ERROR;
        }
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}


JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_setSubstitutionBytes(JNIEnv *env, 
                                                                     jclass jClass, 
                                                                     jlong handle,
                                                                     jbyteArray subChars, 
                                                                     jint length){

    UConverter* cnv = (UConverter*) handle;
    UErrorCode errorCode = U_ZERO_ERROR;
    if(cnv){
        jbyte* u_subChars = (*env)->GetPrimitiveArrayCritical(env,subChars,NULL);
        if(u_subChars){
             char* mySubChars= (char*)malloc(sizeof(char)*length);
             u_UCharsToChars((UChar*)u_subChars,&mySubChars[0],length);
             ucnv_setSubstChars(cnv,mySubChars, (char)length,&errorCode);
             if(U_FAILURE(errorCode)){
                (*env)->ReleasePrimitiveArrayCritical(env,subChars,mySubChars,JNI_COMMIT);
                return errorCode;
             }
             free(mySubChars);
        }
        else{   
           errorCode =  U_ILLEGAL_ARGUMENT_ERROR;
        }
        (*env)->ReleasePrimitiveArrayCritical(env,subChars,u_subChars,JNI_COMMIT); 
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}


#define VALUE_STRING_LENGTH 32

typedef struct{
    int length;
    UChar subChars[256];
    UBool stopOnIllegal;
}SubCharStruct;


static UErrorCode 
setToUCallbackSubs(UConverter* cnv,UChar* subChars, int32_t length,UBool stopOnIllegal ){
    SubCharStruct* substitutionCharS = (SubCharStruct*) malloc(sizeof(SubCharStruct));
    UErrorCode errorCode = U_ZERO_ERROR;
    if(substitutionCharS){
       UConverterToUCallback toUOldAction;
       void* toUOldContext=NULL;
       void* toUNewContext=NULL ;
       if(subChars){
            u_strncpy(substitutionCharS->subChars,subChars,length);
       }else{
           substitutionCharS->subChars[0] =0xFFFD;
       }
       substitutionCharS->subChars[length]=0;
       substitutionCharS->length = length;
       substitutionCharS->stopOnIllegal = stopOnIllegal;
       toUNewContext = substitutionCharS;

       ucnv_setToUCallBack(cnv,
           JNI_TO_U_CALLBACK_SUBSTITUTE,
           toUNewContext,
           &toUOldAction,
           (const void**)&toUOldContext,
           &errorCode);

       if(toUOldContext){
           SubCharStruct* temp = (SubCharStruct*) toUOldContext;
           free(temp);
       }

       return errorCode;
    }
    return U_MEMORY_ALLOCATION_ERROR;
}
JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_setSubstitutionChars(JNIEnv *env, 
                                                                     jclass jClass, 
                                                                     jlong handle, 
                                                                     jcharArray subChars, 
                                                                     jint length){
    UErrorCode errorCode = U_ZERO_ERROR;
    UConverter* cnv = (UConverter*) handle;
    jchar* u_subChars=NULL;
    if(cnv){
        if(subChars){
            int len = (*env)->GetArrayLength(env,subChars);
            u_subChars = (*env)->GetPrimitiveArrayCritical(env,subChars,NULL);
            if(u_subChars){
               errorCode =  setToUCallbackSubs(cnv,u_subChars,len,FALSE);
            }else{
                errorCode = U_ILLEGAL_ARGUMENT_ERROR;
            }
            (*env)->ReleasePrimitiveArrayCritical(env,subChars,u_subChars,JNI_COMMIT);
            return errorCode;
        }
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}


void  JNI_TO_U_CALLBACK_SUBSTITUTE( const void *context,
                                    UConverterToUnicodeArgs *toArgs,
                                    const char* codeUnits,
                                    int32_t length,
                                    UConverterCallbackReason reason,
                                    UErrorCode * err){

    if(context){
        SubCharStruct* temp = (SubCharStruct*)context;
        if( temp){
            if(temp->stopOnIllegal==FALSE){
                if (reason > UCNV_IRREGULAR){
                    return;
                }
                /* reset the error */
                *err = U_ZERO_ERROR;
                ucnv_cbToUWriteUChars(toArgs,temp->subChars ,temp->length , 0, err);
            }else{
                if(reason != UCNV_UNASSIGNED){
                    /* the caller must have set 
                     * the error code accordingly
                     */
                    return;
                }else{
                    *err = U_ZERO_ERROR;
                    ucnv_cbToUWriteUChars(toArgs,temp->subChars ,temp->length , 0, err);
                    return;
                }
            }
        }
    }
    return;
}

JNIEXPORT jboolean JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_canEncode(JNIEnv *env, 
                                                          jclass jClass, 
                                                          jlong handle, 
                                                          jint codeUnit){
    
    UErrorCode errorCode =U_ZERO_ERROR;
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        UChar source[3];
        UChar *mySource=source;
        const UChar* sourceLimit = (codeUnit<0x010000) ? &source[1] : &source[2];
        char target[5];
        char *myTarget = target;
        const char* targetLimit = &target[4];
        int i=0;
        UTF_APPEND_CHAR(&source[0],i,2,codeUnit);

        ucnv_fromUnicode(cnv,&myTarget,targetLimit, 
                         (const UChar**)&mySource, 
                         sourceLimit,NULL, TRUE,&errorCode);

        if(U_SUCCESS(errorCode)){
            return (jboolean)TRUE;
        }
    }
    return (jboolean)FALSE;
}


JNIEXPORT jboolean JNICALL
Java_com_ibm_icu4jni_converters_NativeConverter_canDecode(JNIEnv *env, 
                                                          jclass jClass, 
                                                          jlong handle, 
                                                          jbyteArray source){
    
    UErrorCode errorCode =U_ZERO_ERROR;
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        jint len = (*env)->GetArrayLength(env,source);    
        jbyte* cSource =(jbyte*) (*env)->GetPrimitiveArrayCritical(env,source, NULL);
        if(cSource){
            const jbyte* cSourceLimit = cSource+len;

            /* Assume that we need at most twice the length of source */
            UChar* target = (UChar*) malloc(sizeof(UChar)* (len<<1));
            UChar* targetLimit = target + (len<<1);
            if(target){
                ucnv_toUnicode(cnv,&target,targetLimit, 
                               (const char**)&cSource, 
                               cSourceLimit,NULL, TRUE,&errorCode);

                if(U_SUCCESS(errorCode)){
                    free(target);
                    (*env)->ReleasePrimitiveArrayCritical(env,source,cSource,JNI_COMMIT);        
                    return (jboolean)TRUE;
                }
            }
            free(target);
        }
        (*env)->ReleasePrimitiveArrayCritical(env,source,cSource,JNI_COMMIT);        
    }
    return (jboolean)FALSE;
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_countAvailable(JNIEnv *env, jclass jClass){
    return ucnv_countAvailable();
}

JNIEXPORT jobjectArray JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_getAvailable(JNIEnv *env, jclass jClass){
   
    jobjectArray ret;
    int32_t i = ucnv_countAvailable();
    UErrorCode error = U_ZERO_ERROR;
    const char* name =NULL;

    ret= (jobjectArray)(*env)->NewObjectArray( env,i,
                                               (*env)->FindClass(env,"java/lang/String"),
                                               (*env)->NewStringUTF(env,""));

    for(;--i>=0;) {
        name = ucnv_getAvailableName(i);
        if(strstr(name,",")!=0){
            name = ucnv_getAlias(name,1,&error);

        }
        
        (*env)->SetObjectArrayElement(env,ret,i,(*env)->NewStringUTF(env,name));
        /* printf("canonical name : %s  at %i\n", name,i); */
    }
    return (ret);
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_countAliases(JNIEnv *env, jclass jClass,jstring enc){
    
    UErrorCode error = U_ZERO_ERROR;
    jint num =0;
    const char* encName = (*env)->GetStringUTFChars(env,enc,NULL);
    
    if(encName){
        num = ucnv_countAliases(encName,&error);
    }
    
    (*env)->ReleaseStringUTFChars(env,enc,encName);

    return num;
}


JNIEXPORT jobjectArray JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_getAliases(JNIEnv *env, jclass jClass, jstring enc){

    jobjectArray ret=NULL;
    int32_t aliasNum = 0;
    UErrorCode error = U_ZERO_ERROR;
    const char* encName = (*env)->GetStringUTFChars(env,enc,NULL);
    int i=0;
    int j=0;
    const char* aliasArray[50];

    if(encName){
        aliasNum = ucnv_countAliases(encName,&error);
        if(U_SUCCESS(error)){
            for(i=0,j=0;i<aliasNum;i++){
                const char* name = ucnv_getAlias(encName,(uint16_t)i,&error);
                if(strstr(name,",")==0){
                    aliasArray[j++]= name;
                }
            }
            ret =  (jobjectArray)(*env)->NewObjectArray(env,j,
                                                        (*env)->FindClass(env,"java/lang/String"),
                                                        (*env)->NewStringUTF(env,""));
            for(;--j>=0;) {
                 (*env)->SetObjectArrayElement(env,ret,j,(*env)->NewStringUTF(env,aliasArray[j]));
            }
        }            
    }
   (*env)->ReleaseStringUTFChars(env,enc,encName);

    return (ret);
}

JNIEXPORT jstring 
JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_getCanonicalName(JNIEnv *env, jclass jClass,jstring enc){

    UErrorCode error = U_ZERO_ERROR;
    const char* encName = (*env)->GetStringUTFChars(env,enc,NULL);
    const char* canonicalName = NULL;
    jstring ret;
    if(encName){
        canonicalName = ucnv_getAlias(encName,0,&error);
        if(strstr(canonicalName,",")!=0){
           canonicalName = ucnv_getAlias(canonicalName,1,&error);
        }
        ret = ((*env)->NewStringUTF(env, canonicalName));
    }
    (*env)->ReleaseStringUTFChars(env,enc,encName);
    return ret;
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_setCallbackEncode(JNIEnv *env, 
                                                                  jclass jClass, 
                                                                  jlong handle, 
                                                                  jint mode,
                                                                  jboolean stopOnIllegal){
    UConverter* conv = (UConverter*)handle;
    UErrorCode errorCode =U_ZERO_ERROR;

    if(conv){
        
        UConverterFromUCallback fromUOldAction ;
        void* fromUOldContext;
        void* fromUNewContext=NULL;
        UConverterFromUCallback newAction;
        switch(mode){
        default: /* falls through */
        case com_ibm_icu4jni_converters_NativeConverter_STOP_CALLBACK:
           newAction  = UCNV_FROM_U_CALLBACK_STOP;
           break;
        case com_ibm_icu4jni_converters_NativeConverter_SKIP_CALLBACK:
            newAction = UCNV_FROM_U_CALLBACK_SKIP;
            if(stopOnIllegal==TRUE){
                fromUNewContext = UCNV_SUB_STOP_ON_ILLEGAL;
            }
            break;
        case com_ibm_icu4jni_converters_NativeConverter_SUBSTITUTE_CALLBACK:
            newAction = UCNV_FROM_U_CALLBACK_SUBSTITUTE;
            if(stopOnIllegal==TRUE){
                fromUNewContext = UCNV_SUB_STOP_ON_ILLEGAL;
            }
            break;
        }

        ucnv_setFromUCallBack(conv,
           newAction,
           fromUNewContext,
           &fromUOldAction,
           (const void**)&fromUOldContext,
           &errorCode);


        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_setCallbackDecode(JNIEnv *env, 
                                                                  jclass jClass, 
                                                                  jlong handle, 
                                                                  jint mode, 
                                                                  jboolean stopOnIllegal){
    UConverter* conv = (UConverter*)handle;
    UErrorCode errorCode =U_ZERO_ERROR;
    if(conv){
        
        UConverterToUCallback toUOldAction ;
        void* toUOldContext;
        void* toUNewContext=NULL;
        UConverterToUCallback newAction;

        switch(mode){
        default: /* falls through */
        case com_ibm_icu4jni_converters_NativeConverter_STOP_CALLBACK:
           newAction  = UCNV_TO_U_CALLBACK_STOP;
           break;
        case com_ibm_icu4jni_converters_NativeConverter_SKIP_CALLBACK:
            newAction = UCNV_TO_U_CALLBACK_SKIP ;
            
            if(stopOnIllegal==TRUE){
                toUNewContext = UCNV_SUB_STOP_ON_ILLEGAL;
            }
            
            break;
        case com_ibm_icu4jni_converters_NativeConverter_SUBSTITUTE_CALLBACK:
            return setToUCallbackSubs(conv,NULL,0,stopOnIllegal);
            
        }

        ucnv_setToUCallBack(conv,
           newAction,
           toUNewContext,
           &toUOldAction,
           (const void**)&toUOldContext,
           &errorCode);

        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}

JNIEXPORT jlong 
JNICALL Java_com_ibm_icu4jni_text_NativeConverter_safeClone(JNIEnv *env, 
                                                            jclass obj, 
                                                            jlong handle){

    const UConverter *conv = (const UConverter *)handle;
    UErrorCode status = U_ZERO_ERROR;
    jlong result;
    jint buffersize = U_CNV_SAFECLONE_BUFFERSIZE;

    result = (jlong)ucnv_safeClone(conv, NULL, &buffersize, &status);

    if ( error(env, status) != FALSE) {
        return 0;
    }
 
    return result;
}

JNIEXPORT jint JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_getMaxCharsPerByte(JNIEnv *env, 
                                                                   jclass jClass, 
                                                                   jlong handle){
    /*
     * currently we know that max number of chars per byte is 2
     */
    return 2;
}

JNIEXPORT jfloat JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_getAveCharsPerByte(JNIEnv *env, 
                                                                   jclass jClass, 
                                                                   jlong handle){
    jfloat ret = 0;
    ret = (jfloat)( 1/(jfloat)Java_com_ibm_icu4jni_converters_NativeConverter_getMaxBytesPerChar(env,
                                                                                      jClass,
                                                                                      handle));
    return ret;
}

JNIEXPORT jstring JNICALL 
Java_com_ibm_icu4jni_converters_NativeConverter_getSubstitutionBytes(JNIEnv *env, 
                                                                     jclass jClass, 
                                                                     jlong handle){
    const UConverter * cnv = (const UConverter *) handle;
    UErrorCode status = U_ZERO_ERROR;
    char subBytes[50];
    int8_t len =(char)50;
    if(cnv){
        ucnv_getSubstChars(cnv,subBytes,&len,&status);
        if(U_SUCCESS(status)){
            subBytes[len] = 0; /* Null terminate the array */
            return((*env)->NewStringUTF(env, subBytes));
        }
    }
    return 0;
}
