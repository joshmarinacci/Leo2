package com.joshondesign.treegui;

import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.model.TreeNodeListView;
import com.joshondesign.treegui.modes.amino.*;
import com.joshondesign.treegui.modes.aminojava.*;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

public class TreeGui implements Runnable {

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new TreeGui());
    }

    public void run() {
        final TreeNode<Mode> modes = initModes();
        Mode mode = modes.get(1);
        final SketchDocument doc = initDoc();
        Stage stage = Stage.createStage();
        stage.setWidth(800);
        stage.setHeight(600);

        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
            public void call(Event event) throws Exception {
                System.exit(0);
            }
        });

        Control rootControl;



        try {
            File file = new File("/Users/josh/projects/Leo/nodes.xml");
            Doc xml = XMLParser.parse(file);

            u.p("parsed the xml: " + xml);
            Elem root = xml.xpathElement("/xml/panel");
            rootControl = new ControlLoader().processControls(root);
            stage.setContent(rootControl);

            PropsView propsView = (PropsView) find("propsview", rootControl);

            //hook up the canvas
            final Canvas canvas = (Canvas) find("canvas",rootControl);
            canvas.setMasterRoot(doc.get(0).get(0));
            canvas.setEditRoot(doc.get(0).get(0));
            canvas.setPropsView(propsView);


            //hoook up the props view
            propsView.setPropFilter(new PropsView.PropFilter() {
                public boolean include(Object obj, String name) {
                    return true;
                }
            });
            propsView.setSelection(doc.get(0).get(0).get(0));
            propsView.onUpdate(new Callback<Void>() {
                public void call(Void aVoid) throws Exception {
                    canvas.setLayoutDirty();
                }
            });


            //hook up the actions toolbar
            TreeNode<JAction> actions = (TreeNode<JAction>)mode.get(0);
            HTMLBindingExport exp = new HTMLBindingExport();
            exp.canvas = canvas;
            exp.page = doc.get(0);
            //actions.add(exp);
            actions.add(new AminoJavaXMLExport(canvas,doc.get(0)));
            actions.add(new AminoJavaXMLImport(canvas));
            actions.add(new AminoJavaXMLExport.Save(canvas, doc));

            ToolbarListView toolbar = (ToolbarListView) find("toolbar", rootControl);
            toolbar.setModel(actions);


            //hook up the symbols view

            final TreeNodeListView symbolsView = (TreeNodeListView) find("symbolsview", rootControl);
            TreeNode<SketchNode> symbols = mode.get(1);
            symbolsView.setTreeNodeModel(symbols);
            symbolsView.setRenderer(new ListView.ItemRenderer<TreeNode>() {
                public void draw(GFX gfx, ListView listView, TreeNode treeNode, int index, double x, double y, double w, double h) {
                    if(treeNode == null) return;
                    SketchNode node = (SketchNode) treeNode;
                    gfx.translate(x, y);
                    node.draw(gfx);
                    gfx.setPaint(FlatColor.WHITE);

                    gfx.fillRect(0, h-20, w, 20);
                    gfx.setPaint(FlatColor.BLACK);
                    if(node instanceof DynamicNode) {
                        gfx.drawText(((DynamicNode) node).getName(), Font.DEFAULT, 5, h-4);
                    } else {
                        gfx.drawText(node.getId(), Font.DEFAULT,5,h-4);
                    }
                    gfx.translate(-x,-y);
                    if(listView.getSelectedIndex() == index) {
                        gfx.setPaint(new FlatColor(1.0,0,0,0.3));
                        gfx.fillRect(x,y,w,h);
                    }
                    gfx.setPaint(FlatColor.BLACK);
                    gfx.drawRect(x,y,w,h);
                }
            });
            EventBus.getSystem().addListener(symbolsView, MouseEvent.MouseAll, new Callback<MouseEvent>() {
                public boolean created;
                public SketchNode dupe;
                public double prevx;

                public void call(MouseEvent event) throws Exception {
                    if(event.getType() == MouseEvent.MouseDragged) {
                        Point2D pt = canvas.toEditRootCoords(event.getPointInNodeCoords(canvas));
                        if(created && dupe != null) {
                            //pt = canvas.transformToCanvas(pt);
                            Bounds b = dupe.getInputBounds();
                            dupe.setTranslateX(pt.getX()-b.getWidth()/2);
                            dupe.setTranslateY(pt.getY()-b.getHeight()/2);
                            //context.redraw();
                            canvas.setLayoutDirty();
                        }
                        if(event.getX() < 0 && prevx >= 0 && !created) {
                            created = true;
                            if(symbolsView.getSelectedIndex() < 0) return;
                            SketchNode node = (SketchNode) symbolsView.getModel().get(symbolsView.getSelectedIndex());
                            SketchDocument sd = doc;//context.getDocument();
                            dupe = node.duplicate(null);
                            Bounds b = dupe.getInputBounds();
                            //sd.getCurrentPage().add(dupe);
                            canvas.getEditRoot().add(dupe);
                            //sd.get(0).get(0).add(dupe);
                            //Point2D pt = event.getPointInNodeCoords(context.getCanvas());
                            //pt = context.getSketchCanvas().transformToCanvas(pt);
                            dupe.setTranslateX(pt.getX() - b.getWidth() / 2);
                            dupe.setTranslateY(pt.getY() - b.getHeight() / 2);
                            canvas.redraw();
                        }
                        prevx = event.getX();
                    }
                    if(event.getType() == MouseEvent.MouseReleased) {
                        if(created) {
                            canvas.clearSelection();
                            canvas.addToSelection(dupe);
                            dupe = null;
                            created = false;
                            prevx = 0;
                        }
                        canvas.redraw();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TreeNode<Mode> initModes() {
        TreeNode<Mode> modes = new TreeNode<Mode>();
        Mode aminojs = new Mode();
        aminojs.setId("com.joshondesign.modes.aminojs");
        modes.add(aminojs);

        TreeNode<JAction> actions = new TreeNode<JAction>();
        actions.add(new JAction() {
            @Override
            public void execute() {
            }
            @Override
            public String getShortName() {
                return "Save XML";
            }
        });
        aminojs.add(actions);


        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        Rect rect = new Rect();
        rect.setId("Rect");
        symbols.add(rect);

        PushButton button = new PushButton();
        button.setId("PushButton");
        symbols.add(button);

        ToggleButton togg = new ToggleButton();
        togg.setId("toggle");
        symbols.add(togg);

        CheckButton checkButton = new CheckButton();
        checkButton.setId("check button");
        symbols.add(checkButton);

        com.joshondesign.treegui.modes.amino.Slider slider = new Slider();
        slider.setId("Slider");
        symbols.add(slider);

        Image image = new Image();
        image.setId("image");
        symbols.add(image);

        Textbox tb = new Textbox();
        tb.setId("textbox");
        symbols.add(tb);

        Label label = new Label();
        label.setId("label");
        symbols.add(label);


        //complex controls

        com.joshondesign.treegui.modes.amino.ListView lv = new com.joshondesign.treegui.modes.amino.ListView();
        lv.setId("ListView");
        symbols.add(lv);

        // panels

        PlainPanel plainPanel = new PlainPanel();
        plainPanel.setId("plain panel");
        symbols.add(plainPanel);

        TabPanel tabPanel = new TabPanel();
        tabPanel.setId("tab panel");
        symbols.add(tabPanel);

        StringListModel stringList = new StringListModel();
        stringList.setId("StringList");
        symbols.add(stringList);

        ControlListModel controlList = new ControlListModel();
        controlList.setId("ControList");
        symbols.add(controlList);

        Spinner spinner = new Spinner();
        spinner.setId("Spinner");
        symbols.add(spinner);

        FlickrQuery fq = new FlickrQuery();
        fq.setId("FlickrQuery");
        symbols.add(fq);


        aminojs.add(symbols);

        modes.add(setupJavaMode());



        return modes;
    }

    public static Map<String,DynamicNode.DrawDelegate> drawMap = new HashMap<String, DynamicNode.DrawDelegate>();

    private Mode setupJavaMode() {

        Mode aminojava = new Mode();
        aminojava.setId("com.joshondesign.modes.aminojava");
        TreeNode<JAction> javaactions = new TreeNode<JAction>();
        aminojava.add(javaactions);

        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        aminojava.add(symbols);

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
                .setVisible(false));

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
                .addProperty(new Property("renderer", String.class, "none"))
                .addProperty(new Property("data", List.class, null).setBindable(true))
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
            .addProperty(new Property("class", String.class,"org.joshy.gfx.node.control.Textbox"))
            .addProperty(new Property("id", String.class, "foo2"))
            .addProperty(new Property("text", CharSequence.class, "a textfield").setBindable(true))
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
        serviceBase.addProperty(new Property("translateX", Double.class, 0))
                .addProperty(new Property("translateY", Double.class, 0))
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
                .addProperty(new Property("class", String.class,"com.joshondesign.flickr.FlickrQuery"))
                .addProperty(new Property("execute", ActionProp.class, null).setBindable(true))
                .addProperty(new Property("querystring", String.class, "london").setBindable(true))
                .addProperty(new Property("results", List.class, null).setBindable(true))
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
                .addProperty(new Property("data",List.class, dummyStringListData)
                        .setVisible(true).setBindable(true))
                .addProperty(new Property("this",DynamicNode.class, null)
                        .setVisible(false).setBindable(true))
                ;
        stringList.setDrawDelegate(drawMap.get("servicebase"));
        symbols.add(stringList);


        return aminojava;
    }

    private SketchDocument initDoc() {


        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        layer.add(new Rect().setFill(FlatColor.GREEN).setWidth(50).setHeight(50));
        Page page = new Page();
        page.add(layer);
        doc.add(page);


        return doc;
    }

    private Node find(String name, Control rootControl) {
        if(name.equals(rootControl.getId())) return rootControl;
        if(rootControl instanceof Parent) {
            Parent parent = (Parent) rootControl;
            for(Node node : parent.children()) {
                if(node instanceof Control) {
                    Node nd = find(name, (Control) node);
                    if(nd != null) return nd;
                }
            }
        }
        return null;
    }

}
