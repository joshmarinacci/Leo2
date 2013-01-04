package com.joshondesign.treegui.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<C extends TreeNode> {

    private List<C> _list = new ArrayList<C>();
    private List<TreeListener> listeners = new ArrayList<TreeListener>();

    public static interface TreeListener {
        public void added(TreeNode node);
        public void removed(TreeNode node);
        public void modified(TreeNode node);
    }


    public String getId() {
        return id;
    }

    public TreeNode<C> setId(String id) {
        this.id = id;
        return this;
    }

    String id;


    public TreeNode addListener(TreeListener treeListener) {
        this.listeners.add(treeListener);
        return this;
    }



    public TreeNode<C> markModified(C child) {
        fireModifyEvent(child);
        return this;
    }

    public TreeNode<C> add(C... nodes) {
        for(C n : nodes) {
            _list.add(n);
            fireAddEvent(n);
        }
        return this;
    }

    private void fireAddEvent(C n) {
        for(TreeListener l : listeners) {
            l.added(n);
        }
    }

    private void fireRemoveEvent(C child) {
        for(TreeListener l : listeners) {
            l.removed(child);
        }
    }

    private void fireModifyEvent(C child) {
        for(TreeListener l : listeners) {
            l.modified(child);
        }
    }

    public int getSize() {
        return _list.size();
    }

    public C get(int i) {
        return _list.get(i);
    }

    public void remove(C child1) {
        _list.remove(child1);
        fireRemoveEvent(child1);
    }

    public void remove(int i) {
        C child = _list.remove(i);
        fireRemoveEvent(child);
    }


    //various iterators

    public Iterable<C> children() {
        return _list;
    }

    public Iterable<C> reverseInOrderTraversal() {
        return _list;
    }

    public Iterable<C> inOrderTraversal() {
        return _list;
    }

}
