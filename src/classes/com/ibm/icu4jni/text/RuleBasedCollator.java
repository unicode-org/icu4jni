/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/text/RuleBasedCollator.java,v $ 
* $Date: 2001/09/18 00:33:50 $ 
* $Revision: 1.11 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.text;

import java.util.Locale;
import java.text.ParseException;
import com.ibm.icu4jni.common.ErrorCode;

/**
* Concrete implementation class for Collation.
* <p>
* The collation table is composed of a list of collation rules, where each
* rule is of three forms:
* <pre>
*    < modifier >
*    < relation > < text-argument >
*    < reset > < text-argument >
* </pre>
* <p>
* <code>RuleBasedCollator</code> has the following restrictions for efficiency 
* (other subclasses may be used for more complex languages) :
* <ol>
* <li> If a French secondary ordering is specified it applies to the whole 
*      collator object.
* <li> All non-mentioned Unicode characters are at the end of the collation 
*      order.
* <li> If a character is not located in the RuleBasedCollator, the default 
*      Unicode Collation Algorithm (UCA) rulebased table is automatically 
*      searched as a backup.
* </ol>
*
* The following demonstrates how to create your own collation rules:
* <UL Type=disc>
*    <LI><strong>Text-Argument</strong>: A text-argument is any sequence of
*        characters, excluding special characters (that is, common whitespace 
*        characters [0009-000D, 0020] and rule syntax characters [0021-002F, 
*        003A-0040, 005B-0060, 007B-007E]). If those characters are desired, 
*        you can put them in single quotes (e.g. ampersand => '&'). Note that 
*        unquoted white space characters are ignored; e.g. <code>b c</code> is 
*        treated as <code>bc</code>.
*    <LI><strong>Modifier</strong>: There is a single modifier which is used 
*        to specify that all accents (secondary differences) are backwards.
*        <p>'@' : Indicates that accents are sorted backwards, as in French.
*    <LI><strong>Relation</strong>: The relations are the following:
*        <UL Type=square>
*            <LI>'<' : Greater, as a letter difference (primary)
*            <LI>';' : Greater, as an accent difference (secondary)
*            <LI>',' : Greater, as a case difference (tertiary)
*            <LI>'=' : Equal
*        </UL>
*    <LI><strong>Reset</strong>: There is a single reset which is used 
*        primarily for contractions and expansions, but which can also be used 
*        to add a modification at the end of a set of rules.
*        <p>'&' : Indicates that the next rule follows the position to where
*            the reset text-argument would be sorted.
* </UL>
*
* <p>
* This sounds more complicated than it is in practice. For example, the
* following are equivalent ways of expressing the same thing:
* <blockquote>
* <pre>
* a < b < c
* a < b & b < c
* a < c & a < b
* </pre>
* </blockquote>
* Notice that the order is important, as the subsequent item goes immediately
* after the text-argument. The following are not equivalent:
* <blockquote>
* <pre>
* a < b & a < c
* a < c & a < b
* </pre>
* </blockquote>
* Either the text-argument must already be present in the sequence, or some
* initial substring of the text-argument must be present. (e.g. "a < b & ae <
* e" is valid since "a" is present in the sequence before "ae" is reset). In
* this latter case, "ae" is not entered and treated as a single character;
* instead, "e" is sorted as if it were expanded to two characters: "a"
* followed by an "e". This difference appears in natural languages: in
* traditional Spanish "ch" is treated as though it contracts to a single
* character (expressed as "c < ch < d"), while in traditional German a-umlaut 
* is treated as though it expanded to two characters (expressed as "a,A < b,B 
* ... & ae;ã & AE;Ã"). [ã and Ã are, of course, the escape sequences for 
* a-umlaut.]
* <p>
* <strong>Ignorable Characters</strong>
* <p>
* For ignorable characters, the first rule must start with a relation (the
* examples we have used above are really fragments; "a < b" really should be
* "< a < b"). If, however, the first relation is not "<", then all the all
* text-arguments up to the first "<" are ignorable. For example, ", - < a < b"
* makes "-" an ignorable character, as we saw earlier in the word
* "black-birds". In the samples for different languages, you see that most
* accents are ignorable.
*
* <p><strong>Normalization and Accents</strong>
* <p>
* <code>RuleBasedCollator</code> automatically processes its rule table to
* include both pre-composed and combining-character versions of accented 
* characters. Even if the provided rule string contains only base characters 
* and separate combining accent characters, the pre-composed accented 
* characters matching all canonical combinations of characters from the rule 
* string will be entered in the table.
* <p>
* This allows you to use a RuleBasedCollator to compare accented strings even 
* when the collator is set to NO_DECOMPOSITION. There are two caveats, however.
* First, if the strings to be collated contain combining sequences that may not 
* be in canonical order, you should set the collator to 
* CANONICAL_DECOMPOSITION or FULL_DECOMPOSITION to enable sorting of combining 
* sequences.  Second, if the strings contain characters with compatibility 
* decompositions (such as full-width and half-width forms), you must use 
* FULL_DECOMPOSITION, since the rule tables only include canonical mappings.
* For more information, see
* <A HREF="http://www.aw.com/devpress">The Unicode Standard, Version 3.0</A>.)
*
* <p><strong>Errors</strong>
* <p>
* The following are errors:
* <UL Type=disc>
*     <LI>A text-argument contains unquoted punctuation symbols
*        (e.g. "a < b-c < d").
*     <LI>A relation or reset character not followed by a text-argument
*        (e.g. "a < , b").
*     <LI>A reset where the text-argument (or an initial substring of the
*         text-argument) is not already in the sequence or allocated in the 
*         default UCA table.
*         (e.g. "a < b & e < f")
* </UL>
* If you produce one of these errors, a <code>RuleBasedCollator</code> throws
* a <code>ParseException</code>.
*
* <p><strong>Examples</strong>
* <p>Simple:     "< a < b < c < d"
* <p>Norwegian:  "< a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I< j,J
*                 < k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R< s,S< t,T
*                < u,U< v,V< w,W< x,X< y,Y< z,Z
*                 < å=a?,Å=A?
*                 ;aa,AA< æ,Æ< ø,Ø"
*
* <p>
* Normally, to create a rule-based Collator object, you will use
* <code>Collator</code>'s factory method <code>getInstance</code>.
* However, to create a rule-based Collator object with specialized rules 
* tailored to your needs, you construct the <code>RuleBasedCollator</code>
* with the rules contained in a <code>String</code> object. For example:
* <blockquote>
* <pre>
* String Simple = "< a < b < c < d";
* RuleBasedCollator mySimple = new RuleBasedCollator(Simple);
* </pre>
* </blockquote>
* Or:
* <blockquote>
* <pre>
* String Norwegian = "< a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I< j,J" +
*                 "< k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R< s,S< t,T" +
*                 "< u,U< v,V< w,W< x,X< y,Y< z,Z" +
*                 "< å=a?,Å=A?" +
*                 ";aa,AA< æ,Æ< ø,Ø";
* RuleBasedCollator myNorwegian = new RuleBasedCollator(Norwegian);
* </pre>
* </blockquote>
*
* <p>
* Combining <code>Collator</code>s is as simple as concatenating strings.
* Here's an example that combines two <code>Collator</code>s from two
* different locales:
* <blockquote>
* <pre>
* // Create an en_US Collator object
* RuleBasedCollator en_USCollator = (RuleBasedCollator)
*     Collator.getInstance(new Locale("en", "US", ""));
* // Create a da_DK Collator object
* RuleBasedCollator da_DKCollator = (RuleBasedCollator)
*     Collator.getInstance(new Locale("da", "DK", ""));
* // Combine the two
* // First, get the collation rules from en_USCollator
* String en_USRules = en_USCollator.getRules();
* // Second, get the collation rules from da_DKCollator
* String da_DKRules = da_DKCollator.getRules();
* RuleBasedCollator newCollator =
*     new RuleBasedCollator(en_USRules + da_DKRules);
* // newCollator has the combined rules
* </pre>
* </blockquote>
*
* <p>
* Another more interesting example would be to make changes on an existing
* table to create a new <code>Collator</code> object.  For example, add
* "& C < ch, cH, Ch, CH" to the <code>en_USCollator</code> object to create
* your own:
* <blockquote>
* <pre>
* // Create a new Collator object with additional rules
* String addRules = "& C < ch, cH, Ch, CH";
* RuleBasedCollator myCollator =
*     new RuleBasedCollator(en_USCollator + addRules);
* // myCollator contains the new rules
* </pre>
* </blockquote>
*
* <p>
* The following example demonstrates how to change the order of
* non-spacing accents,
* <blockquote>
* <pre>
* // old rule
* String oldRules = "=¨;¯;´"    // main accents Diaeresis 00A8, Macron 00AF
*                               // Acute 00BF
*                 + "< a , A ; ae, AE ; æ , Æ"
*                 + "< b , B < c, C < e, E & C < d, D";
* // change the order of accent characters
* String addOn = "& ´;¯;¨;"; // Acute 00BF, Macron 00AF, Diaeresis 00A8
* RuleBasedCollator myCollator = new RuleBasedCollator(oldRules + addOn);
* </pre>
* </blockquote>
*
* <p>
* The last example shows how to put new primary ordering in before the
* default setting. For example, in Japanese <code>Collator</code>, you
* can either sort English characters before or after Japanese characters,
* <blockquote>
* <pre>
* // get en_US Collator rules
* RuleBasedCollator en_USCollator = 
*                      (RuleBasedCollator)Collator.getInstance(Locale.US);
* // add a few Japanese character to sort before English characters
* // suppose the last character before the first base letter 'a' in
* // the English collation rule is ?
* String jaString = "& \\u30A2 , \\u30FC < \\u30C8";
* RuleBasedCollator myJapaneseCollator = new
*     RuleBasedCollator(en_USCollator.getRules() + jaString);
* </pre>
* </blockquote>
* <P>
* @author syn wee quek
* @since Jan 17 01
*/
    
