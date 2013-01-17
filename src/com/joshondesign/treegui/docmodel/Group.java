package com.joshondesign.treegui.docmodel;

import com.joshondesign.treegui.MathUtils;
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
    }


    //determines if it contains the point using the union of the bounds of the children.
    //contains is not pre transformed.
    @Override
    public boolean contains(Point2D pt) {
        if(this.getSize() < 1) return false;
        Bounds bounds = getInputBounds();
        bounds = MathUtils.transform(bounds, getTranslateX(),getTranslateY());
        return bounds.contains(pt);
    }


    @Override
    public Bounds getInputBounds() {
        return MathUtils.unionBounds(this);
    }

    @Override
    public boolean isContainer() {
        return true;
    }
}
