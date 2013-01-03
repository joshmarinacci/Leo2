package com.joshondesign.treegui.docmodel;

import java.awt.geom.Point2D;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/*
make group selectable
make group bounds be based on the children
double click to enter the group. shades the rest and disables input, but still shows them.
double click outside the group to exit the group editing.
 */
public class Group extends SketchNode {
    @Override
    public void draw(GFX g) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    //determines if it contains the point using the union of the bounds of the children.
    //contains is not pre transformed.
    @Override
    public boolean contains(Point2D pt) {
        if(this.getSize() < 1) return false;
        Bounds bounds = getInputBounds();
        bounds = transform(bounds, getTranslateX(),getTranslateY());
        return bounds.contains(pt);
    }

    private Bounds transform(Bounds b, double x, double y) {
        return new Bounds(
                b.getX()+x,
                b.getY()+y,
                b.getWidth(),b.getHeight()
                );
    }

    @Override
    public Bounds getInputBounds() {
        Bounds bounds = this.get(0).getInputBounds();
        for(SketchNode child : this.children()) {
            Bounds childBounds = child.getInputBounds();
            childBounds = transform(childBounds,child.getTranslateX(),child.getTranslateY());
            bounds = bounds.union(childBounds);
        }
        return bounds;
    }
}
