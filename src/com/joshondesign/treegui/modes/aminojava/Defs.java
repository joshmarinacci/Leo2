package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import java.net.URL;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Image;
import org.joshy.gfx.util.u;

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


    @Metadata(name = "Image", exportClass = "org.joshy.gfx.node.control.ImageView")
    public static class ImageProxy {
        @Prop public String source = null;
    }

    public static final DynamicNode.DrawDelegate imageDelegate = new DynamicNode.DrawDelegate() {
        public void draw(GFX g, DynamicNode node) {
            double w = node.getWidth();
            double h = node.getHeight();
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0, 0, w, h);

            String source = node.getProperty("source").getStringValue();
            if(source != null) {
                try {
                    Image image = Image.getImageFromCache(new URL(source));
                    g.drawImage(image,0,0);
                } catch (Exception ex) {
                    u.p(ex);
                }
            }

            g.setPaint(FlatColor.BLACK);
            g.drawRect(0 + 10, 0 + 10, w - 10 * 2, h - 10 * 2);
        }
    };

}
