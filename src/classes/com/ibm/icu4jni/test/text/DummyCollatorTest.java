/**
*******************************************************************************
* Copyright (C) 1996-2005, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
*******************************************************************************
*/

package com.ibm.icu4jni.test.text;

import java.util.Locale;
import com.ibm.icu4jni.text.Collator;
import com.ibm.icu4jni.text.RuleBasedCollator;
import com.ibm.icu4jni.text.CollationAttribute;
import com.ibm.icu4jni.test.TestFmwk;

/**
* Testing class for Dummy collator
* Mostly following the test cases for ICU
* @author Syn Wee Quek
* @since jan 23 2001
*/ 
public final class DummyCollatorTest extends TestFmwk
{ 
  
  // constructor ===================================================
  
  /**
  * Constructor
  */
  public DummyCollatorTest() throws Exception
  {
    m_collator_ = Collator.getInstance(Locale.ENGLISH);
  }
  
  // public methods ================================================

  /**
  * Test with primary collation strength
  */
  public void TestPrimary() throws Exception
  {
    String rules = "& C < ch, cH, Ch, CH & Five, 5 & Four, 4 & one, 1 & " +
                                         "Ampersand; '&' & Two, 2 ";
    
    Collator coll = new RuleBasedCollator(rules, 
                                          CollationAttribute.VALUE_OFF, 
                                          CollationAttribute.VALUE_PRIMARY);
    /* problem in strcollinc for unfinshed contractions */
    coll.setAttribute(CollationAttribute.NORMALIZATION_MODE, 
                      CollationAttribute.VALUE_ON);
    
    for (int i = 17; i < 26 ; i ++) {
      CollatorTest.doTest(this, coll, SOURCE_TEST_CASE_[i], 
                          TARGET_TEST_CASE_[i], EXPECTED_TEST_RESULT_[i]);
    }
  }

  /**
  * Test with secondary collation strength
  */
  public void TestSecondary() throws Exception
  {
    String rules = "& C < ch, cH, Ch, CH & Five, 5 & Four, 4 & " +
                              "one, 1 & Ampersand; '&' & Two, 2 ";
    m_collator_.setStrength(CollationAttribute.VALUE_SECONDARY);
    Collator coll = new RuleBasedCollator(rules, 
                                          CollationAttribute.VALUE_OFF, 
                                          CollationAttribute.VALUE_SECONDARY);
    for (int i = 26; i < 34 ; i ++) {
      CollatorTest.doTest(this, coll, SOURCE_TEST_CASE_[i], 
                          TARGET_TEST_CASE_[i], EXPECTED_TEST_RESULT_[i]);
    }
  }
  
  /**
  * Test with tertiary collation strength
  */
  public void TestTertiary() throws Exception
  {
    String rules = "& C < ch, cH, Ch, CH & Five, 5 & Four, 4 & one," +
                          " 1 & Ampersand; '&' & Two, 2 ";
    Collator coll = new RuleBasedCollator(rules, 
                                  CollationAttribute.VALUE_OFF, 
                                  CollationAttribute.VALUE_TERTIARY);
    for (int i = 0; i < 17 ; i ++) {
      CollatorTest.doTest(this, coll, SOURCE_TEST_CASE_[i], 
                          TARGET_TEST_CASE_[i], EXPECTED_TEST_RESULT_[i]);
    }
  }
  
  /**
  * Miscellaneous test
  */
  public void TestMiscellaneous() throws Exception
  {
    m_collator_.setStrength(CollationAttribute.VALUE_TERTIARY);
    int size = MISCELLANEOUS_TEST_CASE_.length - 1;
    for (int i = 0; i < size; i ++) {
      for (int j = i + 1; j < MISCELLANEOUS_TEST_CASE_.length; j ++) {
        CollatorTest.doTest(this, m_collator_, MISCELLANEOUS_TEST_CASE_[i], 
                            MISCELLANEOUS_TEST_CASE_[j], 
                            Collator.RESULT_LESS);
      }
    }
  }
  
  // private variables =============================================
  
  /**
  * RuleBasedCollator for testing
  */
  private Collator m_collator_;
  