public final class RuleBasedCollator extends Collator 
{
  // public constructors ------------------------------------------
  
  /**
  * RuleBasedCollator constructor. This takes the table rules and builds a 
  * collation table out of them. Please see RuleBasedCollator class
  * description for more details on the collation rule syntax.
  * @param rules the collation rules to build the collation table from.
  * @exception ParseException thrown if rules are empty or a Runtime error
  *            if collator can not be created.
  */
  public RuleBasedCollator(String rules) throws ParseException
  {
    
    if (rules.length() == 0)
      throw new ParseException("Build rules empty.", 0);
    m_collator_ = NativeCollation.openCollatorFromRules(rules,
                              Normalizer.UNORM_NONE,
                              CollationAttribute.VALUE_DEFAULT_STRENGTH);
  }

  /**
  * RuleBasedCollator constructor. This takes the table rules and builds a 
  * collation table out of them. Please see RuleBasedCollator class
  * description for more details on the collation rule syntax.
  * @param rules the collation rules to build the collation table from.
  * @param strength collation strength
  * @exception ParseException thrown if rules are empty or a Runtime error
  *            if collator can not be created.
  */
  public RuleBasedCollator(String rules, int strength) throws ParseException
  {
    if (rules.length() == 0)
      throw new ParseException("Build rules empty.", 0);
    if (!CollationAttribute.checkStrength(strength))
      throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
      
    m_collator_ = NativeCollation.openCollatorFromRules(rules,
                                Normalizer.UNORM_NONE,
                                strength);
  }

