
/**
*******************************************************************************
* Copyright (C) 1996-2008, International Business Machines Corporation and	  *
* others. All Rights Reserved.												  *
*******************************************************************************
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.test.converters;



import java.io.UnsupportedEncodingException;

import sun.io.ByteToCharConverter;
import sun.io.CharToByteConverter;
import sun.io.ConversionBufferFullException;
import sun.io.MalformedInputException;
import sun.io.UnknownCharacterException;


import com.ibm.icu4jni.converters.ByteToCharConverterICU;
import com.ibm.icu4jni.converters.CharToByteConverterICU;
import com.ibm.icu4jni.test.TestFmwk;

import com.ibm.icu4jni.charset.*;
import com.ibm.icu4jni.converters.NativeConverter;
import com.ibm.icu4jni.test.TestFmwk;

public class TestCMemoryLeak {
    public static void main(String[] args) {
    int i=0;
        try { 
//            for (int i = 0; i < 10000000; i++) {
            for (;;) {
            i++;
                ByteToCharConverterICU convto = new ByteToCharConverterICU("UTF-8");
//                CharsetDecoder cd = cs.newDecoder();
//                cd = cd.replaceWith("a");
                String str = "abc";

                char chars[] = new char[2048];
                byte b[] = "string".getBytes("UTF-8");
                int len  = convto.convert(b, 0, b.length, chars, 0, chars.length);

                if (i % 50000 == 0) {
                    System.out.print(".");
                    if(i% (50*50000) == 0) {
                        System.out.println(" "+i);
                    }
                    System.out.flush();
                }
            }
    //        System.out.println("PASSED");
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("FAILED- @"+i);
        }
    }
}

