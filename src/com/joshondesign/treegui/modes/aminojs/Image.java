package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.GFX;

@Metadata(visual = true, exportClass = "ImageView")
public class Image extends ResizableRectNode {
    @Prop(bindable = true)
    public String src = "http://projects.joshy.org/demos/AnimatedStartup/earth.gif";

    public Image() {
        setWidth(60);
        setHeight(60);
    }

    @Override
    public void draw(GFX g) {
    }
}
