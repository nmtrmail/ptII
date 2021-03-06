/* Generated By:JJTree&JavaCC: Do not edit this line. MatrixParser.java */
/* Parser for matrices written in matlab format.

 Copyright (c) 1998-2008 The Regents of the University of California.
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
package ptolemy.data.expr;

import java.util.Vector;

import ptolemy.kernel.util.IllegalActionException;

//////////////////////////////////////////////////////////////////////////
//// MatrixParser
/**
This Class provides a parser for read matrices in matlab format.

@author  Bart Kienhuis
@version $Id$
@since Ptolemy II 1.0
@Pt.ProposedRating Red
@Pt.AcceptedRating Red (kienhuis)
*/
@SuppressWarnings("static-access")
public class MatrixParser/*@bgen(jjtree)*/ implements MatrixParserTreeConstants,
        MatrixParserConstants {/*@bgen(jjtree)*/
    protected JJTMatrixParserState jjtree = new JJTMatrixParserState();

    /** Read a Matrix from File.
     *  @return A Vector containing the contents of the file.
     *  @exception IllegalActionException If an error occurs during parsing.
     */
    public Vector readMatrix() throws IllegalActionException {
        Vector m = null;
        try {
            m = matrix();
        } catch (Exception e) {
            throw new IllegalActionException(e.getMessage());
        }
        return m;
    }

    final public Vector matrix() throws ParseException {
        /*@bgen(jjtree) matrix */
        SimpleNode jjtn000 = new SimpleNode(this, JJTMATRIX);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);
        Vector m = new Vector();
        ;
        Vector row = null;
        try {
            jj_consume_token(13);
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case CONSTANT:
            case SIGN:
                row = row();
                break;
            default:
                jj_la1[0] = jj_gen;
                ;
            }
            m.add(row);
            label_1: while (true) {
                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case 14:
                    ;
                    break;
                default:
                    jj_la1[1] = jj_gen;
                    break label_1;
                }
                jj_consume_token(14);
                row = row();
                m.add(row);
            }
            jj_consume_token(15);
            jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            {
                if (true) {
                    return m;
                }
            }
        } catch (Throwable jjte000) {
            if (jjtc000) {
                jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                {
                    if (true) {
                        throw (RuntimeException) jjte000;
                    }
                }
            }
            if (jjte000 instanceof ParseException) {
                {
                    if (true) {
                        throw (ParseException) jjte000;
                    }
                }
            }
            {
                if (true) {
                    throw (Error) jjte000;
                }
            }
        } finally {
            if (jjtc000) {
                jjtree.closeNodeScope(jjtn000, true);
            }
        }
        throw new Error("Missing return statement in function");
    }

    final public Vector row() throws ParseException {
        /*@bgen(jjtree) row */
        SimpleNode jjtn000 = new SimpleNode(this, JJTROW);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);
        Vector row = new Vector();
        double element;
        try {
            element = element();
            row.add(new Double(element));
            label_2: while (true) {
                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case 16:
                    ;
                    break;
                default:
                    jj_la1[2] = jj_gen;
                    break label_2;
                }
                jj_consume_token(16);
                element = element();
                row.add(new Double(element));
            }
            jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            {
                if (true) {
                    return row;
                }
            }
        } catch (Throwable jjte000) {
            if (jjtc000) {
                jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                {
                    if (true) {
                        throw (RuntimeException) jjte000;
                    }
                }
            }
            if (jjte000 instanceof ParseException) {
                {
                    if (true) {
                        throw (ParseException) jjte000;
                    }
                }
            }
            {
                if (true) {
                    throw (Error) jjte000;
                }
            }
        } finally {
            if (jjtc000) {
                jjtree.closeNodeScope(jjtn000, true);
            }
        }
        throw new Error("Missing return statement in function");
    }

    final public double element() throws ParseException {
        /*@bgen(jjtree) element */
        SimpleNode jjtn000 = new SimpleNode(this, JJTELEMENT);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);
        Token i;
        Token s;
        int sign = 1;
        double value;
        try {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case SIGN:
                s = jj_consume_token(SIGN);
                if ((s.image).equals("-")) {
                    sign = -1 * sign;
                }
                break;
            default:
                jj_la1[3] = jj_gen;
                ;
            }
            i = jj_consume_token(CONSTANT);
            value = sign * (new Double(i.image)).doubleValue();
            jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            {
                if (true) {
                    return value;
                }
            }
        } finally {
            if (jjtc000) {
                jjtree.closeNodeScope(jjtn000, true);
            }
        }
        throw new Error("Missing return statement in function");
    }

    /** Generated Token Manager. */
    public MatrixParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    /** Current token. */
    public Token token;
    /** Next token. */
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    final private int[] jj_la1 = new int[4];
    static private int[] jj_la1_0;
    static {
        jj_la1_init_0();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[] { 0x820, 0x4000, 0x10000, 0x800, };
    }

    /** Constructor with InputStream. */
    public MatrixParser(java.io.InputStream stream) {
        this(stream, null);
    }

    /** Constructor with InputStream and supplied encoding */
    public MatrixParser(java.io.InputStream stream, String encoding) {
        try {
            jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        token_source = new MatrixParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 4; i++) {
            jj_la1[i] = -1;
        }
    }

    /** Reinitialise. */
    public void ReInit(java.io.InputStream stream) {
        ReInit(stream, null);
    }

    /** Reinitialise. */
    public void ReInit(java.io.InputStream stream, String encoding) {
        try {
            jj_input_stream.ReInit(stream, encoding, 1, 1);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jjtree.reset();
        jj_gen = 0;
        for (int i = 0; i < 4; i++) {
            jj_la1[i] = -1;
        }
    }

    /** Constructor. */
    public MatrixParser(java.io.Reader stream) {
        jj_input_stream = new SimpleCharStream(stream, 1, 1);
        token_source = new MatrixParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 4; i++) {
            jj_la1[i] = -1;
        }
    }

    /** Reinitialise. */
    public void ReInit(java.io.Reader stream) {
        jj_input_stream.ReInit(stream, 1, 1);
        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jjtree.reset();
        jj_gen = 0;
        for (int i = 0; i < 4; i++) {
            jj_la1[i] = -1;
        }
    }

    /** Constructor with generated Token Manager. */
    public MatrixParser(MatrixParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 4; i++) {
            jj_la1[i] = -1;
        }
    }

    /** Reinitialise. */
    public void ReInit(MatrixParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jjtree.reset();
        jj_gen = 0;
        for (int i = 0; i < 4; i++) {
            jj_la1[i] = -1;
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken;
        if ((oldToken = token).next != null) {
            token = token.next;
        } else {
            token = token.next = token_source.getNextToken();
        }
        jj_ntk = -1;
        if (token.kind == kind) {
            jj_gen++;
            return token;
        }
        token = oldToken;
        jj_kind = kind;
        throw generateParseException();
    }

    /** Get the next Token. */
    final public Token getNextToken() {
        if (token.next != null) {
            token = token.next;
        } else {
            token = token.next = token_source.getNextToken();
        }
        jj_ntk = -1;
        jj_gen++;
        return token;
    }

    /** Get the specific Token. */
    final public Token getToken(int index) {
        Token t = token;
        for (int i = 0; i < index; i++) {
            if (t.next != null) {
                t = t.next;
            } else {
                t = t.next = token_source.getNextToken();
            }
        }
        return t;
    }

    private int jj_ntk() {
        if ((jj_nt = token.next) == null) {
            return (jj_ntk = (token.next = token_source.getNextToken()).kind);
        } else {
            return (jj_ntk = jj_nt.kind);
        }
    }

    private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;

    /** Generate ParseException. */
    public ParseException generateParseException() {
        jj_expentries.clear();
        boolean[] la1tokens = new boolean[17];
        if (jj_kind >= 0) {
            la1tokens[jj_kind] = true;
            jj_kind = -1;
        }
        for (int i = 0; i < 4; i++) {
            if (jj_la1[i] == jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 17; i++) {
            if (la1tokens[i]) {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.add(jj_expentry);
            }
        }
        int[][] exptokseq = new int[jj_expentries.size()][];
        for (int i = 0; i < jj_expentries.size(); i++) {
            exptokseq[i] = jj_expentries.get(i);
        }
        return new ParseException(token, exptokseq, tokenImage);
    }

    /** Enable tracing. */
    final public void enable_tracing() {
    }

    /** Disable tracing. */
    final public void disable_tracing() {
    }

}
