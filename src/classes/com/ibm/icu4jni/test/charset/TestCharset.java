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
    public static void main(String[] args) throws Exception{
       new TestCharset().run(args);
    }   
    
    TestCharset(){
        charset = Charset.forName(encoding);
        decoder = (CharsetDecoder) charset.newDecoder();
        encoder = (CharsetEncoder) charset.newEncoder();
   }
   public void TestAPISemantics(/*String encoding*/) throws Exception{
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
                    errln("ToChars encountered Error");
                    rc=1;
                }

                if (!equals(chars,unistr)) {
                    errln("ToChars does not match");
                    printchars(chars);
                    errln("Expected : ");
                    printchars(unistr);
                    rc=2;
                }
                    
            } catch (Exception e) {
                errln("ToChars - exception in buffer");
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
                    CoderResult result = decoder.decode(b,chars,false);
                }
                if (unistr.length()!=( chars.limit())) {
                    errln("ToChars single len does not match" );
                    rc=3;
                }
                if (!equals(chars,unistr)) {
                    errln("ToChars single does not match");
                    printchars(chars);
                    rc=4;
                }
            } catch (Exception e) {
                errln("ToChars - exception in single");
                e.printStackTrace(System.err);
                rc = 6;
            }

            /* Convert the buffer one at a time to Unicode */
            try {
                CharBuffer chars = CharBuffer.allocate(unistr.length());
                len  = 0;
                decoder.reset();
                for (i=0; i<=gb.length; i++) {
                    gbval.limit(i);
                    CoderResult result=decoder.decode(gbval,chars,false);
                    if(result.isError()){
                        errln("Error while decoding -- FAILED");
                   }
                }
                if (chars.limit()!=unistr.length()) {
                    errln("ToChars Simple buffer len does not match");
                    rc=7;
                }
                if (!equals(chars,unistr)) {
                    errln("ToChars Simple buffer does not match");
                    printchars(chars);
                    err(" Expected : ");
                    printchars(unistr);
                    rc=8;
                }
            } catch (Exception e) {
                errln("ToChars - exception in single buffer");
                e.printStackTrace(System.err);
                rc = 9;
            }
            if (rc!=0) {
               errln("Test Simple ToChars for encoding : FAILED");
            }


            rc = 0;
            len = 0;
            /* Convert the whole buffer from unicode */
            try {
                ByteBuffer bytes = ByteBuffer.allocate(gb.length);
                encoder.reset();
                CoderResult result = encoder.encode(uniVal,bytes,false);
                if (result.isError()) {
                    errln("FromChars reported error: "+ result.toString());
                    rc=1;
                }
                if (!bytes.equals(gbval)) {
                    errln("FromChars does not match");
                    printbytes(bytes);
                    rc=2;
                }
            } catch (Exception e) {
                errln("FromChars - exception in buffer");
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
                    errln("FromChars single len does not match" );
                    rc=3;
                }
                if (!bytes.equals(gbval)) {
                    errln("FromChars single does not match");
                    printbytes(bytes);
                    rc=4;
                }
                    
            } catch (Exception e) {
                errln("FromChars - exception in single");
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
                    errln("FromChars Simple len does not match" );
                    rc=7;
                }
                if (!bytes.equals(gbval)) {
                    errln("FromChars Simple does not match");
                    printbytes(bytes);
                    rc=8;
                }
            } catch (Exception e) {
                errln("FromChars - exception in single buffer");
                e.printStackTrace(System.err);
                rc = 9;
            }
            if (rc!=0) {
               errln("--Test Simple FromChars "+encoding+" --FAILED");
            }
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
            err(hex(chars[i])+" ");
        }
        errln("");
    }
    void printchars(String str){
        char[] chars = str.toCharArray();
        for (int i=0; i<chars.length; i++) {
            err(hex(chars[i])+" ");
        }
        errln("");
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
            System.out.print(hex((char)bytes[i])+" ");
        }       
       errln("");
    }
    
    public  boolean equals(CharBuffer buf,String str){
        return equals(buf,str.toCharArray());
     }

     public  boolean equals(CharBuffer buf, char[] compareTo){
        char[] chars = new char[buf.limit()];
        //save the current position
        int pos=buf.position();
        buf.position(0);
        buf.get(chars);
        //reset to old position
        buf.position(pos);
        return equals(chars,compareTo);
     }
     
     public  boolean equals(char[] chars, char[] compareTo){
        if(chars.length!=compareTo.length){
            errln("Length does not match chars: " + chars.length + " compareTo: " +compareTo.length); 
            return false;
        }else{
            boolean result= true;
            for(int i=0; i<chars.length; i++){
                if(chars[i]!=compareTo[i]){
                    errln("Got: " +hex(chars[i]) + " Expected: " + hex(compareTo[i]) +" At: " +i);
                    result= false;
                }
            }
            return result;
        }
     }
     
    public  boolean equals(ByteBuffer buf, byte[] compareTo){
        byte[] chars = new byte[buf.limit()];
        //save the current position
        int pos=buf.position();
        buf.position(0);
        buf.get(chars);
        //reset to old position
        buf.position(pos);
        return equals(chars, compareTo);
     }
     
     public  boolean equals(byte[] chars, byte[] compareTo){
        if(chars.length!=compareTo.length){
            errln("Length does not match chars: " + chars.length + " compareTo: " +compareTo.length); 
            return false;
        }else{
            boolean result= true;
            for(int i=0; i<chars.length; i++){
                if(chars[i]!=compareTo[i]){
                    errln("Got: " +hex((char)chars[i]) + " Expected: " + hex((char)compareTo[i])+" At: " +i);
                    result= false;
                }
            }
            return result;
        }
     }
     public  boolean equals(ByteBuffer buf, char[] compareTo){
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
       public void TestCallback(/*String encoding*/)throws Exception{
        
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
            char[] expectedResult = {'\u22A6','\u22A7','\u22A8','\u0050','\u0049',};

            if(!equals(myTarget,new String(expectedResult))){
                errln("Test callback GB18030 to Unicode : FAILED");
            }   
        }
     }
     
     private static boolean isFirstSurrogate(char c){
        return (boolean)(((c)&0xfffffc00)==0xd800);
     }
     private static boolean isSecondSurrogate(char c){
        return (boolean)(((c)&0xfffffc00)==0xdc00);
     }
     public void TestCanConvert(/*String encoding*/)throws Exception{
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
                errln("Test canConvert()"+encoding+": FAILED");
            }

 
     }
     private void smBufDecode(CharsetDecoder decoder,String encoding){
        
         ByteBuffer mygbSource = ByteBuffer.wrap(getByteArray(myGBSource));
         {
                decoder.reset();
                CharBuffer myCharTarget = CharBuffer.allocate(myUSource.length);
                mygbSource.position(0);                
                while(true){
                   int pos = mygbSource.position();
                   mygbSource.limit(++pos);  
                   CoderResult result=decoder.decode(mygbSource,myCharTarget,false);
                   if(result.isError()){
                        errln("Test small output buffers while decoding -- FAILED");
                   }
                   if(mygbSource.position()==myGBSource.length){
                        break;
                   }

                }
                                    
                if(!equals(myCharTarget,myUSource)){
                    errln("Test small input buffers while decoding "+encoding+" TO Unicode--failed");
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
                       errln("Test small output buffers while decoding -- FAILED");
                   }
                   if(myCharTarget.position()==myUSource.length){
                        break;
                   }
                }
                                    
                if(!equals(myCharTarget,myUSource)){
                    errln("--Test small output buffers "+encoding+" TO Unicode --FAILED");
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
                    errln("--Test small output buffers "+encoding+" From Unicode --FAILED");
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
                   CoderResult result = encoder.encode(mySource,myTarget,true);
                   if(result.isOverflow()){
                        continue;
                   }
                   if(mySource.position()==myUSource.length){
                        encoder.flush(myTarget);
                        break;
                   }
                   errln("small out buf " + pos);
                }
                                    
                if(!equals(myTarget,myGBSource)){
                    errln("--Test small output buffers "+encoding+" From Unicode --FAILED");
                }
                    
         }
      }
      public void TestConvertAll(/*String encoding*/)throws Exception{
        {
            try{
                decoder.reset();
                ByteBuffer mySource = ByteBuffer.wrap(getByteArray(gbSource));
                CharBuffer myTarget = decoder.decode(mySource);
                if(!equals(myTarget,uSource)){
                    errln("--Test convertAll() "+encoding+" to Unicode  --FAILED");
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
                    errln("--Test convertAll() "+encoding+" to Unicode  --FAILED");
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
                    errln("encode using string API failed");
                }
            }
            {
                String target = new String(getByteArray(gbSource),encoding);
                if(!equals(uSource,target.toCharArray())){
                    errln("decode using string API failed");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
        
    public  void TestFromUnicode(/*String encoding*/)throws Exception{
        ByteBuffer myTarget = ByteBuffer.allocate(gbSource.length);
        CharBuffer mySource = CharBuffer.wrap(uSource);
        encoder.reset();
        encoder.encode(mySource,myTarget,true);
        if(!equals(myTarget,gbSource)){
            errln("--Test Unicode to "+encoding +": FAILED");
        }
        smBufEncode(encoder,encoding);
      }
      
      public  void TestToUnicode(/*String encoding*/)throws Exception{
        CharBuffer myTarget = CharBuffer.allocate(uSource.length);
        ByteBuffer mySource = ByteBuffer.wrap(getByteArray(gbSource));
        decoder.reset();
        CoderResult result= decoder.decode(mySource,myTarget,true);
        if(result.isError()){
            errln("Test ToUnicode -- FAILED");
        }
        if(!equals(myTarget,uSource)){
            errln("--Test "+encoding+" to Unicode :FAILED");
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
            errln("Test "+charset.toString()+" to Unicode :FAILED");
        }
        if(!equals(gbTarget,gbSource)){
            errln("Test "+charset.toString()+" from Unicode :FAILED");
        }
     }
     
     public void TestMultithreaded()throws Exception{
        final Charset cs = Charset.forName(encoding);
        if(cs == charset){
            errln("The objects are equal");
        }
        smBufCharset(cs);
        try{
            final Thread t1 = new Thread(){
                    public void run(){
                    // commented out since the mehtods on
                    // Charset API are supposed to be thread
                    // safe ... to test it we dont sync
                    
                    // synchronized(charset){
                            while(!interrupted()){
                                try{
                                    smBufCharset(cs);
                                }
                                catch(UnsupportedCharsetException ueEx){
                                    errln(ueEx.toString());
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
                                    errln(ueEx.toString());
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
                    break;
                }
                i++;   
            } 
        }catch(Exception e){
            throw e;
        }
     }
     
     public void TestSynchronizedMultithreaded()throws Exception{
        // Methods on CharsetDecoder and CharsetEncoder classes
        // are inherently unsafe if accessed by multiple concurrent
        // thread so we synchronize them
        final Charset charset = Charset.forName(encoding);
        final CharsetDecoder decoder =  charset.newDecoder();
        final CharsetEncoder encoder =  charset.newEncoder();
        try{
            final Thread t1 = new Thread(){
                    public void run(){
                        while(!interrupted()){
                            try{
                                synchronized(encoder){
                                    smBufEncode(encoder,encoding);
                                }
                                synchronized(decoder){
                                    smBufDecode(decoder,encoding);
                                }
                            }
                            catch(UnsupportedCharsetException ueEx){
                                errln(ueEx.toString());
                            }
                        }
                        
                    }
            };
            final Thread t2 = new Thread(){
                    public void run(){
                        while(!interrupted()){
                            try{
                                synchronized(encoder){
                                    smBufEncode(encoder,encoding);
                                }
                                synchronized(decoder){
                                    smBufDecode(decoder,encoding);
                                }
                            }
                            catch(UnsupportedCharsetException ueEx){
                                errln(ueEx.toString());
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
                    break;
                }
                i++;   
            }  
        }catch(Exception e){
            throw e;
        }
        
     }

}
