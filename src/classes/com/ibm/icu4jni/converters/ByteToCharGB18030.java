/*
 * @(#)ByteToCharGB18030.java	
 *
 * Copyright 2000-2004 IBM Corp.
 *
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