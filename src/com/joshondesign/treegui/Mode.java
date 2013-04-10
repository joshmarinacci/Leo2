package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Selection;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.node.control.Menu;

public abstract class Mode extends TreeNode {

    public abstract String getName();

    public TreeNode<SketchNode> getSymbols() {
        return getById("symbols");
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

    public void modifyEditMenu(Menu editMenu, SketchDocument doc) {

    }

    public abstract void modifyFileMenu(Menu fileMenu, SketchDocument doc);

    public DynamicNode findSymbol(String name) {
        for(SketchNode node : getSymbols().children()) {
            if(node instanceof DynamicNode) {
                DynamicNode nd = (DynamicNode) node;
                if(nd.getName().equals(name)) {
                    return nd;
                }
            }
        }
        return null;
    }

    public abstract Map<String, DynamicNode.DrawDelegate> getDrawMap();

    public abstract void filesDropped(List<File> files, Canvas canvas);

    public SketchNode createEmptyGroup() {
        return null;
    }
}
