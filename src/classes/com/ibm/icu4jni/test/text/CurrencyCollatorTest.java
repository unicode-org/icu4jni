/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: 
*  /usr/cvs/icu4j/icu4j/src/com/ibm/icu/test/text/CurrencyCollatorTest.java,v $ 
* $Date: 2001/03/23 19:43:17 $ 
* $Revision: 1.6 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.test.text;

import com.ibm.icu4jni.text.Collator;
import com.ibm.icu4jni.text.CollationKey;
import com.ibm.icu4jni.text.CollationAttribute;
import com.ibm.icu4jni.test.TestFmwk;
import java.util.Locale;

/**
* Testing class for currency collator
* Mostly following the test cases for ICU
* @author Syn Wee Quek
* @since jan 23 2001
*/
public final class CurrencyCollatorTest extends TestFmwk
{ 
  
  // constructor ===================================================
  
  /**
  * Constructor
  */
  public CurrencyCollatorTest() throws Exception
  {
    m_collator_ = Collator.getInstance(Locale.ENGLISH);
  }
  
  // public methods ================================================

  /**
  * Test with primary collation strength
  * @exception thrown when error occurs while setting strength
  */
  public void TestCurrency() throws Exception
  {
    int expectedresult;
    int size = m_currency_.length;
    
    String source;
    String target;
    
    // Compare each currency symbol against all the currency symbols, 
    // including itself
    for (int i = 0; i < size; i ++)
    {
      source = m_currency_[i];
      for (int j = 0; j < size; j ++)
      {
        target = m_currency_[j];
        if (i < j)
          expectedresult = Collator.RESULT_LESS;
        else 
          if ( i == j)
            expectedresult = Collator.RESULT_EQUAL;
          else
            expectedresult = Collator.RESULT_GREATER;
            
         CollationKey skey = m_collator_.getCollationKey(source),
                      tkey = m_collator_.getCollationKey(target);

         if (skey.compareTo(tkey) != expectedresult)
         {
           errln("Fail : Collation keys for " + source + " and " +
                         target + " expected to be " + expectedresult);
           return;
         }
      }
    }
  }
  
  // private variables =============================================
  
  /**
  * Collation element
  */
  private Collator m_collator_;
  
  /**
  * Test data in ascending collation order
  */
  private final String m_currency_[] = {
    "\u00A4", /*00A4; L; [14 36, 03, 03]    # [082B.0020.0002] # CURRENCY SIGN*/
    "\u00A2", /*00A2; L; [14 38, 03, 03]    # [082C.0020.0002] # CENT SIGN*/
    "\uFFE0", /*FFE0; L; [14 38, 03, 05]    # [082C.0020.0003] # FULLWIDTH CENT SIGN*/
    "\u0024", /*0024; L; [14 3A, 03, 03]    # [082D.0020.0002] # DOLLAR SIGN*/
    "\uFF04", /*FF04; L; [14 3A, 03, 05]    # [082D.0020.0003] # FULLWIDTH DOLLAR SIGN*/
    "\uFE69", /*FE69; L; [14 3A, 03, 1D]    # [082D.0020.000F] # SMALL DOLLAR SIGN*/
    "\u00A3", /*00A3; L; [14 3C, 03, 03]    # [082E.0020.0002] # POUND SIGN*/
    "\uFFE1", /*FFE1; L; [14 3C, 03, 05]    # [082E.0020.0003] # FULLWIDTH POUND SIGN*/
    "\u00A5", /*00A5; L; [14 3E, 03, 03]    # [082F.0020.0002] # YEN SIGN*/
    "\uFFE5", /*FFE5; L; [14 3E, 03, 05]    # [082F.0020.0003] # FULLWIDTH YEN SIGN*/
    "\u09F2", /*09F2; L; [14 40, 03, 03]    # [0830.0020.0002] # BENGALI RUPEE MARK*/
    "\u09F3", /*09F3; L; [14 42, 03, 03]    # [0831.0020.0002] # BENGALI RUPEE SIGN*/
    "\u0E3F", /*0E3F; L; [14 44, 03, 03]    # [0832.0020.0002] # THAI CURRENCY SYMBOL BAHT*/
    "\u17DB", /*17DB; L; [14 46, 03, 03]    # [0833.0020.0002] # KHMER CURRENCY SYMBOL RIEL*/
    "\u20A0", /*20A0; L; [14 48, 03, 03]    # [0834.0020.0002] # EURO-CURRENCY SIGN*/
    "\u20A1", /*20A1; L; [14 4A, 03, 03]    # [0835.0020.0002] # COLON SIGN*/
    "\u20A2", /*20A2; L; [14 4C, 03, 03]    # [0836.0020.0002] # CRUZEIRO SIGN*/
    "\u20A3", /*20A3; L; [14 4E, 03, 03]    # [0837.0020.0002] # FRENCH FRANC SIGN*/
    "\u20A4", /*20A4; L; [14 50, 03, 03]    # [0838.0020.0002] # LIRA SIGN*/
    "\u20A5", /*20A5; L; [14 52, 03, 03]    # [0839.0020.0002] # MILL SIGN*/
    "\u20A6", /*20A6; L; [14 54, 03, 03]    # [083A.0020.0002] # NAIRA SIGN*/
    "\u20A7", /*20A7; L; [14 56, 03, 03]    # [083B.0020.0002] # PESETA SIGN*/
    "\u20A9", /*20A9; L; [14 58, 03, 03]    # [083C.0020.0002] # WON SIGN*/
    "\uFFE6", /*FFE6; L; [14 58, 03, 05]    # [083C.0020.0003] # FULLWIDTH WON SIGN*/
    "\u20AA", /*20AA; L; [14 5A, 03, 03]    # [083D.0020.0002] # NEW SHEQEL SIGN*/
    "\u20AB", /*20AB; L; [14 5C, 03, 03]    # [083E.0020.0002] # DONG SIGN*/
    "\u20AC", /*20AC; L; [14 5E, 03, 03]    # [083F.0020.0002] # EURO SIGN*/
    "\u20AD", /*20AD; L; [14 60, 03, 03]    # [0840.0020.0002] # KIP SIGN*/
    "\u20AE", /*20AE; L; [14 62, 03, 03]    # [0841.0020.0002] # TUGRIK SIGN*/
    "\u20AF" /*20AF; L; [14 64, 03, 03]    # [0842.0020.0002] # DRACHMA SIGN*/
  };
}

