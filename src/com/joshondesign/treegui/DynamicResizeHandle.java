package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.MouseEvent;

public class DynamicResizeHandle extends Handle {
    private final DynamicNode node;
    private final SketchDocument doc;

    public DynamicResizeHandle(DynamicNode node, SketchDocument doc) {
        this.node = node;
        this.doc = doc;
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
    public SketchNode getNode() {
        return this.node;
    }

    @Override
    public void drag(MouseEvent mouseEvent, Point2D pt) {
        double w = pt.getX() - node.getTranslateX();
        if(doc.isSnapToGrid()) {
            w = Math.floor(w/10)*10;
        }
        node.setWidth(w);
        //node.getProperty("width").setDoubleValue(w);

        String resize = "any";
        if(node.hasProperty("resize")) {
            resize = node.getProperty("resize").getStringValue();
        }
        if(mouseEvent.isShiftPressed()) {
            if(resize.equals("any")) {
                node.getProperty("height").setDoubleValue(node.getProperty("width").getDoubleValue());
            }
            return;
        }

        if(resize.equals("any")) {
            double h = pt.getY()-node.getTranslateY();
            if(doc.isSnapToGrid()) {
                h = Math.floor(h/10)*10;
            }
            node.getProperty("height").setDoubleValue(h);
        }
    }

    @Override
    public void endDrag(MouseEvent mouseEvent, Point2D pt) {
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
    }


}