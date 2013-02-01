package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.modes.aminojs.TriggerProp;
import com.joshondesign.xml.XMLWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

public class GuiTest {

    public static void main(String... args) throws Exception {
        DynamicNode button = new DynamicNode();
        button.setName("Button");
        button.setResize(Resize.Any);
        button.setVisual(true);

        button.addProperty(new Property("class", String.class, "org.joshy.gfx.node.control.Button"));
        button.addProperty(new Property("id", String.class, "arandomid").setExported(true));
        button.addProperty(new Property("translateX", Double.class, 0));
        button.addProperty(new Property("translateY", Double.class, 0));
        button.addProperty(new Property("width", Double.class, 100));
        button.addProperty(new Property("height", Double.class, 50));
        button.addProperty(new Property("text", String.class, "a button"));
        button.addProperty(new Property("selected", Boolean.class, false));
        button.addProperty(new Property("trigger", TriggerProp.class, 0).setExported(false));

        DynamicNode flickr = new DynamicNode();
        flickr.setName("FlickrQuery");
        flickr.setVisual(false);
        flickr.addProperty(new Property("id", String.class, "arandomid").setExported(true));
        flickr.addProperty(new Property("execute", ActionType.class, 0).setExported(false));
        flickr.addProperty(new Property("results", ListType.class, 0).setExported(false));
        flickr.addProperty(new Property("class", String.class, "com.joshondesign.flickrdemo.FlickrQuery"));

        exportToXML(new PrintWriter(System.out), button);
        exportToXML(new PrintWriter(System.out), flickr);
    }

    public static void exportToXML(PrintWriter printWriter, DynamicNode root) throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException {
        XMLWriter xml = new XMLWriter(printWriter, new URI(""));
        xml.header();
        exportNode(xml,root);
        xml.close();
    }

    private static void exportNode(XMLWriter xml, DynamicNode node) {
        xml.start("node")
                .attr("class", node.getClass().getCanonicalName())
                .attr("visual", Boolean.toString(node.isVisual()))
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
        xml.end();
    }
    public static class ActionType {}
    public static class ListType {}
}


