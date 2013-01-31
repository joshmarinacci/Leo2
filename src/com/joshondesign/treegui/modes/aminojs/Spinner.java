package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.Metadata;
import com.joshondesign.treegui.modes.aminojava.Prop;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

@Metadata(visual = true, exportClass = "Spinner")
public class Spinner extends ResizableRectNode {

    @Prop public boolean visible;
    @Prop public boolean active;

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
