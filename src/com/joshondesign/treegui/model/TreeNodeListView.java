package com.joshondesign.treegui.model;

import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.control.ListView;

public class TreeNodeListView extends ListView<TreeNode> {

    private TreeNode treeNodeModel;

    public void setTreeNodeModel(TreeNode root) {
        this.treeNodeModel = root;
        this.setModel(new ListModel<TreeNode>() {
            public TreeNode get(int i) {
                return treeNodeModel.get(i);
            }

            public int size() {
                return treeNodeModel.getSize();
            }
        });
    }
}
