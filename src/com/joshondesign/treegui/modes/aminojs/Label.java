package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.GFX;

@Metadata(resize= Resize.Any, exportClass = "Label")
public class Label extends ResizableRectNode {
    @Prop(bindable = true ) public String text = "Label";
    @Prop public double fontsize = 12.0;

    public Label() {
        setWidth(70);
        setHeight(20);
    }

    @Override
    public void draw(GFX g) {
    }
}
