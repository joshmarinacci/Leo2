package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

public class Label extends ResizableRectNode {
    private String text = "Label";

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.BLACK);
        g.drawText(getText(), Font.DEFAULT, 5, 20);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new Label();
        }
        return super.duplicate(node);
    }
}
