/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/converters/ByteToCharGB18030.java,v $ 
* $Date: 2001/03/24 02:59:20 $ 
* $Revision: 1.4 $
*
*******************************************************************************
*/ 
 package com.ibm.icu4jni.converters;
 /* A JNI interface converter of GB18030 
  * @author Ram Viswanadha, IBM
  */
 import java.io.UnsupportedEncodingException;
 
 public class ByteToCharGB18030 extends ByteToCharConverterICU{
    
   public ByteToCharGB18030()throws UnsupportedEncodingException{
        super("gb18030");
   }
   protected void finalize() throws Throwable{
        super.finalize();
    }
}