/* Transform Actors using Soot

 Copyright (c) 2001 The Regents of the University of California.
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
@ProposedRating Red (cxh@eecs.berkeley.edu)
@AcceptedRating Red (cxh@eecs.berkeley.edu)
*/


package ptolemy.copernicus.jhdl;

import ptolemy.actor.CompositeActor;
import ptolemy.copernicus.kernel.ActorTransformer;
import ptolemy.copernicus.kernel.SootUtilities;
import ptolemy.copernicus.kernel.KernelMain;
import ptolemy.domains.sdf.kernel.SDFDirector;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.invoke.SiteInliner;
import soot.jimple.toolkits.invoke.StaticInliner;
import soot.jimple.toolkits.invoke.InvokeGraphBuilder;
import soot.jimple.toolkits.scalar.ConditionalBranchFolder;
import soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder;
import soot.jimple.toolkits.scalar.CopyPropagator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.jimple.toolkits.scalar.Evaluator;
import soot.toolkits.graph.*;
import soot.dava.*;
import soot.util.*;

public class Main extends KernelMain {

    ///////////////////////////////////////////////////////////////////
    ////                     public methods                        ////

    /** Read in a MoML model.
     *  @params args The first element of the array is the MoML class
     *  name or file name, subsequent optional arguments are Soot
     *  command line options, see the superclass documentation for details.
     *  @exception IllegalActionException If the model cannot be parsed.
     */
    public Main(String [] args) throws IllegalActionException {
	// args[0] contains the MoML class name.
	super(args[0]);
    }

    /** Add transforms to the Scene.
     */
    public void addTransforms() {
	super.addTransforms();
        // Create instance classes for actors.
	// This transformer takes no input as far as soot is concerned
	// (i.e. no application classes) and creates application
	// classes from the model.
        Scene.v().getPack("wjtp").add(new Transform("wjtp.at",
                ActorTransformer.v(_toplevel)));

        // Add a transformer to convert each actor class to JHDL.
        // "wjtp" means "whole java tranformation package"
        // This transformer is required to be a scene transformer,
        // and it is applied before body transformers.
        // "jhdl" is Michael Wirthlin's hardware design language.
        // "wjtp.jhdl" is the name of the phase.
        Scene.v().getPack("wjtp").add(new Transform("wjtp.jhdl",
                JHDLTransformer.v(_toplevel)));

        // Add transformers to do other passes.
        // "jtp" mean "java tranformation package.
        // These transformers are required to be a body transformer,
        // and are applied after scene transformers.

        // First pass: "cpaf" = "constant propagator and folder"
        Scene.v().getPack("jtp").add(new Transform("jtp.cpaf",
                ConstantPropagatorAndFolder.v()));
        Scene.v().getPack("jtp").add(new Transform("jtp.cbf",
                ConditionalBranchFolder.v()));
        Scene.v().getPack("jtp").add(new Transform("jtp.uce",
                UnreachableCodeEliminator.v()));
	Scene.v().getPack("jtp").add(new Transform("jtp.cp",
                CopyPropagator.v()));
        Scene.v().getPack("jtp").add(new Transform("jtp.dae",
                DeadAssignmentEliminator.v()));
    }

    /** Read in a MoML model, generate .class files for use with JHDL.
     *  @exception IllegalActionException If the model cannot be parsed.
     *  @exception NameDuplicationException If the name of the
     *  model cannot be changed to a Java identifier String.
     */
    public static void main(String[] args)
	throws IllegalActionException, NameDuplicationException {
	Main main = new Main(args);

	// Parse the model.
	CompositeActor toplevel = main.readInModel(args[0]);

	// Create instance classes for the actors.
	main.initialize(toplevel);

	// Add Transforms to the Scene.
	main.addTransforms();
	    
	main.generateCode(args); 
    }
}
