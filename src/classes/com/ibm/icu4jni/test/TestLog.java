/*
 *******************************************************************************
 * Copyright (C) 1996-2001, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 *
 * $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/test/TestLog.java,v $ 
 * $Date: 2001/03/23 19:42:46 $ 
 * $Revision: 1.4 $
 *
 *****************************************************************************************
 */

package com.ibm.icu4jni.test;

public interface TestLog {

    /**
     * Adds given string to the log if we are in verbose mode.
     */
    void log(String message);

    void logln(String message);

    /**
     * Report an error
     */
    void err(String message);

    void errln(String message);
}
