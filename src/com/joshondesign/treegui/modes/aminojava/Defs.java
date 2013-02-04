package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import org.joshy.gfx.draw.FlatColor;
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
}
