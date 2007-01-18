/**
*******************************************************************************
* Copyright (C) 1996-2007, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
*******************************************************************************
*/

package com.ibm.icu4jni.test.charset;

import java.nio.*;
import java.nio.charset.spi.*;
import java.nio.charset.*;
import java.util.*;

//import sun.misc.ASCIICaseInsensitiveComparator;

import com.ibm.icu4jni.charset.*;
import com.ibm.icu4jni.converters.NativeConverter;
import com.ibm.icu4jni.test.TestFmwk;

public class TestCharset extends TestFmwk {

    static String encoding = "gb18030";
    static boolean useICU = false;

    static CharsetProvider provider = new CharsetProviderICU();
    Charset charset;
    CharsetDecoder decoder;
    CharsetEncoder encoder;

    static String unistr = "abcd\u8000\u8001\u00a5\u3000\r\n";
    static byte[] gb =
        {
            (byte) 'a',
            (byte) 'b',
            (byte) 'c',
            (byte) 'd',
            (byte) 0xd2,
            (byte) 0xab,
            (byte) 0xc0,
            (byte) 0xcf,
            (byte) 0x81,
            (byte) 0x30,
            (byte) 0x84,
            (byte) 0x36,
            (byte) 0xa1,
            (byte) 0xa1,
            (byte) 0x0d,
            (byte) 0x0a };
    public static void main(String[] args) throws Exception {
        new TestCharset().run(args);
    }

    public TestCharset() {
        //charset = CharsetICU.forName(encoding);
        charset = provider.charsetForName(encoding);
        decoder = (CharsetDecoder) charset.newDecoder();
        encoder = (CharsetEncoder) charset.newEncoder();
    }
    public void TestUTF16Converter(){
        CharsetProviderICU icu = new CharsetProviderICU();
        Charset icuChar = icu.charsetForName("UTF-16");
        Set aliases = icuChar.aliases();
        Iterator iter = aliases.iterator();
        while(iter.hasNext()){
            logln((String)iter.next());
        }
        Charset cs = icu.charsetForName("UTF16");
        if(!cs.name().equals("UTF-16")){
            errln("Did not get the expected converter for alias UTF16");
        }
        cs = icu.charsetForName("UTF16BE");
        if(!cs.name().equals("UTF-16BE")){
            errln("Did not get the expected converter for alias UTF16BE");
        }
        logln("The name of the charset is: "+ icuChar.name());
        if(!aliases.contains("csUnicode")){
            errln("Did not get the expected alias");
        }
        if(!aliases.contains("UTF-16BE")){
            errln("Did not get the expected alias");
        }
        char[] expchars = new char[]{'\ud800','\udc00','\ud801','\udc01'};
        byte[] expbytes = new byte[]{(byte)0xfe, (byte)0xff,(byte)0xd8,0x00,(byte)0xdc,0x00, (byte)0xd8,0x01,(byte)0xdc,0x01};
        {
            try{
                CharsetEncoder enc =icuChar.newEncoder();
                CharBuffer in = CharBuffer.wrap(expchars);
                ByteBuffer out = enc.encode(in);
                if(!equals(out,expbytes)){
                    errln("did not get the expected output");
                }
            }catch(CharacterCodingException ex){
                errln(ex.getMessage());
            }
        }
        {
            try{
                CharsetDecoder dec =icuChar.newDecoder();
                ByteBuffer in = ByteBuffer.wrap(expbytes);
                CharBuffer out = dec.decode(in);
                if(!equals(out,expchars)){
                    errln("did not get the expected output");
                }
            }catch(CharacterCodingException ex){
                errln(ex.getMessage());
            }
        }
        
    }
    public void TestAPISemantics(/*String encoding*/) 
                throws Exception {
        int rc;
        ByteBuffer gbval = ByteBuffer.wrap(gb);
        CharBuffer uniVal = CharBuffer.wrap(unistr);
        rc = 0;
        decoder.reset();
        /* Convert the whole buffer to Unicode */
        try {
            CharBuffer chars = CharBuffer.allocate(unistr.length());
            CoderResult result = decoder.decode(gbval, chars, false);

            if (result.isError()) {
                errln("ToChars encountered Error");
                rc = 1;
            }
            if (result.isOverflow()) {
                errln("ToChars encountered overflow exception");
                rc = 1;
            }
            if (!equals(chars, unistr)) {
                errln("ToChars does not match");
                printchars(chars);
                errln("Expected : ");
                printchars(unistr);
                rc = 2;
            }

        } catch (Exception e) {
            errln("ToChars - exception in buffer");
            rc = 5;
        }

        /* Convert single bytes to Unicode */
        try {
            CharBuffer chars = CharBuffer.allocate(unistr.length());
            ByteBuffer b = ByteBuffer.wrap(gb);
            decoder.reset();
            CoderResult result=null;
            for (int i = 1; i <= gb.length; i++) {
                b.limit(i);
                result = decoder.decode(b, chars, false);
                if(result.isOverflow()){
                    errln("ToChars single threw an overflow exception");
                }
                if (result.isError()) {
                    errln("ToChars single the result is an error "+result.toString());
                } 
            }
            if (unistr.length() != (chars.limit())) {
                errln("ToChars single len does not match");
                rc = 3;
            }
            if (!equals(chars, unistr)) {
                errln("ToChars single does not match");
                printchars(chars);
                rc = 4;
            }
        } catch (Exception e) {
            errln("ToChars - exception in single");
            //e.printStackTrace();
            rc = 6;
        }

        /* Convert the buffer one at a time to Unicode */
        try {
            CharBuffer chars = CharBuffer.allocate(unistr.length());
            decoder.reset();
            gbval.rewind();
            for (int i = 1; i <= gb.length; i++) {
                gbval.limit(i);
                CoderResult result = decoder.decode(gbval, chars, false);
                if (result.isError()) {
                    errln("Error while decoding: "+result.toString());
                }
                if(result.isOverflow()){
                    errln("ToChars Simple threw an overflow exception");
                }
            }
            if (chars.limit() != unistr.length()) {
                errln("ToChars Simple buffer len does not match");
                rc = 7;
            }
            if (!equals(chars, unistr)) {
                errln("ToChars Simple buffer does not match");
                printchars(chars);
                err(" Expected : ");
                printchars(unistr);
                rc = 8;
            }
        } catch (Exception e) {
            errln("ToChars - exception in single buffer");
            //e.printStackTrace(System.err);
            rc = 9;
        }
        if (rc != 0) {
            errln("Test Simple ToChars for encoding : FAILED");
        }

        rc = 0;
        /* Convert the whole buffer from unicode */
        try {
            ByteBuffer bytes = ByteBuffer.allocate(gb.length);
            encoder.reset();
            CoderResult result = encoder.encode(uniVal, bytes, false);
            if (result.isError()) {
                errln("FromChars reported error: " + result.toString());
                rc = 1;
            }
            if(result.isOverflow()){
                errln("FromChars threw an overflow exception");
            }
            if (!bytes.equals(gbval)) {
                errln("FromChars does not match");
                printbytes(bytes);
                rc = 2;
            }
        } catch (Exception e) {
            errln("FromChars - exception in buffer");
            //e.printStackTrace(System.err);
            rc = 5;
        }

        /* Convert the buffer one char at a time to unicode */
        try {
            ByteBuffer bytes = ByteBuffer.allocate(gb.length);
            CharBuffer c = CharBuffer.wrap(unistr);
            encoder.reset();
            CoderResult result= null;
            for (int i = 1; i <= unistr.length(); i++) {
                c.limit(i);
                result = encoder.encode(c, bytes, false);
                if(result.isOverflow()){
                    errln("FromChars single threw an overflow exception");
                }
                if(result.isError()){
                    errln("FromChars single threw an error: "+ result.toString());
                }
            }
            if (gb.length != bytes.limit()) {
                errln("FromChars single len does not match");
                rc = 3;
            }
            if (!bytes.equals(gbval)) {
                errln("FromChars single does not match");
                printbytes(bytes);
                rc = 4;
            }

        } catch (Exception e) {
            errln("FromChars - exception in single");
            //e.printStackTrace(System.err);
            rc = 6;
        }

        /* Convert one char at a time to unicode */
        try {
            ByteBuffer bytes = ByteBuffer.allocate(gb.length);
            encoder.reset();
            char[] temp = unistr.toCharArray();
            CoderResult result=null;
            for (int i = 0; i <= temp.length; i++) {
                uniVal.limit(i);
                result = encoder.encode(uniVal, bytes, false);
                if(result.isOverflow()){
                    errln("FromChars simple threw an overflow exception");
                }
                if(result.isError()){
                    errln("FromChars simple threw an error: "+ result.toString());
                }
            }
            if (bytes.limit() != gb.length) {
                errln("FromChars Simple len does not match");
                rc = 7;
            }
            if (!bytes.equals(gbval)) {
                errln("FromChars Simple does not match");
                printbytes(bytes);
                rc = 8;
            }
        } catch (Exception e) {
            errln("FromChars - exception in single buffer");
            //e.printStackTrace(System.err);
            rc = 9;
        }
        if (rc != 0) {
            errln("Test Simple FromChars " + encoding + " --FAILED");
        }
    }

