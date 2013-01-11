package com.joshondesign.treegui.modes.aminojava;

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
import javax.xml.xpath.XPathExpressionException;
import org.joshy.gfx.node.Node;
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
        stage.setWidth(600);
        stage.setHeight(400);
    }

    private Node parse(Elem xml) throws ClassNotFoundException, IllegalAccessException, InstantiationException, XPathExpressionException, NoSuchMethodException, InvocationTargetException {
        Class clazz = getClass().forName(xml.attr("class"));
        Node node = (Node) clazz.newInstance();
        for(Elem eprop : xml.xpath("property")) {
            if(eprop.attrEquals("name","class")) continue;
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

        }
        for(Elem echild : xml.xpath("children/node")) {
            Node nchild = parse(echild);
            Container container = (Container) node;
            container.add(nchild);
        }
        return (Node) node;
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
        exportNode(xml,root);
        xml.close();
    }

    private static void exportNode(XMLWriter xml, DynamicNode node) {
        xml.start("node")
                .attr("class", node.getProperty("class").encode())
                .attr("visual", Boolean.toString(node.isVisual()))
                .attr("resizable", Boolean.toString(node.isResizable()))
                .attr("container", Boolean.toString(node.isContainer()))
        ;
        for (Property prop : node.getSortedProperties()) {
            if (!prop.isExported()) continue;
            xml.start("property")
                    .attr("name", prop.getName())
                    .attr("value", prop.encode())
                    .attr("type", prop.getType().getCanonicalName())
                    .end()
            ;
        }
        xml.start("children");
        if(node.getSize() > 0) {
            for(SketchNode nd : node.children()) {
                DynamicNode nd2 = (DynamicNode) nd;
                exportNode(xml,nd2);
            }
        }
        xml.end();
        xml.end();
    }

}
