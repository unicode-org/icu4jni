/*
 *******************************************************************************
 * Copyright (C) 1996-2000, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 *
 * $Source: /xsrl/Nsvn/icu/icu4jni/src/classes/com/ibm/icu4jni/test/text/BasicTest.java,v $ 
 * $Date: 2001/09/18 00:33:49 $ 
 * $Revision: 1.1 $
 *
 *****************************************************************************************
 */
package com.ibm.icu4jni.test.text;

import com.ibm.icu4jni.test.*;
import com.ibm.icu4jni.text.*;
//import com.ibm.util.Utility;
//import java.text.CharacterIterator;
//import java.text.StringCharacterIterator;

public class BasicTest extends TestFmwk {
    public static void main(String[] args) throws Exception {
        new BasicTest().run(args);
    }

    String[][] canonTests = {
        // Input                Decomposed              Composed
        { "cat",                "cat",                  "cat"               },
        { "\u00e0ardvark",      "a\u0300ardvark",       "\u00e0ardvark",    },

        { "\u1e0a",             "D\u0307",              "\u1e0a"            }, // D-dot_above
        { "D\u0307",            "D\u0307",              "\u1e0a"            }, // D dot_above

        { "\u1e0c\u0307",       "D\u0323\u0307",        "\u1e0c\u0307"      }, // D-dot_below dot_above
        { "\u1e0a\u0323",       "D\u0323\u0307",        "\u1e0c\u0307"      }, // D-dot_above dot_below
        { "D\u0307\u0323",      "D\u0323\u0307",        "\u1e0c\u0307"      }, // D dot_below dot_above

        { "\u1e10\u0307\u0323", "D\u0327\u0323\u0307",  "\u1e10\u0323\u0307"}, // D dot_below cedilla dot_above
        { "D\u0307\u0328\u0323","D\u0328\u0323\u0307",  "\u1e0c\u0328\u0307"}, // D dot_above ogonek dot_below

        { "\u1E14",             "E\u0304\u0300",        "\u1E14"            }, // E-macron-grave
        { "\u0112\u0300",       "E\u0304\u0300",        "\u1E14"            }, // E-macron + grave
        { "\u00c8\u0304",       "E\u0300\u0304",        "\u00c8\u0304"      }, // E-grave + macron

        { "\u212b",             "A\u030a",              "\u00c5"            }, // angstrom_sign
        { "\u00c5",             "A\u030a",              "\u00c5"            }, // A-ring

//        { "\u00fdffin",              "A\u0308ffin",          "\u00fdffin"             },
        { "\u00fdffin",              "y\u0301ffin",          "\u00fdffin"             },	//updated with 3.0
//        { "\u00fd\uFB03n",           "A\u0308\uFB03n",       "\u00fd\uFB03n"          },
        { "\u00fd\uFB03n",           "y\u0301\uFB03n",       "\u00fd\uFB03n"          },	//updated with 3.0

        { "Henry IV",           "Henry IV",             "Henry IV"          },
        { "Henry \u2163",       "Henry \u2163",         "Henry \u2163"      },

        { "\u30AC",             "\u30AB\u3099",         "\u30AC"            }, // ga (Katakana)
        { "\u30AB\u3099",       "\u30AB\u3099",         "\u30AC"            }, // ka + ten
        { "\uFF76\uFF9E",       "\uFF76\uFF9E",         "\uFF76\uFF9E"      }, // hw_ka + hw_ten
        { "\u30AB\uFF9E",       "\u30AB\uFF9E",         "\u30AB\uFF9E"      }, // ka + hw_ten
        { "\uFF76\u3099",       "\uFF76\u3099",         "\uFF76\u3099"      }, // hw_ka + ten

        { "A\u0300\u0316", "A\u0316\u0300", "\u00C0\u0316" },
    };

