package com.joshondesign.treegui;

import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import java.util.HashMap;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ScrollPane;
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

            Button obj = new Button("foo");
            obj.setTranslateX(100);
            final Canvas canvas = (Canvas) find("canvas",rootControl);
            canvas.setTarget(obj);


            propsView.setPropFilter(new PropsView.PropFilter() {
                public boolean include(Object obj, String name) {
                    if("skinDirty".equals(name)) return false;
                    if("hovered".equals(name)) return false;
                    if("pressed".equals(name)) return false;
                    if("baseline".equals(name)) return false;
                    if("width".equals(name)) return false;
                    if("height".equals(name)) return false;
                    return true;
                }
            });
            propsView.setSelection(obj);
            propsView.onUpdate(new Callback<Void>() {
                public void call(Void aVoid) throws Exception {
                    canvas.setLayoutDirty();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Node find(String propsview, Control rootControl) {
        if(propsview.equals(rootControl.getId())) return rootControl;
        if(rootControl instanceof Parent) {
            Parent parent = (Parent) rootControl;
            for(Node node : parent.children()) {
                if(node instanceof Control) {
                    Node nd = find(propsview, (Control) node);
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
            return processPanel(root);
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
        button.setText("foo");
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
