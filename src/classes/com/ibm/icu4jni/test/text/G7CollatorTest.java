/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: 
*  /usr/cvs/icu4j/icu4j/src/com/ibm/icu/test/text/G7CollatorTest.java,v $ 
* $Date: 2001/03/23 19:43:17 $ 
* $Revision: 1.6 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.test.text;

import java.util.Locale;
import com.ibm.icu4jni.text.Collator;
import com.ibm.icu4jni.text.RuleBasedCollator;
import com.ibm.icu4jni.test.TestFmwk;

/**
* Testing class for collation with 7 different locales
* Mostly following the test cases for ICU
* @author Syn Wee Quek
* @since jan 23 2001
*/
public final class G7CollatorTest extends TestFmwk
{ 
  
  // constructor ===================================================
  
  /**
  * Constructor
  */
  public G7CollatorTest() throws Exception
  {
    //m_collator_ = Collator.getInstance(new Locale("tr", ""));
  }
  
  // public methods ================================================

  /**
  * Test with all 7 locales
  * @exception thrown when error occurs while setting strength
  */
  public void TestLocales() throws Exception
  {
    RuleBasedCollator collator,
                         testcollator;
    for (int i = 0; i < LOCALES_.length; i ++)
    {
      collator = (RuleBasedCollator)Collator.getInstance(LOCALES_[i]);
      String rules = collator.getRules();
      if (rules != null && rules.length() != 0)
      {
        testcollator = new RuleBasedCollator(rules);
        
        for (int j = 0; j < FIXED_TEST_COUNT_; j ++)
          for (int k = j + 1; k < FIXED_TEST_COUNT_; k ++)
            CollatorTest.doTest(this, testcollator, 
                  SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[i][j]], 
                  SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[i][k]], 
                  Collator.RESULT_LESS);
      }
    }
  }

  /**
  * Test default rules + addition rules.
  * @exception thrown when error occurs while setting strength
  */
  public void TestRules1() throws Exception
  {
    Collator collator = Collator.getInstance(Locale.ENGLISH);
    String rules = ((RuleBasedCollator)collator).getRules();
    String newrules = rules + " & Z < p, P";
    RuleBasedCollator newcollator = new RuleBasedCollator(newrules);

    for (int j = 0; j < FIXED_TEST_COUNT_; j ++)
      for (int k = j + 1; k < FIXED_TEST_COUNT_; k ++)
        CollatorTest.doTest(this, newcollator, 
                       SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[8][j]], 
                       SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[8][k]], 
                       Collator.RESULT_LESS);
  }
  
  /**
  * Test default rules + addition rules.
  * @exception thrown when error occurs while setting strength
  */
  public void TestRules2() throws Exception
  {
    Collator collator = Collator.getInstance(Locale.ENGLISH);
    String rules = ((RuleBasedCollator)collator).getRules();
    String newrules = rules + "& C < ch , cH, Ch, CH";
    
    RuleBasedCollator newcollator = new RuleBasedCollator(newrules);

    for (int i = 0; i < TOTAL_TEST_COUNT_; i ++)
      for (int j = i + 1; j < TOTAL_TEST_COUNT_; j++)
        CollatorTest.doTest(this, newcollator, 
                       SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[9][i]], 
                       SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[9][j]], 
                       Collator.RESULT_LESS);
  }
  
  /**
  * Test default rules + addition rules.
  * @exception thrown when error occurs while setting strength
  */
  public void TestRules3() throws Exception
  {
    Collator collator = Collator.getInstance(Locale.ENGLISH);
    String rules = ((RuleBasedCollator)collator).getRules();
    String newrules = rules + 
             "& Question'-'mark ; '?' & Hash'-'mark ; '#' & Ampersand ; '&'";
    
    RuleBasedCollator newcollator = new RuleBasedCollator(newrules);

    for (int i = 0; i < TOTAL_TEST_COUNT_; i ++)
      for (int j = i + 1; j < TOTAL_TEST_COUNT_; j++)
        CollatorTest.doTest(this, newcollator, 
                       SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[10][i]], 
                       SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[10][j]], 
                       Collator.RESULT_LESS);
  }
  
  /**
  * Test default rules + addition rules.
  * @exception thrown when error occurs while setting strength
  */
  public void TestRules4() throws Exception
  {
    Collator collator = Collator.getInstance(Locale.ENGLISH);
    String rules = ((RuleBasedCollator)collator).getRules();
    String newrules = rules + 
             " & aa ; a'-' & ee ; e'-' & ii ; i'-' & oo ; o'-' & uu ; u'-' ";
    
    RuleBasedCollator newcollator = new RuleBasedCollator(newrules);

    for (int i = 0; i < TOTAL_TEST_COUNT_; i ++)
      for (int j = i + 1; j < TOTAL_TEST_COUNT_; j++)
        CollatorTest.doTest(this, newcollator, 
                       SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[11][i]], 
                       SOURCE_TEST_CASE_[EXPECTED_TEST_RESULT_[11][j]], 
                       Collator.RESULT_LESS);
  }
  
  // private variables =============================================
  
  /**
  * Constant test number
  */
  private int FIXED_TEST_COUNT_ = 15;
  
  /**
  * Constant test number
  */
  private int TOTAL_TEST_COUNT_ = 30;
  
  /**
  * List of 7 locales to be tested
  */
  private final Locale LOCALES_[] = {Locale.US, Locale.UK, Locale.CANADA,
                                   Locale.FRANCE, Locale.CANADA_FRENCH,
                                   Locale.GERMAN, Locale.ITALY, 
                                   Locale.JAPAN};
  
  /**
  * Source strings for testing
  */
  private static final String SOURCE_TEST_CASE_[] = 
  {
    "\u0062\u006c\u0061\u0062\u006b\u0062\u0069\u0072\u0064\u0073",                    /* 9 */
    "\u0050\u0061\u0074",                                                    /* 1 */
    "\u0070\u00E9\u0063\u0068\u00E9",                                    /* 2 */
    "\u0070\u00EA\u0063\u0068\u0065",                           /* 3 */
    "\u0070\u00E9\u0063\u0068\u0065\u0072",            /* 4 */
    "\u0070\u00EA\u0063\u0068\u0065\u0072",            /* 5 */
    "\u0054\u006f\u0064",                                                    /* 6 */
    "\u0054\u00F6\u006e\u0065",                                            /* 7 */
    "\u0054\u006f\u0066\u0075",                                   /* 8 */
    "\u0062\u006c\u0061\u0062\u006b\u0062\u0069\u0072\u0064",                                    /* 12 */
    "\u0054\u006f\u006e",                                                    /* 10 */
    "\u0050\u0041\u0054",                                                    /* 11 */
    "\u0062\u006c\u0061\u0062\u006b\u002d\u0062\u0069\u0072\u0064",                /* 13 */
    "\u0062\u006c\u0061\u0062\u006b\u002d\u0062\u0069\u0072\u0064\u0073",  /* 0 */
    "\u0070\u0061\u0074",                                                    /* 14 */
    "\u0063\u007a\u0061\u0072",                                 /* 15 */
    "\u0063\u0068\u0075\u0072\u006f" ,                  /* 16 */
    "\u0063\u0061\u0074",                                                    /* 17 */ 
    "\u0064\u0061\u0072\u006e" ,                                 /* 18 */
    "\u003f",                                                                                /* 19 */
    "\u0071\u0075\u0069\u0063\u006b" ,                  /* 20 */
    "\u0023" ,                                                  /* 21 */
    "\u0026" ,                                                  /* 22 */
    "\u0061\u002d\u0072\u0064\u0076\u0061\u0072\u006b",                                                        /* 24 */
    "\u0061\u0061\u0072\u0064\u0076\u0061\u0072\u006b",                                                        /* 23 */
    "\u0061\u0062\u0062\u006f\u0074",                   /* 25 */
    "\u0063\u006f\u002d\u0070",                                 /* 27 */
    "\u0063\u006f\u0070",                                                /* 28 */
    "\u0063\u006f\u006f\u0070",                                 /* 26 */
    "\u007a\u0065\u0062\u0072\u0061"                    /* 29 */
  };

  /**
  * Comparison result corresponding to above source and target cases
  */
  private final int EXPECTED_TEST_RESULT_[][] = 
  {
    { 12, 13, 9, 0, 14, 1, 11, 2, 3, 4, 5, 6, 8, 10, 7, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, /* en_US */
    { 12, 13, 9, 0, 14, 1, 11, 2, 3, 4, 5, 6, 8, 10, 7, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, /* en_GB */
    { 12, 13, 9, 0, 14, 1, 11, 2, 3, 4, 5, 6, 8, 10, 7, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, /* en_CA */
    { 12, 13, 9, 0, 14, 1, 11, 3, 2, 4, 5, 6, 8, 10, 7, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, /* fr_FR */
    { 12, 13, 9, 0, 14, 1, 11, 3, 2, 4, 5, 6, 8, 10, 7, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, /* fr_CA */
    { 12, 13, 9, 0, 14, 1, 11, 2, 3, 4, 5, 6, 8, 10, 7, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, /* de_DE */
    { 12, 13, 9, 0, 14, 1, 11, 2, 3, 4, 5, 6, 8, 10, 7, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, /* it_IT */
    { 12, 13, 9, 0, 14, 1, 11, 2, 3, 4, 5, 6, 8, 10, 7, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, /* ja_JP */
    /* new table collation with rules "& Z < p, P"  loop to FIXEDTESTSET */
    { 12, 13, 9, 0, 6, 8, 10, 7, 14, 1, 11, 2, 3, 4, 5, 31, 31, 31, 31, 31, 
      31, 31, 31, 31, 31, 31, 31, 31, 31, 31 }, 
    /* new table collation with rules "& C < ch , cH, Ch, CH " loop to TOTALTESTSET */
    { 19, 22, 21, 23, 24, 25, 12, 13, 9, 0, 17, 26, 28, 27, 15, 16, 18, 14, 
      1, 11, 2, 3, 4, 5, 20, 6, 8, 10, 7, 29 },
    /* new table collation with rules "& Question-mark ; ? & Hash-mark ; # & Ampersand ; '&'  " loop to TOTALTESTSET */
    { 23, 24, 25, 22, 12, 13, 9, 0, 17, 16, 26, 28, 27, 15, 18, 21, 14, 1, 
      11, 2, 3, 4, 5, 19, 20, 6, 8, 10, 7, 29 },
    /* analogous to Japanese rules " & aa ; a- & ee ; e- & ii ; i- & oo ; o- & uu ; u- " */  /* loop to TOTALTESTSET */
    { 19, 22, 21, 24, 23, 25, 12, 13, 9, 0, 17, 16, 28, 26, 27, 15, 18, 14, 
      1, 11, 2, 3, 4, 5, 20, 6, 8, 10, 7, 29 }                                      
  };
}