  /**
  * RuleBasedCollator constructor. This takes the table rules and builds a 
  * collation table out of them. Please see RuleBasedCollator class
  * description for more details on the collation rule syntax.
  * @param rules the collation rules to build the collation table from.
  * @param strength collation strength
  * @param normalizationmode normalization mode
  * @exception thrown when constructor error occurs
  */
  public RuleBasedCollator(String rules, int normalizationmode, int strength)
  {
    if (!CollationAttribute.checkStrength(strength) || 
        !Normalizer.check(normalizationmode)) {
      throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
    }
      
    m_collator_ = NativeCollation.openCollatorFromRules(rules,
                                          normalizationmode, strength);
  }
  
  // public methods -----------------------------------------------
  
  /**
  * Makes a complete copy of the current object.
  * @return a copy of this object if data clone is a success, otherwise null
  */
  public Object clone() 
  {
    RuleBasedCollator result = null;
    long collatoraddress = NativeCollation.safeClone(m_collator_);
    result = new RuleBasedCollator(collatoraddress);
    return (Collator)result;
  }
                              
  /**
  * The comparison function compares the character data stored in two
  * different strings. Returns information about whether a string is less 
  * than, greater than or equal to another string.
  * <p>Example of use:
  * <pre>
  * .  Collator myCollation = Collator.createInstance(Locale::US);
  * .  myCollation.setStrength(CollationAttribute.VALUE_PRIMARY);
  * .  // result would be Collator.RESULT_EQUAL ("abc" == "ABC")
  * .  // (no primary difference between "abc" and "ABC")
  * .  int result = myCollation.compare("abc", "ABC",3);
  * .  myCollation.setStrength(CollationAttribute.VALUE_TERTIARY);
  * .  // result would be Collation::LESS (abc" &lt;&lt;&lt; "ABC")
  * .  // (with tertiary difference between "abc" and "ABC")
  * .  int result = myCollation.compare("abc", "ABC",3);
  * </pre>
  * @param source The source string.
  * @param target The target string.
  * @return result of the comparison, Collator.RESULT_EQUAL, 
  *         Collator.RESULT_GREATER or Collator.RESULT_LESS
  */
  public int compare(String source, String target)
  {
    return NativeCollation.compare(m_collator_, source, target);
  }
                                               
