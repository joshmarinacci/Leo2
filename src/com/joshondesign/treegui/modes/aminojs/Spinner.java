package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/8/13
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Spinner extends ResizableRectNode {

    private boolean visible;
    private boolean active;

    public Spinner() {
        setWidth(50);
        setHeight(50);
        setConstraint(ResizeConstraint.PreserveAspectOnly);
        setVisible(true);
        setActive(false);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.BLACK);
        g.drawRect(10,10,getWidth()-20,getHeight()-20);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }


    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new Spinner();
        }
        return super.duplicate(node);
    }

}
