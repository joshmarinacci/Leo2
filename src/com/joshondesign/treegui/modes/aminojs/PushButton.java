package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.GFX;

@Metadata(name = "PushButton", exportClass = "PushButton", resize = Resize.None)
public class PushButton extends ResizableRectNode {
    @Prop(bindable = true)
    public String text = "push button";

    @Prop(visible = false, bindable = true)
    public TriggerProp trigger;

    public PushButton() {
        setWidth(70);
        setHeight(20);
    }

    @Override
    public void draw(GFX g) {
    }
}
