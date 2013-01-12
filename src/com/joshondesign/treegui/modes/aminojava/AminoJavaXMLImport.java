package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/12/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class AminoJavaXMLImport extends JAction {
    private final Canvas canvas;

    public AminoJavaXMLImport(Canvas canvas) {
        super();
        this.canvas = canvas;
    }

    @Override
    public void execute() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getShortName() {
        return "Open";
    }
}
