package com.joshondesign.treegui.leo2;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import org.joshy.gfx.node.control.ListModel;

/**
* Created with IntelliJ IDEA.
* User: josh
* Date: 1/14/13
* Time: 4:02 PM
* To change this template use File | Settings | File Templates.
*/
public class TreeNodeListModel implements ListModel<TreeNode> {
    private final TreeNode<SketchNode> symbols;

    public TreeNodeListModel(TreeNode<SketchNode> symbols) {
        this.symbols = symbols;
    }

    public TreeNode get(int i) {
        return symbols.get(i);
    }

    public int size() {
        return symbols.getSize();
    }
}
