/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/test/text/CollatorTest.java,v $ 
* $Date: 2001/03/23 19:43:17 $ 
* $Revision: 1.5 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.test.text;

import com.ibm.icu4jni.text.Collator;
import com.ibm.icu4jni.text.CollationKey;
import com.ibm.icu4jni.test.TestFmwk;

/**
* Testing class for Collator
* Mostly following the test cases for ICU
* @author Syn Wee Quek
* @since jan 23 2001
*/
public final class CollatorTest
{ 
  // public methods ================================================
  
  /**
  * Testing rule collation
  * @param test object
  * @param collator to test with
  * @param source test case
  * @param target test case
  * @param result expected
  */
  public static void doTest(TestFmwk test, Collator collator, String source, 
                            String target, int result)
  {
    int compareresult = collator.compare(source, target);
    
    if (compareresult != result)
      test.errln("Failed : Expected result for " + source + " and " + target 
                 + " string comparison is " + result);
    
    CollationKey sortkey1 = collator.getCollationKey(source),
                 sortkey2 = collator.getCollationKey(target);
                 
    if (sortkey1 == null)
    {
      test.errln("Failed : Sort key generation for " + source);
      return;
    }
    if (sortkey2 == null)
    {
      test.errln("Failed : Sort key generation for " + target);
      return;
    }

    compareresult = sortkey1.compareTo(sortkey2);
    
    if (compareresult != result)
      test.errln("Failed : Expected result for " + source + " and " + target 
                 + " sort key comparison is " + result);
  }
}

