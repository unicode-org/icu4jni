# *****************************************************************************
# *
# *   Copyright (C) 1995-2000, International Business Machines
# *   Corporation and others.  All Rights Reserved.
# *
# *   Author: Ram Viswanadha 
# * 		  IBM
# *****************************************************************************
					   
				README
			
			ICU4JNI Version 1.8.1
Contents

	* Introduction
	* System Requirements
	* Installation

Introduction

	ICU4JNI contains Java Native Interface wrappers for ICU4C's character set conversion and collation libraries. 
	These wrappers 	are primarily used when performace of conversion and collation are needed. For using the code with 
	JDK 1.3 and lower version you need to have access to JVM's source code.

System Requirements:

	* JDK1.2 or higher
	* ICU4C built and Installed

Systems Supported:

	Operating System		Compiler		Additional Requirements
	
	AIX 4.3				xlC			gmake	
	Solaris 2.6			GCC			gmake
	Solaris 2.7			GCC			gmake
	RedHat/Linux 6.0		GCC			gmake	
	RedHat/Linux 6.1		GCC			gmake
	RedHat Alpha/Linux 6.1		GCC			gmake
	Win95				CL			nmake
	Win98				CL			nmake
	WinNT				CL			nmake
	Win2000   			CL			nmake
 
Installation

     UNIX
	* Downloaded and install ICU4C from http://oss.software.ibm.com/icu 
	* Set the ICU_DATA environment variable. 
	  eg: If ICU has been installed in /usr/local/lib 
	  export ICU_DATA=/usr/local/lib/icu/<icu_version>/
	* Set the JAVA_HOME environment variabale.
   	  eg: export JAVA_HOME=/java/jdk1.3
	* Run configure with the following command
	  ./configure -prefix=<path to the directory where ICU is installed>
	  eg: ./cofigure --prefix=/usr/local/
	* Run 'make check' to build and run tests

     Win32
	* Downloaded and install ICU4C from http://oss.software.ibm.com/icu 
	* Set the ICU_DATA environment variable. 
   	  eg: set ICU_DATA=<icu_dir>\source\data
	* Set the JAVA_HOME environment variabale.
   	  eg: set JAVA_HOME=c:\jdk1.3
	* Make sure <icu_dir>\bin directory is in you path
	* Type the following command
   	  nmake /f makefile-win32
	* The files are built in <jniroot>/build directory	
	* cd to <jniroot>/build directory
	* run the test with the following command
   	  java -Djava.library.path=<source directory from root>/com/ibm/icu4jni/test/converters/TestConv. 
   	  Eg:java -Djava.library.path=c:\work\icu4jni\build\lib com/ibm/icu4jni/test/converters/TestConv

For Integrating into JVM only:
	* Place the java source files in $(TARGETDIR)/com,i.e, <jdksource>/com 
	* Add the following lines to <jdk>/make/mkinclude/java_java.jmk
		$(TARGDIR)com/ibm/icu4jni/ICUJNIInterface.java \
		$(TARGDIR)com/ibm/icu4jni/CharToByteConverterICU.java \
		$(TARGDIR)com/ibm/icu4jni/ByteToCharConverterICU.java \
		# the files below are test classes
		$(TARGDIR)com/ibm/icu4jni/ByteToCharGB18030.java \
		$(TARGDIR)com/ibm/icu4jni/CharToByteGB18030.java \
	* The JNI wrappers work under the assumption that ICU4C is available and installed on the target platform.
	* For adding additional codepages open CharacterEncoding.java in <jdksource>/sun/io directory and add the 
	  aliases to the converter
		for eg:
		For GB18030 support you add:
		aliasTable.put( "gb18030" , "GB18030");  
		aliasTable.put( "gb-18030", "GB18030");  
		aliasTable.put( "gb_18030", "GB18030");  
		aliasTable.put( "GB-18030", "GB18030");  
	* Compile JNI wrapper code into dll/so using a C compiler
	* Package the ICUJNIInterface.dll <jdkbuild>/<platform>/bin directory 
	* Rebuild the JDK source

	
