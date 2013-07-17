/* Functional Mock-up Interface (FMI) callback functions.

   Copyright (c) 2012 The Regents of the University of California.
   All rights reserved.
   Permission is hereby granted, without written agreement and without
   license or royalty fees, to use, copy, modify, and distribute this
   software and its documentation for any purpose, provided that the above
   copyright notice and the following two paragraphs appear in all copies
   of this software.

   IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
   FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
   ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
   THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
   SUCH DAMAGE.

   THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
   PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
   CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
   ENHANCEMENTS, OR MODIFICATIONS.

   PT_COPYRIGHT_VERSION_2
   COPYRIGHTENDKEY

 */

package org.ptolemy.fmi;

import java.util.Arrays;
import java.util.List;

import org.ptolemy.fmi.FMILibrary.FMICallbackAllocateMemory;
import org.ptolemy.fmi.FMILibrary.FMICallbackFreeMemory;
import org.ptolemy.fmi.FMILibrary.FMICallbackLogger;
import org.ptolemy.fmi.FMILibrary.FMIStepFinished;

import com.sun.jna.Structure;

/**
 * Functional Mock-up Interface (FMI) callback functions needed by
 * Java Native Access (JNA) so that a FMU * can perform functions like
 * allocating and freeing memory, printing log messages and handle the
 * end of a step.
 *
 * <p>The C language interface to Functional Mock-up Unit (FMU) files
 * includes a fmiCallbackFunctions struct whose elements are callbacks
 * to methods are called to log status messages, allocate memory,
 * free memory and to notify the system that the step is finished.
 * This class encapsulates those callbacks.</p>
 *
 * <p>For details about how Callbacks work in JNA, see
 * <a href="http://twall.github.com/jna/3.4.0/javadoc/overview-summary.html#callbacks">http://twall.github.com/jna/3.4.0/javadoc/overview-summary.html#callbacks</a>.</p>
 *
 * <p>This file is based on a file that was autogenerated by
 * <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br> a tool
 * written by <a href="http://ochafik.com/">Olivier Chafik</a> that
 * <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses
 * a few opensource projects.</a>.</p>
 *
 * @author Christopher Brooks
 * @version $Id$
 * @Pt.ProposedRating Red (cxh)
 * @Pt.AcceptedRating Red (cxh)
 */
public class FMICallbackFunctions extends Structure {
    // Note that the name of this class starts with a capital letter because
    // the naming convention is that Java classes start with a capital letter.
    // However, this class represents a C structure whose name starts with a
    // lower case letter.

    /** Instantiate a Java representation of the C structure that
     * contains the FMI call backs.
     */
    public FMICallbackFunctions() {
        super();
        // Don't call initFieldOrder with JNA later than jna-3.5.0
        //initFieldOrder();
    }

    /** Instantiate a Java representation of the C structure that
     * contains the FMI call backs.
     * @param logger The method called to log a status message
     * (C type: fmiCallbackLogger).
     * @param allocateMemory The method called to allocate cleared memory
     * (C type: fmiCallbackAllocateMemory
     * @param freeMemory The method called to free allocated memory
     * (C type: fmiCallbackFreeMemory)
     * @param stepFinished The method called when the step is finished.
     * (C type: FmiStepFinished)
     */
    public FMICallbackFunctions(FMICallbackLogger logger,
            FMICallbackAllocateMemory allocateMemory,
            FMICallbackFreeMemory freeMemory, FMIStepFinished stepFinished) {
        super();
        this.logger = logger;
        this.allocateMemory = allocateMemory;
        this.freeMemory = freeMemory;
        this.stepFinished = stepFinished;
        // Avoid crashes by aligning.
        // See
        // http://today.java.net/article/2009/11/11/simplify-native-code-access-jna
        setAlignType(Structure.ALIGN_GNUC);

        // Don't call initFieldOrder with JNA later than jna-3.5.0
        //initFieldOrder();
    }

