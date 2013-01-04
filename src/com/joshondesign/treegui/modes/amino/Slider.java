package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

public class Slider extends ResizableRectNode {
    public Slider() {
        setWidth(70);
        setHeight(30);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setPaint(FlatColor.BLACK);
        g.fillRect(0,0,getHeight(),getHeight());
    }
    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new Slider();
        }
        return super.duplicate(node);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
