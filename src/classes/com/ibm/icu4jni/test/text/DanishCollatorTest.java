/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: 
*  /usr/cvs/icu4j/icu4j/src/com/ibm/icu/test/text/DanishCollatorTest.java,v $ 
* $Date: 2001/03/23 19:43:17 $ 
* $Revision: 1.5 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.test.text;

import java.util.Locale;
import com.ibm.icu4jni.text.Collator;
import com.ibm.icu4jni.text.CollationAttribute;
import com.ibm.icu4jni.test.TestFmwk;

/**
* Testing class for danish collator
* Mostly following the test cases for ICU
* @author Syn Wee Quek
* @since jan 23 2001
*/
public final class DanishCollatorTest extends TestFmwk 
{ 
  
  // constructor ===================================================
  
  /**
  * Constructor
  */
  public DanishCollatorTest() throws Exception
  {
    m_collator_ = Collator.getInstance(new Locale("da", "DK"));
  }
  
  // public methods ================================================

  /**
  * Test with primary collation strength
  * @exception thrown when error occurs while setting strength
  */
  public void TestPrimary() throws Exception
  {
    m_collator_.setStrength(CollationAttribute.VALUE_PRIMARY);
    for (int i = 5; i < 8; i ++)
      CollatorTest.doTest(this, m_collator_, SOURCE_TEST_CASE_[i], 
                          TARGET_TEST_CASE_[i], EXPECTED_TEST_RESULT_[i]);
  }

  /**
  * Test with tertiary collation strength
  * @exception thrown when error occurs while setting strength
  */
  public void TestTertiary() throws Exception
  {
    m_collator_.setStrength(CollationAttribute.VALUE_TERTIARY);
    m_collator_.setAttribute(CollationAttribute.NORMALIZATION_MODE,
                             CollationAttribute.VALUE_ON);
    
    for (int i = 0; i < 5 ; i ++)
      CollatorTest.doTest(this, m_collator_, SOURCE_TEST_CASE_[i], 
                          TARGET_TEST_CASE_[i], EXPECTED_TEST_RESULT_[i]);
    
    for (int i = 0; i < 53; i ++)
      for (int j = i + 1; j < 54; j ++)
        CollatorTest.doTest(this, m_collator_, BUGS_TEST_CASE_[i], 
                            BUGS_TEST_CASE_[j], Collator.RESULT_LESS);
        
    for (int i = 0; i < 52; i ++)
      for (int j = i + 1; j < 53; j ++)
        CollatorTest.doTest(this, m_collator_, NT_TEST_CASE_[i], 
                            NT_TEST_CASE_[j], Collator.RESULT_LESS);
  }
  
  // private variables =============================================
  
  /**
  * RuleBasedCollator for testing
  */
  private Collator m_collator_;
  
  /**
  * Source strings for testing
  */
  private static final String SOURCE_TEST_CASE_[] = 
  {
    "\u004C\u0075\u0063",
    "\u006C\u0075\u0063\u006B",
    "\u004C\u00FC\u0062\u0065\u0063\u006B",
    "\u004C\u00E4\u0076\u0069",
    "\u004C\u00F6\u0077\u0077",
    "\u004C\u0076\u0069",
    "\u004C\u00E4\u0076\u0069",
    "\u004C\u00FC\u0062\u0065\u0063\u006B"
  };

  /**
  * Target strings for testing
  */
  private final String TARGET_TEST_CASE_[] = 
  {
    "\u006C\u0075\u0063\u006B",
    "\u004C\u00FC\u0062\u0065\u0063\u006B",
    "\u006C\u0079\u0062\u0065\u0063\u006B",
    "\u004C\u00F6\u0077\u0065",
    "\u006D\u0061\u0073\u0074",
    "\u004C\u0077\u0069",
    "\u004C\u00F6\u0077\u0069",
    "\u004C\u0079\u0062\u0065\u0063\u006B"
  };

