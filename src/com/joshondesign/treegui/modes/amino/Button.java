package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/4/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class Button extends ResizableRectNode {
    public Button() {
        setWidth(70);
        setHeight(30);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setPaint(FlatColor.BLACK);
        g.drawText("text", Font.DEFAULT,5,20);
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new Button();
        }
        return super.duplicate(node);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
