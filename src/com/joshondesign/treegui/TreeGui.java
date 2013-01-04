package com.joshondesign.treegui;

import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.*;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.model.TreeNodeListView;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import java.util.HashMap;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.TabPanel;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

public class TreeGui implements Runnable {
    private static HashMap<String, FlatColor> colorMap;

    public static void main(String ... args) throws Exception {
        colorMap = new HashMap<String,FlatColor>();
        colorMap.put("red",FlatColor.RED);
        colorMap.put("green",FlatColor.GREEN);
        colorMap.put("blue",FlatColor.BLUE);

        Core.init();
        Core.getShared().defer(new TreeGui());
    }

    public void run() {
        final TreeNode<Mode> modes = initModes();
        Mode mode = modes.get(0);
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
            rootControl = processControls(root);
            stage.setContent(rootControl);

            PropsView propsView = (PropsView) find("propsview", rootControl);

            final Canvas canvas = (Canvas) find("canvas",rootControl);
            canvas.setTarget(doc.get(0).get(0));
            canvas.setPropsView(propsView);


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


            TreeNode<JAction> actions = (TreeNode<JAction>)mode.get(0);
            HTMLBindingExport exp = new HTMLBindingExport();
            exp.canvas = canvas;
            exp.page = doc.get(0);
            actions.add(exp);

            ToolbarListView toolbar = (ToolbarListView) find("toolbar", rootControl);
            toolbar.setModel(actions);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TreeNode<Mode> initModes() {
        TreeNode<Mode> modes = new TreeNode<Mode>();
        Mode amino = new Mode();
        amino.setId("com.joshondesign.modes.aminojs");
        modes.add(amino);

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
        amino.add(actions);



        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        symbols.add(new ResizableRectNode() {
            @Override
            public String getId() {
                return "Rect";
            }

            @Override
            public void draw(GFX g) {
            }
        });
        symbols.add(new ResizableRectNode() {
            @Override
            public String getId() {
                return "Label";
            }

            @Override
            public void draw(GFX g) {
            }
        });
        symbols.add(new ResizableRectNode() {
            @Override
            public String getId() {
                return "Button";
            }

            @Override
            public void draw(GFX g) {
            }
        });
        symbols.add(new ResizableRectNode() {
            @Override
            public String getId() {
                return "ListView";
            }

            @Override
            public void draw(GFX g) {
            }
        });
        symbols.add(new ResizableRectNode() {
            @Override
            public String getId() {
                return "TwitterSearch";
            }

            @Override
            public void draw(GFX g) {
            }
        });

        amino.add(symbols);

        return modes;
    }

    private SketchDocument initDoc() {
        class Rect extends ResizableRectNode {
            private FlatColor fill = FlatColor.GRAY;
            @Override
            public void draw(GFX g) {
                g.setPaint(this.fill);
                g.fillRect(0,0,getWidth(),getHeight());
            }
            public Rect setFill(FlatColor fill) {
                this.fill = fill;
                return this;
            }

            public boolean isDraggable() {
                return draggable;
            }

            public void setDraggable(boolean draggable) {
                this.draggable = draggable;
            }

            private boolean draggable = false;
        }

        class Slider extends ResizableRectNode {
            @Override
            public void draw(GFX g) {
                g.setPaint(FlatColor.GRAY);
                g.fillRect(0,0,getWidth(),getHeight());
                g.setPaint(FlatColor.BLACK);
                g.fillRect(0,0,getHeight(),getHeight());
            }
        }

        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        layer.add(new Rect().setFill(FlatColor.GREEN).setWidth(50).setHeight(50));
        layer.add(new Slider().setWidth(100).setHeight(30).setTranslateX(100).setTranslateY(100));

        Group group = new Group();
        group.add(new Rect().setFill(FlatColor.PURPLE).setWidth(20).setHeight(20).setTranslateX(50));
        group.add(new Rect().setFill(FlatColor.YELLOW).setWidth(20).setHeight(20));
        group.setTranslateX(300).setTranslateY(200);
        layer.add(group);

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

    private Control processControls(Elem root) throws Exception, ClassNotFoundException {
        if(root.name().equals("panel")) {
            if(root.attrEquals("layout","anchor")) {
                return processAnchorPanel(root);
            }
            return processPanel(root);
        }
        if(root.name().equals("list")) {
            return processList(root);
        }
        if(root.name().equals("tabs")) {
            return processTabbedPanel(root);
        }
        if(root.name().equals("scroll")) {
            return processScroll(root);
        }
        if(root.name().equals("button")) {
            return processButton(root);
        }
        if(root.name().equals("view")) {
            return processView(root);
        }

        return null;
    }

    private Control processList(Elem root) {
        TreeNodeListView view = new TreeNodeListView();
        stdAtts(root, view);
        return view;
    }

    private Control processAnchorPanel(Elem root) throws Exception {
        AnchorPanel panel = new AnchorPanel();
        stdAtts(root,panel);
        panel.setFill(FlatColor.GREEN);
        if(root.hasAttr("fill")) {
            panel.setFill(colorMap.get(root.attr("fill")));
        }
        for(Elem child : root.xpath("*")) {
            Control childControl = processControls(child);
            if(childControl != null) {
                panel.add(childControl, new AnchorPanel.AnchorSettings(child));
            }
        }
        return panel;
    }

    private Control processView(Elem root) throws Exception {
        Control control;
        if(!root.hasAttr("classname")) {
            u.p("view is missing a classname");
        }
        try {
            Class clazz = Class.forName(root.attr("classname"));
            control = (Control) clazz.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            control = new Panel();
        }
        stdAtts(root,control);

        return control;
    }

    private Control processScroll(Elem root) throws Exception {
        ScrollPane panel = new ScrollPane();
        stdAtts(root,panel);
        for(Elem child : root.xpath("*")) {
            Control childControl = processControls(child);
            if(childControl != null) {
                panel.setContent(childControl);
            }
        }
        return panel;
    }

    private void stdAtts(Elem root, Control panel) {
        if(root.hasAttr("x")) {
            panel.setTranslateX(Double.parseDouble(root.attr("x")));
        }
        if(root.hasAttr("y")) {
            panel.setTranslateY(Double.parseDouble(root.attr("y")));
        }
        if(root.hasAttr("width")) {
            panel.setPrefWidth(Double.parseDouble(root.attr("width")));
        }
        if(root.hasAttr("height")) {
            panel.setPrefHeight(Double.parseDouble(root.attr("height")));
        }
        if(root.hasAttr("id")) {
            panel.setId(root.attr("id"));
        }
    }

    private Control processButton(Elem root) {
        Button button = new Button();
        button.setText(root.attr("title"));
        stdAtts(root,button);
        return button;
    }

    private Control processTabbedPanel(Elem root) throws Exception {
        TabPanel panel = new TabPanel();
        if(root.hasAttr("x")) {
            panel.setTranslateX(Double.parseDouble(root.attr("x")));
        }
        if(root.hasAttr("y")) {
            panel.setTranslateY(Double.parseDouble(root.attr("y")));
        }
        if(root.hasAttr("width")) {
            panel.setPrefWidth(Double.parseDouble(root.attr("width")));
        }
        if(root.hasAttr("height")) {
            panel.setPrefHeight(Double.parseDouble(root.attr("height")));
        }
        if(root.hasAttr("id")) {
            panel.setId(root.attr("id"));
        }
        panel.setFill(FlatColor.YELLOW);
        if(root.hasAttr("fill")) {
            panel.setFill(colorMap.get(root.attr("fill")));
        }
        for(Elem child : root.xpath("*")) {
            Control childControl = processControls(child);
            if(childControl != null) {
                panel.add(child.xpathString("title/text()"),childControl);
            }
        }
        return panel;
    }

    private Control processPanel(Elem root) throws Exception {
        Panel panel = new Panel();
        stdAtts(root,panel);
        panel.setFill(FlatColor.GREEN);
        if(root.hasAttr("fill")) {
            panel.setFill(colorMap.get(root.attr("fill")));
        }
        processChildren(root,panel);
        if(root.attrEquals("scroll","true")) {
            return new ScrollPane(panel);
        }
        return panel;
    }

    private void processChildren(Elem root, Panel panel) throws Exception {
        for(Elem child : root.xpath("*")) {
            Control childControl = processControls(child);
            if(childControl != null) {
                panel.add(childControl);
            }
        }
    }
}
