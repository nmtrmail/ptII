/* Holds information about the current undo context

 Copyright (c) 2000-2003 The Regents of the University of California.
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

package ptolemy.moml;

import ptolemy.kernel.Entity;
import ptolemy.kernel.Port;
import ptolemy.kernel.Relation;
import ptolemy.kernel.util.Attribute;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.Nameable;
import ptolemy.kernel.util.NamedObj;

import java.util.Stack;

//////////////////////////////////////////////////////////////////////////
//// UndoContext
/**
Holds information about the current undo context. It is used while parsing
an incremental model change to hold the following information:
<ul>
  <li> Whether or not undo MoML should be generated for the current context.
  <li> Whether or not undo MoML should be generated for any child nodes.
  <li> The undo MoML to start the undo entry for this context.
  <li> The undo MoML for any child elements, pushed onto a stack.
  <li> The closing undo MoML for this context, if any. This is appended
  after the undo MoML for the child nodes.
</ul>
<p>
At the end of an element, if the context is undoable, then the
undo MoML is generated by taking the main undo MoML StringBuffer,
appending the undo MoML for children in the reverse order to the
order they were parsed, and finally appending any closing undo MoML
that is present.

@author     Neil Smyth
@version    $Id$
@since Ptolemy II 2.1
*/
public class UndoContext {

    ///////////////////////////////////////////////////////////////////
    ////                         Constructors                      ////

