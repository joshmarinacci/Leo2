package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.Prop;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

public class StringListModel extends SketchNode {
    @Prop
    public List<String> data;

    public StringListModel() {
        data = new ArrayList<String>();
        data.add("foo");
        data.add("bar");
        data.add("baz");
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.YELLOW);
        g.fillRoundRect(0, 0, 80, 80, 10, 10);
        g.setPaint(FlatColor.BLACK);
        g.drawRoundRect(0, 0, 80, 80, 10, 10);

        g.drawText("List: String", Font.DEFAULT, 10, 15);

        for(int i=0; i<data.size(); i++) {
            g.drawText(data.get(i), Font.DEFAULT, 5, 10 + i*15 + 25);
        }
    }

    @Override
    public boolean isVisual() {
        return false;
    }

    @Override
    public boolean contains(Point2D pt) {
        Point2D.Double pt2 = new Point2D.Double(pt.getX() - getTranslateX(),
                pt.getY() - getTranslateY());
        return getInputBounds().contains(pt2);
    }

    @Override
    public Bounds getInputBounds() {
        return new Bounds(0,0,80,80);
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public StringListModel getThis() {
        return this;
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null)  node = new StringListModel();
        return super.duplicate(node);
    }
}
