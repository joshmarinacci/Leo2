package com.joshondesign.treegui.docmodel;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/31/12
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ResizableRectNode extends SketchNode {


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
}
