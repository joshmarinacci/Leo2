package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.modes.aminojava.Metadata;
import com.joshondesign.treegui.modes.aminojava.Prop;
import java.util.Arrays;
import java.util.List;
import org.joshy.gfx.draw.GFX;

@Metadata(visual = true, exportClass = "ListView")
public class ListView extends ResizableRectNode {

    @Prop public double width = 100;
    @Prop public double height = 100;
    @Prop(bindable = true) public List<String> data;
    @Prop(bindable = true) public int selectedIndex = 0;

    public ListView() {
        setWidth(70);
        setHeight(140);
        setData(Arrays.asList("dummy", "dummy", "dummy"));
    }

    @Override
    public void draw(GFX g) {
    }


    public void setData(List<String> data) {
        this.data = data;
    }

}
