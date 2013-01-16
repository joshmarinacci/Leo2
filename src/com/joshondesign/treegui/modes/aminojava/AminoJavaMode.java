package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.amino.ActionProp;
import com.joshondesign.treegui.modes.amino.Rect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.node.control.ScrollPane;

public class AminoJavaMode extends Mode {
    public static Map<String,DynamicNode.DrawDelegate> drawMap = new HashMap<String, DynamicNode.DrawDelegate>();


    public AminoJavaMode() {
        setId("com.joshondesign.modes.aminojava");

        TreeNode<JAction> javaactions = new TreeNode<JAction>();
        add(javaactions);

        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        add(symbols);

        DynamicNode base = new DynamicNode();
        base.addProperty(new Property("translateX", Double.class, 0));
        base.addProperty(new Property("translateY", Double.class, 0));
        base.addProperty(new Property("width", Double.class, 80)
                .setExportName("prefWidth"));
        base.addProperty(new Property("height", Double.class, 30)
                .setExportName("prefHeight"));
        base.addProperty(new Property("anchorLeft", Boolean.class, true).setBindable(false));
        base.addProperty(new Property("anchorRight", Boolean.class, false).setBindable(false));
        base.addProperty(new Property("anchorTop", Boolean.class, true).setBindable(false));
        base.addProperty(new Property("anchorBottom", Boolean.class, false).setBindable(false));


        DynamicNode button = new DynamicNode();
        button.setName("Button");
        button.setResizable(true);
        button.setVisual(true);

        button.copyPropertiesFrom(base);
        button.addProperty(new Property("class", String.class, "org.joshy.gfx.node.control.Button"));
        button.addProperty(new Property("id", String.class, "arandomid"));
        button.addProperty(new Property("text", CharSequence.class, "a button").setBindable(true));
        button.addProperty(new Property("selected", Boolean.class, false));
        button.addProperty(new Property("resize", String.class, "any")
                .setExported(false)
                .setVisible(false));
        button.addProperty(new Property("trigger", GuiTest.TriggerType.class, 0)
                .setExported(false)
                .setVisible(false)
                .setBindable(true)
        );

        drawMap.put(button.getName(), new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getProperty("width").getDoubleValue();
                double h = node.getProperty("height").getDoubleValue();
                String t = node.getProperty("text").getStringValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawText(t, Font.DEFAULT, 5, 15);
                g.drawRect(0, 0, w, h);
            }
        });
        button.setDrawDelegate(drawMap.get(button.getName()));
        symbols.add(button);


        DynamicNode label = new DynamicNode();
        label.copyPropertiesFrom(base);
        label.setName("Label");
        label.setResizable(true);
        label.setVisual(true);
        label.addProperty(new Property("class", String.class, "org.joshy.gfx.node.control.Label"));
        label.addProperty(new Property("id", String.class, "arandomid").setExported(true));
        label.copyPropertiesFrom(button);
        label.addProperty(new Property("resize", String.class, "width").setExported(false));
        label.addProperty(new Property("text", String.class, "a label").setBindable(true));
        drawMap.put(label.getName(), new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                String t = node.getProperty("text").getStringValue();
                g.setPaint(FlatColor.BLACK);
                g.drawText(t, Font.DEFAULT, 5, 15);
            }
        });
        label.setDrawDelegate(drawMap.get(label.getName()));

        symbols.add(label);

        DynamicNode panel = new DynamicNode();
        panel.copyPropertiesFrom(base);
        panel.setName("Panel");
        panel.setVisual(true);
        panel.setResizable(true);
        panel.addProperty(new Property("class", String.class, "com.joshondesign.treegui.AnchorPanel"))
                .addProperty(new Property("resize", String.class, "any")
                        .setExported(false).setVisible(false))
                .addProperty(new Property("id", String.class, "arandomid"))
                .addProperty(new Property("fill", FlatColor.class, FlatColor.PURPLE))
        ;
        panel.setContainer(true);
        drawMap.put(panel.getName(), new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getProperty("width").getDoubleValue();
                double h = node.getProperty("height").getDoubleValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawRect(0, 0, w, h);
            }
        });
        panel.setDrawDelegate(drawMap.get(panel.getName()));
        symbols.add(panel);



        DynamicNode listview = new DynamicNode();
        listview.copyPropertiesFrom(base);
        listview.setName("ListView");
        listview.setVisual(true);
        listview.setResizable(true);
        listview.addProperty(new Property("class", String.class, "org.joshy.gfx.node.control.ListView"))
                .addProperty(new Property("id", String.class, "foo"))
                .addProperty(new Property("resize", String.class, "any")
                        .setExported(false).setVisible(false))
                .addProperty(new Property("rowHeight", Double.class, 20))
                .addProperty(new Property("columnWidth", Double.class, 100))
                .addProperty(new Property("renderer", ListView.ItemRenderer.class, "none"))
                .addProperty(new Property("model", ListModel.class, null)
                        .setBindable(true).setExported(false).setVisible(false))
                .addProperty(new Property("orientation",
                        ListView.Orientation.class, ListView.Orientation.Vertical))
        ;
        drawMap.put(listview.getName(), new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getProperty("width").getDoubleValue();
                double h = node.getProperty("height").getDoubleValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawRect(0, 0, w, h);
                //lines
                for (int i = 0; i < 5; i++) {
                    g.drawRect(0, i * 15, w, 15);
                }
            }
        });
        listview.setDrawDelegate(drawMap.get(listview.getName()));
        listview.getProperty("width").setDoubleValue(60);
        listview.getProperty("height").setDoubleValue(90);
        symbols.add(listview);


        DynamicNode scroll = new DynamicNode();
        scroll.copyPropertiesFrom(base);
        scroll.setName("ScrollPane");
        scroll.setVisual(true);
        scroll.setResizable(true);
        scroll.setContainer(true);
        scroll.addProperty(new Property("class", String.class,
                "org.joshy.gfx.node.control.ScrollPane"))
                .addProperty(new Property("id", String.class, "foo2"))
                .addProperty(new Property("resize", String.class, "any")
                        .setExported(false).setVisible(false))
                .addProperty(new Property("horizontalVisiblePolicy",
                        ScrollPane.VisiblePolicy.class,
                        ScrollPane.VisiblePolicy.WhenNeeded
                )
                )
                .addProperty(new Property("verticalVisiblePolicy",
                        ScrollPane.VisiblePolicy.class,
                        ScrollPane.VisiblePolicy.WhenNeeded
                )
                )
        ;
        drawMap.put(scroll.getName(), new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getProperty("width").getDoubleValue();
                double h = node.getProperty("height").getDoubleValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawRect(0, 0, w, h);
                g.drawRect(0, h - 10, w, 10);
                g.drawRect(w - 10, 0, 10, h);
            }
        });
        scroll.setDrawDelegate(drawMap.get(scroll.getName()));
        scroll.getProperty("width").setDoubleValue(60);
        scroll.getProperty("height").setDoubleValue(90);

        symbols.add(scroll);


        DynamicNode spinner = new DynamicNode();
        spinner.copyPropertiesFrom(base);
        spinner.setName("Spinner");
        spinner.setVisual(true);
        spinner.setResizable(true);
        spinner
                .addProperty(new Property("class", String.class,
                        "com.joshondesign.treegui.modes.aminojava.Spinner"))
                .addProperty(new Property("id", String.class, "foo2"))
                .addProperty(new Property("resize", String.class, "any")
                        .setExported(false).setVisible(false))
                .addProperty(new Property("active", Boolean.class,Boolean.TRUE)
                        .setBindable(true))
        ;
        drawMap.put(spinner.getName(), new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getProperty("width").getDoubleValue();
                double h = node.getProperty("height").getDoubleValue();
                g.setPaint(FlatColor.GRAY);
                g.fillOval(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawOval(0, 0, w, h);
            }
        });
        spinner.setDrawDelegate(drawMap.get(spinner.getName()));
        spinner.getProperty("width").setDoubleValue(50);
        spinner.getProperty("height").setDoubleValue(50);
        symbols.add(spinner);


        DynamicNode custom = new DynamicNode();
        custom.setName("Custom View");
        custom.copyPropertiesFrom(base);
        custom.setVisual(true);
        custom.setResizable(true);
        custom.setContainer(false);
        custom
                .addProperty(new Property("class", String.class,"org.joshy.gfx.node.control.ScrollPane"))
                .addProperty(new Property("id", String.class, "foo2"))
                .addProperty(new Property("customClass",String.class, "none"))
                .addProperty(new Property("resize", String.class, "any")
                        .setExported(false).setVisible(false))
        ;
        custom.setCustom(true);
        drawMap.put(custom.getName(), new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getProperty("width").getDoubleValue();
                double h = node.getProperty("height").getDoubleValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawRect(0, 0, w, h);
                g.drawLine(0, 0, w, h);
                g.drawLine(0, h, w, 0);
            }
        });
        custom.setDrawDelegate(drawMap.get(custom.getName()));
        custom.getProperty("width").setDoubleValue(90);
        custom.getProperty("height").setDoubleValue(90);
        symbols.add(custom);

        DynamicNode textbox = new DynamicNode();
        textbox.setName("Textbox");
        textbox.copyPropertiesFrom(base);
        textbox.setVisual(true);
        textbox.setResizable(true);
        textbox.setContainer(false);
        textbox
                .addProperty(new Property("class", String.class,
                        "org.joshy.gfx.node.control.Textbox"))
                .addProperty(new Property("id", String.class, "foo2"))
                .addProperty(new Property("text", String.class, "a textfield").setBindable(true))
                .addProperty(new Property("resize", String.class, "any")
                        .setExported(false).setVisible(false))
        ;
        drawMap.put(textbox.getName(), new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getProperty("width").getDoubleValue();
                double h = node.getProperty("height").getDoubleValue();
                String t = node.getProperty("text").getStringValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawRect(0, 0, w, h);
                g.drawText(t, Font.DEFAULT, 5, 15);
            }
        });
        textbox.setDrawDelegate(drawMap.get(textbox.getName()));
        textbox.getProperty("width").setDoubleValue(80);
        textbox.getProperty("height").setDoubleValue(30);
        symbols.add(textbox);

        DynamicNode serviceBase = new DynamicNode();
        serviceBase.addProperty(new Property("translateX", Double.class, 0).setExported(false))
                .addProperty(new Property("translateY", Double.class, 0).setExported(false))
                .addProperty(new Property("width", Double.class, 90).setBindable(false).setExported(false))
                .addProperty(new Property("height", Double.class, 50).setBindable(false).setExported(false))
        ;
        drawMap.put("servicebase",new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = 90;
                double h = 50;
                g.setPaint(FlatColor.YELLOW);
                g.fillRoundRect(0, 0, w, h, 10, 10);
                g.setPaint(FlatColor.BLACK);
                g.drawRoundRect(0, 0, w, h, 10, 10);
                g.drawText(node.getName(), Font.DEFAULT, 5, 15);
            }
        });

        DynamicNode flickrQuery = new DynamicNode();
        flickrQuery.setName("Flickr Search");
        flickrQuery.setVisual(false);
        flickrQuery.setResizable(false);
        flickrQuery.copyPropertiesFrom(serviceBase);
        flickrQuery
                .addProperty(new Property("class", String.class,
                        "com.joshondesign.treegui.modes.aminojava.FlickrQuery"))
                .addProperty(new Property("execute", ActionProp.class, null)
                        .setBindable(true).setVisible(false))
                .addProperty(new Property("query", String.class, "london")
                        .setBindable(true).setVisible(true))
                .addProperty(new Property("results", ListModel.class, null)
                        .setBindable(true).setVisible(false))
                .addProperty(new Property("active", Boolean.class, Boolean.TRUE)
                        .setBindable(true))
        ;
        flickrQuery.setDrawDelegate(drawMap.get("servicebase"));
        symbols.add(flickrQuery);

        DynamicNode action = new DynamicNode();
        action.setName("action");
        action.setVisual(false);
        action.setResizable(false);
        action.copyPropertiesFrom(serviceBase);
        action.addProperty(new Property("class", String.class,"com.joshondesign.flickr.FlickrQuery"))
                .addProperty(new Property("execute", ActionProp.class, null).setBindable(true))
        ;
        action.setDrawDelegate(drawMap.get("servicebase"));
        symbols.add(action);

        DynamicNode document = new DynamicNode();
        document.setName("Document");
        document.setVisual(false);
        document.setResizable(false);
        document.copyPropertiesFrom(serviceBase);
        document
                .addProperty(new Property("class", String.class,"com.joshondesign.flickr.FlickrQuery"))
                .addProperty(new Property("pages", String.class, null).setBindable(true))
                .addProperty(new Property("currentPage", Page.class, null).setBindable(true))
                .addProperty(new Property("selection", List.class, null).setBindable(true))
        ;
        document.setDrawDelegate(drawMap.get("servicebase"));
        symbols.add(document);

        DynamicNode stringList = new DynamicNode();
        stringList.setName("String List");
        stringList.setVisual(false);
        stringList.setResizable(false);
        stringList.copyPropertiesFrom(serviceBase);
        List<String> dummyStringListData = new ArrayList<String>();
        dummyStringListData.add("foo");
        dummyStringListData.add("bar");
        dummyStringListData.add("baz");
        stringList
                .addProperty(new Property("class", String.class,
                        "com.joshondesign.flickr.FlickrQuery"))
                .addProperty(new Property("data", List.class, dummyStringListData)
                        .setVisible(true).setBindable(true))
                .addProperty(new Property("this",DynamicNode.class, null)
                        .setVisible(false).setBindable(true))
        ;
        stringList.setDrawDelegate(drawMap.get("servicebase"));
        symbols.add(stringList);
    }


    @Override
    public SketchDocument createEmptyDoc() {
        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        layer.add(new Rect().setFill(FlatColor.GREEN).setWidth(50).setHeight(50));
        Page page = new Page();
        page.add(layer);
        doc.add(page);
        return doc;
    }
}