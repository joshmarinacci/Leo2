package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.SketchNode;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/9/13
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlListModel extends SketchNode {
    private List<Object> data;

    public ControlListModel() {
        data = new ArrayList<Object>();
    }
    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.YELLOW);
        g.fillRoundRect(0, 0, 80, 80, 10, 10);
        g.setPaint(FlatColor.BLACK);
        g.drawRoundRect(0, 0, 80, 80, 10, 10);

        g.drawText("List: Object", Font.DEFAULT, 10, 15);

        for(int i=0; i<data.size(); i++) {
            g.drawText(data.get(i).toString(), Font.DEFAULT, 5, 10 + i*15 + 25);
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

    public Bounds getInputBounds() {
        return new Bounds(0,0,80,80);
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public void setItem1(Object object) {
    }
    public Object getItem1() {
        return null;
    }
    public void setItem2(Object object) {
    }
    public Object getItem2() {
        return null;
    }
    public void setItem3(Object object) {
    }
    public Object getItem3() {
        return null;
    }

    public ControlListModel getThis() {
        return this;
    }

    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null)  node = new ControlListModel();
        return super.duplicate(node);
    }

}
