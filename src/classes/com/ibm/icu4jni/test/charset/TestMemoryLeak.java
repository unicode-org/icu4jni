/**
*******************************************************************************
* Copyright (C) 1996-2008, International Business Machines Corporation and	  *
* others. All Rights Reserved.												  *
*******************************************************************************
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.test.charset;



import java.io.UnsupportedEncodingException;

import sun.io.ByteToCharConverter;
import sun.io.CharToByteConverter;
import sun.io.ConversionBufferFullException;
import sun.io.MalformedInputException;
import sun.io.UnknownCharacterException;

import sun.nio.*;

import com.ibm.icu4jni.converters.ByteToCharConverterICU;
import com.ibm.icu4jni.converters.CharToByteConverterICU;
import com.ibm.icu4jni.test.TestFmwk;

import com.ibm.icu4jni.charset.*;
import com.ibm.icu4jni.converters.NativeConverter;
import com.ibm.icu4jni.test.TestFmwk;

public class TestMemoryLeak {
    public static void main(String[] args) {
        try { 
            for (int i = 0; i < 10000000; i++) {
                CharsetProviderICU provider = new CharsetProviderICU();
                Charset cs = provider.charsetForName("UTF-8");
                CharsetDecoder cd = cs.newDecoder();
                cd = cd.replaceWith("a");
                String str = "abc";
                cd.decode(ByteBuffer.wrap("string".getBytes("UTF-8")),
                    CharBuffer.wrap(new char[1024]), false);
                if (i % 50000 == 0) {
                    System.out.print(".");
                    System.out.flush();
                }
            }
            System.out.println("PASSED");
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("FAILED");
        }
    }
}
