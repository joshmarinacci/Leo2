package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/11/13
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class AminoJavaXMLExport extends JAction {
    private final Canvas canvas;
    private final Page page;

    public AminoJavaXMLExport(Canvas canvas, Page page) {
        super();
        this.canvas = canvas;
        this.page = page;
    }

    @Override
    public void execute() {
        for(SketchNode nd : page.get(0).children()) {
            if(nd instanceof DynamicNode) {
                try {
                    GuiTest.exportToXML(new PrintWriter(System.out), (DynamicNode) nd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getShortName() {
        return "Export XML";
    }
}