  /**
  * Get the normalization mode for this object.
  * The normalization mode influences how strings are compared.
  * @return normalization mode; one of the values from Normalizer
  */
  public int getDecomposition()
  {
    return NativeCollation.getNormalization(m_collator_);
  }

  /**
  * Set the normalization mode used int this object
  * The normalization mode influences how strings are compared.
  * @param normalizationmode desired normalization mode; one of the values 
  *        from Normalizer
  * @exception thrown when argument does not belong to any normalization mode
  */
  public void setDecomposition(int decompositionmode)
  {
    if (!Normalizer.check(decompositionmode)) 
      throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
    NativeCollation.setNormalization(m_collator_, decompositionmode);
  }

  /**
  * Determines the minimum strength that will be use in comparison or
  * transformation.
  * <p>
  * E.g. with strength == CollationAttribute.VALUE_SECONDARY, the tertiary difference 
  * is ignored
  * </p>
  * <p>
  * E.g. with strength == PRIMARY, the secondary and tertiary difference are 
  * ignored.
  * </p>
  * @return the current comparison level.
  */
  public int getStrength()
  {
    return NativeCollation.getAttribute(m_collator_, 
                                        CollationAttribute.STRENGTH);
  }
  
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
  * @exception thrown when argument does not belong to any collation strength 
  *            mode or error occurs while setting data.
  */
  public void setStrength(int strength)
  {
    if (!CollationAttribute.checkStrength(strength)) 
      throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
    NativeCollation.setAttribute(m_collator_, CollationAttribute.STRENGTH, 
                                 strength);
  }
  
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
  public void setAttribute(int type, int value)
  {
    if (!CollationAttribute.checkAttribute(type, value))
      throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
    NativeCollation.setAttribute(m_collator_, type, value);
  }
  
  /**
  * Gets the attribute to be used in comparison or transformation.
  * @param type the attribute to be set from CollationAttribute
  * @return value attribute value from CollationAttribute
  */
  public int getAttribute(int type)
  {
    if (!CollationAttribute.checkType(type))
      throw ErrorCode.getException(ErrorCode.U_ILLEGAL_ARGUMENT_ERROR);
    return NativeCollation.getAttribute(m_collator_, type);
  }
  
  /**
  * Get the sort key as an CollationKey object from the argument string.
  * To retrieve sort key in terms of byte arrays, use the method as below<br>
  * <code>
  * Collator collator = Collator.getInstance();
  * byte[] array = collator.getSortKey(source);
  * </code><br>
  * Byte array result are zero-terminated and can be compared using 
  * java.util.Arrays.equals();
  * @param source string to be processed.
  * @return the sort key
  */
  public CollationKey getCollationKey(String source)
  {
    return new CollationKey(NativeCollation.getSortKey(m_collator_, source));
  }
  
  /**
  * Get a sort key for the argument string
  * Sort keys may be compared using java.util.Arrays.equals
  * @param collatoraddress address of the C collator
  * @param source string for key to be generated
  * @return sort key
  */
  public byte[] getSortKey(String source)
  {
    return NativeCollation.getSortKey(m_collator_, source);
  }
  
