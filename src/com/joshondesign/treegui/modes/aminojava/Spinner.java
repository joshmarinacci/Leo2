package com.joshondesign.treegui.modes.aminojava;

import org.joshy.gfx.anim.PropertyAnimator;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/13/13
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Spinner extends Control {
    private final PropertyAnimator indeterminateAnim;
    private double animAngle = 0;
    private boolean active;

    public Spinner() {
        doLayout();
        indeterminateAnim = PropertyAnimator.target(this).
                property("animAngle").
                startValue(0).
                endValue(360).
                milliseconds(1000).
                repeat(PropertyAnimator.INDEFINITE);
        this.setActive(false);
    }

    @Override
    public void doLayout() {
        setWidth(50);
        setHeight(50);
    }

    @Override
    public void doPrefLayout() {
    }

    @Override
    public void doSkins() {
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.BLACK);
        double cx = getWidth()/2;
        double cy = getHeight()/2;
        double radius = getWidth()/2;
        g.fillCircle(cx,cy,radius);
        g.setPaint(FlatColor.BLUE);
        g.fillArc(cx,cy,radius,0-animAngle,60-animAngle);
        g.fillArc(cx,cy,radius,120-animAngle,180-animAngle);
        g.fillArc(cx,cy,radius,240-animAngle,300-animAngle);
    }

    public void setAnimAngle(double animAngle) {
        this.animAngle = animAngle;
        setDrawingDirty();
    }


    public void setActive(boolean active) {
        u.p("spinner active setter called");
        this.active = active;
        if(active) {
            indeterminateAnim.start();
        } else {
            indeterminateAnim.stop();
        }
    }

    public boolean isActive() {
        return active;
    }
}
