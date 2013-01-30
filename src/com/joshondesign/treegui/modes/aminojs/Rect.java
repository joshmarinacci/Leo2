package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

public class Rect extends ResizableRectNode {

    private FlatColor fill = FlatColor.GRAY;

    public Rect() {
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(getFill());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new Rect();
        }
        return super.duplicate(node);
    }

    public Rect setFill(FlatColor fill) {
        this.fill = fill;
        return this;
    }

    public FlatColor getFill() {
        return fill;
    }

}
