# Makefile for the Java classes used to test the functional mockup interface actors
#
# @Authors: Christopher Hylands
#
# @Version: $Id$
#
# @Copyright (c) 1997-2015 The Regents of the University of California.
# All rights reserved.
#
# Permission is hereby granted, without written agreement and without
# license or royalty fees, to use, copy, modify, and distribute this
# software and its documentation for any purpose, provided that the
# above copyright notice and the following two paragraphs appear in all
# copies of this software.
#
# IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
# FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
# ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
# THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
#
# THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
# INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
# PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
# CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
# ENHANCEMENTS, OR MODIFICATIONS.
#
# 						PT_COPYRIGHT_VERSION_2
# 						COPYRIGHTENDKEY

# Location of this directory, relative to the Ptolemy II directory
ME =		ptolemy/actor/lib/fmi/test

# Root of the Ptolemy II directory
ROOT =		../../../../..

# JNA_JAR is set by $PTII/configure in $PTII/mk/ptII.mk
CLASSPATH =	$(ROOT)$(CLASSPATHSEPARATOR)$(JNA_JAR)

# Get configuration info
CONFIG =	$(ROOT)/mk/ptII.mk
include $(CONFIG)

# Tcl sources that are part of the testing infrastructure
TCL_SRCS = \
	testDefs.tcl

JSRCS =

# Non-graphical Java tests written in Tcl
JSIMPLE_TESTS = \
	FMUImport.tcl \
	FMUQSS.tcl

# Graphical Java tests that use Tcl.
# If there are no tests, we use a dummy file so that the script that builds
# alljtests.tcl works.  If you add a test, be sure to add
# $(JGRAPHICAL_TESTS) to EXTRA_SRCS
JGRAPHICAL_TESTS = \
	dummy.tcl

EXTRA_SRCS = \
	FMUModelicaSin.xml Modelica_Blocks_Math_Sin.fmu \
	Unnamed30.xml Unnamed.fmu Unnamed.xml \
	$(TCL_SRCS) $(JSRCS) $(JSIMPLE_TESTS) #$(JGRAPHICAL_TESTS)

# Sources that may or may not be present, but if they are present, we don't
# want make checkjunk to barf on them.
MISC_FILES =	alljtests.tcl  junit \
	auto

# make checkjunk will not report OPTIONAL_FILES as trash
# make distclean removes OPTIONAL_FILES
OPTIONAL_FILES =

JCLASS =	$(JSRCS:%.java=%.class)

# Don't include all or install rules here, we want the user
# to run 'make tests' to run the tests.

tests:: $(EXTRA_SRCS) jclass test_java test_jsimple

test_java: jclass

# PtolemyII-wide test definitions.
KERNEL_TESTDEFS = $(ROOT)/util/testsuite/testDefs.tcl

# These tests do not require a graphics terminal, but do use Jacl and Java
test_jsimple: $(EXTRA_SRCS) jclass $(KERNEL_TESTDEFS) alljtests.tcl test_auto
	PATH="$(PTII)/ptolemy/actor/lib/fmi/fmus/win32:$(PATH)" $(JTCLSH) alljtests.tcl


# Create reference csv files for fmus.
# See http://chess.eecs.berkeley.edu/ptexternal/wiki/Main/FMICertification
# fmuCheck command line arguments:
#   -f "Print all variables to the output file. Default is to only print outputs."
#   -s stopTime
#   -h stepSize
# -s and -h values are from FMUSDK-2.0.3/run_all.bat
# fmuCheck has bugs where it does not produce values for 0.8.  See https://trac.fmi-standard.org/ticket/329
FMUCHECK = fmuCheck

FMU20SIM_CS = $(HOME)/src/fmi/fmusdk2/fmu20/bin/fmusim_cs

REF_CSVS =  auto/bouncingBall20_ref.csv auto/dq20_ref.csv auto/inc20_ref.csv \
	auto/vanDerPol20_ref.csv
# Build all the *_ref.csv files
refs: $(REF_CSVS)

clean_refs:
	rm -rf $(REF_CSVS)

auto/bouncingBall20_ref.csv:
	$(FMU20SIM_CS) auto/bouncingBall20.fmu 4.0 0.01
	mv result.csv $@
	#$(FMUCHECK) -f -o $@ -s 4.0 -h 0.01 auto/bouncingBall20.fmu

auto/dq20_ref.csv:
	$(FMU20SIM_CS) auto/dq20.fmu 1.0 0.1
	mv result.csv $@
	#$(FMUCHECK) -f -o $@ -s 1.0 -h 0.1 auto/dq20.fmu

auto/inc20_ref.csv:
	$(FMUCHECK) -f -o $@ -s 15.0 -h 0.5 auto/inc20.fmu

auto/vanDerPol20_ref.csv:
	$(FMU20SIM_CS) auto/vanDerPol20.fmu 5.0 0.1
	mv result.csv $@
	#$(FMUCHECK) -f -o $@ -s 5.0 -h 0.1 auto/vanDerPol20.fmu

vanDerPol20_compare:
	java -jar ~/src/CSVCompare/CSVComparator.jar -olderFile=auto/vanDerPol20_ref.csv -youngerFile=auto/FMUVanDerPol20_out.csv -resultFile=/tmp/foo.csv

# Get the rest of the rules
include $(ROOT)/mk/ptcommon.mk
