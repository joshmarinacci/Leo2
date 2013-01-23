package com.joshondesign.treegui.model;

import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.control.ListView;

public class TreeNodeListModel implements ListModel<TreeNode> {
    private final TreeNode<SketchNode> symbols;

    public TreeNodeListModel(TreeNode<SketchNode> symbols) {
        this.symbols = symbols;
        this.symbols.addListener(new TreeNode.TreeListener() {
            public void added(TreeNode node) {
                fireUpdate();
            }

            public void removed(TreeNode node) {
                fireUpdate();
            }

            public void modified(TreeNode node) {
                fireUpdate();
            }
        });
    }

    private void fireUpdate() {
        EventBus.getSystem().publish(new ListView.ListEvent(ListView.ListEvent.Updated,this));
    }

    public TreeNode get(int i) {
        return symbols.get(i);
    }

    public int size() {
        return symbols.getSize();
    }
}
