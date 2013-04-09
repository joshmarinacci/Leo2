package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.MathUtils;
import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.awt.geom.Point2D;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

public class DynamicGroup extends DynamicNode {
    public DynamicGroup() {
        setResize(Resize.Any);
        setVisual(true);
        setContainer(true);
        this.addProperty(new Property("translateX",Double.TYPE,0));
        this.addProperty(new Property("translateY",Double.TYPE,0));
        this
                .addProperty(new Property("width", Double.class, 90).setBindable(false).setExported(false))
                .addProperty(new Property("height", Double.class, 50).setBindable(false).setExported(false))
        ;

        this.addProperty(new Property("self", Object.class, null).setBindable(true).setVisible(false).setExported(false));
        this.setDrawDelegate(DynamicGroupDelegate);
        this.addProperty(new Property("class", String.class, getClass().getName()));
        this.setResize(Resize.None);

        setName("DynamicGroup");
    }

    public static final  DynamicNode.DrawDelegate DynamicGroupDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            /*
            double w = node.getWidth();
            double h = node.getHeight();
            g.setPaint(FlatColor.YELLOW);
            g.fillRoundRect(0, 0, w, h, 10, 10);
            g.setPaint(FlatColor.BLACK);
            g.drawRoundRect(0, 0, w, h, 10, 10);
            g.drawText(node.getName(), Font.DEFAULT, 5, 15);
            */

        }
    };


    @Override
    public boolean contains(Point2D pt) {
        if(this.getSize() < 1) return false;
        Bounds bounds = getInputBounds();
        bounds = MathUtils.transform(bounds, getTranslateX(), getTranslateY());
        return bounds.contains(pt);
    }

    @Override
    public Bounds getInputBounds() {
        Bounds b = MathUtils.unionBounds(this);
        return b;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    public void normalize() {
        double x = Double.MAX_VALUE;
        double y = Double.MAX_VALUE;

        for(SketchNode node : children()) {
            x = Math.min(x,node.getTranslateX());
            y = Math.min(y,node.getTranslateY());
        }


        setTranslateX(x);
        setTranslateY(y);
        for(SketchNode node : children()) {
            node.setTranslateX(node.getTranslateX()-x);
            node.setTranslateY(node.getTranslateY()-y);
        }
    }
}
