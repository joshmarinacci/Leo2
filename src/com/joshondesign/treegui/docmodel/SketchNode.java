package com.joshondesign.treegui.docmodel;

import com.joshondesign.treegui.model.TreeNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

public abstract class SketchNode extends TreeNode<SketchNode> {
    private TreeNode parent;
    private double width = 100;
    private double height = 100;
    private Resize resize = Resize.None;

    protected SketchNode() {
        setId("id" + (int) Math.floor(Math.random() * 1000000));
    }

    public boolean isVisual() {
        return true;
    }

    public boolean isContainer() {
        return false;
    }




    public double getTranslateX() {
        return translateX;
    }

    public SketchNode setTranslateX(double translateX) {
        this.translateX = translateX;
        if(getParent() != null) {
             getParent().markModified(this);
        }
        return this;
    }

    double translateX;

    public double getTranslateY() {
        return translateY;
    }


    @Override
    public TreeNode<SketchNode> markModified(SketchNode child) {
        if(getParent() != null) {
            getParent().markModified(child);
        }
        return super.markModified(child);
    }

    public SketchNode setTranslateY(double translateY) {
        this.translateY = translateY;
        if(getParent() != null) {
            getParent().markModified(this);
        }
        return this;
    }

    double translateY;

    /**
     * Draw the node. This method assumes the surface has already been transformed into the
     * node's internal coordinate system, ie:  translateX/Y have already be accounted for.
     * @param g graphics drawing surface
     */
    public abstract void draw(GFX g);

    /**
     * Returns true if the point is contained by the node. This method works on the node's
     * internal coordinate system, ie: translateX/Y have already been subtracted from the point.
     *
     */
    public abstract boolean contains(Point2D pt);

    /**
     * Returns the node's input bounds used for positining the node. The returned bounds are
     * relative to the node's internal coordinate system. They do not account for the translateX/Y or
     * other transforms.
     */
    public abstract Bounds getInputBounds();



    public SketchNode duplicate(SketchNode node) {
        if(node != null) {
            node.setTranslateX(getTranslateX());
            node.setTranslateY(getTranslateY());
            node.setWidth(getWidth());
            node.setHeight(getHeight());
            node.setResize(getResize());
        }
        return node;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    @Override
    public TreeNode<SketchNode> add(SketchNode... nodes) {
        for(SketchNode node : nodes) {
            node.setParent(this);
        }
        return super.add(nodes);
    }

    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    public void remove(SketchNode child1) {
        child1.setParent(null);
        super.remove(child1);
    }


    public SketchNode setWidth(double width) {
        this.width = width;
        return this;
    }

    public double getWidth() {
        return width;
    }

    public SketchNode setHeight(double height) {
        this.height = height;
        return this;
    }

    public double getHeight() {
        return height;
    }

    public SketchNode setResize(Resize resize) {
        this.resize = resize;
        return this;
    }

    public Resize getResize() {
        return resize;
    }
}