  /**
  * Get the collation rules of this Collation object
  * The rules will follow the rule syntax.
  * @return collation rules.
  */
  public String getRules()
  {
    return NativeCollation.getRules(m_collator_);
  }
  
  /** 
  * Create a CollationElementIterator object that will iterator over the 
  * elements in a string, using the collation rules defined in this 
  * RuleBasedCollator
  * @param collatoraddress address of C collator
  * @param source string to iterate over
  * @return address of C collationelement
  * @exception thrown when error occurs
  */
  public CollationElementIterator getCollationElementIterator(String source)
  {
    CollationElementIterator result = new CollationElementIterator(
         NativeCollation.getCollationElementIterator(m_collator_, source));
    // result.setOwnCollationElementIterator(true);
    return result;
  }
                             
  /**
  * Returns a hash of this collation object
  * Note this method is not complete, it only returns 0 at the moment.
  * @return hash of this collation object
  */
  public int hashCode()
  {
    // since rules do not change once it is created, we can cache the hash
    if (m_hashcode_ == 0) {
      m_hashcode_ = NativeCollation.hashCode(m_collator_);
      if (m_hashcode_ == 0)
        m_hashcode_ = 1;
    }
    return m_hashcode_;
  }
  
  /**
  * Checks if argument object is equals to this object.
  * @param target object
  * @return true if source is equivalent to target, false otherwise 
  */
  public boolean equals(Object target)
  {
    if (this == target) 
      return true;
    if (target == null) 
      return false;
    if (getClass() != target.getClass()) 
      return false;
    
    RuleBasedCollator tgtcoll = (RuleBasedCollator)target;
    return getRules().equals(tgtcoll.getRules()) && 
           getStrength() == tgtcoll.getStrength() && 
           getDecomposition() == tgtcoll.getDecomposition();
  }
  
  // package constructor ----------------------------------------
  
  /**
  * RuleBasedCollator default constructor. This constructor takes the default 
  * locale. The only caller of this class should be Collator.getInstance(). 
  * Current implementation createInstance() returns a RuleBasedCollator(Locale) 
  * instance. The RuleBasedCollator will be created in the following order,
  * <ul>
  * <li> Data from argument locale resource bundle if found, otherwise
  * <li> Data from parent locale resource bundle of arguemtn locale if found,
  *      otherwise
  * <li> Data from built-in default collation rules if found, other
  * <li> null is returned
  * </ul>
  * @param desiredLocale locale used
  * @param status error code status
  */
  RuleBasedCollator()
  {
    m_collator_ = NativeCollation.openCollator();
  }

  /**
  * RuleBasedCollator constructor. This constructor takes a locale. The 
  * only caller of this class should be Collator.createInstance(). 
  * Current implementation createInstance() returns a RuleBasedCollator(Locale) 
  * instance. The RuleBasedCollator will be created in the following order,
  * <ul>
  * <li> Data from argument locale resource bundle if found, otherwise
  * <li> Data from parent locale resource bundle of arguemtn locale if found,
  *      otherwise
  * <li> Data from built-in default collation rules if found, other
  * <li> null is returned
  * </ul>
  * @param desiredLocale locale used
  * @param status error code status
  */
  RuleBasedCollator(Locale locale)
  {
    if (locale == null) {
      m_collator_ = NativeCollation.openCollator();
    }
    else {
      m_collator_ = NativeCollation.openCollator(locale.toString());
    }
  }
  
  // protected methods --------------------------------------------
  
  /**
  * Garbage collection.
  * Close C collator and reclaim memory.
  */
  protected void finalize()
  {
    NativeCollation.closeCollator(m_collator_);
  }
  
  // private data members -----------------------------------------
  
  /**
  * C collator
  */
  private long m_collator_;
  
  /**
  * Hash code for rules
  */
  private int m_hashcode_ = 0;
  
  // private constructor -----------------------------------------
  
  /**
  * Private use constructor.
  * Does not create any instance of the C collator. Accepts argument as the
  * C collator for new instance.
  * @param collatoraddress address of C collator
  */
  private RuleBasedCollator(long collatoraddress)
  {
    m_collator_ = collatoraddress;
  }
}