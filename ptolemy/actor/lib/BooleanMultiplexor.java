/* A polymorphic multiplexor with boolean select.

 Copyright (c) 1997-2001 The Regents of the University of California.
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

@ProposedRating Yellow (neuendor@eecs.berkeley.edu)
@AcceptedRating Red (neuendor@eecs.berkeley.edu)
*/

package ptolemy.actor.lib;

import ptolemy.actor.*;
import ptolemy.actor.lib.Transformer;
import ptolemy.data.*;
import ptolemy.data.type.*;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.*;


//////////////////////////////////////////////////////////////////////////
//// BooleanMultiplexor
/**
A type polymorphic multiplexor with boolean valued select. 
This actor consumes exactly one token from each input port, and sends one
of the tokens from either <i>trueInput</i> or <i>falseInput</i>
to the output.  The token sent to the output
is determined by the <i>select</i> input, which must be a boolean value.
Because tokens are immutable, the same Token
is sent to the output, rather than a copy.
The <i>trueInput</i> and <i>falseInput</i> port may receive Tokens of any type.

@author Steve Neuendorffer
@version $Id$
*/

public class BooleanMultiplexor extends TypedAtomicActor {

    /** Construct an actor in the specified container with the specified
     *  name.
     *  @param container The container.
     *  @param name The name of this actor within the container.
     *  @exception IllegalActionException If the actor cannot be contained
     *   by the proposed container.
     *  @exception NameDuplicationException If the name coincides with
     *   an actor already in the container.
     */
    public BooleanMultiplexor(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);

        trueInput = new TypedIOPort(this, "trueInput", true, false);
        falseInput = new TypedIOPort(this, "falseInput", true, false);
        select = new TypedIOPort(this, "select", true, false);
        select.setTypeEquals(BaseType.BOOLEAN);
        output = new TypedIOPort(this, "output", false, true);
    }

    ///////////////////////////////////////////////////////////////////
    ////                     ports and parameters                  ////

    /** Input for tokens on the true path. */
    public TypedIOPort trueInput;
    /** Input for tokens on the false path. */
    public TypedIOPort falseInput;
    /** Input for the index of the port to select. The type is BooleanToken. */
    public TypedIOPort select;
    /** Output */
    public TypedIOPort output;

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Read a token from each input port.  If the token from the
     *  <i>select</i> input is true, then output the token consumed from the
     *  <i>trueInput</i> port, otherwise output the token from the
     *  <i>falseInput</i> port.
     *  This method will throw a NoTokenException if any
     *  input channel does not have a token.
     *
     *  @exception IllegalActionException If there is no director.
     */
    public void fire() throws IllegalActionException {
        boolean flag = ((BooleanToken) select.get(0)).booleanValue();
        Token trueToken = trueInput.get(0);
        Token falseToken = falseInput.get(0);
            
        if(flag) {
            output.send(0, trueToken);
        } else {
            output.send(0, falseToken);
        }
    }

    /** Return false if any input channel does not have a token.
     *  Otherwise, return whatever the superclass returns.
     *  @return False if there are not enough tokens to fire.
     *  @exception IllegalActionException If there is no director.
     */
    public boolean prefire() throws IllegalActionException {
        if (!select.hasToken(0)) return false;
        if (!trueInput.hasToken(0)) return false;
        if (!falseInput.hasToken(0)) return false;
        return super.prefire();
    }
}

