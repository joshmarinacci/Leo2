package com.joshondesign.treegui;

import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.model.TreeNodeListView;
import com.joshondesign.treegui.modes.amino.*;
import com.joshondesign.treegui.modes.aminojava.AminoJavaXMLExport;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.GuiTest;
import com.joshondesign.treegui.modes.aminojava.Property;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.awt.geom.Point2D;
import java.io.File;
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
            actions.add(exp);
            actions.add(new AminoJavaXMLExport(canvas,doc.get(0)));
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
                    gfx.drawText(node.getId(), Font.DEFAULT,5,h-4);
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

    private Mode setupJavaMode() {

        Mode aminojava = new Mode();
        aminojava.setId("com.joshondesign.modes.aminojava");
        TreeNode<JAction> javaactions = new TreeNode<JAction>();
        aminojava.add(javaactions);


        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        aminojava.add(symbols);

        //Rect rect = new Rect();
        //rect.setId("Rect");
        //symbols.add(rect);
        //new Button().setText();

        DynamicNode button = new DynamicNode();
        button.setName("Button");
        button.setResizable(true);
        button.setVisual(true);

        button.addProperty(new Property("class", String.class, "org.joshy.gfx.node.control.Button"));
        button.addProperty(new Property("id", String.class, "arandomid"));
        button.addProperty(new Property("translateX", Double.class, 0));
        button.addProperty(new Property("translateY", Double.class, 0));
        button.addProperty(new Property("width", Double.class, 80)
                .setExportName("prefWidth"));
        button.addProperty(new Property("height", Double.class, 30)
                .setExportName("prefHeight"));
        button.addProperty(new Property("text", CharSequence.class, "a button"));
        button.addProperty(new Property("selected", Boolean.class, false));
        button.addProperty(new Property("resize", String.class, "any").setExported(false).setVisible(false));
        button.addProperty(new Property("trigger", GuiTest.TriggerType.class, 0)
                .setExported(false).setVisible(false));
        button.addProperty(new Property("anchorLeft", Boolean.class, true));
        button.addProperty(new Property("anchorRight", Boolean.class, false));
        button.addProperty(new Property("anchorTop", Boolean.class, true));
        button.addProperty(new Property("anchorBottom", Boolean.class, false));
        button.setDrawDelegate(new DynamicNode.DrawDelegate() {
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
        symbols.add(button);

        DynamicNode label = new DynamicNode();
        label.setName("Label");
        label.setResizable(true);
        label.setVisual(true);
        label.addProperty(new Property("class", String.class, "org.joshy.gfx.node.control.Label"));
        label.addProperty(new Property("id", String.class, "arandomid").setExported(true));
        label.addProperty(new Property("translateX", Double.class, 0));
        label.addProperty(new Property("translateY", Double.class, 0));
        label.addProperty(new Property("width", Double.class, 60));
        label.addProperty(new Property("height", Double.class, 20));
        label.addProperty(new Property("resize", String.class, "width").setExported(false));
        label.addProperty(new Property("text", String.class, "a label"));
        label.addProperty(new Property("anchorLeft", Boolean.class, true));
        label.addProperty(new Property("anchorRight", Boolean.class, false));
        label.addProperty(new Property("anchorTop", Boolean.class, true));
        label.addProperty(new Property("anchorBottom", Boolean.class, false));
        label.setDrawDelegate(new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                String t = node.getProperty("text").getStringValue();
                g.setPaint(FlatColor.BLACK);
                g.drawText(t, Font.DEFAULT, 5, 15);
            }
        });

        symbols.add(label);

        DynamicNode panel = new DynamicNode();
        panel.setName("Panel");
        panel.setVisual(true);
        panel.setResizable(true);
        panel.addProperty(new Property("class", String.class, "com.joshondesign.treegui.AnchorPanel"))
            .addProperty(new Property("resize", String.class, "any").setExported(false))
            .addProperty(new Property("id", String.class, "arandomid"))
            .addProperty(new Property("translateX", Double.class, 0))
            .addProperty(new Property("translateY", Double.class, 0))
            .addProperty(new Property("width", Double.class, 50))
            .addProperty(new Property("height", Double.class, 50))
        ;
        panel.setContainer(true);
        panel.setDrawDelegate(new DynamicNode.DrawDelegate() {
            public void draw(GFX g, DynamicNode node) {
                double w = node.getProperty("width").getDoubleValue();
                double h = node.getProperty("height").getDoubleValue();
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0, 0, w, h);
                g.setPaint(FlatColor.BLACK);
                g.drawRect(0, 0, w, h);
            }
        });

        symbols.add(panel);
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
