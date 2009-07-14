/* Code generator adapter class associated with the PtidesBasicDirector class.

 Copyright (c) 2005-2009 The Regents of the University of California.
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
package ptolemy.cg.adapter.generic.program.procedural.c.adapters.ptolemy.domains.ptides.kernel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ptolemy.actor.Actor;
import ptolemy.actor.CompositeActor;
import ptolemy.actor.IOPort;
import ptolemy.actor.TypedIOPort;
import ptolemy.cg.adapter.generic.adapters.ptolemy.actor.Director;
import ptolemy.cg.kernel.generic.program.CodeStream;
import ptolemy.cg.kernel.generic.program.ProgramCodeGeneratorAdapter;
import ptolemy.data.type.BaseType;
import ptolemy.data.type.Type;
import ptolemy.domains.fsm.modal.ModalController;
import ptolemy.domains.ptides.lib.InputDevice;
import ptolemy.domains.ptides.lib.InterruptDevice;
import ptolemy.domains.ptides.lib.OutputDevice;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NamedObj;

//////////////////////////////////////////////////////////////////
//// PtidesBasicDirector

/**
 Code generator adapter associated with the PtidesBasicDirector class. This class
 is also associated with a code generator.
 Also unlike the Ptolemy implementation, the execution does not depend on the WCET
 of actor.
 @author Jia Zou, Isaac Liu, Jeff C. Jensen
 @version $Id: PtidesBasicDirector.java 54211 2009-06-06 19:49:00Z jiazou $
 @since Ptolemy II 7.1
 @Pt.ProposedRating red (jiazou)
 @Pt.AcceptedRating red (jiazou)
 */
public class PtidesBasicDirector extends Director {

    /** Construct the code generator adapter associated with the given
     *  PtidesBasicDirector.
     *  @param ptidesBasicDirector The associated director
     *  ptolemy.domains.ptides.kernel.PtidesBasicDirector
     */
    public PtidesBasicDirector(
            ptolemy.domains.ptides.kernel.PtidesBasicDirector ptidesBasicDirector) {
        super(ptidesBasicDirector);
    }

    ////////////////////////////////////////////////////////////////////////
    ////                         public methods                         ////

    /** Generate the assembly file associated for this PtidyOS program.
     *  Here we return an empty string, but the target specific adapter
     *  should overwrite it.
     *  @return The generated assembly file code.
     *  @exception IllegalActionException
     */
    public StringBuffer generateAsseblyFile() throws IllegalActionException {
        StringBuffer code = new StringBuffer();
        return code;
    }

    /** Generate the initialize code for the associated PtidesBasic director.
     *  @return The generated initialize code.
     *  @exception IllegalActionException If the adapter associated with
     *   an actor throws it while generating initialize code for the actor.
     */
    public String generateInitializeCode() throws IllegalActionException {
        StringBuffer code = new StringBuffer();
        code.append(super.generateInitializeCode());

        code.append(getStrategy().getTemplateParser().getCodeStream().getCodeBlock("initPIBlock"));
        return code.toString();
    }

    /** Generate the preinitialize code for this director.
     *  The preinitialize code for the director is generated by appending
     *  the preinitialize code for each actor.
     *  @return The generated preinitialize code.
     *  @exception IllegalActionException If getting the adapter fails,
     *   or if generating the preinitialize code for a adapter fails,
     *   or if there is a problem getting the buffer size of a port.
     *   NOTE: fire code for each function, as well as the scheduler, should all go here
     *   Take care of platform independent code.
     */
    public String generatePreinitializeCode() throws IllegalActionException {
        StringBuffer code = new StringBuffer(super.generatePreinitializeCode());

        code.append(_generatePtrToEventHeadCodeInputs());

        code.append(_generateActorFireCode());

        List args = new LinkedList();
        args.add(_generateDirectorHeader());

        args.add(((CompositeActor) _director.getContainer()).deepEntityList()
                .size());

        CodeStream codestream = getStrategy().getTemplateParser().getCodeStream();

        codestream.clear();
        code.append(codestream.getCodeBlock("preinitPIBlock", args));

        code.append(codestream.getCodeBlock("initPICodeBlock"));

        return code.toString();
    }

