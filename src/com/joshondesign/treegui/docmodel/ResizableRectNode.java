package com.joshondesign.treegui.docmodel;

import java.awt.geom.Point2D;
import org.joshy.gfx.node.Bounds;

public abstract class ResizableRectNode extends SketchNode {

    protected ResizableRectNode() {
        setConstraint(ResizeConstraint.Any);
    }

    public double getWidth() {
        return width;
    }

    public ResizableRectNode setWidth(double width) {
        this.width = width;
        return this;
    }

    double width = 100;



    public double getHeight() {
        return height;
    }

    public ResizableRectNode setHeight(double height) {
        this.height = height;
        return this;
    }

    double height = 100;


    public static enum ResizeConstraint {
        Any, VerticalOnly, HorizontalOnly, PreserveAspectOnly
    }

    public ResizeConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(ResizeConstraint constraint) {
        this.constraint = constraint;
    }

    private ResizeConstraint constraint;

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
