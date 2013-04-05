package com.joshondesign.treegui.docmodel;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.model.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class Selection extends TreeNode<SketchNode> {
    private final List<SketchNode> buffer;

    public Selection() {
        buffer = new ArrayList<SketchNode>();
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
}