    /**
     * Generate the type conversion fire code. Here we don't actually want to generate
     * any type conversion statement, the type conversion is done within the event creation.
     * @return The generated code.
     * @exception IllegalActionException Not thrown in this base class.
     */
    public String generateTypeConvertFireCode() throws IllegalActionException {
        return "";
    }

    /** Generate variable declarations for inputs and outputs and parameters.
     *  Here we want to overwrite the method in Director to generate nothing,
     *  since we are using Events as holders for data values.
     *  @return code The generated code.
     *  @exception IllegalActionException If the adapter class for the model
     *   director cannot be found.
     */
    public String generateVariableDeclaration() throws IllegalActionException {
        return "";
    }

    /**
     * Generate the shared code. This is the first generate method invoked out
     * of all, so any initialization of variables of this adapter should be done
     * in this method. In this base class, return an empty set. Subclasses may
     * generate code for variable declaration, defining constants, etc.
     * @return An empty set in this base class.
     * @exception IllegalActionException Not thrown in this base class.
     */
    public Set getSharedCode() throws IllegalActionException {

        _modelStaticAnalysis();

        Set sharedCode = new HashSet();
        getStrategy().getTemplateParser().getCodeStream().clear();

        // define the number of actuators in the system as a macro.
        getStrategy().getTemplateParser().getCodeStream().append(
                "#define numActuators " + _actuators.size() + _eol);

        getStrategy().getTemplateParser().getCodeStream().appendCodeBlocks("StructDefBlock");
        getStrategy().getTemplateParser().getCodeStream().appendCodeBlocks("FuncProtoBlock");

        // prototypes for actor functions
        getStrategy().getTemplateParser().getCodeStream().append(_generateActorFuncProtoCode());

        // prototypes for actuator functions.
        getStrategy().getTemplateParser().getCodeStream().append(
                _generateActuatorActuationFuncArrayCode());

        getStrategy().getTemplateParser().getCodeStream().appendCodeBlocks("FuncBlock");

        if (!getStrategy().getTemplateParser().getCodeStream().isEmpty()) {
            sharedCode
                    .add(processCode(getStrategy().getTemplateParser().getCodeStream().toString()));
        }

        return sharedCode;
    }

    ////////////////////////////////////////////////////////////////////////
    ////                         protected methods                      ////

    protected String _generateActuatorActuationFuncArrayCode()
            throws IllegalActionException {
        StringBuffer code = new StringBuffer();

        if (_actuators.size() > 0) {
            code.append("static void (*actuatorActuations[" + _actuators.size()
                    + "])() = {");
            Iterator it = _actuators.keySet().iterator();
            Actor actor = (Actor) it.next();
            code.append("Actuation_"
                    + ProgramCodeGeneratorAdapter
                            .generateName((NamedObj) actor));
            while (it.hasNext()) {
                actor = (Actor) it.next();
                code.append(", Actuation_"
                        + ProgramCodeGeneratorAdapter
                                .generateName((NamedObj) actor));
            }
            code.append("};" + _eol);
        }

        return code.toString();
    }

    protected String _generateActuatorActuationFuncProtoCode()
            throws IllegalActionException {
        StringBuffer code = new StringBuffer();

        for (Actor actor : (List<Actor>) ((CompositeActor) _director
                .getContainer()).deepEntityList()) {
            if (actor instanceof OutputDevice) {
                code.append("void Actuation_"
                        + ProgramCodeGeneratorAdapter
                                .generateName((NamedObj) actor) + "(void);"
                        + _eol);
            }
        }

        return code.toString();
    }

    /** Generate actor function prototypes.
     * @throws IllegalActionException 
     */
    protected String _generateActorFuncProtoCode()
            throws IllegalActionException {
        StringBuffer code = new StringBuffer();

        for (Actor actor : (List<Actor>) ((CompositeActor) _director
                .getContainer()).deepEntityList()) {
            code.append("void "
                    + ProgramCodeGeneratorAdapter
                            .generateName((NamedObj) actor) + "(void);" + _eol);
        }

        code.append(_generateActuatorActuationFuncProtoCode());

        return code.toString();
    }