    String[][] compatTests = {
            // Input                Decomposed              Composed
        { "\uFB4f",             "\u05D0\u05DC",         "\u05D0\u05DC",     }, // Alef-Lamed vs. Alef, Lamed

//        { "\u00fdffin",              "A\u0308ffin",          "\u00fdffin"             },
//       { "\u00fd\uFB03n",           "A\u0308ffin",          "\u00fdffin"             }, // ffi ligature -> f + f + i
        { "\u00fdffin",              "y\u0301ffin",          "\u00fdffin"             },	//updated for 3.0
        { "\u00fd\uFB03n",           "y\u0301ffin",          "\u00fdffin"             }, // ffi ligature -> f + f + i

        { "Henry IV",           "Henry IV",             "Henry IV"          },
        { "Henry \u2163",       "Henry IV",             "Henry IV"          },

        { "\u30AC",             "\u30AB\u3099",         "\u30AC"            }, // ga (Katakana)
        { "\u30AB\u3099",       "\u30AB\u3099",         "\u30AC"            }, // ka + ten

        { "\uFF76\u3099",       "\u30AB\u3099",         "\u30AC"            }, // hw_ka + ten

        /* These two are broken in Unicode 2.1.2 but fixed in 2.1.5 and later
        { "\uFF76\uFF9E",       "\u30AB\u3099",         "\u30AC"            }, // hw_ka + hw_ten
        { "\u30AB\uFF9E",       "\u30AB\u3099",         "\u30AC"            }, // ka + hw_ten
        */
    };

    // With Canonical decomposition, Hangul syllables should get decomposed
    // into Jamo, but Jamo characters should not be decomposed into
    // conjoining Jamo
    String[][] hangulCanon = {
        // Input                Decomposed              Composed
        { "\ud4db",             "\u1111\u1171\u11b6",   "\ud4db"        },
        { "\u1111\u1171\u11b6", "\u1111\u1171\u11b6",   "\ud4db"        },
    };

    // With compatibility decomposition turned on,
    // it should go all the way down to conjoining Jamo characters.
    // THIS IS NO LONGER TRUE IN UNICODE v2.1.8, SO THIS TEST IS OBSOLETE
    String[][] hangulCompat = {
        // Input        Decomposed                          Composed
        // { "\ud4db",     "\u1111\u116e\u1175\u11af\u11c2",   "\ud478\u1175\u11af\u11c2"  },
    };

    public void TestHangulCompose() {
        // Make sure that the static composition methods work
        logln("Canonical composition...");
        staticTest(Normalizer.UNORM_NFC,        0, hangulCanon,  2);
        logln("Compatibility composition...");
        staticTest(Normalizer.UNORM_NFKC, 0, hangulCompat, 2);
     }

    public void TestHangulDecomp() {
        // Make sure that the static decomposition methods work
        logln("Canonical decomposition...");
        staticTest(Normalizer.UNORM_NFD,        0, hangulCanon,  1);
        logln("Compatibility decomposition...");
        staticTest(Normalizer.UNORM_NFKD, 0, hangulCompat, 1);
    }

    public void TestDecomp() {
        staticTest(Normalizer.UNORM_NFD, 0, canonTests, 1);
    }

    public void TestCompatDecomp() {
        staticTest(Normalizer.UNORM_NFKD, 0, compatTests, 1);
    }

    public void TestCanonCompose() {
        staticTest(Normalizer.UNORM_NFC, 0, canonTests, 2);
    }

    public void TestCompatCompose() {
        staticTest(Normalizer.UNORM_NFKC, 0, compatTests, 2);
    }

    public void TestExplodingBase() {
        // \u017f - Latin small letter long s
        // \u0307 - combining dot above
        // \u1e61 - Latin small letter s with dot above
        // \u1e9b - Latin small letter long s with dot above
        String[][] canon = {
            // Input                Decomposed              Composed
            { "Tschu\u017f",        "Tschu\u017f",          "Tschu\u017f"       },
            { "Tschu\u1e9b",        "Tschu\u017f\u0307",    "Tschu\u1e9b"       },
        };
        String[][] compat = {
            // Input                Decomposed              Composed
            { "\u017f",        "s",              "s"           },
            { "\u1e9b",        "s\u0307",        "\u1e61"      },
        };

        staticTest(Normalizer.UNORM_NFD,           0, canon,  1);
        staticTest(Normalizer.UNORM_NFC,          0, canon,  2);

        staticTest(Normalizer.UNORM_NFKD,    0, compat, 1);
        staticTest(Normalizer.UNORM_NFKC,   0, compat, 2);

    }

