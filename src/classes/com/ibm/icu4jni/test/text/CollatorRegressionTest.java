/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: 
*  /usr/cvs/icu4j/icu4j/src/com/ibm/icu/test/text/CollatorRegressionTest.java,v $ 
* $Date: 2001/09/18 00:33:49 $ 
* $Revision: 1.8 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.test.text;


import com.ibm.icu4jni.test.TestFmwk;
import com.ibm.icu4jni.text.Collator;
import com.ibm.icu4jni.text.RuleBasedCollator;
import com.ibm.icu4jni.text.CollationKey;
import com.ibm.icu4jni.text.CollationElementIterator;
import com.ibm.icu4jni.text.Normalizer;
import com.ibm.icu4jni.text.CollationAttribute;
import java.util.Locale;

/**
* Collator regression testing class
* Mostly following the test cases for ICU
* @author Syn Wee Quek
* @since jan 29 2001
*/
public final class CollatorRegressionTest extends TestFmwk
{ 
  
  // constructor ===================================================
  
  /**
  * Constructor
  */
  public CollatorRegressionTest() throws Exception
  {
    m_collator_ = Collator.getInstance(Locale.ENGLISH);
  }
  
  // public methods ================================================

  /**
  * Testing bug 4048446
  * @exception thrown when error occurs while setting strength
  */
  public void Test4048446() throws Exception
  {
    CollationElementIterator i1 = 
      ((RuleBasedCollator)m_collator_).getCollationElementIterator(
                                                            TEST_STRING_1_);
    CollationElementIterator i2 = 
      ((RuleBasedCollator)m_collator_).getCollationElementIterator(
                                                            TEST_STRING_1_);
    if (i1 == null || i2 == null)
    {
      errln("Failed : Creation of the collation element iterator");
      return;
    }

    while (i1.next() != CollationElementIterator.NULLORDER)
    {
    }

    i1.reset();

    int c1 = 0,
        c2;
    while (c1 != CollationElementIterator.NULLORDER)
    {
      c1 = i1.next();
      c2 = i2.next();
      if (c1 != c2)
      {
        errln("Failed : Resetting collation element iterator " +
                      "should revert it back to the orginal state");
        return;
      }
    }
  }

  /**
  * Testing bug 4051866
  * @exception thrown when error occurs while setting strength
  */
  public void Test4051866() throws Exception
  {
    String rules= "& o & oe ,o\u3080& oe ,\u1530 ,O& OE ,O\u3080& OE ," +
                  "\u1520< p ,P";

    // Build a collator containing expanding characters
    RuleBasedCollator c1 = new RuleBasedCollator(rules);

    // Build another using the rules from  the first
    RuleBasedCollator c2 = new RuleBasedCollator(c1.getRules());

    if (!(c1.getRules().equals(c2.getRules())))
      errln("Failed : Rules from equivalent collators should be " +
                    "the same");
  }
  
  /**
  * Testing bug 4053636
  * @exception thrown when error occurs while setting strength
  */
  public void Test4053636() throws Exception
  {
    if (m_collator_.equals("black_bird", "black"))
      errln("Failed : black-bird != black");
  }
  