    /** Generate code for director header.
     *
     *  @return Code that declaresheader for director
     *  @exception IllegalActionException If getting the rate or
     *   reading parameters throws it.
     */
    protected String _generateDirectorHeader() {
        return ProgramCodeGeneratorAdapter.generateName(_director)
                + "_controlBlock";
    }

    /**
     * Generate the type conversion statement for the particular offset of
     * the two given channels. This assumes that the offset is the same for
     * both channel. Advancing the offset of one has to advance the offset of
     * the other.
     * @param source The given source channel.
     * @param sink The given sink channel.
     * @param offset The given offset.
     * @return The type convert statement for assigning the converted source
     *  variable to the sink variable with the given offset.
     * @exception IllegalActionException If there is a problem getting the
     * adapters for the ports or if the conversion cannot be handled.
     */
    protected String _generateTypeConvertStatement(ProgramCodeGeneratorAdapter.Channel source,
            ProgramCodeGeneratorAdapter.Channel sink, int offset) throws IllegalActionException {

        Type sourceType = ((TypedIOPort) source.port).getType();
        Type sinkType = ((TypedIOPort) sink.port).getType();

        // In a modal model, a refinement may have an output port which is
        // not connected inside, in this case the type of the port is
        // unknown and there is no need to generate type conversion code
        // because there is no token transferred from the port.
        if (sourceType == BaseType.UNKNOWN) {
            return "";
        }

        // The references are associated with their own adapter, so we need
        // to find the associated adapter.
        String sourcePortChannel = ProgramCodeGeneratorAdapter
                .generateName(source.port)
                + "#" + source.channelNumber + ", " + offset;
        String sourceRef = ((ProgramCodeGeneratorAdapter) (getCodeGenerator()
                .getAdapter(source.port.getContainer())))
                .getReference(sourcePortChannel);

        String sinkPortChannel = ProgramCodeGeneratorAdapter
                .generateName(sink.port)
                + "#" + sink.channelNumber + ", " + offset;

        // For composite actor, generate a variable corresponding to
        // the inside receiver of an output port.
        // FIXME: I think checking sink.port.isOutput() is enough here.
        if (sink.port.getContainer() instanceof CompositeActor
                && sink.port.isOutput()) {
            sinkPortChannel = "@" + sinkPortChannel;
        }
        String sinkRef = ((ProgramCodeGeneratorAdapter) (getCodeGenerator()
                .getAdapter(sink.port.getContainer()))).getReference(
                sinkPortChannel, true);

        // When the sink port is contained by a modal controller, it is
        // possible that the port is both input and output port. we need
        // to pay special attention. Directly calling getReference() will
        // treat it as output port and this is not correct.
        // FIXME: what about offset?
        if (sink.port.getContainer() instanceof ModalController) {
            sinkRef = ProgramCodeGeneratorAdapter
                    .generateName(sink.port);
            if (sink.port.isMultiport()) {
                sinkRef = sinkRef + "[" + sink.channelNumber + "]";
            }
        }

        String result = sourceRef;

        String sourceCodeGenType = getCodeGenerator().codeGenType(sourceType);
        String sinkCodeGenType = getCodeGenerator().codeGenType(sinkType);

        if (!sinkCodeGenType.equals(sourceCodeGenType)) {
            result = "$convert_" + sourceCodeGenType + "_" + sinkCodeGenType
                    + "(" + result + ")";
        }
        return sinkRef + " = " + result + ";" + _eol;
    }

