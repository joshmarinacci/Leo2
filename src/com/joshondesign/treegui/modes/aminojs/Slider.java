package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

@Metadata(resize = Resize.HorizontalOnly)
public class Slider extends ResizableRectNode {

    @Prop public Double minValue = 0.0;
    @Prop public double maxValue = 100;
    @Prop public double value = 50;

    public Slider() {
        setWidth(70);
        setHeight(20);
    }



    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setPaint(FlatColor.BLACK);
        g.fillRect(0,0,getHeight(),getHeight());
    }
}