  /**
  * Comparison result corresponding to above source and target cases
  */
  private final int EXPECTED_TEST_RESULT_[] = 
  {
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    Collator.RESULT_GREATER,
    Collator.RESULT_LESS,
    Collator.RESULT_LESS,
    /* test primary > 5*/
    Collator.RESULT_EQUAL,
    Collator.RESULT_LESS,
    Collator.RESULT_EQUAL
  };

  /**
  * Bug testing data set.
  * Internet data list.
  */
  private final String BUGS_TEST_CASE_[] = 
  {
    "\u0041\u002F\u0053",
    "\u0041\u004E\u0044\u0052\u0045",
    "\u0041\u004E\u0044\u0052\u00C9",
    "\u0041\u004E\u0044\u0052\u0045\u0041\u0053",
    "\u0041\u0053",
    "\u0043\u0041",
    "\u00C7\u0041",
    "\u0043\u0042",
    "\u00C7\u0043",
    "\u0044\u002E\u0053\u002E\u0042\u002E",
    "\u0044\u0041",                                                                           
    "\u0044\u0042",
    "\u0044\u0053\u0042",
    "\u0044\u0053\u0043",
    "\u00D0\u0041",
    "\u00D0\u0043",
    "\u0045\u004B\u0053\u0054\u0052\u0041\u005F\u0041\u0052\u0042\u0045\u004A\u0044\u0045",
    "\u0045\u004B\u0053\u0054\u0052\u0041\u0042\u0055\u0044",
    "\u0048\u00D8\u0053\u0054",  
    "\u0048\u0041\u0041\u0047",                                                                 
    "\u0048\u00C5\u004E\u0044\u0042\u004F\u0047",
    "\u0048\u0041\u0041\u004E\u0044\u0056\u00C6\u0052\u004B\u0053\u0042\u0041\u004E\u004B\u0045\u004E",
    "\u006B\u0061\u0072\u006C",
    "\u004B\u0061\u0072\u006C",
    "\u004E\u0049\u0045\u004C\u0053\u0020\u004A\u00D8\u0052\u0047\u0045\u004E",
    "\u004E\u0049\u0045\u004C\u0053\u002D\u004A\u00D8\u0052\u0047\u0045\u004E",
    "\u004E\u0049\u0045\u004C\u0053\u0045\u004E",
    "\u0052\u00C9\u0045\u002C\u0020\u0041",
    "\u0052\u0045\u0045\u002C\u0020\u0042",
    "\u0052\u00C9\u0045\u002C\u0020\u004C",                                                    
    "\u0052\u0045\u0045\u002C\u0020\u0056",
    "\u0053\u0043\u0048\u0059\u0054\u0054\u002C\u0020\u0042",
    "\u0053\u0043\u0048\u0059\u0054\u0054\u002C\u0020\u0048",
    "\u0053\u0043\u0048\u00DC\u0054\u0054\u002C\u0020\u0048",
    "\u0053\u0043\u0048\u0059\u0054\u0054\u002C\u0020\u004C",
    "\u0053\u0043\u0048\u00DC\u0054\u0054\u002C\u0020\u004D",
    "\u0053\u0053",
    "\u00DF",
    "\u0053\u0053\u0041",
    "\u0053\u0054\u004F\u0052\u0045\u0020\u0056\u0049\u004C\u0044\u004D\u004F\u0053\u0045",               
    "\u0053\u0054\u004F\u0052\u0045\u004B\u00C6\u0052",
    "\u0053\u0054\u004F\u0052\u004D\u0020\u0050\u0045\u0054\u0045\u0052\u0053\u0045\u004E",
    "\u0053\u0054\u004F\u0052\u004D\u004C\u0059",
    "\u0054\u0048\u004F\u0052\u0056\u0041\u004C\u0044",
    "\u0054\u0048\u004F\u0052\u0056\u0041\u0052\u0044\u0055\u0052",
    "\u0054\u0048\u0059\u0047\u0045\u0053\u0045\u004E",
    "\u00FE\u004F\u0052\u0056\u0041\u0052\u00D0\u0055\u0052",
    "\u0056\u0045\u0053\u0054\u0045\u0052\u0047\u00C5\u0052\u0044\u002C\u0020\u0041",
    "\u0056\u0045\u0053\u0054\u0045\u0052\u0047\u0041\u0041\u0052\u0044\u002C\u0020\u0041",
    "\u0056\u0045\u0053\u0054\u0045\u0052\u0047\u00C5\u0052\u0044\u002C\u0020\u0042",                 
    "\u00C6\u0042\u004C\u0045",
    "\u00C4\u0042\u004C\u0045",
    "\u00D8\u0042\u0045\u0052\u0047",
    "\u00D6\u0042\u0045\u0052\u0047"
  };

