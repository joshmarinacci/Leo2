package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.Control;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/30/12
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Canvas extends Control {
    private TreeNode<SketchNode> target;
    private SketchNode selection;
    private PropsView propsView;

    public Canvas() {
        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, new Callback<MouseEvent>() {
            public void call(MouseEvent mouseEvent) throws Exception {
                if (mouseEvent.getType() == MouseEvent.MousePressed) {
                    SketchNode node = findNode(mouseEvent.getPointInNodeCoords(Canvas.this));
                    if (node == null) return;
                    setSelection(node);
                }
            }
        });
    }

    @Override
    public void doLayout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doPrefLayout() {
        this.setWidth(getPrefWidth());
        this.setHeight(getPrefHeight());
    }

    @Override
    public void doSkins() {
    }

    @Override
    public void setLayoutDirty() {
        super.setLayoutDirty();
    }

    @Override
    public void draw(GFX gfx) {
        gfx.setPaint(FlatColor.PURPLE);
        gfx.fillRect(0,0,getWidth(),getHeight());
        drawTarget(gfx,target);
        drawSelectionOverlay(gfx);
    }

    private void drawSelectionOverlay(GFX gfx) {
        if(getSelection() == null) return;

        SketchNode s = getSelection();
        Bounds b = s.getInputBounds();
        gfx.translate(s.getTranslateX(),s.getTranslateY());

        gfx.setPaint(FlatColor.fromRGBInts(100,100,100));
        gfx.drawRect(b.getX(),b.getY(),b.getWidth(),b.getHeight());
        gfx.setPaint(FlatColor.fromRGBInts(200,200,200));
        gfx.drawRect(b.getX()-1,b.getY()-1,b.getWidth()+2,b.getHeight()+2);

        gfx.translate(-s.getTranslateX(), -s.getTranslateY());
    }

    private void drawTarget(GFX gfx, TreeNode<SketchNode> target) {
        for(SketchNode n : this.target.children()) {
            drawNode(gfx, n);
        }
    }

    private void drawNode(GFX gfx, SketchNode n) {
        gfx.translate(n.getTranslateX(),n.getTranslateY());
        n.draw(gfx);
        for(SketchNode c : n.children()) {
            drawNode(gfx,c);
        }
        gfx.translate(-n.getTranslateX(),-n.getTranslateY());
    }


    private SketchNode findNode(Point2D pt) {
        for(SketchNode n : this.target.children()) {
            if(n.contains(pt)) return n;
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }



    public void setTarget(TreeNode<SketchNode> level) {
        this.target = level;
    }

    public void setSelection(SketchNode selection) {
        this.selection = selection;
        this.getPropsView().setSelection(selection);
        setDrawingDirty();
    }

    public SketchNode getSelection() {
        return selection;
    }

    public void setPropsView(PropsView propsView) {
        this.propsView = propsView;
    }

    public PropsView getPropsView() {
        return propsView;
    }
}
