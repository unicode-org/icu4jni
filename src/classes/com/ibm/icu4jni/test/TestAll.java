/*
 *******************************************************************************
 * Copyright (C) 1996-2000, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 *
 * $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/test/TestAll.java,v $ 
 * $Date: 2001/03/16 05:23:16 $ 
 * $Revision: 1.1 $
 *
 *****************************************************************************************
 */
package com.ibm.icu4jni.test;

/**
 * Top level test used to run all other tests as a batch.
 */
 
public class TestAll extends TestFmwk {

    public static void main(String[] args) throws Exception{
      new TestAll().run(args);
    }

    public void TestCollation() throws Exception{
      run(new TestFmwk[] {
            new com.ibm.icu4jni.test.text.CollatorRegressionTest(),
            new com.ibm.icu4jni.test.text.CollationElementIteratorTest(),
            new com.ibm.icu4jni.test.text.CollatorTest()
            }
            );
    }
}
