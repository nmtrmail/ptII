/* CSPBuffer atomic actor.

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

@ProposedRating Red (nsmyth@eecs.berkeley.edu)

*/

package ptolemy.domains.csp.lib;

import ptolemy.domains.csp.kernel.*;
import ptolemy.actor.*;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.data.Token;

//////////////////////////////////////////////////////////////////////////
//// CSPBuffer
/** 
  Implements a single channel buffer. The default depth of the buffer is 1.
    FIXME: add description!!

@author Neil Smyth
@version $Id$

*/

public class CSPBuffer extends CSPActor {
    public CSPBuffer() throws IllegalActionException, NameDuplicationException{
        super();
        _depth = 1;
        _buffer = new Token[_depth];
         output = new IOPort(this, "bufferOutput", false, true);
        input = new IOPort(this, "bufferInput", true, false);
    }
    
    public CSPBuffer(CompositeActor cont, String name) 
            throws IllegalActionException, NameDuplicationException {
         this(cont, name, 1);
    }

    public CSPBuffer(CompositeActor cont, String name, int depth) 
            throws IllegalActionException, NameDuplicationException {
         super(cont, name);
         _depth = depth;
         _buffer = new Token[_depth];
         output = new IOPort(this, "bufferOutput", false, true);
         input = new IOPort(this, "bufferInput", true, false);
    }

    ////////////////////////////////////////////////////////////////////////
    ////                         public methods                         ////

    public void fire() {
        try {
            int count = 0;
            boolean guard = false;
            boolean continueCDO = true;
            ConditionalBranch[] branches = new ConditionalBranch[2];
            while (continueCDO) {
                // step 1
                guard = (_size < _depth);
                branches[0] = new ConditionalReceive(guard, input, 0, 0);
              
                guard = (_size > 0);
                branches[1] = new ConditionalSend(guard, output, 0, 1, 
                        _buffer[_readFrom]);

                // step 2
                int successfulBranch = chooseBranch(branches);
                                
                // step 3
                if (successfulBranch == 0) {
                    _size++;
                    _buffer[_writeTo] = branches[0].getToken();
                    System.out.println(getName() + " got Token: " + 
                            _buffer[_writeTo].toString() + ", size is: " + 
                            _size);
                    _writeTo = ++_writeTo % _depth;
                } else if (successfulBranch == 1) {
                    _size--;
                    System.out.println(getName() + " sent Token: " + 
                            _buffer[_readFrom].toString() + ", size is: " +
                            _size);
                    _readFrom = ++_readFrom % _depth;
                } else if (successfulBranch == -1) {
                    // all guards false so exit CDO
                    continueCDO = false;
                } else {
                    throw new TerminateProcessException(getName() + ": " +
                            "branch id returned during execution of CDO.");
                }

                count++;
            }
        } catch (IllegalActionException ex) {
            System.out.println("CSPBuffer: IllegalActionException, exiting" + 
                    ex.getMessage());
        } catch (NoTokenException ex) {
            System.out.println("CSPBuffer: cannot get token.");
        } finally {
            _again = false;
        }
    }
    
    public boolean prefire() {
        return _again;
    }

    public IOPort input;
    public IOPort output;

    ////////////////////////////////////////////////////////////////////////
    ////                         private methods                        ////

    // Flag indicating if this actor should be fired again.
    private boolean _again = true;

     // The array storing the buffered Tokens.
    private Token[] _buffer;
    
    // The depth of the buffer.
     private int _depth = 1;

    // The number of Tokens currently stored in the buffer.
    private int _size = 0;

    // The next location to write a Token into.
    private int _writeTo = 0;

    // The next location to read Tokenfromo.
    private int _readFrom = 0;

   }
