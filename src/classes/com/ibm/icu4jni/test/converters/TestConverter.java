/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and	  *
* others. All Rights Reserved.												  *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/test/converters/TestConverter.java,v $ 
* $Date: 2001/10/27 00:34:55 $ 
* $Revision: 1.2 $
*
*******************************************************************************
*/ 

package com.ibm.icu4jni.test.converters;

import java.io.*;
import java.util.*;
import sun.io.*;
import com.ibm.icu4jni.converters.*;
import com.ibm.icu4jni.test.*;

public class TestConverter extends TestFmwk{
	
    static byte [] bytes;
    static char [] chars;
    static String  encoding= "gb18030";
    static boolean useICU = false;
    static ByteToCharConverter convto;
    static CharToByteConverter convfrom;
    static String uniVal = "abcd\u8000\u8001\u00a5\u3000\r\n";
    static byte [] gbval = {
        (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)0xd2, (byte)0xab, (byte)0xc0, (byte)0xcf,
        (byte)0x81, (byte)0x30, (byte)0x84, (byte)0x36, (byte)0xa1, (byte)0xa1, (byte)0x0d, (byte)0x0a
    };
    
    public static void main(String[] args) throws Exception {
        new TestConverter().run(args);
    }

    public void TestAPISemantics(/*String encoding*/){
            int   i, len;
            int   rc;
            try {

                    convto   = (ByteToCharConverter) new ByteToCharConverterICU(encoding);
                    convfrom = (CharToByteConverter) new CharToByteConverterICU(encoding);

            } catch (Exception e) {
                e.printStackTrace(System.err);
                return;
            }

            rc = 0;
            len = 0;

            /* Convert the whole buffer to Unicode */
            try {
                chars = new char[uniVal.length()];
                len = convto.convert(gbval, 0, gbval.length, chars, 0, chars.length);
                if (len != convto.nextCharIndex()) {
                    System.out.println("ToChars len does not match" +
                                " len=" + len + "  CharOff="+convto.nextCharIndex());
                    rc=1;
                }
                if (!(new String(chars).equals(uniVal))) {
                    System.out.println("ToChars does not match");
                    printchars(chars, len);
                    rc=2;
                }
            } catch (Exception e) {
                System.out.println("ToChars - exception in buffer");
                e.printStackTrace(System.err);
                rc = 5;
            }

            /* Convert single bytes to Unicode */
            try {
                chars = new char[uniVal.length()];
                byte [] b = new byte[1];
                len  = 0;
                convto.reset();
                for (i=0; i<gbval.length; i++) {
                    b[0] = gbval[i];
                    len += convto.convert(b, 0, 1, chars, len, chars.length);
                }
                if (len != convto.nextCharIndex()) {
                    System.out.println("ToChars single len does not match" +
                                " len=" + len + "  CharOff="+convto.nextCharIndex());
                    rc=3;
                }
                if (!(new String(chars).equals(uniVal))) {
                    System.out.println("ToChars single does not match");
                    printchars(chars, len);
                    rc=4;
                }
            } catch (Exception e) {
                System.out.println("ToChars - exception in single");
                e.printStackTrace(System.err);
                rc = 6;
            }

            /* Convert the buffer one at a time to Unicode */
            try {
                chars = new char[uniVal.length()];
                len  = 0;
                convto.reset();
                for (i=0; i<gbval.length; i++) {
                    len += convto.convert(gbval, i, i+1, chars, len, chars.length);
                }
                if (len != convto.nextCharIndex()) {
                    System.out.println("ToChars single buffer len does not match" +
                                " len=" + len + "  CharOff="+convto.nextCharIndex());
                    rc=7;
                }
                if (!(new String(chars).equals(uniVal))) {
                    System.out.println("ToChars single buffer does not match");
                    printchars(chars, len);
                    rc=8;
                }
            } catch (Exception e) {
                System.out.println("ToChars - exception in single buffer");
                e.printStackTrace(System.err);
                rc = 9;
            }
            if (rc!=0) {
               // System.out.println("--Test Simple ToChars"+encoding+" --PASSED");
               // printchars(chars, len);
                errln("Test Simple ToChars for encoding " +encoding+" : FAILED");
            }


            rc = 0;
            len = 0;
            chars = uniVal.toCharArray();
            /* Convert the whole buffer from unicode */
            try {
                bytes = new byte[gbval.length];
                len = convfrom.convert(chars, 0, chars.length, bytes, 0, bytes.length);
                if (len != convfrom.nextByteIndex()) {
                    System.out.println("FromChars len does not match" +
                                " len=" + len + "  ByteOff="+convfrom.nextByteIndex());
                    rc=1;
                }
                if (!equals(bytes, gbval)) {
                    System.out.println("FromChars does not match");
                    printbytes(bytes, len);
                    rc=2;
                }
            } catch (Exception e) {
                System.out.println("FromChars - exception in buffer");
                e.printStackTrace(System.err);
                rc = 5;
            }

            /* Convert the buffer one char at a time to unicode */
            try {
                bytes = new byte[gbval.length];
                char [] c = new char[1];
                len  = 0;
                convfrom.reset();
                for (i=0; i<chars.length; i++) {
                    c[0] = chars[i];
                    len += convfrom.convert(c, 0, 1, bytes, len, bytes.length);
                }
                if (len != convfrom.nextByteIndex()) {
                    System.out.println("FromChars single len does not match" +
                                " len=" + len + "  ByteOff="+convfrom.nextByteIndex());
                    rc=3;
                }
                if (!(new String(chars).equals(uniVal))) {
                    System.out.println("FromChars single does not match");
                    printbytes(bytes, len);
                    rc=4;
                }
            } catch (Exception e) {
                System.out.println("FromChars - exception in single");
                e.printStackTrace(System.err);
                rc = 6;
            }

            /* Convert one char at a time to unicode */
            try {
                bytes = new byte[gbval.length];
                len  = 0;
                convfrom.reset();
                for (i=0; i<chars.length; i++) {
                    len += convfrom.convert(chars, i, i+1, bytes, len, bytes.length);
                }
                if (len != convfrom.nextByteIndex()) {
                    System.out.println("FromChars single len does not match" +
                                " len=" + len + "  ByteOff="+convfrom.nextByteIndex());
                    rc=7;
                }
                if (!(new String(chars).equals(uniVal))) {
                    System.out.println("FromChars single does not match");
                    printbytes(bytes, len);
                    rc=8;
                }
            } catch (Exception e) {
                errln("FromChars - exception in single buffer");
                e.printStackTrace(System.err);
                rc = 9;
            }
            if (rc==0) {
                //errln("--Test Simple FromChars "+encoding+" --PASSED");
                //printbytes(bytes, len);
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

    void printchars(char [] chars, int len) {
        int  i;
        for (i=0; i<len; i++) {
            System.out.print(hex4(chars[i])+" ");
        }
        errln("");
    }

    void printbytes(byte [] bytes, int len) {
        int  i;
        for (i=0; i<len; i++) {
            System.out.print(hex2(bytes[i])+" ");
        }
        errln("");
    }

    /*
     * Check for equality of a byte array
     */
    public static boolean equals(byte [] b1, byte [] b2) {
        if (b1.length != b2.length)
            return false;
        for (int i=0; i<b1.length; i++) {
            if (b1[i] != b2[i])
                return false;
        }
        return true;
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
                                0x81,0x39,0xd4,0x37
                            };

      
 
    public void TestCallback(/*String encoding*/){
        
        {
            char[] gbSource = {
                        0x81,0x36,0xDE,0x36 
                        ,0x81,0x36,0xDE,0x37 
                        ,0x81,0x36,0xDE,0x38 
                        ,0xe3,0x32,0x9a,0x36
            };
            try{
                char[] myTarget = new char[gbSource.length];
                byte[] mySource = new byte[gbSource.length];
                char[] subChars = {'P','I'};
                int i=0;
                while(i<gbSource.length){
                    mySource[i]=(byte) gbSource[i];
                    i++;
                }
                ByteToCharConverter gbConv = ByteToCharConverterICU.createConverter(encoding);
                gbConv.setSubstitutionChars(subChars);
                gbConv.convert(mySource, 0, mySource.length, myTarget, 0, myTarget.length);
                gbConv.setSubstitutionMode(false);
//                gbConv.convert(mySource, 0, mySource.length, myTarget, 0, myTarget.length);
                i=0;
                char[] expectedResult = {'\u22A6','\u22A7','\u22A8','\u0050','\u0049',};
                boolean passed =true;
                while(myTarget[i]!='\0'){
                    if(myTarget[i]!= expectedResult[i]){
                        StringBuffer temp = new StringBuffer("Error:  Expected: ");
                        temp.append(Integer.toHexString(expectedResult[i]));
                        temp.append(" Got: ");
                        temp.append(Integer.toHexString(myTarget[i]));
                        System.out.println(temp);
                        passed =false;
                    }
                    i++;
                }
                if(!passed){
                    //System.out.println("--Test callback GB18030 to Unicode --PASSED");
                //}else{
                    errln("Test callback GB18030 to Unicode : FAILED");
                }
                
            }
            catch(UnsupportedEncodingException ueEx){
                System.out.println(ueEx.toString());
            }
            catch (MalformedInputException miEx){
                System.out.println(miEx.toString());
            }
            catch (UnknownCharacterException ucEx){
                System.out.println(ucEx.toString());
            }
            catch (ConversionBufferFullException cbfEx){
                System.out.println(cbfEx.toString());
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
          try{
            char[] mySource= {
                    '\ud800','\udc00',/*surrogate pair */
                    '\u22A6','\u22A7','\u22A8','\u22A9','\u22AA',
                    '\u22AB','\u22AC','\u22AD','\u22AE','\u22AF',
                    '\u22B0','\u22B1','\u22B2','\u22B3','\u22B4',
                    '\ud800','\udc00', /*surrogate pair */
                    '\u22B5','\u22B6','\u22B7','\u22B8','\u22B9',
                    '\u22BA','\u22BB','\u22BC','\u22BD','\u22BE'
                    
                       };
            CharToByteConverter myConv = CharToByteConverterICU.createConverter(encoding);
            boolean passed = true;
            int i=0;
            int temp=0;
            while(i<mySource.length){
                if(isFirstSurrogate(mySource[i])&& i+1< mySource.length){
                    if(isSecondSurrogate(mySource[i+1])){
                        temp = (((mySource[i])<<(long)10)+(mySource[i+1])-((0xd800<<(long)10)+0xdc00-0x10000));
                        if(!((CharToByteConverterICU) myConv).canConvert(temp)){
                            passed=false;
                        }
                        i++;
                        i++;
                    }
                }
                 
                if(!myConv.canConvert(mySource[i])){
                    passed=false;
                }
               i++;
            }
            if(!passed){
                //System.out.println("--Test canConvert()"+encoding+"--PASSED");
            //}else{
                errln("Test canConvert()"+encoding+": FAILED");
            }

         }
         catch(UnsupportedEncodingException ueEx){
            System.out.println(ueEx.toString());
         }   
     }
     private void smOutBufToUnicode(ByteToCharConverter  gbConv,String encoding){
        
         byte[] mygbSource = new byte[myGBSource.length];
         int j=0;
         while(j<myGBSource.length){
                mygbSource[j]=(byte) myGBSource[j];
                j++;
         }
         {
           // try{
                //ByteToCharConverter gbConv = ByteToCharConverter.getConverter("gb18030");
                gbConv.reset();
                char[] myCharTarget = new char[myUSource.length+1];
                int inStart = 0;
                int inStop = myGBSource.length;
                int outStart = 0;
                int outStop = 0;
                int i=1;
                while(true){
                    try{
                        outStop= (outStop+i>myCharTarget.length)? myCharTarget.length : (outStop+i) ;
                        inStart = gbConv.nextByteIndex();
                        outStart = gbConv.nextCharIndex();
                        gbConv.convert(mygbSource, inStart, inStop, myCharTarget, outStart, outStop);
                        if(inStart>=mygbSource.length){
                            break;
                        }
                    }
                    catch(ConversionBufferFullException cbEx){
                        continue;
                    }
                    catch (MalformedInputException miEx){
                        errln(miEx.toString());
                        return;
                    }
                    catch (UnknownCharacterException ucEx){
                        errln(ucEx.toString());
                        return;
                    }
                }
                char[] expectedResult = myUSource;
                boolean passed =true;
                i=0;
                while(i<myUSource.length){
                    if(myCharTarget[i]!= expectedResult[i]){
                        StringBuffer temp = new StringBuffer("Error:  Expected: ");
                        temp.append(Integer.toHexString(expectedResult[i]));
                        temp.append(" Got: ");
                        temp.append(Integer.toHexString(myCharTarget[i]));
                        errln(temp.toString());
                        passed =false;
                    }
                    i++;
                }
                if(!passed){
                    //errln("--Test small output buffers "+encoding+" TO Unicode--PASSED");
                //}else{
                    errln("--Test small output buffers "+encoding+" TO Unicode --FAILED");
                }
              
           /* }
            catch(UnsupportedEncodingException ueEx){
                    System.out.println(ueEx.toString());
            }*/
         }
         {
           // try{
               // ByteToCharConverter gbConv = ByteToCharConverter.getConverter("gb18030");
                gbConv.reset();
                char[] myCharTarget = new char[myUSource.length+1];
                int inStart = 0;
                int inStop = myGBSource.length;
                int outStart = 0;
                int outStop = 0;
                int i=1;
                while(true){
                    try{
                        outStop= (outStop+i>myCharTarget.length)? myCharTarget.length : (outStop+i) ;
                        inStop  = (inStop+i>myGBSource.length)? myGBSource.length : (inStop+i);
                        inStart = gbConv.nextByteIndex();
                        outStart = gbConv.nextCharIndex();
                        gbConv.convert(mygbSource, inStart, inStop, myCharTarget, outStart, outStop);
                        if(inStart>=mygbSource.length){
                            break;
                        }
                    }
                    catch(ConversionBufferFullException cbEx){
                        continue;
                    }
                    catch (MalformedInputException miEx){
                        errln(miEx.toString());
                        return;
                    }
                    catch (UnknownCharacterException ucEx){
                        errln(ucEx.toString());
                        return;
                    }
                }
                char[] expectedResult = myUSource;
                boolean passed =true;
                i=0;
                while(i<myUSource.length){
                    if(myCharTarget[i]!= expectedResult[i]){
                        StringBuffer temp = new StringBuffer("Error:  Expected: ");
                        temp.append(Integer.toHexString(expectedResult[i]));
                        temp.append(" Got: ");
                        temp.append(Integer.toHexString(myCharTarget[i]));
                        errln(temp.toString());
                        passed =false;
                    }
                    i++;
                }
                if(!passed){
                    //System.out.println("--Test small input buffers "+encoding+"TO Unicode--PASSED");
                //}else{
                    errln("--Test small input buffers "+encoding+"TO Unicode --FAILED");
                }
          /*  }
            catch(UnsupportedEncodingException ueEx){
                    System.out.println(ueEx.toString());
            }
            */
         }
      }
      
      public void TestConvertAll(/*String encoding*/){
        {
            try{
                //char[] myTarget = new char[gbSource.length];
                byte[] mySource = new byte[gbSource.length];
                int i=0;
                while(i<gbSource.length){
                    mySource[i]=(byte) gbSource[i];
                    i++;
                }
                ByteToCharConverter gbConv = ByteToCharConverterICU.createConverter(encoding);
                char[] myTarget=gbConv.convertAll(mySource);
                //gbConv.convert(mySource, 0, mySource.length, myTarget, 0, myTarget.length);
                i=0;
                char[] expectedResult = uSource;
                boolean passed =true;
                while( i<expectedResult.length){                   
                    if(myTarget[i]!= expectedResult[i]){
                        StringBuffer temp = new StringBuffer("Error:  Expected: ");
                        temp.append(Integer.toHexString(expectedResult[i]));
                        temp.append(" Got: ");
                        temp.append(Integer.toHexString(myTarget[i]));
                        errln(temp.toString());
                        passed =false;
                    }
                    i++;
                }
                if(!passed){
                    //System.out.println("--Test convertAll() "+encoding+" to Unicode --PASSED");
                //}else{
                    errln("--Test convertAll() "+encoding+" to Unicode  --FAILED");
                }
                
            }
            catch(UnsupportedEncodingException ueEx){
                errln(ueEx.toString());
            }
            catch (MalformedInputException miEx){
                errln(miEx.toString());
            }
        }
        {
             try{
                //byte[] myTarget = new byte[uSource.length * 5]; 
                CharToByteConverter myConv = CharToByteConverterICU.createConverter(encoding);
                //myConv.convert(uSource, 0, uSource.length, myTarget, 0, myTarget.length);
                byte[] myTarget= myConv.convertAll(uSource);
                int i=0;
                char[] expectedResult = gbSource;
                boolean passed =true;
                while(i<myTarget.length){
                    if(myTarget[i]!=(byte)expectedResult[i]){
                        StringBuffer temp = new StringBuffer("Error:  Expected: ");
                        temp.append(Integer.toHexString(expectedResult[i]));
                        temp.append(" Got: ");
                        temp.append(Integer.toHexString(myTarget[i]));
                        errln(temp.toString());
                        passed =false;
                        passed =false;
                    }
                    i++;
                }
                if(!passed){
                    //System.out.println("--Test convertAll() Unicode to "+encoding+" --PASSED");
                //}else{
                  //  System.out.println("--Test convertAll() Unicode to "+encoding+" --FAILED");
                }
                System.gc();
            }
            catch(UnsupportedEncodingException ueEx){
                System.out.println(ueEx.toString());
            }
            catch (MalformedInputException miEx){
                System.out.println(miEx.toString());
            }   
            
        }

      }
      
      public  void TestConvert(){
        try{
            byte[] myTarget = new byte[uSource.length * 5]; 
            CharToByteConverter gbConv = CharToByteConverterICU.createConverter("gb18030");
            gbConv.convert(uSource, 0, uSource.length, myTarget, 0, myTarget.length);
            int i=0;
            char[] expectedResult = gbSource;
            boolean passed =true;
            while(myTarget[i]!='\0'){
                if(myTarget[i]!=(byte)expectedResult[i]){
                    StringBuffer temp = new StringBuffer("Error:  Expected: ");
                    temp.append(Integer.toHexString(expectedResult[i]));
                    temp.append(" Got: ");
                    temp.append(Integer.toHexString(myTarget[i]));
                    errln(temp.toString());
                    passed =false;
                    passed =false;
                }
                i++;
            }
            if(!passed){
                //System.out.println("--Test Unicode to GB18030 --PASSED");
                errln("--Test Unicode to GB18030 --FAILED");
            }
            
        }
        catch(UnsupportedEncodingException ueEx){
            errln(ueEx.toString());
        }
       catch (MalformedInputException miEx){
            errln(miEx.toString());
        }
        catch (UnknownCharacterException ucEx){
            errln(ucEx.toString());
        }
        catch (ConversionBufferFullException cbfEx){
            errln(cbfEx.toString());
        } 

      }    
        
      public  void TestFromUnicode(/*String encoding*/){
        try{
            byte[] myTarget = new byte[uSource.length * 5]; 
            CharToByteConverter myConv = CharToByteConverterICU.createConverter(encoding);
            myConv.convert(uSource, 0, uSource.length, myTarget, 0, myTarget.length);
            
            int i=0;
            char[] expectedResult = gbSource;
            boolean passed =true;
            while(myTarget[i]!='\0'){
                if(myTarget[i]!=(byte)expectedResult[i]){
                    StringBuffer temp = new StringBuffer("Error:  Expected: ");
                    temp.append(Integer.toHexString(expectedResult[i]));
                    temp.append(" Got: ");
                    temp.append(Integer.toHexString(myTarget[i]));
                    errln(temp.toString());
                    passed =false;
                    passed =false;
                }
                i++;
            }
            if(!passed){
                //System.out.println("--Test Unicode to "+ encoding +" --PASSED");
            //}else{
                errln("Test Unicode to "+encoding +": FAILED");
            }
            System.gc();
        }
        catch(UnsupportedEncodingException ueEx){
            errln(ueEx.toString());
        }
        catch (MalformedInputException miEx){
            errln(miEx.toString());
        }
        catch (UnknownCharacterException ucEx){
            errln(ucEx.toString());
        }
        catch (ConversionBufferFullException cbfEx){
            errln(cbfEx.toString());
        }    
        
      }
      public  void TestToUnicode(/*String encoding*/){
         try{
            char[] myTarget = new char[gbSource.length];
            byte[] mySource = new byte[gbSource.length];
            int i=0;
            while(i<gbSource.length){
                mySource[i]=(byte) gbSource[i];
                i++;
            }
            ByteToCharConverter gbConv = ByteToCharConverterICU.createConverter(encoding);
            gbConv.convert(mySource, 0, mySource.length, myTarget, 0, myTarget.length);
            i=0;
            char[] expectedResult = uSource;
            boolean passed =true;
            while(myTarget[i]!='\0'){
                if(myTarget[i]!= expectedResult[i]){
                    StringBuffer temp = new StringBuffer("Error:  Expected: ");
                    temp.append(Integer.toHexString(expectedResult[i]));
                    temp.append(" Got: ");
                    temp.append(Integer.toHexString(myTarget[i]));
                    errln(temp.toString());
                    passed =false;
                }
                i++;
            }
            if(!passed){
                //System.out.println("--Test "+encoding+" to Unicode --PASSED");
            //}else{
                errln("--Test "+encoding+" to Unicode :FAILED");
            }
            smOutBufToUnicode(gbConv,encoding);
            
        }
        catch(UnsupportedEncodingException ueEx){
            errln(ueEx.toString());
        }
        catch (MalformedInputException miEx){
            errln(miEx.toString());
        }
        catch (UnknownCharacterException ucEx){
            errln(ucEx.toString());
        }
        catch (ConversionBufferFullException cbfEx){
            errln(cbfEx.toString());
        } 
        
      }

      private  void smOutBufToUnicode(CharToByteConverter gbConv,String encoding){
        
        {
//            try{
                gbConv.reset();
                //CharToByteConverter gbConv = CharToByteConverter.getConverter("gb18030");
                byte[] myByteTarget = new byte[myUSource.length*5];
                char[] myCharTarget = new char[myUSource.length+1];
                int inStart = 0;
                int inStop = myUSource.length;
                int outStart = 0;
                int outStop = 0;
                int i=5;
                while(true){
                    try{
                        outStop= (outStop+i>myByteTarget.length)? myByteTarget.length : (outStop+i) ;
                        inStart = gbConv.nextCharIndex();
                        outStart = gbConv.nextByteIndex();
                        gbConv.convert(myUSource, inStart, inStop,myByteTarget, outStart, outStop);
                        int stop = gbConv.nextCharIndex();
                        if(stop>=myUSource.length){
                            break;
                        }
                    }
                    catch(ConversionBufferFullException cbEx){
                        continue;
                    }
                    catch (MalformedInputException miEx){
                        errln(miEx.toString());
                        return;
                    }
                    catch (UnknownCharacterException ucEx){
                        errln(ucEx.toString());
                        return;
                    }
                }
                char[] expectedResult = myGBSource;
                boolean passed =true;
                i=0;
                while(myByteTarget[i]!='\0'){
                    if(myByteTarget[i]!=(byte)expectedResult[i]){
                        StringBuffer temp = new StringBuffer("Error:  Expected: ");
                        temp.append(Integer.toHexString(expectedResult[i]));
                        temp.append(" Got: ");
                        temp.append(Integer.toHexString(myByteTarget[i]));
                        errln(temp.toString());
                        passed =false;
                        passed =false;
                    }
                    i++;
                }
                
                if(passed){
                    //System.out.println("--Test small output buffers "+encoding+" from Unicode--PASSED");
                //}else{
                    errln("Test small output buffers "+encoding+" from Unicode : FAILED");
                }
                smOutBufToUnicode(gbConv,encoding);
              
 /*           }
             catch(UnsupportedEncodingException ueEx){
                    System.out.println(ueEx.toString());
            }*/
        }
        {            
           // try{
                //CharToByteConverter gbConv = CharToByteConverter.getConverter("gb18030");
                gbConv.reset();
                byte[] myByteTarget = new byte[myUSource.length*5];
                char[] myCharTarget = new char[myUSource.length+1];
                int inStart = 0;
                int inStop = myUSource.length;
                int outStart = 0;
                int outStop = 0;
                int i=5;
                while(true){
                    try{
                        outStop= (outStop+i>myByteTarget.length)? myByteTarget.length : (outStop+i) ;
                        inStop = (inStop+i>myUSource.length)? myUSource.length: (inStop+i);
                        inStart = gbConv.nextCharIndex();
                        outStart = gbConv.nextByteIndex();
                        gbConv.convert(myUSource, inStart, inStop,myByteTarget, outStart, outStop);
                        if(gbConv.nextCharIndex()>=myUSource.length){
                            break;
                        }
                    }
                    catch(ConversionBufferFullException cbEx){
                        continue;
                    }
                    catch (MalformedInputException miEx){
                        errln(miEx.toString());
                        return;
                    }
                    catch (UnknownCharacterException ucEx){
                        errln(ucEx.toString());
                        return;
                    }
                }
                char[] expectedResult = myGBSource;
                boolean passed =true;
                i=0;
                while(myByteTarget[i]!='\0'){
                    if(myByteTarget[i]!=(byte)expectedResult[i]){
                        StringBuffer temp = new StringBuffer("Error:  Expected: ");
                        temp.append(Integer.toHexString(expectedResult[i]));
                        temp.append(" Got: ");
                        temp.append(Integer.toHexString(myByteTarget[i]));
                        errln(temp.toString());
                        passed =false;
                        passed =false;
                    }
                    i++;
                }
                if(!passed){
                    //System.out.println("--Test small input buffers "+encoding+" from Unicode--PASSED");
                //}else{
                    errln("--Test small input buffers "+encoding+" from Unicode --FAILED");
                }           
           }
      }
      
      
      private void multiThreadedTest(final String encoding){
        //while(true){
        try{
           final ByteToCharConverter bcConv1 = ByteToCharConverterICU.createConverter(encoding);
           final ByteToCharConverter bcConv2 = ByteToCharConverterICU.createConverter(encoding);
           final CharToByteConverter cbConv1 = CharToByteConverterICU.createConverter(encoding);
           final CharToByteConverter cbConv2 = CharToByteConverterICU.createConverter(encoding);
           Thread t1 = new Thread(){
                public void run(){
                    synchronized(bcConv1){
                        while(!interrupted()){
                            try{
                                final ByteToCharConverter bcConv = ByteToCharConverterICU.createConverter(encoding);
                                final CharToByteConverter cbConv = CharToByteConverterICU.createConverter(encoding);
                                smOutBufToUnicode(bcConv,encoding);
                                smOutBufToUnicode(cbConv,encoding);
                            }
                            catch(UnsupportedEncodingException ueEx){
                                System.out.println(ueEx.toString());
                            }
                        }
                            
                    }
                }
            };
            
            Thread t2 = new Thread(){
                public void run(){
                    synchronized(bcConv2){
                        while(!interrupted()){
                            try{
                                final ByteToCharConverter bcConv = ByteToCharConverterICU.createConverter(encoding);
                                smOutBufToUnicode(bcConv2,encoding);
                                smOutBufToUnicode(bcConv,encoding);
                            }
                            catch(UnsupportedEncodingException ueEx){
                                System.out.println(ueEx.toString());
                            }
                        }
                    }
                }
            };
            
            Thread t3 = new Thread(){
                public void run(){
                   synchronized(cbConv1){
                        while(!interrupted()){
                            smOutBufToUnicode(cbConv1,encoding);
                        }
                   }
                }
            };
            
            Thread t4 = new Thread(){
                public void run(){
                   synchronized(cbConv2){
                       while(!interrupted()){
                            smOutBufToUnicode(cbConv2,encoding);
                       }
                   }
                }
            };
            
           t1.start();
           t2.start();         
           t3.start();
           t4.start();
           t1.interrupt();
           t2.interrupt();
           t3.interrupt();
           t4.interrupt();
        }
        
        catch (UnsupportedEncodingException ueEx){
            System.out.println(ueEx.toString());
        }
        //}
    }
}
