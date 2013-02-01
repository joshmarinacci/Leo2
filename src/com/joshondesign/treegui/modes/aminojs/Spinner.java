package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.GFX;

@Metadata(exportClass = "Spinner", resize = Resize.PreserveAspectOnly)
public class Spinner extends ResizableRectNode {

    @Prop public boolean visible = true;
    @Prop(bindable = true) public boolean active = false;

    public Spinner() {
        setWidth(50);
        setHeight(50);
    }

    @Override
    public void draw(GFX g) {
    }
}
