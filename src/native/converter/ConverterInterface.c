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

void  JNI_TO_U_CALLBACK_SUBSTITUTE
 (void *,UConverterToUnicodeArgs *,const char* ,int32_t ,UConverterCallbackReason ,UErrorCode * );

JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_openConverter (JNIEnv *env, jclass jClass, 
                                                           jlongArray handle, jstring converterName){
    
    UConverter* conv=NULL;
    char cnvName[100];
    UErrorCode errorCode = U_ZERO_ERROR;

    jlong* myHandle = (jlong*) (*env)->GetPrimitiveArrayCritical(env,handle, NULL);
    if(myHandle){
        const jchar* u_cnvName= (jchar*) (*env)->GetStringChars(env, converterName,NULL);
        if(u_cnvName){
            jsize count = (*env)->GetStringLength(env,converterName);
            if(count>0){
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

            }
        }
        (*env)->ReleaseStringChars(env, converterName,u_cnvName);
        *myHandle =(jlong) conv;
    }
    (*env)->ReleasePrimitiveArrayCritical(env,handle,(jlong*)myHandle,JNI_COMMIT);
    return errorCode;
}


JNIEXPORT void JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_closeConverter (JNIEnv *env, jclass jClass, jlong handle){
     
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        ucnv_close(cnv);
    }
}

JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_setSubstitutionModeCharToByte (JNIEnv *env, jclass jClass, 
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
               &fromUOldContext,
               &errorCode);

        }
        else{

            ucnv_setFromUCallBack(conv,
               UCNV_FROM_U_CALLBACK_STOP,
               fromUNewContext,
               &fromUOldAction,
               &fromUOldContext,
               &errorCode);
         
        }
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}

JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_convertCharToByte(JNIEnv *env, jclass jClass, 
                                                        jlong handle, 
                                                        jcharArray source, jint sourceEnd, 
                                                        jbyteArray target, jint targetEnd, 
                                                        jintArray data, jboolean flush){
    

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
                }
                (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
            }
            (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT); 
        }
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}


JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_convertByteToChar(JNIEnv *env, jclass jClass, 
                                                      jlong handle, 
                                                      jbyteArray source, jint sourceEnd, 
                                                      jcharArray target,jint targetEnd, 
                                                      jintArray data,jboolean flush){

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
                }
                (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
            }
            (*env)->ReleasePrimitiveArrayCritical(env,source,(jchar*)uSource,JNI_COMMIT); 
        }
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}


JNIEXPORT void JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_resetByteToChar(JNIEnv *env, jclass jClass, jlong handle){

    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        ucnv_resetToUnicode(cnv);
    }
}

JNIEXPORT void JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_resetCharToByte(JNIEnv *env, jclass jClass, jlong handle){

    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        ucnv_resetFromUnicode(cnv);
    }

}

JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_countInvalidBytes (JNIEnv *env, jclass jClass, 
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


JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_countInvalidChars(JNIEnv *env, jclass jClass, 
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

JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_getMaxBytesPerChar(JNIEnv *env, jclass jClass, 
                                                               jlong handle){
    UConverter* cnv = (UConverter*)handle;
    if(cnv){
        return (jint)ucnv_getMaxCharSize(cnv);
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_flushByteToChar(JNIEnv *env, jclass jClass, 
                                                            jlong handle, 
                                                            jcharArray target, jint targetEnd, 
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
                const char* mySourceLimit=0;
                UChar* cTarget=uTarget+ *targetOffset;
                const UChar* cTargetLimit=uTarget+targetEnd;

                ucnv_toUnicode( cnv , &cTarget, cTargetLimit,(const char**)&mySource,
                               mySourceLimit,NULL,TRUE, &errorCode);

                *targetOffset = cTarget - uTarget;
                if(U_FAILURE(errorCode)){
                    (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
                    (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
                    return errorCode;
                }
            }
            (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);

        }
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}

JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_flushCharToByte (JNIEnv *env, jclass jClass, 
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
                const UChar* mySourceLimit= 0;
                char* cTarget=uTarget+ *targetOffset;
                const char* cTargetLimit=uTarget+targetEnd;

                ucnv_fromUnicode( cnv , &cTarget, cTargetLimit,&mySource,
                                  mySourceLimit,NULL,TRUE, &errorCode);
            
                *targetOffset =(jbyte*) cTarget - uTarget;
                if(U_FAILURE(errorCode)){
                    (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
                
                    (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
                    return errorCode;
                }
            }
            (*env)->ReleasePrimitiveArrayCritical(env,target,uTarget,JNI_COMMIT);
        }
        (*env)->ReleasePrimitiveArrayCritical(env,data,(jint*)myData,JNI_COMMIT);
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}


JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_setSubstitutionBytes(JNIEnv *env, jclass jClass, 
                                                                 jlong handle,
                                                                 jbyteArray subChars, 
                                                                 jint length){

    UConverter* cnv = (UConverter*) handle;
    UErrorCode errorCode = U_ZERO_ERROR;
    if(cnv){
        jchar* u_subChars = (*env)->GetPrimitiveArrayCritical(env,subChars,NULL);
        if(u_subChars){
             char* mySubChars= (char*)malloc(sizeof(char)*length);
             u_UCharsToChars(u_subChars,&mySubChars[0],length);
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
    UChar* subChars;
}SubCharStruct;


JNIEXPORT jint JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_setSubstitutionChars(JNIEnv *env, jclass jClass, 
                                                                 jlong handle, 
                                                                 jcharArray subChars, 
                                                                 jint length){
    UConverter* cnv = (UConverter*) handle;
    UErrorCode errorCode = U_ZERO_ERROR;
    jchar* u_subChars=NULL;
    if(cnv){
        if(subChars){
            u_subChars = (*env)->GetPrimitiveArrayCritical(env,subChars,NULL);
            if(u_subChars){
                SubCharStruct* substitutionCharS = (SubCharStruct*) malloc(sizeof(SubCharStruct));
                if(substitutionCharS){
                    substitutionCharS->subChars =(UChar*)malloc(sizeof(UChar)*length);
                    if(substitutionCharS->subChars){
                       UConverterToUCallback toUOldAction;
                       void* toUOldContext;
                       void* toUNewContext=NULL ;
                       u_strcpy(substitutionCharS->subChars,u_subChars);
                       substitutionCharS->length = length;
                       toUNewContext = substitutionCharS;

                       ucnv_setToUCallBack(cnv,
                           JNI_TO_U_CALLBACK_SUBSTITUTE,
                           toUNewContext,
                           &toUOldAction,
                           &toUOldContext,
                           &errorCode);

                       if(toUOldContext){
                           SubCharStruct* temp = (SubCharStruct*) toUOldContext;
                           free(temp->subChars);
                           free(temp);
                       }
               
                       if(U_FAILURE(errorCode)){
                          (*env)->ReleasePrimitiveArrayCritical(env,subChars,u_subChars,JNI_COMMIT);
                          return errorCode;
                       }

                    }
                   
                }
            }
            (*env)->ReleasePrimitiveArrayCritical(env,subChars,u_subChars,JNI_COMMIT);
        }
        else{

            UConverterToUCallback toUOldAction;
            void* toUOldContext;
            void* toUNewContext=NULL ;

            ucnv_setToUCallBack(cnv,
               UCNV_TO_U_CALLBACK_STOP,
               toUNewContext,
               &toUOldAction,
               &toUOldContext,
               &errorCode);
   
            if(toUOldContext){
                SubCharStruct* temp = (SubCharStruct*) toUOldContext;
                free(temp);
            }
        }
        return errorCode;
    }
    errorCode = U_ILLEGAL_ARGUMENT_ERROR;
    return errorCode;
}


void  JNI_TO_U_CALLBACK_SUBSTITUTE(void *context,
                        UConverterToUnicodeArgs *toArgs,
                        const char* codeUnits,
                        int32_t length,
                        UConverterCallbackReason reason,
                        UErrorCode * err){


    SubCharStruct* temp = (SubCharStruct*)context;
    if( temp){
        if (reason > UCNV_IRREGULAR){
            free(temp->subChars);
            free(temp);
            return;
        }

        /* reset the error */
        *err = U_ZERO_ERROR;
        ucnv_cbToUWriteUChars(toArgs,temp->subChars ,temp->length , 0, err);
    }
    return;
}

JNIEXPORT jboolean JNICALL Java_com_ibm_icu4jni_converters_NativeConverter_canConvert(JNIEnv *env, jclass jClass, 
                                                           jlong handle, jint codeUnit){
    
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

        ucnv_fromUnicode(cnv,
            &myTarget,targetLimit, 
            (const UChar**)&mySource, sourceLimit,NULL, TRUE,&errorCode);

        if(U_SUCCESS(errorCode)){
            return (jboolean)TRUE;
        }
    }
    return (jboolean)FALSE;
}


