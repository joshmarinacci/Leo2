package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;

public class Defs {

    @Metadata(name = "Panel", container = true,
            exportClass = "com.joshondesign.treegui.AnchorPanel")
    public static class PanelProxy {
        @Prop
        public FlatColor fill = FlatColor.GRAY;
    }

    public static final DynamicNode.DrawDelegate panelDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            FlatColor fill = node.getProperty("fill").getColorValue();
            g.setPaint(fill);
            g.fillRect(0, 0, w, h);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, w, h);
        }
    };


    @Metadata(name ="Label", exportClass = "org.joshy.gfx.node.control.Label")
    public static class LabelProxy {
        @Prop(bindable = true)
        public CharSequence text = "a label";
        @Prop
        public FlatColor color = FlatColor.BLACK;
        @Prop
        public double fontSize = 12.5;
    }

    public static final DynamicNode.DrawDelegate labelDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            String t = node.getProperty("text").getStringValue();
            FlatColor color = node.getProperty("color").getColorValue();
            double size = node.getProperty("fontSize").getDoubleValue();
            g.setPaint(color);
            Font font = Font.name(Font.DEFAULT.getName()).size((float) size).resolve();
            double y = font.getAscender() + font.getDescender();
            g.drawText(t,font,5,y);
        }
    };

}
