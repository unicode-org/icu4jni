/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/text/Collator.java,v $ 
* $Date: 2001/06/06 19:49:16 $ 
* $Revision: 1.7 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.text;

import java.util.Locale;
import com.ibm.icu4jni.text.RuleBasedCollator;

/**
* Abstract class handling locale specific collation via JNI and ICU.
* Subclasses implement specific collation strategies. One subclass, 
* com.ibm.icu4jni.text.RuleBasedCollator, is currently provided and is 
* applicable to a wide set of languages. Other subclasses may be created to 
* handle more specialized needs. 
* You can use the static factory method, getInstance(), to obtain the 
* appropriate Collator object for a given locale. 
* 
* <pre>
* // Compare two strings in the default locale
* Collator myCollator = Collator.getInstance();
* if (myCollator.compare("abc", "ABC") < 0) {
*   System.out.println("abc is less than ABC");
* }
* else {
*   System.out.println("abc is greater than or equal to ABC");
* }
* </pre>
*
* You can set a Collator's strength property to determine the level of 
* difference considered significant in comparisons. 
* Five strengths in CollationAttribute are provided: VALUE_PRIMARY, 
* VALUE_SECONDARY, VALUE_TERTIARY, VALUE_QUARTENARY and VALUE_IDENTICAL. 
* The exact assignment of strengths to language features is locale dependant. 
* For example, in Czech, "e" and "f" are considered primary differences, while 
* "e" and "ê" latin small letter e with circumflex are secondary differences, 
* "e" and "E" are tertiary differences and "e" and "e" are identical. 
*
* <p>
* The following shows how both case and accents could be ignored for US 
* English. 
* <pre>
* //Get the Collator for US English and set its strength to PRIMARY
* Collator usCollator = Collator.getInstance(Locale.US);
* usCollator.setStrength(Collator.PRIMARY);
* if (usCollator.compare("abc", "ABC") == 0) {
*   System.out.println("Strings are equivalent");
* }
* </pre>
* For comparing Strings exactly once, the compare method provides the best 
* performance. 
* When sorting a list of Strings however, it is generally necessary to compare 
* each String multiple times. 
* In this case, com.ibm.icu4jni.text.CollationKey provide better performance. 
* The CollationKey class converts a String to a series of bits that can be 
* compared bitwise against other CollationKeys. 
* A CollationKey is created by a Collator object for a given String. 
* Note: CollationKeys from different Collators can not be compared. 
* </p>
*
* Considerations :
* 1) ErrorCode not returned to user throw exceptions instead
* 2) Similar API to java.text.Collator
* @author syn wee quek
* @since Jan 17 01
*/

public abstract class Collator implements Cloneable
{ 
  // public data member -------------------------------------------
  
  // Collation result constants -----------------------------------
  // corresponds to ICU's UCollationResult enum balues
  /** 
  * string a == string b 
  */
  public static final int RESULT_EQUAL = 0;
  /** 
  * string a > string b 
  */
  public static final int RESULT_GREATER = 1;
  /** 
  * string a < string b 
  */
  public static final int RESULT_LESS = -1;
  /** 
  * accepted by most attributes 
  */
  public static final int RESULT_DEFAULT = -1;
  
  // public methods -----------------------------------------------
  
  /**
  * Factory method to create an appropriate Collator which uses the default
  * locale collation rules.
  * Current implementation createInstance() returns a RuleBasedCollator(Locale) 
  * instance. The RuleBasedCollator will be created in the following order,
  * <ul>
  * <li> Data from argument locale resource bundle if found, otherwise
  * <li> Data from parent locale resource bundle of arguemtn locale if found,
  *      otherwise
  * <li> Data from built-in default collation rules if found, other
  * <li> null is returned
  * </ul>
  * @return an instance of Collator
  */
  public static Collator getInstance()
  {
    return getInstance(null);
  }

  /**
  * Factory method to create an appropriate Collator which uses the argument
  * locale collation rules.<br>
  * Current implementation createInstance() returns a RuleBasedCollator(Locale) 
  * instance. The RuleBasedCollator will be created in the following order,
  * <ul>
  * <li> Data from argument locale resource bundle if found, otherwise
  * <li> Data from parent locale resource bundle of arguemtn locale if found,
  *      otherwise
  * <li> Data from built-in default collation rules if found, other
  * <li> null is returned
  * </ul>
  * @param locale to be used for collation
  * @return an instance of Collator
  */
  public static Collator getInstance(Locale locale)
  {
    RuleBasedCollator result = new RuleBasedCollator(locale);
    return result;
  }