    /**
     * The Tibetan vowel sign AA, 0f71, was messed up prior to Unicode version 2.1.9.
     * Once 2.1.9 or 3.0 is released, uncomment this test.
     */
    public void TestTibetan() {
        String[][] decomp = {
            { "\u0f77", "\u0f77", "\u0fb2\u0f71\u0f80" }
        };
        String[][] compose = {
            { "\u0fb2\u0f71\u0f80", "\u0fb2\u0f71\u0f80", "\u0fb2\u0f71\u0f80" }
        };

        staticTest(Normalizer.UNORM_NFD,           0, decomp, 1);
        staticTest(Normalizer.UNORM_NFKD,    0, decomp, 2);
        staticTest(Normalizer.UNORM_NFC,          0, compose, 1);
        staticTest(Normalizer.UNORM_NFKC,   0, compose, 2);
    }

    /**
     * Make sure characters in the CompositionExclusion.txt list do not get
     * composed to.
     */
    public void TestCompositionExclusion() {
        // This list is generated from CompositionExclusion.txt.
        // Update whenever the normalizer tables are updated.  Note
        // that we test all characters listed, even those that can be
        // derived from the Unicode DB and are therefore commented
        // out.
        String EXCLUDED = 
            "\u0340\u0341\u0343\u0344\u0374\u037E\u0387\u0958" +
            "\u0959\u095A\u095B\u095C\u095D\u095E\u095F\u09DC" +
            "\u09DD\u09DF\u0A33\u0A36\u0A59\u0A5A\u0A5B\u0A5E" +
            "\u0B5C\u0B5D\u0F43\u0F4D\u0F52\u0F57\u0F5C\u0F69" +
            "\u0F73\u0F75\u0F76\u0F78\u0F81\u0F93\u0F9D\u0FA2" +
            "\u0FA7\u0FAC\u0FB9\u1F71\u1F73\u1F75\u1F77\u1F79" +
            "\u1F7B\u1F7D\u1FBB\u1FBE\u1FC9\u1FCB\u1FD3\u1FDB" +
            "\u1FE3\u1FEB\u1FEE\u1FEF\u1FF9\u1FFB\u1FFD\u2000" +
            "\u2001\u2126\u212A\u212B\u2329\u232A\uF900\uFA10" +
            "\uFA12\uFA15\uFA20\uFA22\uFA25\uFA26\uFA2A\uFB1F" +
            "\uFB2A\uFB2B\uFB2C\uFB2D\uFB2E\uFB2F\uFB30\uFB31" +
            "\uFB32\uFB33\uFB34\uFB35\uFB36\uFB38\uFB39\uFB3A" +
            "\uFB3B\uFB3C\uFB3E\uFB40\uFB41\uFB43\uFB44\uFB46" +
            "\uFB47\uFB48\uFB49\uFB4A\uFB4B\uFB4C\uFB4D\uFB4E";
        for (int i=0; i<EXCLUDED.length(); ++i) {
            String a = String.valueOf(EXCLUDED.charAt(i));
            String b = Normalizer.normalize(a, Normalizer.UNORM_NFKD);
            String c = Normalizer.normalize(b, Normalizer.UNORM_NFC);
            if (c.equals(a)) {
                errln("FAIL: " + hex(a) + " x DECOMP_COMPAT => " +
                      hex(b) + " x COMPOSE => " +
                      hex(c));
            } else if (isVerbose()) {
                logln("Ok: " + hex(a) + " x DECOMP_COMPAT => " +
                      hex(b) + " x COMPOSE => " +
                      hex(c));                
            }
        }
        // The following method works too, but it is somewhat
        // incestuous.  It uses UInfo, which is the same database that
        // NormalizerBuilder uses, so if something is wrong with
        // UInfo, the following test won't show it.  All it will show
        // is that NormalizerBuilder has been run with whatever the
        // current UInfo is.
        // 
        // We comment this out in favor of the test above, which
        // provides independent verification (but also requires
        // independent updating).
//      logln("---");
//      UInfo uinfo = new UInfo();
//      for (int i=0; i<=0xFFFF; ++i) {
//          if (!uinfo.isExcludedComposition((char)i) ||
//              (!uinfo.hasCanonicalDecomposition((char)i) &&
//               !uinfo.hasCompatibilityDecomposition((char)i))) continue;
//          String a = String.valueOf((char)i);
//          String b = Normalizer.normalize(a, Normalizer.DECOMP_COMPAT, 0);
//          String c = Normalizer.normalize(b, Normalizer.COMPOSE, 0);
//          if (c.equals(a)) {
//              errln("FAIL: " + hex(a) + " x DECOMP_COMPAT => " +
//                    hex(b) + " x COMPOSE => " +
//                    hex(c));
//          } else if (isVerbose()) {
//              logln("Ok: " + hex(a) + " x DECOMP_COMPAT => " +
//                    hex(b) + " x COMPOSE => " +
//                    hex(c));                
//          }
//      }
    }

