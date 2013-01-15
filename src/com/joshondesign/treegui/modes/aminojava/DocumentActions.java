package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/14/13
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class DocumentActions {

    public static void deleteSelection(Canvas canvas) {
        List<SketchNode> toMove = new ArrayList<SketchNode>();
        for(SketchNode child: canvas.getSelection().children()) {
            toMove.add(child);
        }
        for(SketchNode child : toMove){
            canvas.getEditRoot().remove(child);
        }
        canvas.clearSelection();
    }
}
