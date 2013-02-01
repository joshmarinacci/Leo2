package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.GFX;

@Metadata(resize= Resize.None)
public class Label extends ResizableRectNode {
    @Prop(bindable = true ) public String text = "Label";

    public Label() {
        setWidth(70);
        setHeight(20);
    }

    @Override
    public void draw(GFX g) {
    }
}
