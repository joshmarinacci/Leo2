package com.joshondesign.treegui;

import java.awt.geom.Point2D;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.MouseEvent;

public abstract class Handle {
    public abstract void draw(GFX gfx);
    public abstract void drag(MouseEvent mouseEvent, Point2D point);
    public abstract boolean contains(Point2D pointInNodeCoords);
}
