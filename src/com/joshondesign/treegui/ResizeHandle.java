package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.Resize;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.MouseEvent;

public class ResizeHandle extends Handle {
    private final ResizableRectNode node;
    private boolean active;

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
    public void startDrag(MouseEvent mouseEvent) {
        this.active = true;
    }

    @Override
    public void drag(MouseEvent mouseEvent, Point2D pt) {
        node.setWidth(pt.getX()-node.getTranslateX());
        if(mouseEvent.isShiftPressed()) {
            if(node.getConstraint() == Resize.Any) {
                node.setHeight(node.getWidth());
            }
            return;
        }
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
    public void endDrag(MouseEvent mouseEvent, Point2D pt) {
        this.active = false;
    }

    @Override
    public void draw(GFX gfx) {
        gfx.translate(node.getTranslateX(),node.getTranslateY());
        gfx.setPaint(FlatColor.PURPLE);
        gfx.fillCircle(node.getWidth(),node.getHeight(),5);

        if(active) {
            double dx = node.getWidth()+10;
            double dy = node.getHeight()/2 - 25/2;
            gfx.translate(dx,dy);
            gfx.setPaint(FlatColor.GRAY);
            gfx.fillRoundRect(0,0,100,25, 5, 5);
            gfx.setPaint(FlatColor.BLACK);
            gfx.drawText(node.getWidth()+" x " + node.getHeight(), Font.DEFAULT, 10, 15);
            gfx.translate(-dx,-dy);
        }
        gfx.translate(-node.getTranslateX(), -node.getTranslateY());
    }


}
