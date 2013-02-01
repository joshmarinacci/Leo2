package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import java.util.Arrays;
import java.util.List;
import org.joshy.gfx.draw.GFX;

@Metadata(visual = true, exportClass = "ListView")
public class ListView extends ResizableRectNode {
    @Prop public double width = 100;
    @Prop public double height = 100;
    @Prop(bindable = true) public List<String> data = Arrays.asList("dummy", "dummy", "dummy");
    @Prop(bindable = true, exported = false, compound=true, master="data")
    public Object selectedObject = null;
    @Prop(bindable = true) public int selectedIndex = 0;

    public ListView() {
        setWidth(70);
        setHeight(140);
    }

    public void draw(GFX g) { }


}
