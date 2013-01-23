package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DocumentActions {

    public static void deleteSelection(SketchDocument document) {
        List<SketchNode> toMove = new ArrayList<SketchNode>();
        for(SketchNode child: document.getSelection().children()) {
            toMove.add(child);
        }
        for(SketchNode child : toMove){
            document.findParent(child).remove(child);
        }

        ListIterator<Binding> it = document.getBindings().listIterator();
        while(it.hasNext()) {
            Binding binding = it.next();
            if(toMove.contains(binding.getSource()) ||
                    toMove.contains(binding.getTarget())) {
                it.remove();
            }
        }
        document.getSelection().clear();
    }
}
