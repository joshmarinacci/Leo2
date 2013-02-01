package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.GFX;

@Metadata(exportClass = "Textbox", resize = Resize.HorizontalOnly)
public class Textbox extends ResizableRectNode {

    @Prop(bindable = true) public String text = "textbox";

    public Textbox() {
        setWidth(80);
        setHeight(20);
    }

    @Override
    public void draw(GFX g) {
    }

}
