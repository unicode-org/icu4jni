/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/native/common/ErrorCode.h,v $ 
* $Date: 2001/09/18 00:36:03 $ 
* $Revision: 1.6 $
*
*******************************************************************************
*/

#ifndef ERRORCODE_H
#define ERRORCODE_H

#include <jni.h>
#include "unicode/utypes.h"
#include "unicode/putil.h"

/**
* Checks if an error has occured. 
* Throws a generic Java RuntimeException if an error has occured.
* @param env JNI environment variable
* @param errorcode code to determine if it is an erro
* @return 0 if errorcode is not an error, 1 if errorcode is an error, but the 
*         creation of the exception to be thrown fails
* @exception thrown if errorcode represents an error
*/
UBool error(JNIEnv *env, UErrorCode errorcode);

#endif
