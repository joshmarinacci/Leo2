package com.joshondesign.treegui;

import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/11/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class DynamicResizeHandle extends Handle {
    private final DynamicNode node;

    public DynamicResizeHandle(DynamicNode node) {
        this.node = node;
    }

    @Override
    public boolean contains(Point2D point) {
        double w = node.getProperty("width").getDoubleValue();
        double h = node.getProperty("height").getDoubleValue();
        double x = node.getTranslateX() + w;
        double y = node.getTranslateY() + h;
        if (point.getX() > x - 5 && point.getX() < x + 5) {
            if (point.getY() > y - 5 && point.getY() < y + 5) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void drag(MouseEvent mouseEvent, Point2D pt) {
        double w = pt.getX() - node.getTranslateX();
        w = Math.floor(w/10)*10;
        node.getProperty("width").setDoubleValue(w);
        String resize = node.getProperty("resize").getStringValue();
        if(resize.equals("any")) {
            double h = pt.getY()-node.getTranslateY();
            h = Math.floor(h/10)*10;
            node.getProperty("height").setDoubleValue(h);
        }
    }

    @Override
    public void endDrag(MouseEvent mouseEvent, Point2D pt) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void draw(GFX gfx) {
        double w = node.getProperty("width").getDoubleValue();
        double h = node.getProperty("height").getDoubleValue();
        gfx.translate(node.getTranslateX(), node.getTranslateY());
        gfx.setPaint(FlatColor.PURPLE);
        gfx.fillCircle(w, h, 5);
        gfx.translate(-node.getTranslateX(), -node.getTranslateY());
    }

    @Override
    public void startDrag(MouseEvent mouseEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}