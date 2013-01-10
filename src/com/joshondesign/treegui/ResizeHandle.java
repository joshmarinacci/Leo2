package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/6/13
 * Time: 1:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResizeHandle extends Handle {
    private final ResizableRectNode node;

    public ResizeHandle(ResizableRectNode node) {
        this.node = node;
    }

    @Override
    public boolean contains(Point2D point) {
        double x = node.getTranslateX()+node.getWidth();
        double y = node.getTranslateY()+node.getHeight();
        if(point.getX() > x-5 && point.getX() < x+5) {
            if(point.getY() > y-5 && point.getY() < y+5) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void drag(MouseEvent mouseEvent, Point2D pt) {
        node.setWidth(pt.getX()-node.getTranslateX());
        switch(node.getConstraint()) {
            case PreserveAspectOnly:
                node.setHeight(node.getWidth());
                break;
            case Any:
            case VerticalOnly:
                node.setHeight(pt.getY()-node.getTranslateY());
                break;
        }
    }

    @Override
    public void draw(GFX gfx) {
        gfx.translate(node.getTranslateX(),node.getTranslateY());
        gfx.setPaint(FlatColor.PURPLE);
        gfx.fillCircle(node.getWidth(),node.getHeight(),5);
        gfx.translate(-node.getTranslateX(),-node.getTranslateY());
    }


}
