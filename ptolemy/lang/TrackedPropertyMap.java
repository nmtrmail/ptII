/*
A base class for objects that may be visited.

Copyright (c) 1998-2001 The Regents of the University of California.
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

@ProposedRating Red (ctsay@eecs.berkeley.edu)
@AcceptedRating Red (ctsay@eecs.berkeley.edu)
*/

package ptolemy.lang;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;

//////////////////////////////////////////////////////////////////////////
//// TrackedPropertyMap
/** A base class for objects that may be visited. Visitors may mark
such objects with their corresponding Class objects.
@author Jeff Tsay, Steve Neuendorffer
@version $Id$
 */
public class TrackedPropertyMap extends PropertyMap {

    /** Create a new TrackedPropertyMap. */
    public TrackedPropertyMap() {
        super();
    }


    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Add the visitor with the argument class object to the set of visitors
     *  that have visited this object.
     *  @param The class to be added.
     *  @return true if the set did not already contain the specified element.
     */
    public boolean addVisitor(Class c) {
        if(_visitedBySet == null) {
            _visitedBySet = new HashSet(1);
        }
        return _visitedBySet.add(c);
    }

    /** Clear all traces of visitation from any visitor. */
    public void clearVisitors() {
        if(_visitedBySet == null) 
            return;
        else
            _visitedBySet.clear();
    }

    /** Remove the visitor with the argument class object from the set of
     *  visitors that have visited this object.
     *  @param The class to be added.
     */
    public boolean removeVisitor(Class c) {
        if(_visitedBySet == null)
            return false;
        else
            return _visitedBySet.remove(c);
    }

    /** Return an iterator over the class objects of the visitors that have
     *  visited this object.
     *  @return The Iterator of visitor class objects that have visited.
     */
    public Iterator visitorIterator() {
        if(_visitedBySet == null)
            return Collections.EMPTY_LIST.iterator();
        else 
            return _visitedBySet.iterator();
    }

    /** Return true iff this object was visited by a visitor with the
     *  argument class object.
     *  @return true if the class argument has visited.
     */
    public boolean wasVisitedBy(Class c) {
        if(_visitedBySet == null)
            return false;
        else 
            return _visitedBySet.contains(c);
    }


    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    /** A set of class objects of the visitors that have visited this
     *  object. The initial capacity is set to 1 to conserve memory.
     */
    // Notice that this is not initialized.  The code attempts to be
    // optimistict that most subclasses will never actually use this field
    private HashSet _visitedBySet = null;
}

