package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.MouseEvent;

public abstract class Handle {
    public abstract void draw(GFX gfx);
    public abstract void startDrag(MouseEvent mouseEvent);
    public abstract void drag(MouseEvent mouseEvent, Point2D point);
    public abstract void endDrag(MouseEvent mouseEvent, Point2D pt);
    public abstract boolean contains(Point2D pointInNodeCoords);
    public abstract SketchNode getNode();
}
