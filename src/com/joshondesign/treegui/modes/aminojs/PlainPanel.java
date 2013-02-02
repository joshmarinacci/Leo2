package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.GFX;

@Metadata(visual = true, name = "PlainPanel", container = true, exportClass = "PlainPanel")
public class PlainPanel extends ResizableRectNode {
    @Prop
    public String title = "title";

    @Prop public double translateX = 0;
    @Prop public double width = 100;
    @Prop public double height = 100;

    @Override
    public void draw(GFX g) {
    }
}
