/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and	  *
* others. All Rights Reserved.												  *
*******************************************************************************
*
* $Source: 
* $Date: 
* $Revision: 
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.test.charset;

import java.nio.*;
import java.nio.charset.spi.*;
import java.nio.charset.*;
import java.util.*;
import com.ibm.icu4jni.charset.*;
import com.ibm.icu4jni.test.*;
import java.util.Iterator;
import sun.misc.Service;

public class TestCharset extends TestFmwk{
	
    static String  encoding= "gb18030";
    static boolean useICU = false;

    static CharsetProvider provider = new CharsetProviderICU(); 
    Charset charset;
    CharsetDecoder decoder ;
    CharsetEncoder encoder ;
    
    static String unistr = "abcd\u8000\u8001\u00a5\u3000\r\n";
    static byte [] gb= {
        (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)0xd2, (byte)0xab, (byte)0xc0, (byte)0xcf,
        (byte)0x81, (byte)0x30, (byte)0x84, (byte)0x36, (byte)0xa1, (byte)0xa1, (byte)0x0d, (byte)0x0a
    };
    
    public static void main(String[] args) throws Exception {
        TestCharset.runTest(args);
    }
    public static void runTest(String[] args){
        TestCharset tcs = new TestCharset();
        tcs.TestAPISemantics();
        tcs.TestConvertAll();
        tcs.TestCallback();
        tcs.TestCanConvert();
        tcs.TestFromUnicode();
        tcs.TestToUnicode();
        tcs.TestString();
        tcs.TestMultithreaded();
        tcs.TestSynchronizedMultithreaded();
    }
    