  /**
  * Source test cases
  */
  private final String SOURCE_TEST_CASE_[] = 
  {
    "\u0061\u0062\u0027\u0063",
    "\u0063\u006f\u002d\u006f\u0070",
    "\u0061\u0062",
    "\u0061\u006d\u0070\u0065\u0072\u0073\u0061\u0064",
    "\u0061\u006c\u006c",
    "\u0066\u006f\u0075\u0072",
    "\u0066\u0069\u0076\u0065",
    "\u0031",
    "\u0031",
    "\u0031",                                            /*  10 */
    "\u0032",
    "\u0032",
    "\u0048\u0065\u006c\u006c\u006f",
    "\u0061\u003c\u0062",
    "\u0061\u003c\u0062",
    "\u0061\u0063\u0063",
    "\u0061\u0063\u0048\u0063",  /*  simple test */
    "\u0070\u00EA\u0063\u0068\u0065",
    "\u0061\u0062\u0063",
    "\u0061\u0062\u0063",                                  /*  20 */
    "\u0061\u0062\u0063",
    "\u0061\u0062\u0063",
    "\u0061\u0062\u0063",
    "\u0061\u00E6\u0063",
    "\u0061\u0063\u0048\u0063",  /*  primary test */
    "\u0062\u006c\u0061\u0063\u006b",
    "\u0066\u006f\u0075\u0072",
    "\u0066\u0069\u0076\u0065",
    "\u0031",
    "\u0061\u0062\u0063",                                        /*  30 */
    "\u0061\u0062\u0063",                                  
    "\u0061\u0062\u0063\u0048",
    "\u0061\u0062\u0063",
    "\u0061\u0063\u0048\u0063",                              /*  34 */
    "\u0061\u0063\u0065\u0030",
    "\u0031\u0030",
    "\u0070\u00EA\u0030"                                    /* 37     */
};

  /**
  * Target test cases
  */
  private final String TARGET_TEST_CASE_[] = 
  {
    "\u0061\u0062\u0063\u0027",
    "\u0043\u004f\u004f\u0050",
    "\u0061\u0062\u0063",
    "\u0026",
    "\u0026",
    "\u0034",
    "\u0035",
    "\u006f\u006e\u0065",
    "\u006e\u006e\u0065",
    "\u0070\u006e\u0065",                                  /*  10 */
    "\u0074\u0077\u006f",
    "\u0075\u0077\u006f",
    "\u0068\u0065\u006c\u006c\u004f",
    "\u0061\u003c\u003d\u0062",
    "\u0061\u0062\u0063",
    "\u0061\u0043\u0048\u0063",
    "\u0061\u0043\u0048\u0063",  /*  simple test */
    "\u0070\u00E9\u0063\u0068\u00E9",
    "\u0061\u0062\u0063",
    "\u0061\u0042\u0043",                                  /*  20 */
    "\u0061\u0062\u0063\u0068",
    "\u0061\u0062\u0064",
    "\u00E4\u0062\u0063",
    "\u0061\u00C6\u0063",
    "\u0061\u0043\u0048\u0063",  /*  primary test */
    "\u0062\u006c\u0061\u0063\u006b\u002d\u0062\u0069\u0072\u0064",
    "\u0034",
    "\u0035",
    "\u006f\u006e\u0065",
    "\u0061\u0062\u0063",
    "\u0061\u0042\u0063",                                  /*  30 */
    "\u0061\u0062\u0063\u0068",
    "\u0061\u0062\u0064",
    "\u0061\u0043\u0048\u0063",                                /*  34 */
    "\u0061\u0063\u0065\u0030",
    "\u0031\u0030",
    "\u0070\u00EB\u0030"                                    /* 37 */
};

  /**
  * Source test cases
  */
  private final String MISCELLANEOUS_TEST_CASE_[] = 
  {
    "\u0061",
    "\u0041",
    "\u00e4",
    "\u00c4",
    "\u0061\u0065",
    "\u0061\u0045",
    "\u0041\u0065",
    "\u0041\u0045",
    "\u00e6",
    "\u00c6",
    "\u0062",
    "\u0063",
    "\u007a"
  };
 
  /**
  * Test result expected
  */
  private final static int EXPECTED_TEST_RESULT_[] = 
  {
    Collator.RESULT_LESS,
    Collator.RESULT_LESS, /*Collator.RESULT_GREATER,*/
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    Collator.RESULT_GREATER,
    Collator.RESULT_GREATER,
    Collator.RESULT_LESS,                                     /*  10 */
    Collator.RESULT_GREATER,
    Collator.RESULT_LESS,
    Collator.RESULT_GREATER,
    Collator.RESULT_GREATER,
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    /*  test primary > 17 */
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,                                    /*  20 */
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,
    Collator.RESULT_LESS,
    /*  test secondary > 26 */
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,                                    /*  30 */
    Collator.RESULT_EQUAL,
    Collator.RESULT_LESS,
    Collator.RESULT_EQUAL,                                     /*  34 */
    Collator.RESULT_EQUAL,
    Collator.RESULT_EQUAL,
    Collator.RESULT_LESS    
  };
}

