/* A GR Shape consisting of a generalized cylinder with a polygon base

 Copyright (c) 1998-2000 The Regents of the University of California.
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

@ProposedRating Red (chf@eecs.berkeley.edu)
@AcceptedRating Red (chf@eecs.berkeley.edu)
*/
package ptolemy.domains.gr.lib;

import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.*;
import ptolemy.data.*;
import ptolemy.data.expr.Parameter;
import ptolemy.data.type.*;
import ptolemy.actor.*;
import ptolemy.actor.lib.*;
import ptolemy.domains.dt.kernel.DTDebug;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;

//////////////////////////////////////////////////////////////////////////
//// PolyCylinder3D
/**

This actor produces a generalized cylindrical shape in the GR domain. 
The output port is used to connect this actor to the Java3D scene
graph. This actor will only have meaning in the GR domain.

The parameter <i>polygon</i> determines the polygonal shape of the base
of this generalized cylinder. One will need to enter an array of coordinates
in the X-Y plane to specify the polygonal shape for the base. The parameter
<i>thickness</i> determines the thickness of the generalized cylinder

@author C. Fong
*/

public class PolyCylinder3D extends GRShadedShape {
    
    /** Construct an actor with the given container and name.
     *  @param container The container.
     *  @param name The name of this actor.
     *  @exception IllegalActionException If the actor cannot be contained
     *   by the proposed container.
     *  @exception NameDuplicationException If the container already has an
     *   actor with this name.
     */
    public PolyCylinder3D(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {

        super(container, name);
        DoubleToken[] tokenArray = new DoubleToken[6];
        tokenArray[0] = new DoubleToken(0.0);
        tokenArray[1] = new DoubleToken(0.5*1.0);
        tokenArray[2] = new DoubleToken(0.5*-0.866);
        tokenArray[3] = new DoubleToken(0.5*-0.5);
        tokenArray[4] = new DoubleToken(0.5*0.866);
        tokenArray[5] = new DoubleToken(0.5*-0.5);
      	ArrayToken defaultPolygon = new ArrayToken(tokenArray);
        polygon = new Parameter(this, "polygon", defaultPolygon);
        polygon.setTypeEquals(new ArrayType(BaseType.ANY));
        thickness = new Parameter(this, "thickness", new DoubleToken(0.3));
    }
    
    
    ///////////////////////////////////////////////////////////////////
    ////                         parameters                        ////

    /** The polygonal shape of the base
     *  This parameter should contain a ArrayToken with an even number
     *   of DoubleToken values.
     *  The default shape for this polygon is a triangle
     */
    public Parameter polygon;
    
    /** The thickness of the generalized cylinder
     *  This parameter should contain a DoubleToken.
     *  The default value of this parameter is the DoubleToken 0.3
     */
    public Parameter thickness;
    
   
    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////
    
    /** Clone the actor into the specified workspace. This calls the
     *  base class and then sets the parameters of the new actor.
     *  @param ws The workspace for the new object.
     *  @return A new actor.
     *  @exception CloneNotSupportedException If a derived class contains
     *   an attribute that cannot be cloned.
     */
    public Object clone(Workspace workspace) throws CloneNotSupportedException {
        PolyCylinder3D newobj = (PolyCylinder3D)super.clone(workspace);
        newobj.polygon = (Parameter) newobj.getAttribute("polygon");
        newobj.thickness = (Parameter) newobj.getAttribute("thickness");
        return newobj;
    }
    

    /** Return the encapsulated Java3D node of this 3D actor. The encapsulated
     *  node for this actor is a generalized polygonal cylinder.
     *  @return the generalized polygonal cylinder
     */   
    public Node getNodeObject() {
        return (Node) containedNode;
    }
    
    
    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    /** Create the shape and appearance of the encapsulated generalized cylinder
     *  @exception IllegalActionException If the value of some parameters can't
     *   be obtained
     */
    protected void _createModel() throws IllegalActionException {
    
        super._createModel();
        
        float data[] = _getPolygon();
        int numberOfVertices = _getVertexCount();
        int[] stripCount = new int[2+numberOfVertices];
        stripCount[0] = numberOfVertices;
        stripCount[1] = numberOfVertices;
        int i;
        for(i=2; i < 2+numberOfVertices; i++) {
            stripCount[i] = 4;
        }
        int j=0;
        int k=0;
        float thickness = (float) _getThickness();
        
        data[numberOfVertices*3] = data[0];
        data[numberOfVertices*3+1] = data[1];
        data[numberOfVertices*3+2] = data[2] - thickness;
        
        j = numberOfVertices*3 - 3;
        k = numberOfVertices*3 + 3;
        for(i=1; i<numberOfVertices;i++) {
            data[k] = data[j];
            data[k+1] = data[j+1];
            data[k+2] = data[j+2] - thickness;
            j = j - 3;
            k = k + 3;
        }
        
        j = 0;
        k = 2 * 3 * numberOfVertices;
        for(i=0; i<numberOfVertices;i++) {
            data[k]   = data[j];   
            data[k+1] = data[j+1];
            data[k+2] = data[j+2];
            
            data[k+3] = data[j];
            data[k+4] = data[j+1];
            data[k+5] = data[j+2] - thickness;
            
            j = j + 3;
            if (j == numberOfVertices*3) {
                j = 0;
            }
            
            
            data[k+6] = data[j];
            data[k+7] = data[j+1];
            data[k+8] = data[j+2] - thickness;
            
            data[k+9]  = data[j];
            data[k+10] = data[j+1];
            data[k+11] = data[j+2];
            k = k + 12;
        }
        
           
        GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(data);
        gi.setStripCounts(stripCount);
        
        Triangulator tr = new Triangulator();
        tr.triangulate(gi);
        gi.recomputeIndices();

        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);
        gi.recomputeIndices();

        Stripifier st = new Stripifier();
        st.stripify(gi);
        gi.recomputeIndices();
        
        containedNode = new Shape3D();
        containedNode.setAppearance(_appearance);
        containedNode.setGeometry(gi.getGeometryArray());
    }
    
    
    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /** Get the array that contains the 2D polygonal representation
     *  of the base of this cylinder
     *  @return the float[] array that contains the 2D polygonal vertex coordinates
     *  @exception IllegalActionException If the value of some parameters can't
     *   be obtained
     */
    private float[] _getPolygon() throws IllegalActionException {
        ArrayToken arrayToken = ((ArrayToken) polygon.getToken());
        int numberOfElements = arrayToken.length()/2;
        float[] data = new float[numberOfElements*18];
        int i=0;
        int j=0;
   
        for(i=0 ;i < numberOfElements * 2; i = i + 2) {
            data[j++] = (float) ((DoubleToken)arrayToken.getElement(i)).doubleValue();
            data[j++] = (float) ((DoubleToken) arrayToken.getElement(i+1)).doubleValue();
            data[j++] = 0.0f;
        }
        
        return data;
    }

    /** Get the thickness of this generalized cylinder
     *  @return the thickness of this generalized cylinder
     *  @exception IllegalActionException If the value of some parameters can't
     *   be obtained
     */
    private double _getThickness() throws IllegalActionException  {
        return ((DoubleToken) thickness.getToken()).doubleValue();
    }
    
    /** Get the number of vertices in the 2D polygonal base of this generalized cylinder
     *  @return the number of vertices in the base polygon
     *  @exception IllegalActionException If the value of some parameters can't
     *   be obtained
     */
    private int _getVertexCount() throws IllegalActionException {
        ArrayToken arrayToken = ((ArrayToken) polygon.getToken());
        int numberOfElements = arrayToken.length()/2;
        
        return numberOfElements;
    }
    
    
    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    private Shape3D containedNode;
}
