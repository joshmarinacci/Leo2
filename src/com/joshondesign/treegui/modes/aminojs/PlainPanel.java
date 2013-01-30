package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.Prop;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

public class PlainPanel extends ResizableRectNode {
    @Prop
    public String title;

    public PlainPanel() {
        setTitle("A Panel");
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setPaint(FlatColor.BLACK);
        g.drawRect(0,0,getWidth(),getHeight());
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new PlainPanel();
        }
        return super.duplicate(node);
    }

    public PlainPanel getThis() {
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
