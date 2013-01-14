package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.AnchorPanel;
import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import com.joshondesign.xml.XMLWriter;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/11/13
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class AminoJavaXMLExport extends JAction {
    private final Canvas canvas;
    private final Page page;
    private Stage demoStage;

    public AminoJavaXMLExport(Canvas canvas, Page page) {
        super();
        this.canvas = canvas;
        this.page = page;
    }

    @Override
    public void execute() {
        try {
            File file = File.createTempFile("foo",".xml");
            //PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            PrintWriter pw = new PrintWriter(System.out);
            exportToXML(pw, page, canvas);
            loadAndRun(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAndRun(File file) throws Exception {
        Doc xml = XMLParser.parse(file);
        Node node = parsePage(xml.root());
        if(demoStage == null) {
            demoStage = Stage.createStage();
            demoStage.setWidth(600);
            demoStage.setHeight(400);
            demoStage.setAlwaysOnTop(true);
        }
        node.setTranslateX(0);
        node.setTranslateY(0);
        demoStage.setContent(node);
        demoStage.raiseToTop();
    }

    private static Node parsePage(Elem root) throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>();

        Node last = null;
        for(Elem vis : root.xpath("nodes/node")) {
            Class clazz  = Class.forName(vis.attr("class"));
            Object obj = clazz.newInstance();
            objectMap.put(vis.attr("id"),obj);
            if(obj instanceof Node && vis.attrEquals("visual","true")) {
                last = (Node) obj;
                processNodeChildren(last,vis,objectMap);
            }
            u.p("last node is set to: " + last);
        }

        for(Elem binding : root.xpath("bindings/binding")) {
            Object src = objectMap.get(binding.attr("sourceid"));
            u.p("processing binding for " + src + " " + binding.attr("sourceprop"));
            final Object tgt = objectMap.get(binding.attr("targetid"));
            final String tgtProp = binding.attr("targetprop");

            if(binding.attrEquals("sourcetype", GuiTest.TriggerType.class.getName())) {
                u.p("doing a trigger binding");
                EventBus.getSystem().addListener(src, ActionEvent.Action, new Callback<Event>() {
                    public void call(Event event) throws Exception {
                        u.p("binding action callback called");
                        tgt.getClass().getMethod(tgtProp).invoke(tgt);
                    }
                });
            }
            if(binding.attrEquals("sourcetype","java.lang.String")) {
                setWithSetter(
                        src,binding.attr("sourceprop"),
                        tgt, tgtProp, binding.attr("targettype"));
            }
            if(binding.attrEquals("sourcetype", ListModel.class.getName())) {
                setWithSetter(
                        src,binding.attr("sourceprop"),
                        tgt, tgtProp, binding.attr("targettype"));
            }
        }

        return last;
    }

    private static void setWithSetter(Object src, String sourceprop, Object tgt, String tgtProp, String tgttype) throws Exception {
        String getterName = "get"+sourceprop.substring(0,1).toUpperCase()+sourceprop.substring(1);
        String setterName = "set"+tgtProp.substring(0,1).toUpperCase()+tgtProp.substring(1);
        u.p("using getter " + getterName + " on " + src);
        Method getter = src.getClass().getMethod(getterName);
        Object value = getter.invoke(src);
        u.p("using setter " + setterName + " oin " + tgt);
        Method setter = tgt.getClass().getMethod(setterName, Class.forName(tgttype));
        setter.invoke(tgt,value);
    }

    private static void processNodeChildren(Node root, Elem elem, Map<String, Object> objectMap) throws XPathExpressionException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        for(Elem vis : elem.xpath("children/node")) {
            Class clazz  = Class.forName(vis.attr("class"));
            Object obj = clazz.newInstance();
            objectMap.put(vis.attr("id"),obj);

            initObject(obj,vis);

            if(obj instanceof Node) {
                Node child = (Node) obj;

                if(root instanceof Container) {
                    Container container = (Container) root;
                    if(container instanceof AnchorPanel && child instanceof Control) {
                        AnchorPanel anchorPanel = (AnchorPanel) container;
                        Control control = (Control) child;
                        AnchorPanel.AnchorSettings anchor = parseAnchor(vis);
                        //anchorPanel.DEBUG = true;
                        anchorPanel.add(control, anchor);
                    }else {
                        container.add(child);
                    }

                }

                processNodeChildren(child, vis, objectMap);
            }
        }
    }

    private static void initObject(Object node, Elem xml) throws XPathExpressionException {
        List<String> skipList = new ArrayList<String>();
        skipList.add("anchorLeft");
        skipList.add("anchorRight");
        skipList.add("anchorTop");
        skipList.add("anchorBottom");
        skipList.add("right");
        skipList.add("bottom");

        Class clazz = node.getClass();
        for(Elem eprop : xml.xpath("property")) {
            if(skipList.contains(eprop.attr("name"))) continue;
            if(eprop.attrEquals("exported", Boolean.FALSE.toString())) continue;
            if(eprop.attrEquals("name", "class")) continue;
            String setter = "set" + eprop.attr("name").substring(0,1).toUpperCase()
                    + eprop.attr("name").substring(1);


            String value = eprop.attr("value");
            u.p(" setting " + eprop.attr("name") + " with " + setter + " to " + value);
            try {
                if(eprop.attrEquals("type","java.lang.String")) {
                    Method method = clazz.getMethod(setter, Class.forName(eprop.attr("type")));
                    method.invoke(node, eprop.attr("value"));
                }
                if(eprop.attrEquals("type","java.lang.CharSequence")) {
                    Method method = clazz.getMethod(setter, Class.forName(eprop.attr("type")));
                    method.invoke(node, eprop.attr("value"));
                }
                if(eprop.attrEquals("type","java.lang.Double")) {
                    Method method = findSetter(clazz, setter, eprop);
                    method.invoke(node, Double.parseDouble(value));
                }
                if(eprop.attrEquals("type","org.joshy.gfx.draw.FlatColor")) {
                    Method method = clazz.getMethod(setter, Class.forName(eprop.attr("type")));
                    method.invoke(node, new FlatColor(eprop.attr("value")));
                }
                if(eprop.attrEquals("enum","true")) {
                    Class clazz2 = clazz.forName(eprop.attr("type"));
                    Method method = clazz.getMethod(setter, clazz2);
                    method.invoke(node, Enum.valueOf(clazz2, eprop.attr("value")));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private static AnchorPanel.AnchorSettings parseAnchor(Elem echild) throws XPathExpressionException {

        double left = 0;
        boolean leftSet = false;
        double right = 0;
        boolean rightSet = false;
        double top = 0;
        boolean topSet = false;
        double bottom = 0;
        boolean bottomSet = false;

        double translateX = 0;
        double translateY = 0;
        for(Elem eprop : echild.xpath("property")) {
            if(eprop.attrEquals("name","translateX")) {
                translateX = Double.parseDouble(eprop.attr("value"));
            }
            if(eprop.attrEquals("name","translateY")) {
                translateY = Double.parseDouble(eprop.attr("value"));
            }
            if(eprop.attrEquals("name","anchorLeft")) {
                leftSet = eprop.attrEquals("value", "true");
            }
            if(eprop.attrEquals("name","anchorRight")) {
                rightSet = eprop.attrEquals("value","true");
            }
            if(eprop.attrEquals("name","right")) {
                right = Double.parseDouble(eprop.attr("value"));
            }
            if(eprop.attrEquals("name","anchorTop")) {
                topSet = eprop.attrEquals("value", "true");
            }
            if(eprop.attrEquals("name","anchorBottom")) {
                bottomSet = eprop.attrEquals("value","true");
            }
            if(eprop.attrEquals("name","bottom")) {
                bottom = Double.parseDouble(eprop.attr("value"));
            }
        }
        if(leftSet) {
            left = translateX;
        }
        if(topSet) {
            top = translateY;
        }
        return new AnchorPanel.AnchorSettings(left, leftSet, right, rightSet, top, topSet, bottom, bottomSet);
    }

    private static Method findSetter(Class clazz, String setter, Elem eprop) throws ClassNotFoundException, NoSuchMethodException {
        try {
            Method method = clazz.getMethod(setter, Class.forName(eprop.attr("type")));
            return method;
        } catch (NoSuchMethodException e) {
            Method method = clazz.getMethod(setter, Double.TYPE);
            return method;
        }
    }

    @Override
    public String getShortName() {
        return "Run";
    }

    public static void exportToXML(PrintWriter printWriter, Page page, Canvas canvas) throws URISyntaxException {
        XMLWriter xml = new XMLWriter(printWriter, new URI(""));
        xml.header();
        xml.start("page");
        // render non-visual nodes first

        xml.start("nodes");
        //render visual nodes next
        for(Layer layer : page.children()) {
            for(SketchNode node : layer.children()) {
                if(node instanceof DynamicNode) {
                    if(node.isVisual()) {
                        visualNodeToXML(xml, (DynamicNode) node, 200, 200, false);
                    } else {
                        exportNonVisualNode(node, xml);
                    }
                }
            }
        }
        xml.end();

        //bindings
        xml.start("bindings");
        for(Binding binding : canvas.getBindings()) {
            xml.start("binding");
            xml.attr("sourceid", binding.getSource().getId());
            xml.attr("sourceprop", binding.getSourceProperty());
            xml.attr("sourcetype",binding.getSourceType().getName());
            xml.attr("targetid",binding.getTarget().getId());
            xml.attr("targetprop",binding.getTargetProperty());
            xml.attr("targettype",binding.getTargetType().getName());
            xml.end();
        }
        xml.end();

        xml.end();
        xml.close();
    }

    private static void exportNonVisualNode(SketchNode node, XMLWriter xml) {
        if(!node.isVisual() && node instanceof DynamicNode) {
            DynamicNode nd = (DynamicNode) node;
            u.p("spitting out " + nd);
            xml.start("node")
                    .attr("class", nd.getProperty("class").encode())
                    .attr("name", nd.getName())
                    .attr("id", nd.getId())
            ;
            for (Property prop : nd.getSortedProperties()) {
                xml.start("property");
                xml.attr("name", prop.getName());
                xml.attr("value", prop.encode());
                xml.attr("type", prop.getType().getName());
                xml.attr("exported",Boolean.toString(prop.isExported()));
                xml.end();
            }
            xml.end();
        }
        for(SketchNode child : node.children()) {
            exportNonVisualNode(child,xml);
        }
    }

    private static void visualNodeToXML(XMLWriter xml, DynamicNode node, double width, double height, boolean parentAnchor) {
        xml.start("node")
                .attr("id", node.getId())
                .attr("class", node.getProperty("class").encode())
                .attr("visual", Boolean.toString(node.isVisual()))
                .attr("resizable", Boolean.toString(node.isResizable()))
                .attr("container", Boolean.toString(node.isContainer()))
                .attr("custom", Boolean.toString(node.isCustom()))
                .attr("name", node.getName())
        ;
        if(node.isCustom()) {
            xml.attr("customClass",node.getProperty("customClass").getStringValue());
        }
        for (Property prop : node.getSortedProperties()) {

            if(prop.getName().equals("customClass")) continue;
            xml.start("property");
            if(prop.getExportName() != null) {
                xml.attr("name", prop.getExportName());
            }else {
                xml.attr("name", prop.getName());
            }
            if(prop.getType().isEnum()) {
                u.p("name = " + prop.getType().getName());
                xml.attr("enum", "true");
            }
            xml.attr("value", prop.encode())
                .attr("type", prop.getType().getName());
            xml.attr("exported",Boolean.toString(prop.isExported()));
            xml.end();
        }

        if(parentAnchor) {
            double w = node.getProperty("width").getDoubleValue();
            double h = node.getProperty("height").getDoubleValue();
            double tx = node.getTranslateX();
            double ty = node.getTranslateY();
            xml.start("property","name","right")
                    .attr("type","java.lang.Double")
                    .attr("value",""+(width-tx - w))
                .end()
            ;
            xml.start("property","name","bottom")
                    .attr("type","java.lang.Double")
                    .attr("value",""+(height-ty-h))
                    .end();
        }

        xml.start("children");
        if(node.getSize() > 0) {
            for(SketchNode nd : node.children()) {
                DynamicNode nd2 = (DynamicNode) nd;
                if(nd2.isVisual()) {
                    visualNodeToXML(xml, nd2,
                            node.getProperty("width").getDoubleValue(),
                            node.getProperty("height").getDoubleValue(), true);
                } else {
                    exportNonVisualNode(nd2,xml);
                }
            }
        }
        xml.end();
        xml.end();
    }

    public static class Save extends JAction {

        private final SketchDocument doc;
        private final Canvas canvas;

        public Save(Canvas canvas, SketchDocument doc) {
            this.doc = doc;
            this.canvas = canvas;
        }

        @Override
        public void execute() {
            File file = new File("foo.xml");
            try {
                exportToXML(new PrintWriter(new FileOutputStream(file)), doc.get(0), canvas);
                u.p("exported to : " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getShortName() {
            return "Save";
        }
    }

    public static class Test extends JAction {
        private final Canvas canvas;

        public Test(Canvas canvas) {
            this.canvas = canvas;
        }

        @Override
        public void execute() {
            try {
                File file = new File("foo.xml");
                Doc xml = XMLParser.parse(file);
                Node node = parsePage(xml.root());
                Stage demoStage = null;
                if(demoStage == null) {
                    demoStage = Stage.createStage();
                    demoStage.setWidth(600);
                    demoStage.setHeight(400);
                    demoStage.setAlwaysOnTop(true);
                }
                node.setTranslateX(0);
                node.setTranslateY(0);
                demoStage.setContent(node);
                demoStage.raiseToTop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public String getShortName() {
            return "Test";
        }
    }
}
