
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

import com.ibm.icu4jni.converters.NativeConverter;
import com.ibm.icu4jni.test.TestFmwk;

public class TestCMemoryLeak {
    static final int THREAD_COUNT = 20;
    static final int LOOP_COUNT=200470;
    static  int nn = 0;

    private static class TestThread extends Thread {
        public void run() {
        while(doRun == false) {
            try {
             Thread.sleep(500);
//             System.err.println("oc reprintable");
            } catch(Throwable t) {
            }
        }     
        try {
            for (int i = 0; i < LOOP_COUNT; i++) {
            
                ByteToCharConverterICU convto = new ByteToCharConverterICU("UTF-8");
//                CharsetDecoder cd = cs.newDecoder();
//                cd //= cd.replaceWith("a");
                String str = "abc";
                nn++;

                char chars[] = new char[2048];
                byte b[] = "string".getBytes("UTF-8");
                int len  = convto.convert(b, 0, b.length, chars, 0, chars.length);
                
                convto.close();

//                if (i % 50000 == 0) {
//                    System.out.print(".");
//                    if(i% (50*50000) == 0) {
//                        System.out.println(" "+i);
//                    }
//                    System.out.flush();
//                }
                }
         } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("FAILED- @"+this.toString());

            }
        }
    };

    public static boolean doRun = false;

    static void doMultithreadTest() throws Throwable {
        Thread th[] = new Thread[THREAD_COUNT];
        for(int i=0;i<THREAD_COUNT;i++) {
            th[i] = new TestThread();
            th[i].start();
        }
        System.err.println(THREAD_COUNT+" threads started.");
        doRun = true;
        for(;;) {
            int j = 0;
            for(int i=0;i<THREAD_COUNT;i++) {
                if(th[i].isAlive()) {
                    j++;
                }
            }
            System.err.println(j+" threads alive- ops: " + nn + " - " + freeMem());
            
            if(j==0) { return; }
            
            Thread.sleep(2000);
        }
//        System.err.println("Done.");
    }
 
    public static void main(String[] args) throws Throwable {
    int i=0;
        if(args.length<=0||args[0].equals("-st")) {
            doMultithreadTest();
            return;
        }
        try { 
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

    /**
     * Print memory stats
     * @return
     */
    public static String freeMem() {
        Runtime r = Runtime.getRuntime();
        double total = r.totalMemory();
        total = total / 1024000.0;
        double free = r.freeMemory();
        free = free / 1024000.0;
        return "Free memory: " + (int)free + "M / " + total + "M";

    }

}

