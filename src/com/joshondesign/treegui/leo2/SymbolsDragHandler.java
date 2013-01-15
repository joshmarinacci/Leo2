package com.joshondesign.treegui.leo2;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.ListView;

/**
* Created with IntelliJ IDEA.
* User: josh
* Date: 1/14/13
* Time: 4:00 PM
* To change this template use File | Settings | File Templates.
*/
public class SymbolsDragHandler implements Callback<MouseEvent> {
    private final Canvas canvasView;
    private final ListView symbolsView;
    private final SketchDocument doc;
    public boolean created;
    public SketchNode dupe;
    public double prevx;

    public SymbolsDragHandler(Canvas canvasView, ListView symbolsView, SketchDocument doc) {
        this.canvasView = canvasView;
        this.symbolsView = symbolsView;
        this.doc = doc;
    }

    public void call(MouseEvent event) throws Exception {

        if (event.getType() == MouseEvent.MouseDragged) {
            Point2D pt = canvasView.toEditRootCoords(event.getPointInNodeCoords(canvasView));
            if (created && dupe != null) {
                Bounds b = dupe.getInputBounds();
                dupe.setTranslateX(pt.getX() - b.getWidth() / 2);
                dupe.setTranslateY(pt.getY() - b.getHeight() / 2);
                canvasView.setLayoutDirty();
            }

            double w = symbolsView.getWidth();
            if (event.getX() > w && prevx <= w && !created) {
                created = true;
                if (symbolsView.getSelectedIndex() < 0) return;
                SketchNode node = (SketchNode) symbolsView.getModel().get(symbolsView.getSelectedIndex());
                dupe = node.duplicate(null);
                Bounds b = dupe.getInputBounds();
                canvasView.getEditRoot().add(dupe);
                dupe.setTranslateX(pt.getX() - b.getWidth() / 2);
                dupe.setTranslateY(pt.getY() - b.getHeight() / 2);
                canvasView.redraw();
            }
            prevx = event.getX();
        }
        if (event.getType() == MouseEvent.MouseReleased) {
            if (created) {
                canvasView.clearSelection();
                canvasView.addToSelection(dupe);
                dupe = null;
                created = false;
                prevx = 0;
            }
            canvasView.redraw();
        }

    }
}