    /**
     *  Create a new UndoContext which may or may not need undo MoML generated
     *
     *  @param undoableContext Whether or not undo MoML is required for this
     *  context
     */
    public UndoContext(boolean undoableContext) {
        _undoable = undoableContext;
        _undoChildEntries = new Stack();
        _undoMoML = new StringBuffer();
        _closingUndoMoML = new StringBuffer();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Append some MoML to be appended after the undo MoML for child nodes.
     *  Note that these will appear in the reverse order in which this method
     *  is called, which makes sense since generally these are nested calls.
     *  @param undoMoML The MoMl to be appended after any child nodes
     */
    public void appendClosingUndoMoML(String undoMoML) {
        _closingUndoMoML.insert(0, undoMoML);
        return;
    }

    /**
     *  Append some MoML to the current buffer.
     *
     *  @param undoMoML The undo MoML to append to the current buffer.
     */
    public void appendUndoMoML(String undoMoML) {
        _undoMoML.append(undoMoML);
        return;
    }

    /**
     *  Used to handle the "rename" element. Replace the value of the "name"
     *  attribute the given value. This is a bit of a hack as ideally a child
     *  context should not modify the parent context, but with rename that is
     *  exactly what is required.
     *
     *  @param newName the value to give to the value of the name attribute.
     *  @exception IllegalActionException if there is currently no undo MoML at
     *  this level, or if there is no name attribute present.
     */
    public void applyRename(String newName) throws IllegalActionException {
        if (_undoMoML.length() == 0) {
            // this should not happen
            throw new IllegalActionException(
                    "Failed to create undo entry:\n" +
                    "Cannot rename an element whose parent " +
                    "undo context does not have any undo MoML. Requested " +
                    "new name: " + newName);
        }
        String undo = _undoMoML.toString();
        String marker = "name=\"";
        int startIndex = undo.indexOf("name=\"");
        if (startIndex == -1) {
            // this should not happen
            throw new IllegalActionException(
                    "Failed to create undo entry:\n" +
                    "Cannot rename an element whose parent " +
                    "undo context does not have a name attribute in its " +
                    "undo MoML. Requested new name: " + newName);
        }
        // Move the startIndex to after the marker
        startIndex += marker.length();
        // Now get the end index
        int endIndex = undo.indexOf("\"", startIndex);
        if (endIndex == -1) {
            // Also should not happen
            // this should not happen
            throw new IllegalActionException(
                    "Failed to create undo entry:\n" +
                    "Cannot rename an element whose parent " +
                    "undo context does not have a valid name attribute " +
                    "in its undo MoML. Requested new name: " + newName);
        }
        // Finally update the string buffer
        _undoMoML.replace(startIndex, endIndex, newName);
    }

    /**
     *  Generate the undo entry by processing children entries and the
     *  closing undo MoML. First appends the contents of the children
     *  undo entry stack to this elements undo MoML, followed by any
     *  closing MoML. Note that child elements have their undo MoML
     *  appended in reverse order to that in which they were parsed.
     *
     *  @return the generated undo entry.
     */
    public String generateUndoEntry() {
        // First append the undo MoML for the children
        while (!_undoChildEntries.isEmpty()) {
            _undoMoML.append((String)_undoChildEntries.pop());
        }

        // Next append any closing MoML
        _undoMoML.append(_closingUndoMoML.toString());

        // Return the result
        return _undoMoML.toString();
    }

    /**
     *  Get the undo MoML for this element as it currently stands. Note that
     *  this method does not append the MoML for children or any closing MoML
     *  that has been set.
     *
     *  @return The UndoMoML value.
     */
    public String getUndoMoML() {
        return _undoMoML.toString();
    }

    /**
     *  Return whether or not this context has any undo MoML to be processed.
     *
     *  @return true if this context has any undo MoML to be processed.
     */
    public boolean hasUndoMoML() {
        return _undoMoML.length() > 0;
    }

    /**
     *  Return whether or not child nodes need to generate MoML.
     *
     *  @return true if this context has undoable children.
     */
    public boolean hasUndoableChildren() {
        return _childrenUndoable;
    }


    /**
     *  Tells if the current context is undoable or not.
     *
     *  @return true if this context is undoable.
     */
    public boolean isUndoable() {
        return _undoable;
    }

    /**
     *  Generate and append undo MoML to move the current
     *  context. This is done by generating a new entity element that
     *  makes the difference between the passed in container and
     *  containee objects.
     *
     *  <p>For example, if the container has full name ".top" and the
     *  containee has a full name ".top.a.b.c.d", then MoML to move
     *  down the model such as the following is generated: <entity
     *  name="a.b.c" >
     *
     * @param container The container to move relative to
     * @param containee The containee who is contained by the container
     */
    public void moveContextStart(NamedObj container,
            NamedObj containee) {
        String entityContext = _getRelativeContext(container, containee);
        // Get the name of the MoML element to move with
        String elemName = _getContainingContextType(containee);
        // Need to move to the correct level to handle deep names
        if (entityContext != null) {
            appendUndoMoML("<" + elemName + " name=\"" + entityContext +
                    "\" >\n");
        }
    }

    /** Generate and append closing MoML for the current move.
     *
     *  <p>For example, if the containee is not already immediately
     *  contained, and the container is an entity, the </entity>
     *  is appended to the model.
     *
     * @param container The container to move relative to.
     * @param containee The containee who is contained by the container.
     */
    public void moveContextEnd(NamedObj container,
            NamedObj containee) {
        String entityContext = _getRelativeContext(container, containee);
        // Get the name of the MoML element to move with
        String elemName = _getContainingContextType(containee);
        // Need to move to the correct level to handle deep names
        if (entityContext != null) {
            appendUndoMoML("</" + elemName + ">\n");
        }
    }

    /**
     *  Push the passed in MoML onto the stack of element undo entries. Note
     *  that undo entries are pushed onto a stack so that at the end of the
     *  element they can be aggregated to form the full undo MoML - in the
     *  reverse order to that in which the elements originally appeared.
     *
     * @param  entry  Description of Parameter
     */
    public void pushUndoEntry(String entry) {
        // FIXME: Should we set _childrenUndoable = true here?
        // If we push an undo child entry, then is it always undoable?
        _undoChildEntries.push(entry);
    }

    /**
     *  Set whether or not the child contexts are undoable
     *
     * @param  isUndoable  the new statet
     */
    public void setChildrenUndoable(boolean isUndoable) {
        _childrenUndoable = isUndoable;
    }

    /**
     *  Set whether or not the current context is undoable
     *
     * @param  isUndoable  the new statet
     */
    public void setUndoable(boolean isUndoable) {
        _undoable = isUndoable;
    }

    /** Return a string representation of this object */
    public String toString() {
        return "UndoContext: "
            + (isUndoable() ? "are" : "are not")
            + " undoable and "
            + (hasUndoableChildren() ? "has" : "does not have")
            + " undoable children\n"
            + "undoMoML: " + getUndoMoML() + "\n"
            + "closingUndoMoML: " + _closingUndoMoML.toString() + "\n";
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /**
     *  Get the entity context ot use to move to the correct level in a model.
     *  It works as follows: given the container (first arg), what entity
     *  element name is needed to move down the hierarchy so that the containee
     *  can be referred to by an immediate name.
     *
     * @param container The container.
     * @param containee The containee.
     * @return the relative context.
     */
    private String _getRelativeContext(NamedObj container,
            NamedObj containee) {
        // First get the full name of the containee relative to the
        // container
        String relativeName = containee.getName(container);
        int lastDotIndex = relativeName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            // already is immediately contained, so no extra context needed
            return null;
        } else {
            return relativeName.substring(0, lastDotIndex);
        }
    }

    /**
     *  Return the MoML element name to use for a containing context
     *  for the passed in NamedObj. This is used with the
     *  moveContextStart() method to determine the type of containing
     *  element. The type of containing element is used to move the
     *  context when relative names are used that go down more than
     *  one level.
     *
     * @param named the object whose containing context needs to be
     * determined.
     * @return the MoML element name of the containing context, or null
     * if the argument was null.
     */
    private String _getContainingContextType(NamedObj named) {
        if (named == null) {
            return null;
        }
        Nameable container = named.getContainer();
        if (container instanceof Entity) {
            return "entity";
        } else if (container instanceof Port) {
            return "port";
        } else if (container instanceof Relation) {
            return "relation";
        } else if (container instanceof Attribute) {
            return "property";
        } else {
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private members                   ////

    // Flag indicating if child elements should be undoable
    private boolean _childrenUndoable;

    // Whether or not this level is undoable
    private boolean _undoable;

    // Holds the currently generated undoable MoML for this level. Note the
    // MoML is generated in reverse element order for any given context.
    private StringBuffer _undoMoML;

    // Holds the undo MoML that is to be appended after the MoML for
    // any child nodes
    private StringBuffer _closingUndoMoML;

    // Holds the stack of MoML entries, one for each element for
    // which undo MoML was generated
    private Stack _undoChildEntries;

}
