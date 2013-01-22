package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Selection;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.event.AminoAction;
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

    public List<AminoAction> getContextMenuActions(SketchDocument document, Selection selection) {
        return new ArrayList<AminoAction>();
    }
}
