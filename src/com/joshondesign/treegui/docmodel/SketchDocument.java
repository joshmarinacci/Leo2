package com.joshondesign.treegui.docmodel;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.model.TreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SketchDocument extends TreeNode<Page> {
    private final Selection selection;
    private File file;
    List<Binding> bindings = new ArrayList<Binding>();
    private Page selectedPage;
    private boolean snapToGrid = false;

    public SketchDocument() {
        selection = new Selection();
        file = null;
    }

    public Selection getSelection() {
        return selection;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public TreeNode findParent(SketchNode target) {
        for(Page page : children()) {
            for(Layer root : page.children()) {
                for(SketchNode child : root.children()) {
                    if(child == target) return root;
                    SketchNode parent = findParent(child,target);
                    if(parent != null) return parent;
                }
            }
        }
        return null;
    }

    private SketchNode findParent(SketchNode root, SketchNode target) {
        for(SketchNode child : root.children()) {
            if(child == target) return root;
            SketchNode parent = findParent(child,target);
            if(parent != null) return parent;
        }
        return null;
    }

    public List<Binding> getBindings() {
        return bindings;
    }

    public Page getSelectedPage() {
        if(selectedPage == null) {
            selectedPage = get(0);
        }
        return selectedPage;
    }

    public void setSelectedPage(Page selectedPage) {
        this.selectedPage = selectedPage;
        markModified(this.selectedPage);
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }
}
