package com.joshondesign.treegui;

import com.joshondesign.treegui.model.TreeNodeListView;
import com.joshondesign.xml.Elem;
import java.util.HashMap;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.TabPanel;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/4/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ControlLoader {
    private static HashMap<String, FlatColor> colorMap;
    static {
        colorMap = new HashMap<String,FlatColor>();
        colorMap.put("red",FlatColor.RED);
        colorMap.put("green",FlatColor.GREEN);
        colorMap.put("blue",FlatColor.BLUE);
    }

    public Control processControls(Elem root) throws Exception, ClassNotFoundException {
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
        if(root.hasAttr("layout")) {
            if(root.attrEquals("layout","horizontal-wrap")) {
                view.setOrientation(ListView.Orientation.HorizontalWrap);
            }
        }
        if(root.hasAttr("cellwidth")) {
            view.setColumnWidth(Double.parseDouble(root.attr("cellwidth")));
        }
        if(root.hasAttr("cellheight")) {
            view.setRowHeight(Double.parseDouble(root.attr("cellheight")));
        }
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