    TestCharset(){
        charset = Charset.forName(encoding);
        decoder = (CharsetDecoder) charset.newDecoder();
        encoder = (CharsetEncoder) charset.newEncoder();
        System.out.println(charset.getClass());
    }
    public void TestAPISemantics(/*String encoding*/){
            int   i, len;
            int   rc;
            ByteBuffer gbval= ByteBuffer.wrap(gb);
            CharBuffer uniVal = CharBuffer.wrap(unistr);
            rc = 0;
            len = 0;
            decoder.reset();
            /* Convert the whole buffer to Unicode */
            try {
                CharBuffer chars = CharBuffer.allocate(unistr.length());
                CoderResult result = decoder.decode(gbval,chars,false);
                
                if (result.isError()) {
                    System.out.println("ToChars encountered Error");
                    rc=1;
                }

                if (!equals(chars,unistr)) {
                    System.out.println("ToChars does not match");
                    printchars(chars);
                    System.out.print("Expected : ");
                    printchars(unistr);
                    rc=2;
                }else{
                    System.out.println("--Test ToChars "+encoding+" --PASSED");
                }
                    
            } catch (Exception e) {
                System.out.println("ToChars - exception in buffer");
                e.printStackTrace(System.err);
                rc = 5;
            }

            /* Convert single bytes to Unicode */
            try {
                CharBuffer chars = CharBuffer.allocate(unistr.length());
                ByteBuffer b = ByteBuffer.allocate(1);
                len  = 0;
                decoder.reset();
                byte[] temp = new byte[1];
                for (i=0; i<gb.length; i++) {
                    b.rewind();
                    temp[0]=gb[i];
                    b.put(temp);
                    b.rewind();
                    //len += decoder.convert(b, 0, 1, chars, len, chars.length);
                    CoderResult result = decoder.decode(b,chars,false);
                }
                if (unistr.length()!=( chars.limit())) {
                    System.out.println("ToChars single len does not match" );
                    rc=3;
                }
                if (!equals(chars,unistr)) {
                    System.out.println("ToChars single does not match");
                    printchars(chars);
                    rc=4;
                }else{
                    System.out.println("--Test ToChars Single "+encoding+" --PASSED");
                }
            } catch (Exception e) {
                System.out.println("ToChars - exception in single");
                e.printStackTrace(System.err);
                rc = 6;
            }

            /* Convert the buffer one at a time to Unicode */
            try {
                CharBuffer chars = CharBuffer.allocate(unistr.length());
                len  = 0;
                decoder.reset();
                for (i=0; i<=gb.length; i++) {
                    //gbval.position(i-1);
                    gbval.limit(i);
                    CoderResult result=decoder.decode(gbval,chars,false);
                    if(result.isError()){
                        System.out.println("Error while decoding -- FAILED");
                   }
                }
                if (chars.limit()!=unistr.length()) {
                    System.out.println("ToChars Simple buffer len does not match");
                    rc=7;
                }
                if (!equals(chars,unistr)) {
                    System.out.println("ToChars Simple buffer does not match");
                    printchars(chars);
                    System.out.print(" Expected : ");
                    printchars(unistr);
                    rc=8;
                }
            } catch (Exception e) {
                System.out.println("ToChars - exception in single buffer");
                e.printStackTrace(System.err);
                rc = 9;
            }
            if (rc==0) {
               System.out.println("--Test ToChars Simple "+encoding+" --PASSED");
               // errln("Test Simple ToChars for encoding : FAILED");
            }


            rc = 0;
            len = 0;
           // chars = uniVal.toCharArray();
            /* Convert the whole buffer from unicode */
            try {
                ByteBuffer bytes = ByteBuffer.allocate(gb.length);
                encoder.reset();
                CoderResult result = encoder.encode(uniVal,bytes,false);
                if (result.isError()) {
                    System.out.println("FromChars reported error: "+ result.toString());
                    rc=1;
                }
                if (!bytes.equals(gbval)) {
                    System.out.println("FromChars does not match");
                    printbytes(bytes);
                    rc=2;
                }else{
                    System.out.println("--Test FromChars "+encoding+" --PASSED");
                }
            } catch (Exception e) {
                System.out.println("FromChars - exception in buffer");
                e.printStackTrace(System.err);
                rc = 5;
            }

            /* Convert the buffer one char at a time to unicode */
            try {
                ByteBuffer bytes = ByteBuffer.allocate(gb.length);
                CharBuffer c = CharBuffer.allocate(1);
                len  = 0;
                encoder.reset();
                char[] temp = new char[1];
                for (i=0; i<unistr.length(); i++) {
                    temp[0]=unistr.charAt(i);
                    c.put(temp);
                    c.rewind();
                    CoderResult result= encoder.encode(c,bytes,false);
                    c.rewind();
                }
                if (gb.length!= bytes.limit()) {
                    System.out.println("FromChars single len does not match" );
                    rc=3;
                }
                if (!bytes.equals(gbval)) {
                    System.out.println("FromChars single does not match");
                    printbytes(bytes);
                    rc=4;
                }else{
                    System.out.println("--Test FromChars Single "+encoding+" --PASSED");
                }
                    
            } catch (Exception e) {
                System.out.println("FromChars - exception in single");
                e.printStackTrace(System.err);
                rc = 6;
            }

            /* Convert one char at a time to unicode */
            try {
                ByteBuffer bytes = ByteBuffer.allocate(gb.length);
                len  = 0;
                encoder.reset();
                char[] temp = unistr.toCharArray();
                
                for (i=0; i<=temp.length; i++) {
                    uniVal.limit(i);
                    CoderResult result = encoder.encode(uniVal,bytes,false);
                }
                if (bytes.limit()!=gb.length) {
                    System.out.println("FromChars Simple len does not match" );
                    rc=7;
                }
                if (!bytes.equals(gbval)) {
                    System.out.println("FromChars Simple does not match");
                    printbytes(bytes);
                    rc=8;
                }else{
                    System.out.println("--Test FromChars Simple "+encoding+" --PASSED");
                }
            } catch (Exception e) {
                //errln("FromChars - exception in single buffer");
                e.printStackTrace(System.err);
                rc = 9;
            }
            if (rc==0) {
               System.out.println("--Test Simple FromChars "+encoding+" --PASSED");
               // printbytes(bytes);
            }
    }
    private final static char hexarray[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    /*
     * hex4: Create a 4 character string representing a short int
     */
    public static String hex4(int val) {
        char hexs[] = new char[4];
        hexs[0] = hexarray[(val>>12)&0x0f];
        hexs[1] = hexarray[(val>>8)&0x0f];
        hexs[2] = hexarray[(val>>4)&0x0f];
        hexs[3] = hexarray[val&0x0f];
        return new String(hexs);
    }
    /*
     * hex2: Create a 2 character string representing a short int
     */
    public static String hex2(int val) {
        char hexs[] = new char[2];
        hexs[0] = hexarray[(val>>4)&0x0f];
        hexs[1] = hexarray[val&0x0f];
        return new String(hexs);
    }

    void printchars(CharBuffer buf) {
        int  i;
        char[] chars = new char[buf.limit()];
        //save the current position
        int pos=buf.position();
        buf.position(0);
        buf.get(chars);
        //reset to old position
        buf.position(pos);
        for (i=0; i<chars.length; i++) {
            System.out.print(hex4(chars[i])+" ");
        }
        System.out.println();
        //errln("");
    }
    void printchars(String str){
        char[] chars = str.toCharArray();
        for (int i=0; i<chars.length; i++) {
            System.out.print(hex4(chars[i])+" ");
        }
        System.out.println();
    }
    void printbytes(ByteBuffer buf) {
        int  i;
        byte[] bytes = new byte[buf.limit()];
        //save the current position
        int pos=buf.position();
        buf.position(0);
        buf.get(bytes);
        //reset to old position
        buf.position(pos);
        for (i=0; i<bytes.length; i++) {
            System.out.print(hex2(bytes[i])+" ");
        }
        System.out.println();
        
       // errln("");
    }
    
    public static boolean equals(CharBuffer buf,String str){
        return equals(buf,str.toCharArray());
     }

     public static boolean equals(CharBuffer buf, char[] compareTo){
        char[] chars = new char[buf.limit()];
        //save the current position
        int pos=buf.position();
        buf.position(0);
        buf.get(chars);
        //reset to old position
        buf.position(pos);
        return equals(chars,compareTo);
     }
     
     public static boolean equals(char[] chars, char[] compareTo){
        if(chars.length!=compareTo.length){
            System.out.println("Length does not match chars: " + chars.length + " compareTo: " +compareTo.length); 
            return false;
        }else{
            boolean result= true;
            for(int i=0; i<chars.length; i++){
                if(chars[i]!=compareTo[i]){
                    System.out.println("Got: " +hex4(chars[i]) + " Expected: " + hex4(compareTo[i]) +" At: " +i);
                    result= false;
                }
            }
            return result;
        }
     }
     
    public static boolean equals(ByteBuffer buf, byte[] compareTo){
        byte[] chars = new byte[buf.limit()];
        //save the current position
        int pos=buf.position();
        buf.position(0);
        buf.get(chars);
        //reset to old position
        buf.position(pos);
        return equals(chars, compareTo);
     }
     
     public static boolean equals(byte[] chars, byte[] compareTo){
        if(chars.length!=compareTo.length){
            System.out.println("Length does not match chars: " + chars.length + " compareTo: " +compareTo.length); 
            return false;
        }else{
            boolean result= true;
            for(int i=0; i<chars.length; i++){
                if(chars[i]!=compareTo[i]){
                    System.out.println("Got: " +hex4(chars[i]) + " Expected: " + hex4(compareTo[i])+" At: " +i);
                    result= false;
                }
            }
            return result;
        }
     }
     public static boolean equals(ByteBuffer buf, char[] compareTo){
        return equals(buf,getByteArray(compareTo));
     }
     
   static    char[] uSource= {
                    '\u22A6','\u22A7','\u22A8','\u22A9','\u22AA',
                    '\u22AB','\u22AC','\u22AD','\u22AE','\u22AF',
                    '\u22B0','\u22B1','\u22B2','\u22B3','\u22B4',
                    '\u22B5','\u22B6','\u22B7','\u22B8','\u22B9',
                    '\u22BA','\u22BB','\u22BC','\u22BD','\u22BE'
                       };
    static char[] gbSource = {
                             0x81,0x36,0xDE,0x36 
                            ,0x81,0x36,0xDE,0x37 
                            ,0x81,0x36,0xDE,0x38 
                            ,0x81,0x36,0xDE,0x39 
                            ,0x81,0x36,0xDF,0x30 
                            ,0x81,0x36,0xDF,0x31 
                            ,0x81,0x36,0xDF,0x32 
                            ,0x81,0x36,0xDF,0x33 
                            ,0x81,0x36,0xDF,0x34 
                            ,0x81,0x36,0xDF,0x35 
                            ,0x81,0x36,0xDF,0x36 
                            ,0x81,0x36,0xDF,0x37 
                            ,0x81,0x36,0xDF,0x38 
                            ,0x81,0x36,0xDF,0x39 
                            ,0x81,0x36,0xE0,0x30 
                            ,0x81,0x36,0xE0,0x31 
                            ,0x81,0x36,0xE0,0x32 
                            ,0x81,0x36,0xE0,0x33 
                            ,0x81,0x36,0xE0,0x34 
                            ,0x81,0x36,0xE0,0x35 
                            ,0x81,0x36,0xE0,0x36 
                            ,0x81,0x36,0xE0,0x37 
                            ,0x81,0x36,0xE0,0x38 
                            ,0x81,0x36,0xE0,0x39 
                            ,0x81,0x36,0xE1,0x30 
                               
                            };
      

    static char[] myUSource = {
                            '\u32d9','\u32da','\u32db',
                            '\u32dc','\u32dd','\u32de','\u32df', 
                            '\u32e0','\u32e1','\u32e2','\u32e3',
                            '\u32e4','\u32e5','\u32e6','\u32e7',
                            '\u32e8','\u32e9','\u32ea','\u32eb',
                            '\u32ec','\u32ed','\u32ee'
                          };
    static char[] myGBSource = {
                                0x81,0x39,0xd2,0x35,
                                0x81,0x39,0xd2,0x36,
                                0x81,0x39,0xd2,0x37,
                                0x81,0x39,0xd2,0x38,
                                0x81,0x39,0xd2,0x39,
                                0x81,0x39,0xd3,0x30,
                                0x81,0x39,0xd3,0x31,
                                0x81,0x39,0xd3,0x32,
                                0x81,0x39,0xd3,0x33,
                                0x81,0x39,0xd3,0x34,
                                0x81,0x39,0xd3,0x35,
                                0x81,0x39,0xd3,0x36,
                                0x81,0x39,0xd3,0x37,
                                0x81,0x39,0xd3,0x38,
                                0x81,0x39,0xd3,0x39,
                                0x81,0x39,0xd4,0x30,
                                0x81,0x39,0xd4,0x31,
                                0x81,0x39,0xd4,0x32,
                                0x81,0x39,0xd4,0x33,
                                0x81,0x39,0xd4,0x34,
                                0x81,0x39,0xd4,0x35,
                                0x81,0x39,0xd4,0x36,
                                
                            };
       public void TestCallback(/*String encoding*/){
        
        {
            byte[] gbSource = {
                        (byte)0x81,(byte)0x36,(byte)0xDE,(byte)0x36 
                        ,(byte)0x81,(byte)0x36,(byte)0xDE,(byte)0x37 
                        ,(byte)0x81,(byte)0x36,(byte)0xDE,(byte)0x38 
                        ,(byte)0xe3,(byte)0x32,(byte)0x9a,(byte)0x36
            };


            char[] subChars = {'P','I'};

            decoder.reset();   
            
            decoder.replaceWith(new String(subChars));
            ByteBuffer mySource = ByteBuffer.wrap(gbSource);
            CharBuffer myTarget = CharBuffer.allocate(5);
            
            decoder.decode(mySource,  myTarget,true);
//                gbConv.convert(mySource, 0, mySource.length, myTarget, 0, myTarget.length);
            char[] expectedResult = {'\u22A6','\u22A7','\u22A8','\u0050','\u0049',};

            if(!equals(myTarget,new String(expectedResult))){
                System.out.println("Test callback GB18030 to Unicode --failed");
            //}else{
                //errln("Test callback GB18030 to Unicode : FAILED");
            }   
        }
     }
     
     private static boolean isFirstSurrogate(char c){
        return (boolean)(((c)&0xfffffc00)==0xd800);
     }
     private static boolean isSecondSurrogate(char c){
        return (boolean)(((c)&0xfffffc00)==0xdc00);
     }
     public void TestCanConvert(/*String encoding*/){
            char[] mySource= {
                    '\ud800','\udc00',/*surrogate pair */
                    '\u22A6','\u22A7','\u22A8','\u22A9','\u22AA',
                    '\u22AB','\u22AC','\u22AD','\u22AE','\u22AF',
                    '\u22B0','\u22B1','\u22B2','\u22B3','\u22B4',
                    '\ud800','\udc00', /*surrogate pair */
                    '\u22B5','\u22B6','\u22B7','\u22B8','\u22B9',
                    '\u22BA','\u22BB','\u22BC','\u22BD','\u22BE'
                    
                    };
            encoder.reset();
            if(!encoder.canEncode(new String(mySource))){
                System.out.println("Test canConvert()"+encoding+" --failed");
            //}else{
                //errln("Test canConvert()"+encoding+": FAILED");
            }

 
     }
     private void smBufDecode(CharsetDecoder decoder,String encoding){
        
         ByteBuffer mygbSource = ByteBuffer.wrap(getByteArray(myGBSource));
         //System.out.println("In smBufDecode");
         {
           // try{
                //ByteToCharConverter gbConv = ByteToCharConverter.getConverter("gb18030");
                decoder.reset();
                CharBuffer myCharTarget = CharBuffer.allocate(myUSource.length);
                mygbSource.position(0);                
                while(true){
                   int pos = mygbSource.position();
                   mygbSource.limit(++pos);  
                   CoderResult result=decoder.decode(mygbSource,myCharTarget,false);
                   if(result.isError()){
                        System.out.println("Test small output buffers while decoding -- FAILED");
                   }
                   if(mygbSource.position()==myGBSource.length){
                        break;
                   }
                   //System.out.println(pos);
                }
                                    
                if(!equals(myCharTarget,myUSource)){
                    System.out.println("Test small input buffers while decoding "+encoding+" TO Unicode--failed");

                    //errln("--Test small output buffers "+encoding+" TO Unicode --FAILED");
                } else  {
                //    System.out.println("Decode small output buffers passed");
                }             
         }
         {
                decoder.reset();
                CharBuffer myCharTarget = CharBuffer.allocate(myUSource.length);
                myCharTarget.position(0);    
                mygbSource.rewind();
                while(true){
                   int pos =myCharTarget.position();
                   myCharTarget.limit(++pos);  
                   CoderResult result= decoder.decode(mygbSource,myCharTarget,false);
                   if(result.isError()){
                     //   System.out.println("Test small output buffers while decoding -- FAILED");
                   }
                   if(myCharTarget.position()==myUSource.length){
                        break;
                   }
                }
                                    
                if(!equals(myCharTarget,myUSource)){
                    System.out.println("Test small output buffers while decoding "+encoding+" TO Unicode--failed");
                  //  errln("--Test small output buffers "+encoding+" TO Unicode --FAILED");
                }else{
                  //  System.out.println("Decode small input buffers passed");
                }
         }
      }
      private  void smBufEncode(CharsetEncoder encoder,String encoding){
         CharBuffer mySource = CharBuffer.wrap(myUSource); 
         {
                encoder.reset();
                ByteBuffer myTarget = ByteBuffer.allocate(myGBSource.length);
                mySource.position(0);  
                CoderResult result=null;
                while(mySource.position()<myUSource.length){
                   int pos = mySource.position();
                   mySource.limit(pos+1);                     
                   result=encoder.encode(mySource,myTarget,false);
                }                     
                if(!equals(myTarget,myGBSource)){
                    System.out.println("Test small output buffers encode "+encoding+" TO Unicode--failed");

                    //errln("--Test small output buffers "+encoding+" From Unicode --FAILED");
                }else  {
                   //System.out.println("Encode small output buffers passed");
                }
         }
         {
                encoder.reset();
                ByteBuffer myTarget = ByteBuffer.allocate(myGBSource.length);
                myTarget.position(0);     
                mySource.rewind();
                while(true){
                   int pos = myTarget.position();
                   myTarget.limit(pos+1);  
                   //System.out.println(pos + " limit : "+myTarget.limit()+ " sourceLen " + myGBSource.length);
                   CoderResult result = encoder.encode(mySource,myTarget,true);
                   if(result.isOverflow()){
                        continue;
                   }
                   if(mySource.position()==myUSource.length){
                        encoder.flush(myTarget);
                        break;
                   }
                   System.out.println("small out buf " + pos);
                }
                                    
                if(!equals(myTarget,myGBSource)){
                    System.out.println("Test small output buffers ecode "+encoding+" TO Unicode--failed");
                  //  errln("--Test small output buffers "+encoding+" From Unicode --FAILED");
                }else{
                   // System.out.println("Encode small input buffers passed");
                }
                    
         }
      }
      public void TestConvertAll(/*String encoding*/){
        {
            try{
                decoder.reset();
                ByteBuffer mySource = ByteBuffer.wrap(getByteArray(gbSource));
                CharBuffer myTarget = decoder.decode(mySource);
                if(!equals(myTarget,uSource)){
                    System.out.println("Test convertAll() "+encoding+" to Unicode --failed");
                //}else{
                    //errln("--Test convertAll() "+encoding+" to Unicode  --FAILED");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        {
            try{
                encoder.reset();
                CharBuffer mySource = CharBuffer.wrap(uSource);
                ByteBuffer myTarget = encoder.encode(mySource);
                if(!equals(myTarget,gbSource)){
                    System.out.println("Test convertAll() "+encoding+" from Unicode --failed");
                //}else{
                    //errln("--Test convertAll() "+encoding+" to Unicode  --FAILED");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    
      }
    public void TestString(){
        try{
            {
                String source = new String(uSource);
                byte[] target = source.getBytes(encoding);
                if(!equals(target,getByteArray(gbSource))){
                    System.out.println("encode using string API failed");
                }
            }
            {
                String target = new String(getByteArray(gbSource),encoding);
                if(!equals(uSource,target.toCharArray())){
                    System.out.println("decode using string API failed");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
        
    public  void TestFromUnicode(/*String encoding*/){
        ByteBuffer myTarget = ByteBuffer.allocate(gbSource.length);
        CharBuffer mySource = CharBuffer.wrap(uSource);
        encoder.reset();
        encoder.encode(mySource,myTarget,true);
        if(!equals(myTarget,gbSource)){
            //errln("Test Unicode to "+encoding +": FAILED");
            System.out.println("Test Unicode to "+encoding +": FAILED");
        }
        smBufEncode(encoder,encoding);
      }
      
      public  void TestToUnicode(/*String encoding*/){
        CharBuffer myTarget = CharBuffer.allocate(uSource.length);
        ByteBuffer mySource = ByteBuffer.wrap(getByteArray(gbSource));
        decoder.reset();
        CoderResult result= decoder.decode(mySource,myTarget,true);
        if(result.isError()){
            System.out.println("Test ToUnicode -- FAILED");
        }
        if(!equals(myTarget,uSource)){
           // errln("--Test "+encoding+" to Unicode :FAILED");
            System.out.println("Test "+encoding+" to Unicode :FAILED");
        }
        smBufDecode(decoder,encoding);
      }     
      
      public static byte[] getByteArray(char[] source){
         byte[] target = new byte[source.length];
         int i = source.length;
         for(;--i>=0;){
            target[i]=(byte)source[i];
         }
         return target;
     }
     private void smBufCharset(Charset charset){
        ByteBuffer gbTarget = charset.encode(CharBuffer.wrap(uSource));      
        CharBuffer uTarget = charset.decode(ByteBuffer.wrap(getByteArray(gbSource)));
        
        if(!equals(uTarget,uSource)){
            System.out.println("Test "+charset.toString()+" to Unicode :FAILED");
        }
        if(!equals(gbTarget,gbSource)){
            System.out.println("Test "+charset.toString()+" from Unicode :FAILED");
        }
        System.out.println("Called smBufCharset");
     }
     
     public void TestMultithreaded(){
        final Charset cs = Charset.forName(encoding);
        if(cs == charset){
            System.out.println("The objects are equal");
        }
        smBufCharset(cs);
        
        final Thread t1 = new Thread(){
                public void run(){
                   // synchronized(charset){
                        while(!interrupted()){
                            try{
                                smBufCharset(cs);
                            }
                            catch(UnsupportedCharsetException ueEx){
                                System.out.println(ueEx.toString());
                            }
                        }
                                    
                   // }
                }
           };
          final Thread t2 = new Thread(){
                public void run(){
                   // synchronized(charset){
                        while(!interrupted()){
                            try{
                                smBufCharset(cs);
                            }
                            catch(UnsupportedCharsetException ueEx){
                                System.out.println(ueEx.toString());
                            }
                        }
                                    
                    //}
                }
           };
        t1.start();
        t2.start();
        int i=0;
        for(;;){
            if(i>1000000000){
                try{
                    t1.interrupt();
                }catch(Exception e){
                }
                try{
                    t2.interrupt();
                }catch(Exception e){
                }
                System.out.println("--Threads Interrupted");
                break;
            }
            i++;   
        } 
     }
     
     public void TestSynchronizedMultithreaded(){
        final Charset charset = Charset.forName(encoding);
             final Thread t1 = new Thread(){
                public void run(){
                    synchronized(charset){
                        while(!interrupted()){
                            try{
                                final CharsetDecoder decoder1 =  charset.newDecoder();
                                final CharsetEncoder encoder1 =  charset.newEncoder();
                                smBufEncode(encoder,encoding);
                                smBufDecode(decoder,encoding);
                            }
                            catch(UnsupportedCharsetException ueEx){
                                System.out.println(ueEx.toString());
                            }
                        }
                                    
                    }
                }
           };
          final Thread t2 = new Thread(){
                public void run(){
                    synchronized(charset){
                        while(!interrupted()){
                            try{
                                final CharsetDecoder decoder1 =  charset.newDecoder();
                                final CharsetEncoder encoder1 =  charset.newEncoder();
                                smBufEncode(encoder,encoding);
                                smBufDecode(decoder,encoding);
                            }
                            catch(UnsupportedCharsetException ueEx){
                                System.out.println(ueEx.toString());
                            }
                        }
                                    
                    }
                }
           };
        t1.start();
        t2.start();
        int i=0;
        for(;;){
            if(i>1000000000){
                try{
                    t1.interrupt();
                }catch(Exception e){
                }
                try{
                    t2.interrupt();
                }catch(Exception e){
                }
                System.out.println("--TestMultithreaded --PASSED");
                break;
            }
            i++;   
        }  
        
     }

}
