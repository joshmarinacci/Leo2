package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

public class TabPanel extends ResizableRectNode {
    private List<Object> data;

    public TabPanel() {
        List<Object> obs = new ArrayList<Object>();
        obs.addAll(Arrays.asList("dummy", "dummy", "dummy"));
        setListModel(obs);
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setPaint(FlatColor.BLACK);
        g.drawRect(0,0,getWidth(),getHeight());
    }

    public void setListModel(List<Object> data) {
        this.data = data;
    }

    public List<Object> getListModel() {
        return data;
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            node = new TabPanel();
        }
        return super.duplicate(node);
    }
}
