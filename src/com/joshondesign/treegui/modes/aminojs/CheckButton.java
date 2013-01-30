package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

public class CheckButton extends ToggleButton {
    public CheckButton() {
        setText("check button");
        setWidth(100);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0, 0, getHeight(), getHeight());
        g.setPaint(FlatColor.BLACK);
        g.drawText(getText(), Font.DEFAULT, 5 + getHeight(), 15);
        g.drawRect(0,0,getHeight(),getHeight());
    }
    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new CheckButton();
        }
        return super.duplicate(node);
    }
}
