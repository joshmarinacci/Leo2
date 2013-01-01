package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
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

    public void setTarget(TreeNode<SketchNode> level) {
        this.target = level;
    }
}