    protected void _modelStaticAnalysis() {
        _actuators = new HashMap<Actor, Integer>();
        _sensors = new HashMap<Actor, Integer>();

        int actuatorIndex = 0;
        int sensorIndex = 0;
        for (Actor actor : (List<Actor>) ((CompositeActor) _director
                .getContainer()).deepEntityList()) {
            // FIXME: should I be using Interrupt/ActuationDevice or just Input/OutputDevice?
            if (1==1) {
                throw new RuntimeException("This code broke the build");
            }
//             if (actor instanceof ActuationDevice) {
//                 _actuators.put(actor, new Integer(actuatorIndex));
//                 actuatorIndex++;
//             }

            if (actor instanceof InterruptDevice) {
                _sensors.put(actor, new Integer(sensorIndex));
                sensorIndex++;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////
    ////                         private methods                        ////

    /** fire methods for each actor.
     * @return fire methods for each actor
     * @exception IllegalActionException
     * @exception IllegalActionException If thrown when getting the port's adapter.
     */
    private String _generateActorFireCode() throws IllegalActionException {
        StringBuffer code = new StringBuffer();
        Iterator actors = ((CompositeActor) _director.getContainer())
                .deepEntityList().iterator();

        while (actors.hasNext()) {
            Actor actor = (Actor) actors.next();

            ProgramCodeGeneratorAdapter adapter = (ProgramCodeGeneratorAdapter) getCodeGenerator()
                    .getAdapter(actor);

            if (1==1) {
                throw new RuntimeException("This code broke the build");
            }
//             if (actor instanceof ActuationDevice) {
//                 code
//                         .append("void Actuation_"
//                                 + ProgramCodeGeneratorAdapterStrategy
//                                         .generateName((NamedObj) actor)
//                                 + "() {" + _eol);
//                 code
//                         .append(((ptolemy.cg.adapter.generic.program.procedural.c.adapters.ptolemy.domains.ptides.lib.OutputDevice) adapter)
//                                 .generateActuatorActuationFuncCode());
//                 code.append("}" + _eol);
//             }

            if (actor instanceof InterruptDevice) {
                code
                        .append("void Sensing_"
                                + ProgramCodeGeneratorAdapter
                                        .generateName((NamedObj) actor)
                                + "() {" + _eol);
                code
                        .append(((ptolemy.cg.adapter.generic.program.procedural.c.adapters.ptolemy.domains.ptides.lib.InputDevice) adapter)
                                .generateSensorSensingFuncCode());
                code.append("}" + _eol);
            }
            code.append("void "
                    + ProgramCodeGeneratorAdapter
                            .generateName((NamedObj) actor) + "() " + "{"
                    + _eol);
            code.append(adapter.generateFireCode());

            // After each actor firing, the Event Head ptr needs to point to null
            code.append(_generateClearEventHeadCode(actor));
            code.append("}" + _eol);
        }

        return code.toString();
    }

    /** This code reset the Event_Head pointer for each channel to null.
     * @param actor The actor which the input channels reside, whose pointers are pointed to null
     * @return
     * @throws IllegalActionException 
     */
    private String _generateClearEventHeadCode(Actor actor)
            throws IllegalActionException {
        // if the actor is an input device, the input is fake.
        if (actor instanceof InputDevice) {
            return "";
        }
        StringBuffer code = new StringBuffer();
        code.append("/* generate code for clearing Event Head buffer. */"
                + _eol);
        for (IOPort inputPort : (List<IOPort>) actor.inputPortList()) {
            for (int channel = 0; channel < inputPort.getWidth(); channel++) {
                code.append("Event_Head_"
                        + ProgramCodeGeneratorAdapter
                                .generateName(inputPort) + "[" + channel
                        + "] = NULL;" + _eol);
            }
        }
        return code.toString();
    }

    private String _generatePtrToEventHeadCodeInputs()
            throws IllegalActionException {
        StringBuffer code = new StringBuffer();
        for (Actor actor : (List<Actor>) (((CompositeActor) _director
                .getContainer()).deepEntityList())) {
            if (!(actor instanceof InputDevice)) {
                for (IOPort inputPort : (List<IOPort>) actor.inputPortList()) {
                    if (inputPort.getWidth() > 0) {
                        code.append("Event* Event_Head_"
                                + ProgramCodeGeneratorAdapter
                                        .generateName(inputPort) + "["
                                + inputPort.getWidth() + "] = {NULL");
                        for (int channel = 1; channel < inputPort.getWidth(); channel++) {
                            code.append(", NULL");
                        }
                        code.append("};" + _eol);
                    }
                }
            }
        }
        return code.toString();
    }

    public Map<Actor, Integer> _actuators;
    public Map<Actor, Integer> _sensors;
}
