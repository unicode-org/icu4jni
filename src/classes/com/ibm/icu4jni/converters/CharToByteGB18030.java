
/*
 * @(#)CharToByteGB18030.java	
 *
 * Copyright 2000-2004 IBM Corp.
 *
 */
 package com.ibm.icu4jni.converters;
 /* A JNI interface converter of GB18030 
  * @author Ram Viswanadha
  */
  import java.io.UnsupportedEncodingException; 
  
  public class CharToByteGB18030 extends CharToByteConverterICU {
        
    public CharToByteGB18030()throws UnsupportedEncodingException{
        super("gb18030");
    }
    
    protected void finalize() throws Throwable{
        super.finalize();
    }
    /* Overides the super class method and returns
     * true since all Unicode codepoints can be
     * converted to GB18030
     */
   /* public boolean canConvert(char c){
        return true;
    }*/
  }