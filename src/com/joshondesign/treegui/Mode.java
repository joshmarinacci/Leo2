package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import org.joshy.gfx.node.control.Menu;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/4/13
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class Mode extends TreeNode {


    public TreeNode<SketchNode> getSymbols() {
        return get(1);
    }

    public SketchDocument createEmptyDoc() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void modifyNodeMenu(Menu nodeMenu, SketchDocument doc) {
    }
}