  /**
  * Data set for testing.
  * NT data list
  */
  private final String NT_TEST_CASE_[] = 
  {
    "\u0061\u006E\u0064\u0065\u0072\u0065",
    "\u0063\u0068\u0061\u0071\u0075\u0065",
    "\u0063\u0068\u0065\u006D\u0069\u006E",
    "\u0063\u006F\u0074\u0065",
    "\u0063\u006F\u0074\u00e9",
    "\u0063\u00f4\u0074\u0065",
    "\u0063\u00f4\u0074\u00e9",
    "\u010d\u0075\u010d\u0113\u0074",
    "\u0043\u007A\u0065\u0063\u0068",
    "\u0068\u0069\u0161\u0061",
    "\u0069\u0072\u0064\u0069\u0073\u0063\u0068",
    "\u006C\u0069\u0065",
    "\u006C\u0069\u0072\u0065",
    "\u006C\u006C\u0061\u006D\u0061",
    "\u006C\u00f5\u0075\u0067",
    "\u006C\u00f2\u007A\u0061",
    "\u006C\u0075\u010d",                                
    "\u006C\u0075\u0063\u006B",
    "\u004C\u00fc\u0062\u0065\u0063\u006B",
    "\u006C\u0079\u0065",                               
    "\u006C\u00e4\u0076\u0069",
    "\u004C\u00f6\u0077\u0065\u006E",
    "\u006D\u00e0\u0161\u0074\u0061",
    "\u006D\u00ee\u0072",
    "\u006D\u0079\u006E\u0064\u0069\u0067",
    "\u004D\u00e4\u006E\u006E\u0065\u0072",
    "\u006D\u00f6\u0063\u0068\u0074\u0065\u006E",
    "\u0070\u0069\u00f1\u0061",
    "\u0070\u0069\u006E\u0074",
    "\u0070\u0079\u006C\u006F\u006E",
    "\u0161\u00e0\u0072\u0061\u006E",
    "\u0073\u0061\u0076\u006F\u0069\u0072",
    "\u0160\u0065\u0072\u0062\u016b\u0072\u0061",
    "\u0053\u0069\u0065\u0074\u006C\u0061",
    "\u015b\u006C\u0075\u0062",
    "\u0073\u0075\u0062\u0074\u006C\u0065",
    "\u0073\u0079\u006D\u0062\u006F\u006C",
    "\u0073\u00e4\u006D\u0074\u006C\u0069\u0063\u0068",
    "\u0077\u0061\u0066\u0066\u006C\u0065",
    "\u0076\u0065\u0072\u006B\u0065\u0068\u0072\u0074",
    "\u0077\u006F\u006F\u0064",
    "\u0076\u006F\u0078",                                 
    "\u0076\u00e4\u0067\u0061",
    "\u0079\u0065\u006E",
    "\u0079\u0075\u0061\u006E",
    "\u0079\u0075\u0063\u0063\u0061",
    "\u017e\u0061\u006C",
    "\u017e\u0065\u006E\u0061",
    "\u017d\u0065\u006E\u0113\u0076\u0061",
    "\u007A\u006F\u006F",
    "\u005A\u0076\u0069\u0065\u0064\u0072\u0069\u006A\u0061",
    "\u005A\u00fc\u0072\u0069\u0063\u0068",
    "\u007A\u0079\u0073\u006B",             
    "\u00e4\u006E\u0064\u0065\u0072\u0065"                  
  };
}

