/*
 *******************************************************************************
 * Copyright (C) 1996-2001, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 *
 * $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/test/TestAll.java,v $ 
 * $Date: 2001/09/18 00:33:49 $ 
 * $Revision: 1.5 $
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
            new com.ibm.icu4jni.test.text.CollatorAPITest(),
            new com.ibm.icu4jni.test.text.EnglishCollatorTest(),
            new com.ibm.icu4jni.test.text.SpanishCollatorTest(),
            new com.ibm.icu4jni.test.text.ThaiCollatorTest(),
            new com.ibm.icu4jni.test.text.TurkishCollatorTest(),
            new com.ibm.icu4jni.test.text.FinnishCollatorTest(),
            new com.ibm.icu4jni.test.text.CurrencyCollatorTest(),
            new com.ibm.icu4jni.test.text.DanishCollatorTest(),
            new com.ibm.icu4jni.test.text.DummyCollatorTest(),
            new com.ibm.icu4jni.test.text.FrenchCollatorTest(),
            new com.ibm.icu4jni.test.text.G7CollatorTest(),
            new com.ibm.icu4jni.test.text.GermanCollatorTest(),
            new com.ibm.icu4jni.test.text.KanaCollatorTest(),
            new com.ibm.icu4jni.test.text.MonkeyCollatorTest()
            }
      );
    }
    public void TestNormalization() throws Exception{
        run(new TestFmwk[]{
                new com.ibm.icu4jni.test.text.BasicTest(),
                new com.ibm.icu4jni.test.text.ConformanceTest()
            }
       );
    }
    public void TestConversion() throws Exception{
        run( new TestFmwk[]{
                new com.ibm.icu4jni.test.converters.TestConverter()
            }
        );
    }
               
}