    /**
     *  A class that contains references to the callback functions.
     *
     *  <p>This class is used to register callbacks and pass the callbacks
     *  to the fmiInstantiateSlave() or fmiInstantiateModel() method.
     *  For example FMUCoSimulation.java contains the following code:</p>
     *
     *  <pre>
     *   FMICallbackFunctions.ByValue callbacks = new FMICallbackFunctions.ByValue(
     *           new FMULibrary.FMULogger(), new FMULibrary.FMUAllocateMemory(),
     *           new FMULibrary.FMUFreeMemory(),
     *           new FMULibrary.FMUStepFinished());
     *
     *   Function instantiateSlave = fmiModelDescription.getFmiFunction("fmiInstantiateSlave");
     *
     *   Pointer fmiComponent = (Pointer) instantiateSlave.invoke(Pointer.class,
     *            new Object[] { _modelIdentifier, fmiModelDescription.guid,
     *                    fmuLocation, mimeType, timeout, visible, interactive,
     *                   callbacks, loggingOn });
     *  </pre>
     *
     *  <p>The Structure.ByValue interface is a marker interface that indicates
     *  that the <b>value</b> value of a Structure is to be used by function
     *  invocations instead of the address of the Structure.  This is necessary
     *  because the default is that Structure arguments and return values
     *  are usually accessed by reference.  For details, see
     * <a href="http://twall.github.com/jna/3.4.0/javadoc/com/sun/jna/Structure.ByValue.html">http://twall.github.com/jna/3.4.0/javadoc/com/sun/jna/Structure.ByValue.html</a>.</p>
     *
     *  <p>For details about how Callbacks work in JNA, see
     *  <a href="http://twall.github.com/jna/3.4.0/javadoc/overview-summary.html#callbacks">http://twall.github.com/jna/3.4.0/javadoc/overview-summary.html#callbacks</a>.</p>
     */
    public static class ByValue extends FMICallbackFunctions implements
            Structure.ByValue {
        /**  Access the structure by reference.
         *
         *  <p>See
         *  <a href="http://twall.github.com/jna/3.4.0/javadoc/overview-summary.html#structures">http://twall.github.com/jna/3.4.0/javadoc/overview-summary.html#structures</a>:
         *  "To pass a structure by value, first define the structure,
         *  then define an empty class from that which implements
         *  Structure.ByValue. Use the ByValue class as the argument
         *  or return type."
         * @param logger The method called to log a status message
         * (C type: fmiCallbackLogger).
         * @param allocateMemory The method called to allocate cleared memory
         * (C type: fmiCallbackAllocateMemory
         * @param freeMemory The method called to free allocated memory
         * (C type: fmiCallbackFreeMemory)
         * @param stepFinished The method called when the step is finished.
         * (C type: FmiStepFinished)
         */
        public ByValue(FMICallbackLogger logger,
                FMICallbackAllocateMemory allocateMemory,
                FMICallbackFreeMemory freeMemory, FMIStepFinished stepFinished) {
            super(logger, allocateMemory, freeMemory, stepFinished);
        }
    };

    /** C type: fmiCallbackLogger. */
    public FMICallbackLogger logger;

    /** C type : fmiCallbackAllocateMemory. */
    public FMICallbackAllocateMemory allocateMemory;

    /** C type: fmiCallbackFreeMemory. */
    public FMICallbackFreeMemory freeMemory;

    /** C type: fiStepFinished. */
    public FMIStepFinished stepFinished;

    /** Return the field names in the proper order.
     *  <p>This is new in jna-3.5.0.   
     *  @return a list of strings that name the fields in order.
     */
    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "logger", "allocateMemory", "freeMemory",
                "stepFinished" });
    }

    /** Set the initialization order of the fields so that the order
     * matches the order of the C structure.
     * @deprecated As of jna-3.5.0, use getFieldOrder() instead.
     */
    protected void initFieldOrder() {
        // Note that the name of this method does not have a leading
        // underscore because the name of the protected method in the
        // parent class does not have an underscore.
        setFieldOrder(new String[] { "logger", "allocateMemory", "freeMemory",
                "stepFinished" });
    }
}
