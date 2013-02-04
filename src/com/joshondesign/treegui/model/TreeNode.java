package com.joshondesign.treegui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeNode<C extends TreeNode> {

    private List<C> _list = new ArrayList<C>();
    private List<TreeListener> listeners = new ArrayList<TreeListener>();

    public TreeNode<C> clear() {
        List<C> toDelete = new ArrayList<C>();
        toDelete.addAll(_list);
        _list.clear();
        for(C c : toDelete) {
            fireRemoveEvent(c);
        }
        return this;
    }

    public void removeAll(TreeNode<C> nodes) {
        _list.removeAll(nodes._list);
    }

    public int indexOf(C node) {
        return _list.indexOf(node);
    }

    public void addAll(int index, TreeNode<C> nodes) {
        _list.addAll(index,nodes._list);
        fireAddEvent(nodes.get(0));
    }

    public void addAll(TreeNode<C> nodes) {
        _list.addAll(nodes._list);
        fireAddEvent(nodes.get(0));
    }

    public static interface TreeListener<C> {
        public void added(C node);
        public void removed(C node);
        public void modified(C node);
        public void selfModified(TreeNode self);
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

    public TreeNode removeListener(TreeListener listener) {
        this.listeners.remove(listener);
        return this;
    }


    public TreeNode<C> markModified(C child) {
        fireModifyEvent(child);
        return this;
    }

    public TreeNode<C> markSelfModified() {
        fireSelfModifyEvent();
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
    private void fireSelfModifyEvent() {
        for(TreeListener l : listeners) {
            l.selfModified(this);
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

    public Iterable<C> reverseChildren() {
        List<C> list = new ArrayList<C>();
        list.addAll(_list);
        Collections.reverse(list);
        return list;
    }

    public Iterable<C> reverseInOrderTraversal() {
        return _list;
    }

    public Iterable<C> inOrderTraversal() {
        return _list;
    }

    protected C getById(String id) {
        for(C c : children()){
            if(c.getId() != null && c.getId().equals(id)) return c;
        }
        return null;
    }

}
