/*
 * $Id$
 *
 * Copyright (c) 1998-2001 The Regents of the University of California.
 * All rights reserved. See the file COPYRIGHT for details.
 */
package diva.gui.toolbox;

import java.awt.event.InputEvent;

import javax.swing.JPopupMenu;

import diva.canvas.Figure;
import diva.canvas.event.LayerEvent;
import diva.canvas.event.MouseFilter;
import diva.canvas.interactor.AbstractInteractor;

/**
 * This interactor creates a menu when it is activated.  By default, this
 * interactor is associated with the right mouse button.  This class is
 * commonly used to create context sensitive menus for figures in a canvas.
 *
 *
 * @author Stephen Neuendorffer (neuendor@eecs.berkeley.edu)
 * @version $Revision$
 */
public class MenuCreator extends AbstractInteractor {
    /** The menu factory.
     */
    MenuFactory _factory;

    /** Return the menu factory.
     */
    public MenuFactory getMenuFactory() {
        return _factory;
    }

    /**
     * Construct a new interactor with a right button mouse filter.
     * Set the menu factory to the given factory.
     */
    public MenuCreator(MenuFactory factory) {
        MouseFilter filter = new MouseFilter (
                InputEvent.BUTTON3_MASK);
        setMouseFilter(filter);
        setMenuFactory(factory);
    }

    /**
     * When a mouse press happens, ask the factory to create a menu and show
     * it on the screen.  Consume the mouse event.  If the factory is set to
     * null, then ignore the event and do not consume it.
     */
    public void mouseReleased(LayerEvent e) {
        if(_factory != null) {
            Figure source = e.getFigureSource();
            JPopupMenu menu = _factory.create(source);
            if(menu == null) return;
            menu.show(e.getComponent(), e.getX(), e.getY());
            e.consume();
        }
    }

    /** Set the menu factory.
     */
    public void setMenuFactory(MenuFactory factory) {
        _factory = factory;
    }
}