  /**
  * Testing bug 4054238
  * @exception thrown when error occurs while setting strength
  */
  public void Test4054238() throws Exception
  {
    try
    {
    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();

    c.setDecomposition(Normalizer.UNORM_NFD);
    c.getCollationElementIterator(TEST_STRING_3_);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
  
  /**
  * Testing bug 4054734
  * @exception thrown when error occurs while setting strength
  */
  public void Test4054734() throws Exception
  {
    final String decomp[] = {"\u0001", "\u0002", 
                             "\u0001", "\u0001", 
                             "\u0041\u0001", "\u007e\u0002",
                             "\u00c0", "\u0041\u0300"};
    final int result[] = {Collator.RESULT_LESS, Collator.RESULT_EQUAL,
                          Collator.RESULT_GREATER, Collator.RESULT_EQUAL};

    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();

    c.setStrength(CollationAttribute.VALUE_IDENTICAL);

    c.setDecomposition(Normalizer.UNORM_NFD);
    compareStrings(c, decomp, result);
  }
  
  /**
  * Testing bug 4054734
  * @exception thrown when error occurs while setting strength
  */
  public void Test4054736() throws Exception
  {
    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();

    c.setDecomposition(Normalizer.UNORM_NFKD);
    // synwee : changed
    c.setStrength(CollationAttribute.VALUE_SECONDARY);

    final String tests[] = {"\uFB4F", "\u05D0\u05DC"};  
                           // Alef-Lamed vs. Alef, Lamed
    final int result[] = {Collator.RESULT_EQUAL};

    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4058613
  * @exception thrown when error occurs while setting strength
  */
  public void Test4058613() throws Exception
  {
    Locale oldDefault = Locale.getDefault();
    Locale.setDefault(Locale.KOREAN);

    Collator c = Collator.getInstance(Locale.ENGLISH);

    // Since the fix to this bug was to turn off decomposition for Korean 
    // collators, ensure that's what we got
    // synwee : changed
    /*
    if (c.getDecomposition() != Normalizer.NO_NORMALIZATION)    
      errln("Failed : Decomposition is not set to NO_DECOMPOSITION " +
                    "for Korean collator");
    */
    if (c.getDecomposition() != Normalizer.UNORM_NONE)    
      errln("Failed : Decomposition is not set to NO_DECOMPOSITION " +
                    "for Korean collator");

    Locale.setDefault(oldDefault);
  }
  
  /**
  * Testing bug 4059820
  * @exception thrown when error occurs while setting strength
  */
  public void Test4059820() throws Exception
  {
    String rules = "< a < b , c/a < d < z";
    
    RuleBasedCollator c = new RuleBasedCollator(rules);

    if (c.getRules().indexOf("c/a") == -1)
      errln("Failed : Rules should contain 'c/a'");
  }
  
  /**
  * Testing bug 4060154
  * @exception thrown when error occurs while setting strength
  */
  public void Test4060154() throws Exception
  {
    String rules = "< g, G < h, H < i, I < j, J & H < \u0131, \u0130, i, I";
    RuleBasedCollator c = new RuleBasedCollator(rules);

    c.setDecomposition(Normalizer.UNORM_NFD);

    final String tertiary[] = {"\u0041", "\u0042", "\u0048", "\u0131", 
                               "\u0048", "\u0049", "\u0131", "\u0130", 
                               "\u0130", "\u0069", "\u0130", "\u0048"};
    final int tresult[] = {Collator.RESULT_LESS, Collator.RESULT_LESS,
                          Collator.RESULT_LESS, Collator.RESULT_LESS,
                          Collator.RESULT_LESS, Collator.RESULT_GREATER};

    c.setStrength(CollationAttribute.VALUE_TERTIARY);
    compareStrings(c, tertiary, tresult);

    final String secondary[] = {"\u0048", "\u0049", "\u0131", "\u0130"};
    final int result[] = {Collator.RESULT_LESS, Collator.RESULT_EQUAL}; 

    c.setStrength(CollationAttribute.VALUE_PRIMARY);
    compareStrings(c, secondary, result);
  }
  
  /**
  * Testing bug 4062418
  * @exception thrown when error occurs while setting strength
  */
  public void Test4062418() throws Exception
  {
    RuleBasedCollator c = (RuleBasedCollator)Collator.getInstance(
                                                                Locale.FRANCE);

    c.setStrength(CollationAttribute.VALUE_SECONDARY);

    final String tests[] = {"\u0070\u00EA\u0063\u0068\u0065",
                            "\u0070\u00E9\u0063\u0068\u00E9"};
   final int result[] = {Collator.RESULT_LESS};

    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4065540
  * @exception thrown when error occurs while setting strength
  */
  public void Test4065540() throws Exception
  {
    if (m_collator_.equals("abcd e", "abcd f"))
      errln("Failed : 'abcd e' != 'abcd f'");
  }
  
  /**
  * Testing bug 4066189
  * @exception thrown when error occurs while setting strength
  */
  public void Test4066189() throws Exception
  {
    String chars1 = "\u1EB1";
    String chars2 = "\u0061\u0306\u0300";
    String test1 = chars1;
    String test2 = chars2;

    RuleBasedCollator c1 = (RuleBasedCollator)m_collator_.clone();
    c1.setDecomposition(Normalizer.UNORM_NFKD);
    CollationElementIterator i1 = c1.getCollationElementIterator(test1);

    RuleBasedCollator c2 = (RuleBasedCollator)m_collator_.clone();
    c2.setDecomposition(Normalizer.UNORM_NONE);
    CollationElementIterator i2 = c2.getCollationElementIterator(test2);

    int ce1 = 1,
        ce2;
    while (ce1 != CollationElementIterator.NULLORDER)
    {
      ce1 = i1.next();
      ce2 = i2.next();

      if (ce1 != ce2)
      {
        errln("Failed : \u1EB1 == \u0061\u0306\u0300");
        return;
      }
    }
  }
  
  /**
  * Testing bug 4066696
  * @exception thrown when error occurs while setting strength
  */
  public void Test4066696() throws Exception
  {
    RuleBasedCollator c = (RuleBasedCollator)Collator.getInstance(
                                                                Locale.FRANCE);

    c.setStrength(CollationAttribute.VALUE_SECONDARY);

    final String tests[] = {"\u00E0", "\u01FA"};
    final int result[] = {Collator.RESULT_GREATER};

    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4076676
  * @exception thrown when error occurs while setting strength
  */
  public void Test4076676() throws Exception
  {
    // These combining characters are all in the same class, so they should not
    // be reordered, and they should compare as unequal.
    String s1 = "\u0041\u0301\u0302\u0300";
    String s2 = "\u0041\u0302\u0300\u0301";

    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();
    c.setStrength(CollationAttribute.VALUE_TERTIARY);

    if (c.compare(s1, s2) == Collator.RESULT_EQUAL)
      errln("Failed : Reordered combining chars of the same class " + 
                    "are not equal");
  }
  
  /**
  * Testing bug 4078588
  * @exception thrown when error occurs while setting strength
  */
  public void Test4078588() throws Exception
  {
    RuleBasedCollator rbc = new RuleBasedCollator("< a < bb");

    int result = rbc.compare("a", "bb");

    if (result != Collator.RESULT_LESS)
       errln("Failed : a < bb");
  }
  
  /**
  * Testing bug 4081866
  * @exception thrown when error occurs while setting strength
  */
  public void Test4081866() throws Exception
  {
    // These combining characters are all in different classes,
    // so they should be reordered and the strings should compare as equal.
    String s1 = "\u0041\u0300\u0316\u0327\u0315";
    String s2 = "\u0041\u0327\u0316\u0315\u0300";

    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();
    c.setStrength(CollationAttribute.VALUE_TERTIARY);
    
    // Now that the default collators are set to NO_DECOMPOSITION
    // (as a result of fixing bug 4114077), we must set it explicitly
    // when we're testing reordering behavior.
    c.setDecomposition(Normalizer.UNORM_NFD);

    if (!c.equals(s1, s2))
      errln("Failed : \u0041\u0300\u0316\u0327\u0315 = " +
            "\u0041\u0327\u0316\u0315\u0300");
  }
  
  /**
  * Testing bug 4087241
  * @exception thrown when error occurs while setting strength
  */
  public void Test4087241() throws Exception
  {
    Locale da_DK = new Locale("da", "DK");
    RuleBasedCollator c = (RuleBasedCollator)Collator.getInstance(da_DK);

    c.setStrength(CollationAttribute.VALUE_SECONDARY);

    final String tests[] = {"\u007a", "\u00E6", // z < ae
                      // a-unlaut < a-ring
                      "\u0061\u0308", "\u0061\u030A", 
                      "\u0059", "\u0075\u0308"}; // Y < u-umlaut
    final int result[] = {Collator.RESULT_LESS, Collator.RESULT_LESS,
                          Collator.RESULT_LESS};

    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4087243
  * @exception thrown when error occurs while setting strength
  */
  public void Test4087243() throws Exception
  {
    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();
    c.setStrength(CollationAttribute.VALUE_TERTIARY);

    final String tests[] = {"\u0031\u0032\u0033", "\u0031\u0032\u0033\u0001"};
    // 1 2 3  =  1 2 3 ctrl-A
    final int result[] = {Collator.RESULT_EQUAL};
                          
    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4092260
  * @exception thrown when error occurs while setting strength
  */
  public void Test4092260() throws Exception
  {
    Locale el = new Locale("el", "");
    Collator c = Collator.getInstance(el);
    c.setStrength(CollationAttribute.VALUE_SECONDARY);
  
    final String tests[] = {"\u00B5", "\u03BC"};
    final int result[] = {Collator.RESULT_EQUAL};

    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4095316
  * @exception thrown when error occurs while setting strength
  */
  public void Test4095316() throws Exception
  {
    Locale el = new Locale("el", "GR");
    Collator c = Collator.getInstance(el);
    c.setStrength(CollationAttribute.VALUE_SECONDARY);
  
    final String tests[] = {"\u03D4", "\u03AB"};
    final int result[] = {Collator.RESULT_EQUAL};

    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4101940
  * @exception thrown when error occurs while setting strength
  */
  public void Test4101940() throws Exception
  {
    RuleBasedCollator c = new RuleBasedCollator("< a < b");
   
    CollationElementIterator i = c.getCollationElementIterator("");
    i.reset();

    if (i.next() != CollationElementIterator.NULLORDER)
      errln("Failed : next did not return NULLORDER");
  }
  
  /**
  * Testing bug 4103436
  * @exception thrown when error occurs while setting strength
  */
  public void Test4103436() throws Exception
  {
    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();
    c.setStrength(CollationAttribute.VALUE_TERTIARY);

    final String tests[] = { "\u0066\u0069\u006c\u0065", 
          "\u0066\u0069\u006c\u0065\u0020\u0061\u0063\u0063\u0065\u0073\u0073",
          "\u0066\u0069\u006c\u0065",
          "\u0066\u0069\u006c\u0065\u0061\u0063\u0063\u0065\u0073\u0073"};
    final int result[] = {Collator.RESULT_LESS, Collator.RESULT_LESS};

    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4114076
  * @exception thrown when error occurs while setting strength
  */
  public void Test4114076() throws Exception
  {
    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();
    c.setStrength(CollationAttribute.VALUE_TERTIARY);

    final String tests[] = { "\ud4db", "\u1111\u1171\u11b6"};
    final int result[] = {Collator.RESULT_EQUAL};

    c.setDecomposition(Normalizer.UNORM_NFD);
    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4124632
  * @exception thrown when error occurs while setting strength
  */
  public void Test4124632() throws Exception
  {
    Collator c = Collator.getInstance(Locale.JAPAN);
    String test = "\u0041\u0308\u0062\u0063";
    c.getCollationKey(test);
  }
  
  /**
  * Testing bug 4132736
  * @exception thrown when error occurs while setting strength
  */
  public void Test4132736() throws Exception
  {
    Collator c = Collator.getInstance(Locale.FRANCE);
    final String tests[] = {"\u0065\u0300\u0065\u0301",  
                      "\u0065\u0301\u0065\u0300", "\u0065\u0300\u0301",       
                      "\u0065\u0301\u0300"};
    final int result[] = {Collator.RESULT_LESS, Collator.RESULT_LESS};
    c.setStrength(CollationAttribute.VALUE_TERTIARY);
    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4133509
  * @exception thrown when error occurs while setting strength
  */
  public void Test4133509() throws Exception
  {
    Collator c = Collator.getInstance(Locale.FRANCE);
    final String tests[] = {
        "\u0045\u0078\u0063\u0065\u0070\u0074\u0069\u006f\u006e", 
        "\u0045\u0078\u0063\u0065\u0070\u0074\u0069\u006f\u006e\u0049\u006e\u0049\u006e\u0069\u0074\u0069\u0061\u006c\u0069\u007a\u0065\u0072\u0045\u0072\u0072\u006f\u0072",
        "\u0047\u0072\u0061\u0070\u0068\u0069\u0063\u0073", 
        "\u0047\u0072\u0061\u0070\u0068\u0069\u0063\u0073\u0045\u006e\u0076\u0069\u0072\u006f\u006e\u006d\u0065\u006e\u0074",
        "\u0053\u0074\u0072\u0069\u006e\u0067", 
        "\u0053\u0074\u0072\u0069\u006e\u0067\u0042\u0075\u0066\u0066\u0065\u0072"
    };
    final int result[] = {Collator.RESULT_LESS, Collator.RESULT_LESS,
                          Collator.RESULT_LESS};
    compareStrings(c, tests, result);
  }
  
  /**
  * Testing bug 4114077
  * @exception thrown when error occurs while setting strength
  */
  public void Test4114077() throws Exception
  {
    // Ensure that we get the same results with decomposition off
    // as we do with it on....
    
    RuleBasedCollator c = (RuleBasedCollator)m_collator_.clone();
    c.setStrength(CollationAttribute.VALUE_TERTIARY);
    
    final String test2[] = {"\u0041\u0300\u0316", "\u0041\u0316\u0300"};
                      // Reordering --> equal                  
    final int result[] = {Collator.RESULT_EQUAL};

    c.setDecomposition(Normalizer.UNORM_NFD);
    compareStrings(c, test2, result);
  }
  
  /**
  * Testing bug 4139572
  * @exception thrown when error occurs while setting strength
  */
  public void Test4139572() throws Exception
  {
    // Rather than just creating a Swedish collator, we might as well
    // try to instantiate one for every locale available on the system
    // in order to prevent this sort of bug from cropping up in the future
    // Code pasted straight from the bug report
    // (and then translated to C++ ;-)
    Locale l = new Locale("es", "es");
    Collator col = Collator.getInstance(l);

    col.getCollationKey("Nombre De Objeto");
  }
  
  // private variables =============================================
  
  /**
  * Test collator
  */
  private Collator m_collator_;
  
  /**
  * Source strings for testing
  */
  private final String TEST_STRING_1_ = "XFILE What subset of all possible " +
       "test cases has the highest probability of detecting the most errors?";
  private final String TEST_STRING_2_ = "Xf ile What subset of all " +
    "possible test cases has the lowest probability of detecting the least " +
    "errors?";
  private final char CHAR_ARRAY_[] = {0x0061, 0x00FC, 0x0062, 0x0065, 0x0063, 
                                      0x006b, 0x0020, 0x0047, 0x0072, 0x00F6, 
                                      0x00DF, 0x0065, 0x0020, 0x004c, 0x00FC, 
                                      0x0062, 0x0063, 0x006b
                                      };
  private final String TEST_STRING_3_ = new String(CHAR_ARRAY_);
  
  // private methods ------------------------------------------------------
  
  /**
  * Comparing strings
  */
  private void compareStrings(Collator c, String tests[], int result[])
  {
    int testcount = result.length;

    for (int i = 0; i < testcount; i ++)
    {
      String source = tests[i << 1];
      String target = tests[(i << 1) + 1];
      int expectedResult = result[i];

      CollationKey sourceKey, targetKey;
      
      sourceKey = c.getCollationKey(source);
      targetKey = c.getCollationKey(target);

      if (sourceKey.compareTo(targetKey) != expectedResult)
      {
        errln("Failed : String comparison of " + source + " and " +
                      target + " should be " + expectedResult);
        return;
      }
    }
  }
}

