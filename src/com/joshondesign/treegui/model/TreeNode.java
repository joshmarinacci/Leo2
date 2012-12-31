package com.joshondesign.treegui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/31/12
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class TreeNode {

    private List<TreeNode> _list = new ArrayList<TreeNode>();

    public static interface TreeListener {
        public void added(TreeNode node);
        public void removed(TreeNode node);
        public void modified(TreeNode node);
    }

    public TreeNode setListener(TreeListener treeListener) {
        return this;
    }



    public TreeNode markModified(TreeNode child2) {
        return this;
    }





    public TreeNode add(TreeNode ... nodes) {
        for(TreeNode n : nodes) {
            _list.add(n);
        }
        return this;
    }

    public int getSize() {
        return _list.size();
    }

    public TreeNode get(int i) {
        return _list.get(i);
    }

    public void remove(TreeNode child1) {
        _list.remove(child1);
    }

    public void remove(int i) {
        _list.remove(i);
    }



    //various iterators

    public Iterable<? extends TreeNode> children() {
        return _list;
    }

    public Iterable<? extends TreeNode> reverseInOrderTraversal() {
        return _list;
    }

    public Iterable<? extends TreeNode> inOrderTraversal() {
        return _list;
    }

}
