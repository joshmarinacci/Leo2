package com.joshondesign.treegui.docmodel;

import com.joshondesign.treegui.model.TreeNode;

public class Layer extends TreeNode<SketchNode>  {

    @Override
    public TreeNode<SketchNode> add(SketchNode... nodes) {
        for(SketchNode node : nodes) {
            node.setParent(this);
        }
        return super.add(nodes);
    }

    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    public void remove(SketchNode child1) {
        child1.setParent(null);
        super.remove(child1);
    }


    /*public Bounds getMaxBounds() {
    }*/
}
