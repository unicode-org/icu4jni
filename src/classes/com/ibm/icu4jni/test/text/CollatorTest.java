/**
*******************************************************************************
* Copyright (C) 1996-2000, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/test/text/CollatorTest.java,v $ 
* $Date: 2001/03/16 05:52:26 $ 
* $Revision: 1.3 $
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
public final class CollatorTest extends TestFmwk
{ 
  // private variables =============================================
  
  // constructor ===================================================
  
  /**
  * Constructor
  */
  public CollatorTest()
  {
  }
  
  // public methods ================================================
  
  /**
  * Testing rule collation
  * @param collator to test with
  * @param source test case
  * @param target test case
  * @param result expected
  */
  public void doTest(Collator collator, String source, String target, 
                     int result)
  {
    int compareResult = collator.compare(source, target);
    
    CollationKey sortkey1 = collator.getCollationKey(source),
                 sortkey2 = collator.getCollationKey(target);
                 
    if (sortkey1 == null)
    {
      errln("Failed : Sort key generation for " + source);
      return;
    }
    if (sortkey2 == null)
    {
      errln("Failed : Sort key generation for " + target);
      return;
    }

    int compareresult = sortkey1.compareTo(sortkey2);
    
    if (compareresult != result)
      errln("Failed : Expected result for " + source + " and " + target +
            " sort key comparison is " + result);
  }
  
  /**
  * Testing English rule collation
  */
  public void TestCollationEnglish()
  {
    try
    {
      EnglishCollatorTest englishcollator = 
                                             new EnglishCollatorTest(this);
  
      englishcollator.TestPrimary();
      System.out.println("Tested primary");
      englishcollator.TestSecondary();
      System.out.println("Tested secondary");
      englishcollator.TestTertiary();
      System.out.println("Tested tertiary");
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing French rule collation
  */
  public void TestCollationFrench()
  {
    try
    {
      FrenchCollatorTest frenchcollator = new FrenchCollatorTest(this);
  
      frenchcollator.TestBugs();
      frenchcollator.TestSecondary();
      frenchcollator.TestTertiary();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing German rule collation
  */
  public void TestCollationGerman()
  {
    try
    {
      GermanCollatorTest germancollator = new GermanCollatorTest(this);
  
      germancollator.TestPrimary();
      germancollator.TestTertiary();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing Danish rule collation
  */
  public void TestCollationDanish()
  {
    try
    {
      DanishCollatorTest danishcollator = new DanishCollatorTest(this);
  
      danishcollator.TestPrimary();
      danishcollator.TestTertiary();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing Spanish rule collation
  */
  public void TestCollationSpanish()
  {
    try
    {
      SpanishCollatorTest spanishcollator = 
                                            new SpanishCollatorTest(this);
  
      spanishcollator.TestPrimary();
      spanishcollator.TestTertiary();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing Finnish rule collation
  */
  public void TestCollationFinnish()
  {
    try
    {
      SpanishCollatorTest spanishcollator = 
                                            new SpanishCollatorTest(this);
  
      spanishcollator.TestPrimary();
      spanishcollator.TestTertiary();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing Kana rule collation
  */
  public void TestCollationKana()
  {
    try
    {
      KanaCollatorTest kanacollator = new KanaCollatorTest(this);
  
      kanacollator.TestTertiary();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing Turkish rule collation
  */
  public void TestCollationTurkish()
  {
    try
    {
      TurkishCollatorTest turkishcollator = 
                                          new TurkishCollatorTest(this);
      turkishcollator.TestPrimary();
      turkishcollator.TestTertiary();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing dummy collation
  */
  public void TestCollationDummy()
  {
    try
    {
      DummyCollatorTest dummycollator = new DummyCollatorTest(this);
      dummycollator.TestPrimary();
      dummycollator.TestSecondary();
      dummycollator.TestTertiary();
      dummycollator.TestMiscellaneous();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing G7 rule collation
  */
  public void TestCollationG7()
  {
    try
    {
      G7CollatorTest g7collator = new G7CollatorTest(this);
      g7collator.TestLocales();
      g7collator.TestRules1();
      g7collator.TestRules2();
      g7collator.TestRules3();
      g7collator.TestRules4();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing Monkey rule collation
  */
  public void TestCollationMonkey()
  {
    try
    {
      MonkeyCollatorTest monkeycollator = new MonkeyCollatorTest(this);
      monkeycollator.TestCollationKey();
      monkeycollator.TestCompare();
      monkeycollator.TestRules();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing collation API
  */
  public void TestCollationAPI()
  {
    try
    {
      CollatorAPITest collator = new CollatorAPITest();
      collator.TestProperties();
      collator.TestCollationKey();
      collator.TestCompare();
      collator.TestDuplicate();
      collator.TestElementIterator();
      collator.TestHashCode();
      collator.TestOperators();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing currency collation
  */
  public void TestCollationCurrency()
  {
    try
    {
      CurrencyCollatorTest test = new CurrencyCollatorTest(this);
      test.TestCurrency();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
  
  /**
  * Testing Thai rule collation
  */
  public void TestCollationThai()
  {
    try
    {
      ThaiCollatorTest test = new ThaiCollatorTest(this);
      test.TestStrings();
      test.TestOddCase();
    }
    catch (Exception e)
    {
      errln("Failed : " + e.getMessage());
    }
  }
}

