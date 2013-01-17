package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.util.ArrayList;
import java.util.List;

public class DocumentActions {

    public static void deleteSelection(SketchDocument document) {
        List<SketchNode> toMove = new ArrayList<SketchNode>();
        for(SketchNode child: document.getSelection().children()) {
            toMove.add(child);
        }
        for(SketchNode child : toMove){
            document.findParent(child).remove(child);
        }
        document.getSelection().clear();
    }
}
