/**
*******************************************************************************
* Copyright (C) 1996-2001, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*
* $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/text/Attic/NormalizationMode.java,v $ 
* $Date: 2001/03/23 19:42:16 $ 
* $Revision: 1.5 $
*
*******************************************************************************
*/

package com.ibm.icu4jni.text;

/**
* Internal interface for storing ICU normalization equivalent enum values.
* Used by RuleBaseCollator.
* @author syn wee quek
* @since Jan 18 01
*/

public final class NormalizationMode
{ 
  // public static data members -----------------------------------
  
  public static final int NO_NORMALIZATION = 1;
  /** 
  * Canonical decomposition 
  */
  public static final int DECOMP_CAN = 2;
  /** 
  * Compatibility decomposition 
  */
  public static final int DECOMP_COMPAT = 3;
  /** 
  * Default normalization 
  */
  public static final int DEFAULT_NORMALIZATION = DECOMP_COMPAT;
  /** 
  * Canonical decomposition followed by canonical composition 
  */
  public static final int DECOMP_CAN_COMP_COMPAT = 4;
  /** 
  * Compatibility decomposition followed by canonical composition 
  */
  public static final int DECOMP_COMPAT_COMP_CAN = 5;
  
  // public methods ------------------------------------------------------
  
  /**
  * Checks if argument is a valid normalization format for use
  * @param normalization format
  * @return true if strength is a valid collation strength, false otherwise
  */
  static boolean check(int normalization)
  {
    if (normalization < NO_NORMALIZATION || 
        (normalization > DECOMP_COMPAT_COMP_CAN))
      return false;
    return true;
  }
}
