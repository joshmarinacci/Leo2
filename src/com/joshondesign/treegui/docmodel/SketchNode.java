package com.joshondesign.treegui.docmodel;

import com.joshondesign.treegui.model.TreeNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

public abstract class SketchNode extends TreeNode<SketchNode> {
    public double getTranslateX() {
        return translateX;
    }

    public SketchNode setTranslateX(double translateX) {
        this.translateX = translateX;
        return this;
    }

    double translateX;

    public double getTranslateY() {
        return translateY;
    }

    public SketchNode setTranslateY(double translateY) {
        this.translateY = translateY;
        return this;
    }

    double translateY;

    public abstract void draw(GFX g);

    public abstract boolean contains(Point2D pt);

    public abstract Bounds getInputBounds();
}