    /**
     * Test for a problem that showed up just before ICU 1.6 release
     * having to do with combining characters with an index of zero.
     * Such characters do not participate in any canonical
     * decompositions.  However, having an index of zero means that
     * they all share one typeMask[] entry, that is, they all have to
     * map to the same canonical class, which is not the case, in
     * reality.
     */
    public void TestZeroIndex() {
        String[] DATA = {
            // Expect col1 x COMPOSE_COMPAT => col2
            // Expect col2 x DECOMP => col3
            "A\u0316\u0300", "\u00C0\u0316", "A\u0316\u0300",
            "A\u0300\u0316", "\u00C0\u0316", "A\u0316\u0300",
            "A\u0327\u0300", "\u00C0\u0327", "A\u0327\u0300",
            "c\u0321\u0327", "c\u0321\u0327", "c\u0321\u0327",
            "c\u0327\u0321", "\u00E7\u0321", "c\u0327\u0321",
        };

        for (int i=0; i<DATA.length; i+=3) {
            String a = DATA[i];
            String b = Normalizer.normalize(a, Normalizer.UNORM_NFKC);
            String exp = DATA[i+1];
            if (b.equals(exp)) {
                logln("Ok: " + hex(a) + " x COMPOSE_COMPAT => " + hex(b));
            } else {
                errln("FAIL: " + hex(a) + " x COMPOSE_COMPAT => " + hex(b) +
                      ", expect " + hex(exp));
            }
            a = Normalizer.normalize(b, Normalizer.UNORM_NFD);
            exp = DATA[i+2];
            if (a.equals(exp)) {
                logln("Ok: " + hex(b) + " x DECOMP => " + hex(a));
            } else {
                errln("FAIL: " + hex(b) + " x DECOMP => " + hex(a) +
                      ", expect " + hex(exp));
            }
        }
    }

