package com.joshondesign.treegui.modes.sketch;

import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;

public class SketchMode extends Mode {


    public SketchMode() {
        setId("com.joshondesign.modes.sketch");

        TreeNode<JAction> javaactions = new TreeNode<JAction>();
        add(javaactions);

        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        add(symbols);


        DynamicNode resizeBase = new DynamicNode();
        resizeBase.addProperty(new Property("translateX", Double.class, 0));
        resizeBase.addProperty(new Property("translateY", Double.class, 0));
        resizeBase.addProperty(new Property("width", Double.class, 80));
        resizeBase.addProperty(new Property("height", Double.class, 30));

        DynamicNode rect = new DynamicNode();
        rect.setName("Rectangle");
        rect.setResizable(true);
        rect.setVisual(true);
        rect.copyPropertiesFrom(resizeBase);
        rect.addProperty(new Property("resize", String.class, "any"));
        rect.setDrawDelegate(new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                g.setPaint(FlatColor.RED);
                g.drawRect(0,0,node.getProperty("width").getDoubleValue(), node.getProperty("height").getDoubleValue());
            }
        });
        symbols.add(rect);


        DynamicNode oval = new DynamicNode();
        oval.setName("Oval");
        oval.setResizable(true);
        oval.setVisual(true);
        oval.copyPropertiesFrom(resizeBase);
        oval.addProperty(new Property("resize", String.class, "any"));
        oval.addProperty(new Property("stroke", FlatColor.class, FlatColor.BLACK));
        oval.addProperty(new Property("fill", FlatColor.class, FlatColor.GREEN));
        oval.setDrawDelegate(new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                g.setPaint(node.getProperty("fill").getColorValue());
                g.fillOval(0, 0, node.getProperty("width").getDoubleValue(), node.getProperty("height").getDoubleValue());
                g.setPaint(node.getProperty("stroke").getColorValue());
                g.drawOval(0, 0, node.getProperty("width").getDoubleValue(), node.getProperty("height").getDoubleValue());
            }
        });
        symbols.add(oval);

    }

    @Override
    public SketchDocument createEmptyDoc() {
        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        Page page = new Page();
        page.add(layer);
        doc.add(page);
        return doc;
    }
}
