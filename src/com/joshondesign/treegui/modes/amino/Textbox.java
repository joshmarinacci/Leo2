package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/8/13
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class Textbox extends ResizableRectNode {
    private String text;

    public Textbox() {
        setWidth(80);
        setHeight(20);
        setText("a text box");
        setConstraint(ResizeConstraint.HorizontalOnly);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setPaint(FlatColor.BLACK);
        //g.drawRect(0+10,0+10,getWidth()-10*2,getHeight()-10*2);
        g.drawText(getText(), Font.DEFAULT,5,15);
        g.drawRect(0, 0, getWidth(), getHeight());
    }

    public Textbox setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new Textbox();
        }
        return super.duplicate(node);
    }
}
