package com.joshondesign.treegui.docmodel;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.model.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class Selection extends TreeNode<SketchNode> implements TreeNode.TreeListener<SketchNode> {
    private final List<SketchNode> buffer;

    public Selection() {
        buffer = new ArrayList<SketchNode>();
    }


    @Override
    public TreeNode<SketchNode> add(SketchNode... nodes) {
        for(SketchNode node : nodes) {
            node.addListener(this);
        }
        return super.add(nodes);
    }

    @Override
    public TreeNode<SketchNode> clear() {
        for(SketchNode node : children()) {
            node.removeListener(this);
        }
        return super.clear();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void cut(SketchDocument document) {
        buffer.clear();
        for(SketchNode node : children()) {
            document.findParent(node).remove(node);
            buffer.add(node);
        }
        clear();
    }

    public void copy(SketchDocument document) {
        buffer.clear();
        for(SketchNode node : children()) {
            buffer.add(node);
        }
    }

    public void paste(SketchDocument document, Canvas canvas) {
        clear();
        for(SketchNode node : buffer) {
            SketchNode dup = node.duplicate(null);
            canvas.getEditRoot().add(dup);
            add(dup);
        }

    }


    public void added(SketchNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removed(SketchNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void modified(SketchNode node) {
        markModified(node);
    }

    public void selfModified(TreeNode self) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
