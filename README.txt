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
License Information 
	The ICU projects (ICU4C and ICU4J) have changed their licenses from the IPL (IBM Public License) to the X license. 
	The X license is a non-viral and recommended free software license that is compatible with the GNU GPL license. 
	This is effective starting with release 1.8.1 of ICU4C and release 1.3.1 of ICU4J. All previous ICU releases will
	continue to utilize the IPL. New ICU releases will adopt the X license. The users of previous releases of ICU will 
	need to accept the terms and conditions of the X license in order to adopt the new ICU releases. The main effect 
	of the change is to provide GPL compatibility. The X license is isted as GPL compatible, see the gnu page at 				http://www.gnu.org/philosophy/license-list.html#GPLCompatibleLicenses The text of the X license is available at				http://www.x.org/terms.htm. The IBM version contains the essential text of the license, omitting the X-specific 
	trademarks and 	copyright notices. For more details please see the press announcement and the Project FAQ.


Installation

     UNIX
	* Downloaded and install ICU4C from http://oss.software.ibm.com/icu 
	* Set the ICU_DATA environment variable. 
	  eg: If ICU has been installed in /usr/local/lib 
	  export ICU_DATA=/usr/local/lib/icu/<icu_version>/
	* Set the JAVA_HOME environment variabale.
   	  eg: export JAVA_HOME=/java/jdk1.3
	* Run configure with the following command
	  ./configure --prefix=<path to the directory where ICU is installed>  --<enable/disable>-jdk14 

	  eg: ./cofigure --prefix=/usr/local/ --enable-jdk14

	* Run 'make check' to build and run tests

     Win32
	* Downloaded and install ICU4C from http://oss.software.ibm.com/icu 
	* Either set the ICU_DATA environment variable or pass ICUBIN on the command line 
   	  eg: set ICU_DATA=<icu_dir>\source\data
	* Either set the JAVA_HOME environment variabale or pass JAVAPATH on the command line.
   	  eg: set JAVA_HOME=c:\jdk1.3
	* Make sure <icu_dir>\bin directory is in you path
	* Make is configured to build with or without JDK 1.4 and with the ICU_DATA environment variable being phased out
from ICU the make needs to find out the location of ICU libraries.

	  For building with JDK 1.4 type the following command
   	  	nmake /f makefile-win32 CFG="Debug" ICUBIN="<location of ICU>\icu\bin" JDK14="TRUE" JAVAPATH="<location of JDK>\bin"

	  For building with JDK1.3 type the following command
	  	nmake /f makefile-win32 CFG="Debug" ICUBIN="<location of ICU>\bin" JDK14="FALSE" JAVAPATH="<location of JDK>\bin"

	* The files are built in <jniroot>/build directory	
	* run the test with the following command
   	  java -Djava.library.path=<source directory from root>\build\lib -classpath ;<source directory from root>/build/classes com.ibm.icu4jni.test.TestAll. 
   	  Eg:java -Djava.library.path=c:\work\icu4jni\build\lib -classpath=c:\work\icu4jni\build\classes com.ibm.icu4jni.test.TestAll

	  Alternatively you can also run the test by doing
	  	<full nmake command as described above> check

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

	
