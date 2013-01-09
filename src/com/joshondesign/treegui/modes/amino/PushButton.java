package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

public class PushButton extends ResizableRectNode {
    private String text;

    public PushButton() {
        setWidth(70);
        setHeight(30);
        setText("a button");
        setConstraint(ResizeConstraint.HorizontalOnly);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setPaint(FlatColor.BLACK);
        g.drawText(getText(), Font.DEFAULT,5,20);
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new PushButton();
        }
        return super.duplicate(node);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public TriggerProp getTrigger() {
        return new TriggerProp();
    }
}
