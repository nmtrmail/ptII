/* A request to initialize an actor.

 Copyright (c) 1998 The Regents of the University of California.
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
@ProposedRating Yellow (eal@eecs.berkeley.edu)
@AcceptedRating Red (reviewmoderator@eecs.berkeley.edu)

*/

package ptolemy.kernel.event;

import ptolemy.kernel.util.*;
import ptolemy.kernel.*;
import ptolemy.actor.Actor;

//////////////////////////////////////////////////////////////////////////
//// InitializeActor
/** 
A request to initialize an actor.
This request is created automatically when an AddEntity request is
executed.  Initialization is done via a change request so that a
batch of mutations can be completed before any of the added entities
is initialized.

@author  Edward A. Lee
@version $Id$
@see ptolemy.actor.Actor
@see AddEntity
*/
public class InitializeActor extends ChangeRequest {

    /** Construct a request with the specified originator and
     *  actor to be initialized.  The actor must also implement the
     *  Nameable interface or a ClassCastException will occur.
     *  @param originator The source of the change request.
     *  @param actor The actor.
     */	
    public InitializeActor(Nameable originator, Actor actor) {
        super(originator, "Initialize " + ((Nameable)actor).getFullName());
        _actor = actor;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Execute the change by calling the initialize() method of the
     *  actor.
     *  @exception ChangeFailedException If the initialize method throws
     *   an exception.
     */	
    public void execute() throws ChangeFailedException {
        try {
            _actor.initialize();
        } catch (KernelException ex) {
            throw new ChangeFailedException(this, ex);
        }
    }

    /** Get the actor.
     *  @return The actor to be added.
     */
    public Actor getActor() {
        return _actor;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    // The actor to add.
    private Actor _actor;
}
