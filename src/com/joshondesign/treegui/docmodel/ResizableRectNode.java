package com.joshondesign.treegui.docmodel;

import com.joshondesign.treegui.modes.aminojava.Prop;
import java.awt.geom.Point2D;
import org.joshy.gfx.node.Bounds;

public abstract class ResizableRectNode extends SketchNode {

    protected ResizableRectNode() {
        setConstraint(ResizeConstraint.Any);
    }

    public static enum ResizeConstraint {
        Any, VerticalOnly, HorizontalOnly, PreserveAspectOnly
    }

    public ResizeConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(ResizeConstraint constraint) {
        this.constraint = constraint;
    }


    @Prop(visible = false) public ResizeConstraint constraint;

    @Override
    public boolean contains(Point2D pt) {
        if(pt.getX() < this.getTranslateX()) return false;
        if(pt.getX() > this.getTranslateX()+this.getWidth()) return false;
        if(pt.getY() < this.getTranslateY()) return false;
        if(pt.getY() > this.getTranslateY()+this.getHeight()) return false;
        return true;
    }

    @Override
    public Bounds getInputBounds() {
        return new Bounds(0,0,getWidth(),getHeight());
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node instanceof ResizableRectNode) {
            ResizableRectNode rect = (ResizableRectNode) node;
            rect.setWidth(getWidth());
            rect.setHeight(getHeight());
            rect.setConstraint(getConstraint());
        }
        return super.duplicate(node);
    }
}
