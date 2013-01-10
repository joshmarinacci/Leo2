package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.util.Arrays;
import java.util.List;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

public class ListView extends ResizableRectNode {
    private List<String> data;

    public ListView() {
        setWidth(70);
        setHeight(140);
        setData(Arrays.asList("dummy", "dummy", "dummy"));
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setPaint(FlatColor.BLACK);

        for(int i=0; i<data.size(); i++) {
            g.drawText(data.get(i), Font.DEFAULT, 5, i*20+20);
        }

        g.drawRect(0,0,getWidth(),getHeight());
        //g.drawText(getText(), Font.DEFAULT,5,20);
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new ListView();
        }
        return super.duplicate(node);
    }
}
