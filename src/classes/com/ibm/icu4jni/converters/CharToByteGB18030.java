
/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/converters/CharToByteGB18030.java,v $ 
* $Date: 2002/12/18 22:54:29 $ 
* $Revision: 1.5 $
*
*******************************************************************************
*/ 
 package com.ibm.icu4jni.converters;
 /**
  * A JNI interface converter of GB18030 
  * @author Ram Viswanadha
  */
  import java.io.UnsupportedEncodingException; 
  
  public class CharToByteGB18030 extends CharToByteConverterICU {
    /**
     * Create an instance of CharToByteGB18030 converter
     *
     * @exception UnsupportedEncodingException if the converter could not be opened
     * @internal ICU 2.4
     */    
    public CharToByteGB18030()throws UnsupportedEncodingException{
        super("gb18030");
    }
   /**
    * finalize method
    * @internal ICU 2.4
    */
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