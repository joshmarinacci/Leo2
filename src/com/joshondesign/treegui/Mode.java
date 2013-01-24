package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Selection;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.node.control.Menu;

public abstract class Mode extends TreeNode {

    public abstract String getName();

    public TreeNode<SketchNode> getSymbols() {
        return get(1);
    }

    public abstract SketchDocument createEmptyDoc();

    public void modifyNodeMenu(Menu nodeMenu, SketchDocument doc) {
    }

    public List<AminoAction> getContextMenuActions(SketchDocument document, Selection selection) {
        return new ArrayList<AminoAction>();
    }

    public void modifyViewMenu(Menu viewMenu, SketchDocument doc) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void modifyShareMenu(Menu shareMenu, SketchDocument doc) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void modifyDocumentMenu(Menu documentMenu, SketchDocument doc) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public abstract void modifyFileMenu(Menu fileMenu, SketchDocument doc);
}