    void printchars(CharBuffer buf) {
        int i;
        char[] chars = new char[buf.limit()];
        //save the current position
        int pos = buf.position();
        buf.position(0);
        buf.get(chars);
        //reset to old position
        buf.position(pos);
        for (i = 0; i < chars.length; i++) {
            err(hex(chars[i]) + " ");
        }
        errln("");
    }
    void printchars(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            err(hex(chars[i]) + " ");
        }
        errln("");
    }
    void printbytes(ByteBuffer buf) {
        int i;
        byte[] bytes = new byte[buf.limit()];
        //save the current position
        int pos = buf.position();
        buf.position(0);
        buf.get(bytes);
        //reset to old position
        buf.position(pos);
        for (i = 0; i < bytes.length; i++) {
            System.out.print(hex(bytes[i]) + " ");
        }
        errln("");
    }

    public boolean equals(CharBuffer buf, String str) {
        return equals(buf, str.toCharArray());
    }

    public boolean equals(CharBuffer buf, char[] compareTo) {
        char[] chars = new char[buf.limit()];
        //save the current position
        int pos = buf.position();
        buf.position(0);
        buf.get(chars);
        //reset to old position
        buf.position(pos);
        return equals(chars, compareTo);
    }

    public boolean equals(char[] chars, char[] compareTo) {
        if (chars.length != compareTo.length) {
            errln(
                "Length does not match chars: "
                    + chars.length
                    + " compareTo: "
                    + compareTo.length);
            return false;
        } else {
            boolean result = true;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] != compareTo[i]) {
                    errln(
                        "Got: "
                            + hex(chars[i])
                            + " Expected: "
                            + hex(compareTo[i])
                            + " At: "
                            + i);
                    result = false;
                }
            }
            return result;
        }
    }

    public boolean equals(ByteBuffer buf, byte[] compareTo) {
        byte[] chars = new byte[buf.limit()];
        //save the current position
        int pos = buf.position();
        buf.position(0);
        buf.get(chars);
        //reset to old position
        buf.position(pos);
        return equals(chars, compareTo);
    }

    public boolean equals(byte[] chars, byte[] compareTo) {
        if (chars.length != compareTo.length) {
            errln(
                "Length does not match chars: "
                    + chars.length
                    + " compareTo: "
                    + compareTo.length);
            return false;
        } else {
            boolean result = true;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] != compareTo[i]) {
                    errln(
                        "Got: "
                            + hex(chars[i])
                            + " Expected: "
                            + hex(compareTo[i])
                            + " At: "
                            + i);
                    result = false;
                }
            }
            return result;
        }
    }
    public boolean equals(ByteBuffer buf, char[] compareTo) {
        return equals(buf, getByteArray(compareTo));
    }

    static char[] uSource =
        {
            '\u22A6',
            '\u22A7',
            '\u22A8',
            '\u22A9',
            '\u22AA',
            '\u22AB',
            '\u22AC',
            '\u22AD',
            '\u22AE',
            '\u22AF',
            '\u22B0',
            '\u22B1',
            '\u22B2',
            '\u22B3',
            '\u22B4',
            '\u22B5',
            '\u22B6',
            '\u22B7',
            '\u22B8',
            '\u22B9',
            '\u22BA',
            '\u22BB',
            '\u22BC',
            '\u22BD',
            '\u22BE' };
    static char[] gbSource =
        {
            0x81,
            0x36,
            0xDE,
            0x36,
            0x81,
            0x36,
            0xDE,
            0x37,
            0x81,
            0x36,
            0xDE,
            0x38,
            0x81,
            0x36,
            0xDE,
            0x39,
            0x81,
            0x36,
            0xDF,
            0x30,
            0x81,
            0x36,
            0xDF,
            0x31,
            0x81,
            0x36,
            0xDF,
            0x32,
            0x81,
            0x36,
            0xDF,
            0x33,
            0x81,
            0x36,
            0xDF,
            0x34,
            0x81,
            0x36,
            0xDF,
            0x35,
            0x81,
            0x36,
            0xDF,
            0x36,
            0x81,
            0x36,
            0xDF,
            0x37,
            0x81,
            0x36,
            0xDF,
            0x38,
            0x81,
            0x36,
            0xDF,
            0x39,
            0x81,
            0x36,
            0xE0,
            0x30,
            0x81,
            0x36,
            0xE0,
            0x31,
            0x81,
            0x36,
            0xE0,
            0x32,
            0x81,
            0x36,
            0xE0,
            0x33,
            0x81,
            0x36,
            0xE0,
            0x34,
            0x81,
            0x36,
            0xE0,
            0x35,
            0x81,
            0x36,
            0xE0,
            0x36,
            0x81,
            0x36,
            0xE0,
            0x37,
            0x81,
            0x36,
            0xE0,
            0x38,
            0x81,
            0x36,
            0xE0,
            0x39,
            0x81,
            0x36,
            0xE1,
            0x30 };

    static char[] myUSource =
        {
            '\u32d9',
            '\u32da',
            '\u32db',
            '\u32dc',
            '\u32dd',
            '\u32de',
            '\u32df',
            '\u32e0',
            '\u32e1',
            '\u32e2',
            '\u32e3',
            '\u32e4',
            '\u32e5',
            '\u32e6',
            '\u32e7',
            '\u32e8',
            '\u32e9',
            '\u32ea',
            '\u32eb',
            '\u32ec',
            '\u32ed',
            '\u32ee' };
    static char[] myGBSource =
        {
            0x81,
            0x39,
            0xd2,
            0x35,
            0x81,
            0x39,
            0xd2,
            0x36,
            0x81,
            0x39,
            0xd2,
            0x37,
            0x81,
            0x39,
            0xd2,
            0x38,
            0x81,
            0x39,
            0xd2,
            0x39,
            0x81,
            0x39,
            0xd3,
            0x30,
            0x81,
            0x39,
            0xd3,
            0x31,
            0x81,
            0x39,
            0xd3,
            0x32,
            0x81,
            0x39,
            0xd3,
            0x33,
            0x81,
            0x39,
            0xd3,
            0x34,
            0x81,
            0x39,
            0xd3,
            0x35,
            0x81,
            0x39,
            0xd3,
            0x36,
            0x81,
            0x39,
            0xd3,
            0x37,
            0x81,
            0x39,
            0xd3,
            0x38,
            0x81,
            0x39,
            0xd3,
            0x39,
            0x81,
            0x39,
            0xd4,
            0x30,
            0x81,
            0x39,
            0xd4,
            0x31,
            0x81,
            0x39,
            0xd4,
            0x32,
            0x81,
            0x39,
            0xd4,
            0x33,
            0x81,
            0x39,
            0xd4,
            0x34,
            0x81,
            0x39,
            0xd4,
            0x35,
            0x81,
            0x39,
            0xd4,
            0x36,
            };
    public void TestCallback(/*String encoding*/
    ) throws Exception {

        {
            byte[] gbSource =
                {
                    (byte) 0x81,
                    (byte) 0x36,
                    (byte) 0xDE,
                    (byte) 0x36,
                    (byte) 0x81,
                    (byte) 0x36,
                    (byte) 0xDE,
                    (byte) 0x37,
                    (byte) 0x81,
                    (byte) 0x36,
                    (byte) 0xDE,
                    (byte) 0x38,
                    (byte) 0xe3,
                    (byte) 0x32,
                    (byte) 0x9a,
                    (byte) 0x36 };

            char[] subChars = { 'P', 'I' };

            decoder.reset();

            decoder.replaceWith(new String(subChars));
            ByteBuffer mySource = ByteBuffer.wrap(gbSource);
            CharBuffer myTarget = CharBuffer.allocate(5);

            decoder.decode(mySource, myTarget, true);
            char[] expectedResult =
                { '\u22A6', '\u22A7', '\u22A8', '\u0050', '\u0049', };

            if (!equals(myTarget, new String(expectedResult))) {
                errln("Test callback GB18030 to Unicode : FAILED");
            }
        }
    }
    /*
    private static boolean isFirstSurrogate(char c) {
        return (boolean) (((c) & 0xfffffc00) == 0xd800);
    }
    private static boolean isSecondSurrogate(char c) {
        return (boolean) (((c) & 0xfffffc00) == 0xdc00);
    }
    */
    public void TestCanConvert(/*String encoding*/)throws Exception {
        char[] mySource = { 
            '\ud800', '\udc00',/*surrogate pair */
            '\u22A6','\u22A7','\u22A8','\u22A9','\u22AA',
            '\u22AB','\u22AC','\u22AD','\u22AE','\u22AF',
            '\u22B0','\u22B1','\u22B2','\u22B3','\u22B4',
            '\ud800','\udc00',/*surrogate pair */
            '\u22B5','\u22B6','\u22B7','\u22B8','\u22B9',
            '\u22BA','\u22BB','\u22BC','\u22BD','\u22BE' 
            };
            
        encoder.reset();
        if (!encoder.canEncode(new String(mySource))) {
            errln("Test canConvert() " + encoding + " failed. "+encoder);
        }

    }
    private void smBufDecode(CharsetDecoder decoder, String encoding) {

        ByteBuffer mygbSource = ByteBuffer.wrap(getByteArray(myGBSource));
        {
            decoder.reset();
            CharBuffer myCharTarget = CharBuffer.allocate(myUSource.length);
            int inputLen = mygbSource.limit();
            mygbSource.position(0);
            for(int i=1; i<=inputLen; i++) {
                mygbSource.limit(i);
                CoderResult result =
                    decoder.decode(mygbSource, myCharTarget, false);
                if (result.isError()) {
                    errln("Test small input buffers while decoding failed. "+result.toString());
                }
                if (result.isOverflow()) {
                    errln("Test small input buffers while decoding threw overflow exception");
                }

            }

            if (!equals(myCharTarget, myUSource)) {
                errln(
                    "Test small input buffers while decoding "
                        + encoding
                        + " TO Unicode--failed");
            }
        }
        {
            decoder.reset();
            CharBuffer myCharTarget = CharBuffer.allocate(myUSource.length);
            myCharTarget.position(0);
            mygbSource.rewind();
            while (true) {
                int pos = myCharTarget.position();
                myCharTarget.limit(++pos);
                CoderResult result =
                    decoder.decode(mygbSource, myCharTarget, false);
                if (result.isError()) {
                    errln("Test small output buffers while decoding "+ result.toString());
                }
                if (mygbSource.position()== mygbSource.limit()) {
                    result = decoder.decode(mygbSource, myCharTarget, true);
                    if (result.isError()) {
                        errln("Test small output buffers while decoding "+result.toString());
                    }
                    result = decoder.flush(myCharTarget);
                    if (result.isError()) {
                        errln("Test small output buffers while decoding "+ result.toString());
                    }
                    break;
                }
            }

            if (!equals(myCharTarget, myUSource)) {
                errln(
                    "Test small output buffers "
                        + encoding
                        + " TO Unicode failed");
            }
        }
    }
    /*
    private  int put(Iterator i, Map m) {
        int ret = 0;
        while (i.hasNext()) {
            Charset cs = (Charset)i.next();
            if (!m.containsKey(cs.name())){
                m.put(cs.name(), cs);
            }else{
                logln(" The map contains "+cs.name());
                ret++;
            }
        }
        return ret;
    }
    private int size(Iterator iter){
        int num = 0;
        while(iter.hasNext()){
            iter.next();
            num++;
        }
        return num;
    }
    */
    public void TestAvailableCharsets() {
        SortedMap map = Charset.availableCharsets();
        Set keySet = map.keySet();
        Iterator iter = keySet.iterator();
        while(iter.hasNext()){
            logln("Charset name: "+iter.next().toString());
        }
        String[] charsets = NativeConverter.getAvailable();
        int mapSize = map.size();
        if(mapSize < charsets.length){
            errln("Charset.availableCharsets() returned a number less than the number returned by icu. ICU: " + charsets.length
                    + " JDK: " + mapSize);
        }
        logln("Total Number of chasets = " + map.size());
	}
    public void TestWindows936(){
        CharsetProviderICU icu = new CharsetProviderICU();
        Charset cs = icu.charsetForName("windows-936-2000");
        String canonicalName = cs.name();
        if(!canonicalName.equals("GBK")){
            errln("Did not get the expected canonical name. Got: "+canonicalName); //get the canonical name
        }
    }
    public void TestICUAvailableCharsets() {
        String[] charsets = NativeConverter.getAvailable();
        CharsetProviderICU icu = new CharsetProviderICU();
        for(int i=0;i<charsets.length;i++){
            Charset cs = icu.charsetForName(charsets[i]);
            try{
                CharsetEncoder encoder = cs.newEncoder();
                if(encoder == null){
                    errln("newEncoder() returned null for: "+charsets[i]);
                }
            }catch(Exception ex){
                errln("Could not instantiate encoder for "+charsets[i]+". Error: "+ex.toString());
            }
            try{
                CharsetDecoder decoder = cs.newDecoder();
                if(decoder == null){
                    errln("newDecoder() returned null for: "+charsets[i]);
                }
            }catch(Exception ex){
                errln("Could not instantiate decoder for "+charsets[i]+". Error: "+ex.toString());
            }
        }
    }
    /* jitterbug 4312 */
    public void TestUnsupportedCharset(){
        CharsetProvider icu = new CharsetProviderICU();
        Charset icuChar = icu.charsetForName("impossible");
        if(icuChar != null){
            errln("ICU does not conform to the spec");
        }
    }
    /* jitterbug 4313 */
    public void TestPutCharsets(){
        try{
            CharsetProviderICU icu = new CharsetProviderICU();
            TreeMap map = new TreeMap();
            icu.putCharsets(map);
            Set set = map.keySet();
            Iterator iter = set.iterator();
            while(iter.hasNext()){
                String key = (String)iter.next();
                Charset cs = (Charset)map.get(key);
                if(cs == null){
                    errln("Could not create charset for name: " + key);
                }
                try{
                    cs.newEncoder();
                }catch(Exception e){
                    errln("Could not create encoder for " + key + " error: " + e.toString());
                }
                try{
                    cs.newDecoder();
                }catch(Exception e){
                    errln("Could not create decoder for " + key + " error: " + e.toString());
                }
            } 
            logln("Number of entries in the map: " + set.size());
        }catch(ClassCastException ex){
            errln("CharsetProviderICU.putCharsets does not conform to the spec");
        }
    }

    public void TestEncoderCreation(){
        try{
            Charset cs = Charset.forName("GB_2312-80");
            CharsetEncoder enc = cs.newEncoder();
            if(enc!=null){
                logln("Successfully created the encoder");
            }
        }catch(Exception e){
            errln("Error creating charset encoder."+ e.toString());
           // e.printStackTrace();
        }
        try{
            Charset cs = Charset.forName("x-ibm-971_P100-1995");
            CharsetEncoder enc = cs.newEncoder();
            if(enc!=null){
                logln("Successfully created the encoder");
            }
        }catch(Exception e){
            errln("Error creating charset encoder."+ e.toString());
        }
    }
    public void TestSubBytes(){
        try{
            //create utf-8 decoder
            CharsetDecoder decoder = new CharsetProviderICU().charsetForName("utf-8").newDecoder();
    
            //create a valid byte array, which can be decoded to " buffer"
            byte[] unibytes = new byte[] { 0x0020, 0x0062, 0x0075, 0x0066, 0x0066, 0x0065, 0x0072 };
    
            ByteBuffer buffer = ByteBuffer.allocate(20);
    
            //add a evil byte to make the byte buffer be malformed input
            buffer.put((byte)0xd8);
    
            //put the valid byte array
            buffer.put(unibytes);
    
            //reset postion
            buffer.flip();  
            
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            CharBuffer out = decoder.decode(buffer);
            String expected = "\ufffd buffer";
            if(!expected.equals(new String(out.array()))){
                errln("Did not get the expected result for substitution chars. Got: "+
                       new String(out.array()) + "("+ hex(out.array())+")");
            }
            logln("Output: "+  new String(out.array()) + "("+ hex(out.array())+")");
        }catch (CharacterCodingException ex){
            errln("Unexpected exception: "+ex.toString());
        }
    }
    public void TestImplFlushFailure(){
   
       try{
           CharBuffer in = CharBuffer.wrap("\u3005\u3006\u3007\u30FC\u2015\u2010\uFF0F");
           CharsetEncoder encoder = new CharsetProviderICU().charsetForName("iso-2022-jp").newEncoder();
           ByteBuffer out = ByteBuffer.allocate(30);
           encoder.encode(in, out, true);
           encoder.flush(out);
           if(out.position()!= 20){
               errln("Did not get the expected position from flush");
           }
           
       }catch (Exception ex){
           errln("Unexpected exception: "+ex.toString());
       } 
    }

    public void TestISO88591() {
        CharsetEncoder encoder = new CharsetProviderICU().charsetForName("iso-8859-1").newEncoder();
        boolean enc = encoder.canEncode("\uc2a3");
        if(enc==true){
            errln("88591 encoder returned true for \\uc2a3");
        }
    }
    public  void TestUTF8Encode() {
        CharsetEncoder encoderICU = new CharsetProviderICU().charsetForName(
                "utf-8").newEncoder();
        ByteBuffer out = ByteBuffer.allocate(30);
        CoderResult result = encoderICU.encode(CharBuffer.wrap("\ud800"), out, true);
       
        if (result.isMalformed()) {
            logln("\\ud800 is malformed for ICU4JNI utf-8 encoder");
        } else if (result.isUnderflow()) {
            errln("\\ud800 is OK for ICU4JNI utf-8 encoder");
        }

        CharsetEncoder encoderJDK = Charset.forName("utf-8").newEncoder();
        result = encoderJDK.encode(CharBuffer.wrap("\ud800"), ByteBuffer
                .allocate(10), true);
        if (result.isUnderflow()) {
            errln("\\ud800 is OK for JDK utf-8 encoder");
        } else if (result.isMalformed()) {
            logln("\\ud800 is malformed for JDK utf-8 encoder");
        }
    }

    private void printCB(CharBuffer buf){
        buf.rewind();
        while(buf.hasRemaining()){
            System.out.println(hex(buf.get()));
        }
        buf.rewind();
    }
    public void TestUTF8() throws CharacterCodingException{
           try{
               CharsetEncoder encoderICU = new CharsetProviderICU().charsetForName("utf-8").newEncoder();
               encoderICU.encode(CharBuffer.wrap("\ud800"));
               errln("\\ud800 is OK for ICU4JNI utf-8 encoder");
           }catch (MalformedInputException e) {
               logln("\\ud800 is malformed for JDK utf-8 encoder");
              //e.printStackTrace();
           }
           
           CharsetEncoder encoderJDK = Charset.forName("utf-8").newEncoder();
           try {
               encoderJDK.encode(CharBuffer.wrap("\ud800"));
               errln("\\ud800 is OK for JDK utf-8 encoder");
           } catch (MalformedInputException e) {
               logln("\\ud800 is malformed for JDK utf-8 encoder");
               //e.printStackTrace();
           }         
    }
    public void TestUTF16BOM(){

        Charset cs = (new CharsetProviderICU()).charsetForName("UTF-16");
        char[] in = new char[] { 0x1122, 0x2211, 0x3344, 0x4433,
                                0x5566, 0x6655, 0x7788, 0x8877, 0x9900 };
        CharBuffer inBuf = CharBuffer.allocate(in.length);
        inBuf.put(in);
        CharsetEncoder encoder = cs.newEncoder();
        ByteBuffer outBuf = ByteBuffer.allocate(in.length*2+2);
        inBuf.rewind();
        encoder.encode(inBuf, outBuf, true);
        outBuf.rewind();
        if(outBuf.get(0)!= (byte)0xFE && outBuf.get(1)!= (byte)0xFF){
            errln("The UTF16 encoder did not appended bom. Length returned: " + outBuf.remaining());
        }
        while(outBuf.hasRemaining()){
            logln("0x"+hex(outBuf.get()));
        }
        CharsetDecoder decoder = cs.newDecoder();
        outBuf.rewind();
        CharBuffer rt = CharBuffer.allocate(in.length+1);
        CoderResult cr = decoder.decode(outBuf, rt, true);
        if(cr.isError()){
            errln("Decoding with BOM failed. Error: "+ cr.toString());
        }
        rt.limit(rt.position());
        equals(rt, in);
        {
            rt.clear();
            outBuf.rewind();
            Charset utf16 = Charset.forName("UTF-16");
            CharsetDecoder dc = utf16.newDecoder();
            cr = dc.decode(outBuf, rt, true);
            rt.limit(rt.position());
            equals(rt, in);
        }
    }
    public void TestUTF32BOM(){

        Charset cs = (new CharsetProviderICU()).charsetForName("UTF-32");
        char[] in = new char[] { 0xd800, 0xdc00, 
                                 0xd801, 0xdc01,
                                 0xdbff, 0xdfff, 
                                 0xd900, 0xdd00, 
                                 0x0000, 0x0041,
                                 0x0000, 0x0042,
                                 0x0000, 0x0043};
        
        CharBuffer inBuf = CharBuffer.allocate(in.length);
        inBuf.put(in);
        CharsetEncoder encoder = cs.newEncoder();
        ByteBuffer outBuf = ByteBuffer.allocate(in.length*4+4);
        inBuf.rewind();
        encoder.encode(inBuf, outBuf, true);
        outBuf.rewind();
        if(outBuf.get(0)!= (byte)0xFF && outBuf.get(1)!= (byte)0xFE){
            errln("The UTF16 encoder did not appended bom. Length returned: " + outBuf.remaining());
        }
        while(outBuf.hasRemaining()){
            logln("0x"+hex(outBuf.get()));
        }
        CharsetDecoder decoder = cs.newDecoder();
        outBuf.rewind();
        CharBuffer rt = CharBuffer.allocate(in.length);
        CoderResult cr = decoder.decode(outBuf, rt, true);
        if(cr.isError()){
            errln("Decoding with BOM failed. Error: "+ cr.toString());
        }
        equals(rt, in);
        try{
            rt.clear();
            outBuf.rewind();
            Charset utf16 = Charset.forName("UTF-32");
            CharsetDecoder dc = utf16.newDecoder();
            cr = dc.decode(outBuf, rt, true);
            equals(rt, in);
        }catch(UnsupportedCharsetException ex){
            // swallow the expection.
        }
    }
    private void smBufEncode(CharsetEncoder encoder, String encoding) {
        logln("Running smBufEncode for "+ encoding + " with class " + encoder);
        CharBuffer mySource = CharBuffer.wrap(myUSource);
        {
            logln("Running tests on small input buffers for "+ encoding);
            encoder.reset();
            ByteBuffer myTarget = ByteBuffer.allocate(myGBSource.length);
            mySource.position(0);
            CoderResult result=null;
            for(int i=1; i<=myUSource.length; i++) {
                mySource.limit(i);
                result = encoder.encode(mySource, myTarget, false);
                if (result.isError()) {
                    errln("Test small input buffers while encoding failed. "+result.toString());
                }
                if (result.isOverflow()) {
                    errln("Test small input buffers while encoding threw overflow exception");
                }

            }
            if (!equals(myTarget, myGBSource)) {

                errln("Test small input buffers "+ encoding+ " From Unicode failed");

            }
            logln("Tests on small input buffers for "+ encoding +" passed");
        }
        {
            logln("Running tests on small output buffers for "+ encoding);
            encoder.reset();
            ByteBuffer myTarget = ByteBuffer.allocate(myGBSource.length);
            myTarget.position(0);
            myTarget.limit(0);
            mySource.rewind();
            logln("myTarget.limit: " + myTarget.limit() + " myTarget.capcity: " + myTarget.capacity());
            
            while (true) {
                int pos = myTarget.position();
                myTarget.limit(++pos);
                CoderResult result = encoder.encode(mySource, myTarget, false);
                logln("myTarget.Position: "+ pos + " myTarget.limit: " + myTarget.limit());
                logln("mySource.position: " + mySource.position() + " mySource.limit: " + mySource.limit());
                

                if (result.isError()) {
                    errln("Test small output buffers while encoding "+result.toString());
                }
                if (mySource.position() == mySource.limit()) {
                    result = encoder.encode(mySource, myTarget, true);
                    if (result.isError()) {
                        errln("Test small output buffers while encoding "+result.toString());
                    }
                    
                    myTarget.limit(myTarget.capacity());
                    result = encoder.flush(myTarget);
                    if (result.isError()) {
                        errln("Test small output buffers while encoding "+result.toString());
                    }
                    break;
                }
            }
            if (!equals(myTarget, myGBSource)) {
                errln("Test small output buffers "+ encoding+ " From Unicode failed.");
            }
            logln("Tests on small output buffers for "+ encoding +" passed");

        }
    }
    public void TestConvertAll(/*String encoding*/) throws Exception {
        {
            try {
                decoder.reset();
                ByteBuffer mySource = ByteBuffer.wrap(getByteArray(gbSource));
                CharBuffer myTarget = decoder.decode(mySource);
                if (!equals(myTarget, uSource)) {
                    errln(
                        "--Test convertAll() "
                            + encoding
                            + " to Unicode  --FAILED");
                }
            } catch (Exception e) {
                //e.printStackTrace();
                errln(e.getMessage());
            }
        }
        {
            try {
                encoder.reset();
                CharBuffer mySource = CharBuffer.wrap(uSource);
                ByteBuffer myTarget = encoder.encode(mySource);
                if (!equals(myTarget, gbSource)) {
                    errln(
                        "--Test convertAll() "
                            + encoding
                            + " to Unicode  --FAILED");
                }
            } catch (Exception e) {
                //e.printStackTrace();
                errln("encoder.encode() failed "+ e.getMessage()+" "+e.toString());
            }
        }

    }
    public void TestString() {
        try {
            {
                String source = new String(uSource);
                byte[] target = source.getBytes(encoding);
                if (!equals(target, getByteArray(gbSource))) {
                    errln("encode using string API failed");
                }
            }
            {

                String target = new String(getByteArray(gbSource), encoding);
                if (!equals(uSource, target.toCharArray())) {
                    errln("decode using string API failed");
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            errln(e.getMessage());
        }
    }

    public void TestFromUnicode(/*String encoding*/) throws Exception {
        
        logln("Loaded Charset: " + charset.getClass().toString());
        logln("Loaded CharsetEncoder: " + encoder.getClass().toString());
        logln("Loaded CharsetDecoder: " + decoder.getClass().toString());
        
        ByteBuffer myTarget = ByteBuffer.allocate(gbSource.length);
        logln("Created ByteBuffer of length: " + uSource.length);
        CharBuffer mySource = CharBuffer.wrap(uSource);
        logln("Wrapped ByteBuffer with CharBuffer  ");
        encoder.reset();
        logln("Test Unicode to " + encoding );
        encoder.encode(mySource, myTarget, true);
        if (!equals(myTarget, gbSource)) {
            errln("--Test Unicode to " + encoding + ": FAILED");
        } 
        logln("Test Unicode to " + encoding +" passed");
        smBufEncode(encoder, encoding);
    }

    public void TestToUnicode(/*String encoding*/ ) throws Exception {
        
        logln("Loaded Charset: " + charset.getClass().toString());
        logln("Loaded CharsetEncoder: " + encoder.getClass().toString());
        logln("Loaded CharsetDecoder: " + decoder.getClass().toString());
        
        CharBuffer myTarget = CharBuffer.allocate(uSource.length);
        ByteBuffer mySource = ByteBuffer.wrap(getByteArray(gbSource));
        decoder.reset();
        CoderResult result = decoder.decode(mySource, myTarget, true);
        if (result.isError()) {
            errln("Test ToUnicode -- FAILED");
        }
        if (!equals(myTarget, uSource)) {
            errln("--Test " + encoding + " to Unicode :FAILED");
        }
        smBufDecode(decoder, encoding);
    }

    public static byte[] getByteArray(char[] source) {
        byte[] target = new byte[source.length];
        int i = source.length;
        for (; --i >= 0;) {
            target[i] = (byte) source[i];
        }
        return target;
    }
    private void smBufCharset(Charset charset) {
        try {
            ByteBuffer gbTarget = charset.encode(CharBuffer.wrap(uSource));
            CharBuffer uTarget =
                charset.decode(ByteBuffer.wrap(getByteArray(gbSource)));

            if (!equals(uTarget, uSource)) {
                errln("Test " + charset.toString() + " to Unicode :FAILED");
            }
            if (!equals(gbTarget, gbSource)) {
                errln("Test " + charset.toString() + " from Unicode :FAILED");
            }
        } catch (Exception ex) {
            errln("Encountered exception in smBufCharset");
        }
    }

    public void TestMultithreaded() throws Exception {
        final Charset cs = Charset.forName(encoding);
        if (cs == charset) {
            errln("The objects are equal");
        }
        smBufCharset(cs);
        try {
            final Thread t1 = new Thread() {
                public void run() {
                    // commented out since the mehtods on
                    // Charset API are supposed to be thread
                    // safe ... to test it we dont sync
            
                    // synchronized(charset){
                   while (!interrupted()) {
                        try {
                            smBufCharset(cs);
                        } catch (UnsupportedCharsetException ueEx) {
                            errln(ueEx.toString());
                        }
                    }

                    // }
                }
            };
            final Thread t2 = new Thread() {
                public void run() {
                        // synchronized(charset){
                    while (!interrupted()) {
                        try {
                            smBufCharset(cs);
                        } catch (UnsupportedCharsetException ueEx) {
                            errln(ueEx.toString());
                        }
                    }

                    //}
                }
            };
            t1.start();
            t2.start();
            int i = 0;
            for (;;) {
                if (i > 1000000000) {
                    try {
                        t1.interrupt();
                    } catch (Exception e) {
                    }
                    try {
                        t2.interrupt();
                    } catch (Exception e) {
                    }
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void TestSynchronizedMultithreaded() throws Exception {
        // Methods on CharsetDecoder and CharsetEncoder classes
        // are inherently unsafe if accessed by multiple concurrent
        // thread so we synchronize them
        final Charset charset = Charset.forName(encoding);
        final CharsetDecoder decoder = charset.newDecoder();
        final CharsetEncoder encoder = charset.newEncoder();
        try {
            final Thread t1 = new Thread() {
                public void run() {
                    while (!interrupted()) {
                        try {
                            synchronized (encoder) {
                                smBufEncode(encoder, encoding);
                            }
                            synchronized (decoder) {
                                smBufDecode(decoder, encoding);
                            }
                        } catch (UnsupportedCharsetException ueEx) {
                            errln(ueEx.toString());
                        }
                    }

                }
            };
            final Thread t2 = new Thread() {
                public void run() {
                    while (!interrupted()) {
                        try {
                            synchronized (encoder) {
                                smBufEncode(encoder, encoding);
                            }
                            synchronized (decoder) {
                                smBufDecode(decoder, encoding);
                            }
                        } catch (UnsupportedCharsetException ueEx) {
                            errln(ueEx.toString());
                        }
                    }
                }
            };
            t1.start();
            t2.start();
            int i = 0;
            for (;;) {
                if (i > 1000000000) {
                    try {
                        t1.interrupt();
                    } catch (Exception e) {
                    }
                    try {
                        t2.interrupt();
                    } catch (Exception e) {
                    }
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            throw e;
        }

    }
    public void TestJB4897(){
        CharsetProviderICU provider = new CharsetProviderICU();
        Charset charset = provider.charsetForName("x-abracadabra");  
        if(charset!=null && charset.canEncode()== true){
            errln("provider.charsetForName() does not validate the charset names" );
        }
    }

    public void TestJB5027() {
        CharsetProviderICU provider= new CharsetProviderICU();

        Charset fake = provider.charsetForName("doesNotExist");
        if(fake != null){
            errln("\"doesNotExist\" returned " + fake);
        }
        Charset xfake = provider.charsetForName("x-doesNotExist");
        if(xfake!=null){
            errln("\"x-doesNotExist\" returned " + xfake);
        }
    }
    //test to make sure that number of aliases and canonical names are in the charsets that are in
    public void TestAllNames() {
        CharsetProviderICU provider= new CharsetProviderICU();
        Map javaMap = new HashMap();
        provider.putCharsets(javaMap);
        String[] available = NativeConverter.getAvailable();
        for(int i=0; i<available.length;i++){
            String canon  = NativeConverter.getICUCanonicalName(available[i]);
            // ',' is not allowed by Java's charset name checker
            if(canon.indexOf(',')>=0){
                continue;
            }
            Charset cs = (Charset)javaMap.get(available[i]);
            Object[] javaAliases =  cs.aliases().toArray();
            //seach for ICU canonical name in javaAliases
            boolean inAliasList = false;
            for(int j=0; j<javaAliases.length; j++){
                String java = (String) javaAliases[j];
                if(java.equals(canon)){
                    logln("javaAlias: " + java + " canon: " + canon);
                    inAliasList = true;
                }
            }
            if(inAliasList == false){
                errln("Could not find ICU canonical name: "+canon+ " for java canonical name: "+ available[i]+ " "+ i);
            }
        }
    }
    public void TestDecoderImplFlush() {
        CharsetProviderICU provider = new CharsetProviderICU();
        Charset ics = provider.charsetForName("UTF-16");
        Charset jcs = Charset.forName("UTF-16"); // Java's UTF-16 charset
        execDecoder(jcs);
        execDecoder(ics);
    }
    public void TestEncoderImplFlush() {
        CharsetProviderICU provider = new CharsetProviderICU();
        Charset ics = provider.charsetForName("ISO-8859-1");
        Charset jcs = Charset.forName("ISO-8859-1"); // Java's UTF-16 charset
        execEncoder(jcs);
        execEncoder(ics);
    }
    private void execDecoder(Charset cs){
        CharsetDecoder decoder = cs.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer out = CharBuffer.allocate(10);
        CoderResult result = decoder.decode(ByteBuffer.wrap(new byte[] { -1,
                -2, 32, 0, 98 }), out, false);
        result = decoder.decode(ByteBuffer.wrap(new byte[] { 98 }), out, true);

        logln(cs.getClass().toString()+ ":" +result.toString());
        try {
            result = decoder.flush(out);
            logln(cs.getClass().toString()+ ":" +result.toString());
        } catch (Exception e) {
            errln(e.getMessage()+" "+cs.getClass().toString());
        }
    }
    private void execEncoder(Charset cs){
        CharsetEncoder encoder = cs.newEncoder();
        encoder.onMalformedInput(CodingErrorAction.REPORT);
        encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        ByteBuffer out = ByteBuffer.allocate(10);
        CoderResult result = encoder.encode(CharBuffer.wrap(new char[] { '\uFFFF',
                '\u2345', 32, 98 }), out, false);
        logln(cs.getClass().toString()+ ":" +result.toString());
        result = encoder.encode(CharBuffer.wrap(new char[] { 98 }), out, true);

        logln(cs.getClass().toString()+ ":" +result.toString());
        try {
            result = encoder.flush(out);
            logln(cs.getClass().toString()+ ":" +result.toString());
        } catch (Exception e) {
            errln(e.getMessage()+" "+cs.getClass().toString());
        }
    }
    public void TestDecodeMalformed() {
        CharsetProviderICU provider = new CharsetProviderICU();
        Charset ics = provider.charsetForName("UTF-16BE");
        //Use SUN's charset
        Charset jcs = Charset.forName("UTF-16");
        CoderResult ir = execMalformed(ics);
        CoderResult jr = execMalformed(jcs);
        if(ir!=jr){
            errln("ICU's decoder did not return the same result as Sun. ICU: "+ir.toString()+" Sun: "+jr.toString());
        }
    }
    private CoderResult execMalformed(Charset cs){
        CharsetDecoder decoder = cs.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        ByteBuffer in = ByteBuffer.wrap(new byte[] { 0x00, 0x41, 0x00, 0x42, 0x01 });
        CharBuffer out = CharBuffer.allocate(3);
        return decoder.decode(in, out, true);
    }
    
    public void TestJavaUTF16Decoder(){
        CharsetProviderICU provider = new CharsetProviderICU();
        Charset ics = provider.charsetForName("UTF-16BE");
        //Use SUN's charset
        Charset jcs = Charset.forName("UTF-16");
        Exception ie = execConvertAll(ics);
        Exception je = execConvertAll(jcs);
        if(ie!=je){
            errln("ICU's decoder did not return the same result as Sun. ICU: "+ie.toString()+" Sun: "+je.toString());
        }
    }
    private Exception execConvertAll(Charset cs){
        ByteBuffer in = ByteBuffer.allocate(400);
        int i=0;
        while(in.position()!=in.capacity()){
            in.put((byte)0xD8);
            in.put((byte)i);
            in.put((byte)0xDC);
            in.put((byte)i);
            i++;
        }
        in.limit(in.position());
        in.position(0);
        CharsetDecoder decoder = cs.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        try{
            CharBuffer out = decoder.decode(in);
        }catch ( Exception ex){
            return ex;
        }
        return null;
    }
}
