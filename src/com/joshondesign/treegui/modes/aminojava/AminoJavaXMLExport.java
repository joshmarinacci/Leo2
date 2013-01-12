package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.AnchorPanel;
import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import com.joshondesign.xml.XMLWriter;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;
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

    public AminoJavaXMLExport(Canvas canvas, Page page) {
        super();
        this.canvas = canvas;
        this.page = page;
    }

    @Override
    public void execute() {
        for(SketchNode nd : page.get(0).children()) {
            if(nd instanceof DynamicNode) {
                try {
                    File file = File.createTempFile("foo",".xml");
                    exportToXML(new PrintWriter(new FileWriter(file)), (DynamicNode) nd);
                    u.p("wrote out to " + file);
                    u.p(u.fileToString(new FileInputStream(file)));

                    loadAndRun(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadAndRun(File file) throws Exception {
        Doc xml = XMLParser.parse(file);
        Node node = parse(xml.root());
        Stage stage = Stage.createStage();
        stage.setContent(node);
        node.setTranslateX(0);
        node.setTranslateY(0);
        stage.setWidth(600);
        stage.setHeight(400);
    }

    private Node parse(Elem xml) throws ClassNotFoundException, IllegalAccessException, InstantiationException, XPathExpressionException, NoSuchMethodException, InvocationTargetException {
        Class clazz = getClass().forName(xml.attr("class"));
        Node node = (Node) clazz.newInstance();

        List<String> skipList = new ArrayList<String>();
        skipList.add("anchorLeft");
        skipList.add("anchorRight");
        skipList.add("anchorTop");
        skipList.add("anchorBottom");
        skipList.add("right");
        skipList.add("bottom");

        for(Elem eprop : xml.xpath("property")) {
            if(skipList.contains(eprop.attr("name"))) continue;
            if(eprop.attrEquals("name", "class")) continue;
            String setter = "set" + eprop.attr("name").substring(0,1).toUpperCase()
                    + eprop.attr("name").substring(1);

            String value = eprop.attr("value");
            u.p(" setting " + eprop.attr("name") + " with " + setter + " to " + value);
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

        }
        for(Elem echild : xml.xpath("children/node")) {
            Node nchild = parse(echild);
            Container container = (Container) node;
            if(container instanceof AnchorPanel && nchild instanceof Control) {
                AnchorPanel anchorPanel = (AnchorPanel) container;
                Control control = (Control) nchild;
                AnchorPanel.AnchorSettings anchor = parseAnchor(echild);
                u.p("anchor = " + anchor);
                anchorPanel.DEBUG = true;
                anchorPanel.add(control, anchor);
            }else {
                container.add(nchild);
            }
        }
        return (Node) node;
    }

    private AnchorPanel.AnchorSettings parseAnchor(Elem echild) throws XPathExpressionException {

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

    private Method findSetter(Class clazz, String setter, Elem eprop) throws ClassNotFoundException, NoSuchMethodException {
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
        return "Export XML";
    }

    public static void exportToXML(PrintWriter printWriter, DynamicNode root) throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException {
        XMLWriter xml = new XMLWriter(printWriter, new URI(""));
        xml.header();
        exportNode(xml,root, 200,200, false);
        xml.close();
    }

    private static void exportNode(XMLWriter xml, DynamicNode node, double width, double height, boolean parentAnchor) {
        u.p("exporting. parent size = " + width + " " + height);
        xml.start("node")
                .attr("class", node.getProperty("class").encode())
                .attr("visual", Boolean.toString(node.isVisual()))
                .attr("resizable", Boolean.toString(node.isResizable()))
                .attr("container", Boolean.toString(node.isContainer()))
        ;
        for (Property prop : node.getSortedProperties()) {
            if (!prop.isExported()) continue;
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
                .attr("type", prop.getType().getName())
                .end()
            ;
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
                exportNode(xml,nd2, node.getProperty("width").getDoubleValue(), node.getProperty("height").getDoubleValue(), true);
            }
        }
        xml.end();
        xml.end();
    }

}
