/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: 
*  /usr/cvs/icu4j/icu4j/src/com/ibm/icu/test/text/KanaCollatorTest.java,v $ 
* $Date: 2001/11/02 17:56:44 $ 
* $Revision: 1.7 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.test.text;

import java.util.Locale;
import com.ibm.icu4jni.text.Collator;
import com.ibm.icu4jni.text.CollationAttribute;
import com.ibm.icu4jni.text.Normalizer;
import com.ibm.icu4jni.test.TestFmwk;

/**
* Testing class for Kana collator
* Mostly following the test cases for ICU
* @author Syn Wee Quek
* @since jan 23 2001
*/
public final class KanaCollatorTest extends TestFmwk
{ 
  
  // constructor ===================================================
  
  /**
  * Constructor
  */
  public KanaCollatorTest() throws Exception
  {
    m_collator_ = Collator.getInstance(Locale.JAPAN);
    m_collator_.setDecomposition(Normalizer.UNORM_NFD);
  }
  
  // public methods ================================================

  /**
  * Test with tertiary collation strength
  * @exception thrown when error occurs while setting strength
  */
  public void TestTertiary() throws Exception
  {
    m_collator_.setStrength(CollationAttribute.VALUE_TERTIARY);
    m_collator_.setAttribute(CollationAttribute.NORMALIZATION_MODE, 
                             CollationAttribute.VALUE_ON);
    m_collator_.setAttribute(CollationAttribute.CASE_LEVEL, 
                             CollationAttribute.VALUE_ON);
    for (int i = 0; i < SOURCE_TEST_CASE_.length ; i ++)
      CollatorTest.doTest(this, m_collator_, SOURCE_TEST_CASE_[i], 
                          TARGET_TEST_CASE_[i], EXPECTED_TEST_RESULT_[i]);
  }
  
  /**
  * Testing base letters 
  * @exception thrown when error occurs while setting strength or 
  *            normalization mode
  */
  public void TestBase()
  {
    m_collator_.setStrength(CollationAttribute.VALUE_PRIMARY);
    for (int i = 0; i < 3 ; i ++)
      CollatorTest.doTest(this, m_collator_, BASE_CASE_[i], 
                          BASE_CASE_[i + 1], Collator.RESULT_LESS);
  }

  /** 
  * Testing plain, Daku-ten, Handaku-ten letters 
  * @exception thrown when error occurs while setting strength or 
  *            normalization mode
  */
  public void TestPlainDakutenHandakuten()
  {
    m_collator_.setStrength(CollationAttribute.VALUE_SECONDARY);
    for (int i = 0; i < 3 ; i ++)
      CollatorTest.doTest(this, m_collator_, 
                          PLAIN_DAKUTEN_HANDAKUTEN_CASE_[i], 
                          PLAIN_DAKUTEN_HANDAKUTEN_CASE_[i + 1], 
                          Collator.RESULT_LESS);
  }

  /* 
  * Test Small, Large letters
  * @exception thrown when error occurs while setting strength or 
  *            normalization mode or caselevel
  */
  public void TestSmallLarge()
  {
    m_collator_.setAttribute(CollationAttribute.NORMALIZATION_MODE,
                             CollationAttribute.VALUE_ON);
    m_collator_.setStrength(CollationAttribute.VALUE_TERTIARY);
    m_collator_.setAttribute(CollationAttribute.CASE_LEVEL, 
                         CollationAttribute.VALUE_ON);
    for (int i = 0; i < 3 ; i ++)
      CollatorTest.doTest(this, m_collator_, SMALL_LARGE_CASE_[i], 
                          SMALL_LARGE_CASE_[i + 1], Collator.RESULT_LESS);
  }

  /*
  * Test Katakana, Hiragana letters
  * @exception thrown when error occurs while setting strength or 
  *            normalization mode or caselevel
  */
  public void TestKatakanaHiragana()
  {
    m_collator_.setDecomposition(Normalizer.UNORM_NFKD);
    m_collator_.setStrength(CollationAttribute.VALUE_QUATERNARY);
    m_collator_.setAttribute(CollationAttribute.CASE_LEVEL, 
                         CollationAttribute.VALUE_ON);
    for (int i = 0; i < 3 ; i ++) {
      CollatorTest.doTest(this, m_collator_, KATAKANA_HIRAGANA_CASE_[i], 
                          KATAKANA_HIRAGANA_CASE_[i + 1], 
                          Collator.RESULT_LESS);
    }
  }

  /**
  * Test Choo-on kigoo
  * @exception thrown when error occurs while setting strength or 
  *            normalization mode or caselevel
  */
  public void TestChooonKigoo()
  {
    m_collator_.setDecomposition(Normalizer.UNORM_NFKD);
    m_collator_.setStrength(CollationAttribute.VALUE_QUATERNARY);
    m_collator_.setAttribute(CollationAttribute.CASE_LEVEL, 
                         CollationAttribute.VALUE_ON);
    for (int i = 0; i < 6 ; i ++) {
      CollatorTest.doTest(this, m_collator_, CHOOON_KIGOO_CASE_[i], 
                          CHOOON_KIGOO_CASE_[i + 1], Collator.RESULT_LESS);
    }
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
    "\uff9E",
    "\u3042",
    "\u30A2",
    "\u3042\u3042",
    "\u30A2\u30FC",
    "\u30A2\u30FC\u30C8" 
  };

  /**
  * Target strings for testing
  */
  private final String TARGET_TEST_CASE_[] = 
  {
    "\uFF9F",
    "\u30A2",
    "\u3042\u3042",
    "\u30A2\u30FC",
    "\u30A2\u30FC\u30C8",
    "\u3042\u3042\u3068" 
  };

  /**
  * Comparison result corresponding to above source and target cases
  */
  private final int EXPECTED_TEST_RESULT_[] = 
  {
    Collator.RESULT_LESS,
    Collator.RESULT_EQUAL,
    Collator.RESULT_LESS, 
    Collator.RESULT_GREATER,
    Collator.RESULT_LESS,
    Collator.RESULT_LESS                                                     
  };
 
  /* *
  * Test data for simple base level comparison
  */
  private final String BASE_CASE_[] = {
    "\u30AB", "\u30AB\u30AD", "\u30AD", "\u30AD\u30AD"
  };

  /** 
  * Test data for plain, daku-ten, (handaku-ten) comparison
  */
  private final String PLAIN_DAKUTEN_HANDAKUTEN_CASE_[] = {
    "\u30CF\u30AB", "\u30D0\u30AB", "\u30CF\u30AD", 
    "\u30D0\u30AD"
  };

  /**
  * Test data for case comparison
  */
  private final String SMALL_LARGE_CASE_[] = {
    "\u30C3\u30CF", "\u30C4\u30CF", "\u30C3\u30D0", "\u30C4\u30D0"
  };

  /**
  * Test data for Katakana Hiragana comparison
  */
  private final String KATAKANA_HIRAGANA_CASE_[] = {
    "\u3042\u30C3", "\u30A2\u30C3", "\u3042\u30C4", "\u30A2\u30C4"
  };
  
  /**
  * Test data for Choo-on Kigoo comparison 
  */
  private final String CHOOON_KIGOO_CASE_[] = {
    "\u30AB\u30FC\u3042", "\u30AB\u30FC\u30A2", "\u30AB\u30A4\u3042", 
    "\u30AB\u30A4\u30A2", "\u30AD\u30FC\u3042", "\u30AD\u30FC\u30A2",
    "\u30AD\u30A4\u3042", "\u30AD\u30A4\u30A2"
  };
}

