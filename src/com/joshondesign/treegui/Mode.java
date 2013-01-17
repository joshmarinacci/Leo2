package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import org.joshy.gfx.node.control.Menu;

public class Mode extends TreeNode {


    public TreeNode<SketchNode> getSymbols() {
        return get(1);
    }

    public SketchDocument createEmptyDoc() {
        return null;
    }

    public void modifyNodeMenu(Menu nodeMenu, SketchDocument doc) {
    }
}
