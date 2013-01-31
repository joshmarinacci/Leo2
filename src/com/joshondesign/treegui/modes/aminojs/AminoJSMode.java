package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.BindingUtils;
import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.Menu;

public class AminoJSMode extends Mode {
    public static Map<String, DynamicNode.DrawDelegate> drawMap = new HashMap<String, DynamicNode.DrawDelegate>();

    public AminoJSMode() {
        setId("com.joshondesign.modes.aminojs");
        TreeNode<JAction> actions = new TreeNode<JAction>();
        add(actions);


        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        Rect rect = new Rect();
        rect.setId("Rect");
        symbols.add(rect);


        DynamicNode visualBase = new DynamicNode();
        visualBase
                .addProperty(new Property("translateX", Double.class, 0))
                .addProperty(new Property("translateY", Double.class, 0))
                .addProperty(new Property("width", Double.class, 80))
                .addProperty(new Property("height", Double.class, 30))
        ;

        drawMap.put("PushButton", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                String t = node.getProperty("text").getStringValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawText(t, Font.DEFAULT, 5, 15);
                g.drawRect(0, 0, w, h);
            }
        });

        drawMap.put("CheckButton", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                String t = node.getProperty("text").getStringValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, h, h);
                g.setPaint(FlatColor.BLACK);
                g.drawText(t, Font.DEFAULT, 5 + h, 15);
                g.drawRect(0,0,h,h);
            }
        });

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new PushButton(), drawMap.get("PushButton"))
                .setVisual(true)
                .copyPropertiesFrom(visualBase));

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new ToggleButton(), drawMap.get("PushButton"))
                .setDrawDelegate(drawMap.get("PushButton"))
                .copyPropertiesFrom(visualBase));

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new CheckButton(), drawMap.get("CheckButton"))
                .copyPropertiesFrom(visualBase));


        drawMap.put("Slider", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.fillRect(0, 0, h, h);
            }
        });
        drawMap.put("Image", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawRect(0 + 10, 0 + 10, w - 10 * 2, h - 10 * 2);
            }
        });
        drawMap.put("TextBox", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                String t = node.getProperty("text").getStringValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawText(t, Font.DEFAULT, 5, 15);
                g.drawRect(0, 0, w, h);
            }
        });
        drawMap.put("Label", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                String t = node.getProperty("text").getStringValue();
                g.setPaint(FlatColor.BLACK);
                g.drawText(t, Font.DEFAULT, 5, 15);
            }
        });
        drawMap.put("ListView", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);

                List<String> data = (List<String>) node.getProperty("data").getRawValue();

                for(int i=0; i<data.size(); i++) {
                    g.drawText(data.get(i), Font.DEFAULT, 5, i*20+20);
                }

                g.drawRect(0, 0, w, h);
            }
        });

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new Slider(), drawMap.get("Slider"))
                .copyPropertiesFrom(visualBase));

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new Image(), drawMap.get("Image"))
                .copyPropertiesFrom(visualBase));
        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new Textbox(), drawMap.get("TextBox"))
                .copyPropertiesFrom(visualBase));
        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new Label(), drawMap.get("Label"))
                .copyPropertiesFrom(visualBase));

        DynamicNode listview = BindingUtils
                .parseAnnotatedPOJO(new ListView(), drawMap.get("ListView"));
        listview.copyPropertiesFrom(visualBase);

        Property subProp = new Property("selectedObject",Object.class,null);
        subProp.setBindable(true);
        subProp.setCompound(true);
        subProp.setMasterProperty("data");
        listview.addProperty(subProp);

        symbols.add(listview);




        drawMap.put("PlainPanel", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawRect(0,0,w,h);
            }
        });

        drawMap.put("Spinner", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                g.setPaint(FlatColor.BLACK);
                g.drawOval(10, 10, w - 20, h - 20);
            }
        });
        drawMap.put("FlickrQuery", new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getWidth();
                double h = node.getHeight();
                g.setPaint(FlatColor.YELLOW);
                g.fillRoundRect(0, 0, 80, 80, 10, 10);
                g.setPaint(FlatColor.BLACK);
                g.drawRoundRect(0, 0, 80, 80, 10, 10);
                g.drawText("Flickr Query", Font.DEFAULT, 10, 15);
            }
        });

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new PlainPanel(), drawMap.get("PlainPanel"))
                .copyPropertiesFrom(visualBase));

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new TabPanel(), drawMap.get("PlainPanel"))
                .copyPropertiesFrom(visualBase));

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new StringListModel(), drawMap.get("PlainPanel"))
                .copyPropertiesFrom(visualBase));
        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new ControlListModel(), drawMap.get("TabPanel"))
                .copyPropertiesFrom(visualBase));

        symbols.add(BindingUtils
                .parseAnnotatedPOJO(new Spinner(), drawMap.get("Spinner"))
                .copyPropertiesFrom(visualBase));


        DynamicNode photo = BindingUtils.parseAnnotatedPOJO(new com.joshondesign.treegui.modes.aminojava.FlickrQuery.Photo("a","b"), null);
        photo.setVisual(false);
        //photo.copyPropertiesFrom(serviceBase);

        DynamicNode flickr =  BindingUtils
                .parseAnnotatedPOJO(new FlickrQuery(), drawMap.get("FlickrQuery"));
        flickr.copyPropertiesFrom(visualBase);
        flickr.getProperty("results").setList(true).setItemPrototype(photo);
        symbols.add(flickr);


        add(symbols);

    }

    @Override
    public String getName() {
        return "Amino JS";
    }

    @Override
    public SketchDocument createEmptyDoc() {
        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        layer.add(findSymbol("PlainPanel").duplicate(null));
        Page page = new Page();
        page.add(layer);
        doc.add(page);
        return doc;
    }

    @Override
    public void modifyFileMenu(Menu fileMenu, SketchDocument doc) {
        fileMenu.addItem("Export HTML", "E", new HTMLBindingExport(doc));
    }
}
