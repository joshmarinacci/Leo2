package com.joshondesign.treegui.model;

import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.control.ListView;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/31/12
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
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
