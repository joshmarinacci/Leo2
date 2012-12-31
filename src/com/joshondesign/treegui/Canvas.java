package com.joshondesign.treegui;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/30/12
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Canvas extends Control {
    private Button target;

    @Override
    public void doLayout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doPrefLayout() {
        this.setWidth(getPrefWidth());
        this.setHeight(getPrefHeight());
        if(this.target != null) {
            this.target.doSkins();
            this.target.doPrefLayout();
            this.target.doLayout();
        }
    }

    @Override
    public void doSkins() {
    }

    @Override
    public void setLayoutDirty() {
        super.setLayoutDirty();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void draw(GFX gfx) {
        gfx.setPaint(FlatColor.PURPLE);
        gfx.fillRect(0,0,getWidth(),getHeight());
        if(this.target != null) {
            this.target.draw(gfx);
        }
    }

    public void setTarget(Button obj) {
        this.target = obj;
    }
}