  /**
  * Locale dependent equality check for the argument strings.
  * @param source string
  * @param target string
  * @return true if source is equivalent to target, false otherwise 
  */
  public boolean equals(String source, String target)
  {
    return (compare(source, target) == RESULT_EQUAL);
  }
  
  /**
  * Checks if argument object is equals to this object.
  * @param target object
  * @return true if source is equivalent to target, false otherwise 
  */
  public abstract boolean equals(Object target);
  
  /**
  * Makes a copy of the current object.
  * @return a copy of this object
  */
  public abstract Object clone() throws CloneNotSupportedException;
  
  /**
  * The comparison function compares the character data stored in two
  * different strings. Returns information about whether a string is less 
  * than, greater than or equal to another string.
  * <p>Example of use:
  * <pre>
  * .  Collator myCollation = Collator.getInstance(Locale::US);
  * .  myCollation.setStrength(CollationAttribute.VALUE_PRIMARY);
  * .  // result would be CollationAttribute.VALUE_EQUAL 
  * .  // ("abc" == "ABC")
  * .  // (no primary difference between "abc" and "ABC")
  * .  int result = myCollation.compare("abc", "ABC",3);
  * .  myCollation.setStrength(CollationAttribute.VALUE_TERTIARY);
  * .  // result would be Collation.LESS (abc" &lt;&lt;&lt; "ABC")
  * .  // (with tertiary difference between "abc" and "ABC")
  * .  int result = myCollation.compare("abc", "ABC",3);
  * </pre>
  * @param source source string.
  * @param target target string.
  * @return result of the comparison, Collator.RESULT_EQUAL, 
  *         Collator.RESULT_GREATER or Collator.RESULT_LESS
  */
  public abstract int compare(String source, String target);
                                               
  /**
  * Get the decomposition mode of this Collator
  * Return values from com.ibm.icu4jni.text.Normalization.
  * @return the decomposition mode
  */
  public abstract int getDecomposition();

  /**
  * Set the decomposition mode of the Collator object. 
  * Argument values from com.ibm.icu4jni.text.Normalization.
  * @param decompositionmode the new decomposition mode
  */
  public abstract void setDecomposition(int mode);

  /**
  * Determines the minimum strength that will be use in comparison or
  * transformation.
  * <p>
  * E.g. with strength == CollationAttribute.VALUE_SECONDARY, the 
  * tertiary difference 
  * is ignored
  * </p>
  * <p>
  * E.g. with strength == CollationAttribute.VALUE_PRIMARY, the 
  * secondary and tertiary difference are ignored.
  * </p>
  * @return the current comparison level.
  */
  public abstract int getStrength();
  
  /**
  * Gets the attribute to be used in comparison or transformation.
  * @param type the attribute to be set from CollationAttribute
  * @return value attribute value from CollationAttribute
  */
  public abstract int getAttribute(int type);
  
  /**
  * Sets the minimum strength to be used in comparison or transformation.
  * <p>Example of use:
  * <pre>
  * . Collator myCollation = Collator.createInstance(Locale::US);
  * . myCollation.setStrength(CollationAttribute.VALUE_PRIMARY);
  * . // result will be "abc" == "ABC"
  * . // tertiary differences will be ignored
  * . int result = myCollation->compare("abc", "ABC");
  * </pre>
  * @param strength the new comparison level.
  */
  public abstract void setStrength(int strength);
  
  /**
  * Sets the attribute to be used in comparison or transformation.
  * <p>Example of use:
  * <pre>
  * . Collator myCollation = Collator.createInstance(Locale::US);
  * . myCollation.setAttribute(CollationAttribute.CASE_LEVEL, 
  * .                          CollationAttribute.VALUE_ON);
  * . int result = myCollation->compare("\\u30C3\\u30CF", 
  * .                                   "\\u30C4\\u30CF");
  * . // result will be Collator.RESULT_LESS.
  * </pre>
  * @param type the attribute to be set from CollationAttribute
  * @param value attribute value from CollationAttribute
  */
  public abstract void setAttribute(int type, int value);
  
  /**
  * Get the sort key as an CollationKey object from the argument string.
  * To retrieve sort key in terms of byte arrays, use the method as below<br>
  * <code>
  * Collator collator = Collator.getInstance();
  * CollationKey collationkey = collator.getCollationKey("string");
  * byte[] array = collationkey.toByteArray();
  * </code><br>
  * Byte array result are zero-terminated and can be compared using 
  * java.util.Arrays.equals();
  * @param source string to be processed.
  * @return the sort key
  */
  public abstract CollationKey getCollationKey(String source);
  
  /**
  * Returns a hash of this collation object
  * @return hash of this collation object
  */
  public abstract int hashCode();
}
