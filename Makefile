# Generated automatically from Makefile.in by configure.
#/**********************************************************************
#*   Copyright (C) 2001, International Business Machines
#*   Corporation and others.  All Rights Reserved.
#***********************************************************************/
## Makefile.in for icu4jni
## Ram Viswanadha 

srcdir=.
top_srcdir=.

# srcdir must be set before this is included
include /usr/local/lib/icu/Makefile.inc


## Build directory information
top_builddir = .
subdir = /native
CSRC_DIR=src/native
BUILDDIR=build
BUILD_DIR=build/lib
JAVAC_EXEC= $(JAVA_HOME)/bin/javac
JAVA_EXEC= $(JAVA_HOME)/bin/java
JAVA_FLAGS=-d $(BUILDDIR)\classes
JAVA_T_FLAGS=-d $(BUILDDIR)/classes -classpath $(BUILDDIR)/classes;.
CLASSDIR=classes/com/ibm/icu4jni
CONVDIR=$(CLASSDIR)/converters
COMDIR=$(CLASSDIR)/common
TESTDIR=$(CLASSDIR)/test
COLDIR=$(CLASSDIR)/text
TTDIR=$(TESTDIR)/text
TCDIR=$(TESTDIR)/converters
SRCDIR=src
MAKE_CLASS=$(SRCDIR)/$(CONVDIR)/ICUConverterInterface.java \
$(SRCDIR)/$(COMDIR)/ErrorCode.java \
$(SRCDIR)/$(CONVDIR)/ByteToCharConverterICU.java \
$(SRCDIR)/$(CONVDIR)/CharToByteConverterICU.java \
$(SRCDIR)/$(CONVDIR)/ByteToCharGB18030.java \
$(SRCDIR)/$(CONVDIR)/CharToByteGB18030.java \
$(SRCDIR)/$(TCDIR)/TestConv.java \
$(SRCDIR)/$(COLDIR)/NativeCollation.java \
$(SRCDIR)/$(COLDIR)/CollationAttribute.java \
$(SRCDIR)/$(COLDIR)/CollationElementIterator.java \
$(SRCDIR)/$(COLDIR)/CollationKey.java \
$(SRCDIR)/$(COLDIR)/Collator.java \
$(SRCDIR)/$(COLDIR)/NormalizationMode.java \
$(SRCDIR)/$(COLDIR)/RuleBasedCollator.java \
$(SRCDIR)/$(TESTDIR)/TestAll.java \
$(SRCDIR)/$(TESTDIR)/TestFmwk.java \
$(SRCDIR)/$(TESTDIR)/TestLog.java \
$(SRCDIR)/$(TTDIR)/CollationElementIteratorTest.java \
$(SRCDIR)/$(TTDIR)/CollatorAPITest.java \
$(SRCDIR)/$(TTDIR)/CollatorPerformanceTest.java \
$(SRCDIR)/$(TTDIR)/CollatorRegressionTest.java \
$(SRCDIR)/$(TTDIR)/CollatorTest.java \
$(SRCDIR)/$(TTDIR)/CurrencyCollatorTest.java \
$(SRCDIR)/$(TTDIR)/DanishCollatorTest.java \
$(SRCDIR)/$(TTDIR)/DummyCollatorTest.java \
$(SRCDIR)/$(TTDIR)/EnglishCollatorTest.java \
$(SRCDIR)/$(TTDIR)/FinnishCollatorTest.java \
$(SRCDIR)/$(TTDIR)/FrenchCollatorTest.java \
$(SRCDIR)/$(TTDIR)/G7CollatorTest.java \
$(SRCDIR)/$(TTDIR)/GermanCollatorTest.java \
$(SRCDIR)/$(TTDIR)/KanaCollatorTest.java \
$(SRCDIR)/$(TTDIR)/MonkeyCollatorTest.java \
$(SRCDIR)/$(TTDIR)/SpanishCollatorTest.java \
$(SRCDIR)/$(TTDIR)/ThaiCollatorTest.java \
$(SRCDIR)/$(TTDIR)/TurkishCollatorTest.java


## Extra files to remove for 'make clean'
CLEANFILES = *~

## Target information
TARGET = $(BUILD_DIR)/libICUInterface1d.$(SO)
TARGETR = $(BUILD_DIR)/libICUInterface1.$(SO)

OBJECTS =  $(CSRC_DIR)/common/ErrorCode.o  $(CSRC_DIR)/collation/CollationInterface.o  $(CSRC_DIR)/converter/ConverterInterface.o

HADERS =   $(CSRC_DIR)/common/ErrorCode.h  $(CSRC_DIR)/collation/CollationInterface.h  $(CSRC_DIR)/converter/ConverterInterface.h

DEPS = $(OBJECTS:.o=.d) 

# We want to get the local copies of these files first, as the
# dependency. Even though they do get exported.
#LDFLAGS += -L../usort/lib -L./util 

# get the hostname the code is built on
CPPFLAGS := -I$(JAVA_HOME)/include  -I$(JAVA_HOME)/include/$(OSTYPE) -I $(CSRC_DIR)/common -I $(CSRC_DIR)/collation -I $(CSRC_DIR)/converter $(CPPFLAGS)

## List of phony targets
.PHONY : all all-local install install-local clean clean-local \
distclean distclean-local dist dist-local check check-local

## Clear suffix list
.SUFFIXES :

## List of standard targets
all: all-local java
install: install-local
clean: clean-local
distclean : distclean-local
dist: dist-local
check: check-local
debug: all-local java
release:all-release javar

all-local: $(TARGET) 

install-local: all-local install-headers install-library

install-library: all-local
	$(MKINSTALLDIRS) $(libdir)
	$(INSTALL) $(TARGET) $(libdir)/$(TARGET)


install-headers:
	$(MKINSTALLDIRS) $(includedir)
	@list='$(HEADERS)'; for file in $$list; do \
	 echo " $(INSTALL_DATA) $$file $(includedir)/$$file"; \
	 $(INSTALL_DATA) $$file $(includedir)/$$file; \
	done


dist-local:

clean-local:
	test -z "$(CLEANFILES)" || rm -f $(CLEANFILES)
	rm -f $(OBJECTS) $(BUILD_DIR)/$(TARGET)
	rm -f $(BUILD_DIR)/$(TARGETR)
	rm -rf $(BUILDDIR)/classes/com

distclean-local: clean-local
	rm -f Makefile $(DEPS)

check-local:

Makefile: $(srcdir)/Makefile.in  $(top_builddir)/config.status
	cd $(top_builddir) \
	 && CONFIG_FILES=$(subdir)/$@ CONFIG_HEADERS= $(SHELL) ./config.status

all-release: $(TARGETR)

# Make the class file
java : 
	$(JAVAC_EXEC) -g -d $(BUILDDIR)/classes $(MAKE_CLASS)

javar :
	$(JAVAC_EXEC) -g:none -O -d $(BUILDDIR)/classes $(MAKE_CLASS)

# ICULIBS includes all standard ICU libraries.
# if we wanted only part of the ICU, we could use (for example) just
#         '$(LIBS) -licu-uc -lusort' ...
$(TARGET) : $(OBJECTS)
	$(SHLIB.c) -o $@ $^ $(ICULIBS)

$(TARGETR) :$(OBJECTS)
	$(SHLIB.c) -mno-debug-info -o $@ $^ $(ICULIBS) 

ifneq ($(MAKECMDGOALS),distclean)
-include $(DEPS)
endif