    /**
     * Test for a problem found by Verisign.  Problem is that
     * characters at the start of a string are not put in canonical
     * order correctly by compose() if there is no starter.
     */
    public void TestVerisign() {
        String[] inputs = {
            "\u05b8\u05b9\u05b1\u0591\u05c3\u05b0\u05ac\u059f",
            "\u0592\u05b7\u05bc\u05a5\u05b0\u05c0\u05c4\u05ad"
        };
        String[] outputs = {
            "\u05b1\u05b8\u05b9\u0591\u05c3\u05b0\u05ac\u059f",
            "\u05b0\u05b7\u05bc\u05a5\u0592\u05c0\u05ad\u05c4"
        };

        for (int i = 0; i < inputs.length; ++i) {
            String input = inputs[i];
            String output = outputs[i];
            String result = Normalizer.decompose(input, false);
            if (!result.equals(output)) {
                errln("FAIL input: " + TestFmwk.escape(input));
                errln(" decompose: " + TestFmwk.escape(result));
                errln("  expected: " + TestFmwk.escape(output));
            }
            result = Normalizer.compose(input, false);
            if (!result.equals(output)) {
                errln("FAIL input: " + TestFmwk.escape(input));
                errln("   compose: " + TestFmwk.escape(result));
                errln("  expected: " + TestFmwk.escape(output));
            }
        }
    }
    public void  TestQuickCheckResultNO(){
        final char CPNFD[] = {0x00C5, 0x0407, 0x1E00, 0x1F57, 0x220C, 
                                0x30AE, 0xAC00, 0xD7A3, 0xFB36, 0xFB4E};
        final char CPNFC[] = {0x0340, 0x0F93, 0x1F77, 0x1FBB, 0x1FEB, 
                                0x2000, 0x232A, 0xF900, 0xFA1E, 0xFB4E};
        final char CPNFKD[] = {0x00A0, 0x02E4, 0x1FDB, 0x24EA, 0x32FE, 
                                0xAC00, 0xFB4E, 0xFA10, 0xFF3F, 0xFA2D};
        final char CPNFKC[] = {0x00A0, 0x017F, 0x2000, 0x24EA, 0x32FE, 
                                0x33FE, 0xFB4E, 0xFA10, 0xFF3F, 0xFA2D};


        final int SIZE = 10;

        int count = 0;
        for (; count < SIZE; count ++)
        {
            if (Normalizer.quickCheck(String.valueOf(CPNFD[count]), Normalizer.UNORM_NFD) != 
                                                                    Normalizer.UNORM_NO)
            {
                errln("ERROR in NFD quick check at U+" + Integer.toHexString(CPNFD[count]));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(CPNFC[count]), Normalizer.UNORM_NFC) != 
                                                                    Normalizer.UNORM_NO)
            {
                errln("ERROR in NFC quick check at U+"+ Integer.toHexString(CPNFC[count]));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(CPNFKD[count]),Normalizer.UNORM_NFKD) != 
                                                                    Normalizer.UNORM_NO)
            {
                errln("ERROR in NFKD quick check at U+"+ Integer.toHexString(CPNFKD[count]));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(CPNFKC[count]), Normalizer.UNORM_NFKC) != 
                                                                    Normalizer.UNORM_NO)
            {
                errln("ERROR in NFKC quick check at U+"+ Integer.toHexString(CPNFKC[count]));
                return;
            }
        }
    }

 
    public void TestQuickCheckResultYES() {
        final char CPNFD[] = {0x00C6, 0x017F, 0x0F74, 0x1000, 0x1E9A, 
                                0x2261, 0x3075, 0x4000, 0x5000, 0xF000};
        final char CPNFC[] = {0x0400, 0x0540, 0x0901, 0x1000, 0x1500, 
                                0x1E9A, 0x3000, 0x4000, 0x5000, 0xF000};
        final char CPNFKD[] = {0x00AB, 0x02A0, 0x1000, 0x1027, 0x2FFB, 
                                0x3FFF, 0x4FFF, 0xA000, 0xF000, 0xFA27};
        final char CPNFKC[] = {0x00B0, 0x0100, 0x0200, 0x0A02, 0x1000, 
                                0x2010, 0x3030, 0x4000, 0xA000, 0xFA0E};

        final int SIZE = 10;
        int count = 0;

        char cp = 0;
        while (cp < 0xA0)
        {
            if (Normalizer.quickCheck(String.valueOf(cp), Normalizer.UNORM_NFD) != Normalizer.UNORM_YES)
            {
                errln("ERROR in NFD quick check at U+"+ Integer.toHexString(cp));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(cp), Normalizer.UNORM_NFC) != 
                                                                    Normalizer.UNORM_YES)
            {
                errln("ERROR in NFC quick check at U+"+ Integer.toHexString(cp));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(cp), Normalizer.UNORM_NFKD) != Normalizer.UNORM_YES)
            {
                errln("ERROR in NFKD quick check at U+" + Integer.toHexString(cp));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(cp), Normalizer.UNORM_NFKC) != 
                                                                    Normalizer.UNORM_YES)
            {
                errln("ERROR in NFKC quick check at U+"+ Integer.toHexString(cp));
                return;
            }
            cp++;
        }

        for (; count < SIZE; count ++)
        {
            if (Normalizer.quickCheck(String.valueOf(CPNFD[count]),  Normalizer.UNORM_NFD) != 
                                                                    Normalizer.UNORM_YES)
            {
                errln("ERROR in NFD quick check at U+"+ Integer.toHexString(CPNFD[count]));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(CPNFC[count]),  Normalizer.UNORM_NFC) 
                                                                != Normalizer.UNORM_YES)
            {
                errln("ERROR in NFC quick check at U+"+ Integer.toHexString(CPNFC[count]));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(CPNFKD[count]), Normalizer.UNORM_NFKD) != 
                                                                    Normalizer.UNORM_YES)
            {
                errln("ERROR in NFKD quick check at U+"+ Integer.toHexString(CPNFKD[count]));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(CPNFKC[count]), Normalizer.UNORM_NFKC) != 
                                                                    Normalizer.UNORM_YES)
            {
                errln("ERROR in NFKC quick check at U+"+ Integer.toHexString(CPNFKC[count]));
                return;
            }
        }
    }

    public void TestQuickCheckResultMAYBE() {
        
        final char CPNFC[] = {0x0306, 0x0654, 0x0BBE, 0x102E, 0x1161, 
                                0x116A, 0x1173, 0x1175, 0x3099, 0x309A};
        final char CPNFKC[] = {0x0300, 0x0654, 0x0655, 0x09D7, 0x0B3E, 
                                0x0DCF, 0xDDF, 0x102E, 0x11A8, 0x3099};


        final int SIZE = 10;

        int count = 0;

        /* NFD and NFKD does not have any MAYBE codepoints */
        for (; count < SIZE; count ++)
        {
            if (Normalizer.quickCheck(String.valueOf(CPNFC[count]), Normalizer.UNORM_NFC) != 
                                                                Normalizer.UNORM_MAYBE)
            {
                errln("ERROR in NFC quick check at U+"+ Integer.toHexString(CPNFC[count]));
                return;
            }
            if (Normalizer.quickCheck(String.valueOf(CPNFKC[count]), Normalizer.UNORM_NFKC) != 
                                                                Normalizer.UNORM_MAYBE)
            {
                errln("ERROR in NFKC quick check at U+"+ Integer.toHexString(CPNFKC[count]));
                return;
            }
        }
    }
    
    public void TestQuickCheckStringResult() 
    {
        int count;
        String d;
        String c;

        for (count = 0; count < canonTests.length; count ++)
        {
            d = canonTests[count][1];
            c = canonTests[count][2];
            if (Normalizer.quickCheck(d,Normalizer.UNORM_NFD) != Normalizer.UNORM_YES)
            {
                errln("ERROR in NFD quick check for string at count " + count);
                return;
            }

            if (Normalizer.quickCheck(c, Normalizer.UNORM_NFC) == Normalizer.UNORM_NO)
            {
                errln("ERROR in NFC quick check for string at count " + count);
                return;
            }
        }

        for (count = 0; count < compatTests.length; count ++)
        {
            d = compatTests[count][1];
            c = compatTests[count][2];
            if (Normalizer.quickCheck(d, Normalizer.UNORM_NFKD) != Normalizer.UNORM_YES)
            {
                errln("ERROR in NFKD quick check for string at count " + count);
                return;
            }

            if (Normalizer.quickCheck(c,  Normalizer.UNORM_NFKC) != Normalizer.UNORM_YES)
            {
                errln("ERROR in NFKC quick check for string at count " + count);
                return;
            }
        }  
    }
    //------------------------------------------------------------------------
    // Internal utilities
    //
   
    private void staticTest(int mode, int options, String[][] tests, int outCol)
    {
        for (int i = 0; i < tests.length; i++)
        {
            String input = tests[i][0];
            String expect = tests[i][outCol];

            logln("Normalizing '" + input + "' (" + hex(input) + ")" );

            String output = Normalizer.normalize(input, mode);

            if (!output.equals(expect)) {
                errln("FAIL: case " + i
                    + " expected '" + expect + "' (" + hex(expect) + ")"
                    + " but got '" + output + "' (" + hex(output) + ")" );
            }
        }
    }  
}
