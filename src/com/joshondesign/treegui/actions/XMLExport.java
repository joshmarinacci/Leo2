package com.joshondesign.treegui.actions;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import com.joshondesign.xml.XMLWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import org.joshy.gfx.util.u;

public class XMLExport {

    public static void exportToXML(PrintWriter printWriter, Page page, SketchDocument document) throws URISyntaxException {
        XMLWriter xml = new XMLWriter(printWriter, new URI(""));
        xml.header();
        xml.start("page");
        xml.attr("mode",document.getModeId());
        // render non-visual nodes first

        xml.start("nodes");
        //render visual nodes next
        for(Layer layer : page.children()) {
            for(SketchNode node : layer.children()) {
                if(node instanceof DynamicNode) {
                    if(node.isVisual()) {
                        visualNodeToXML(xml, (DynamicNode) node, 200, 200, false);
                    } else {
                        nonvisualNodeToXML(node, xml);
                    }
                }
            }
        }
        xml.end();

        //bindings
        xml.start("bindings");
        for(Binding binding : document.getBindings()) {
            u.p("exporting: " + binding.toString());
            xml.start("binding");
            xml.attr("sourceid", binding.getSource().getId());
            Property src = binding.getSourceProperty();
            xml.attr("sourceprop", src.getName());
            xml.attr("sourcevirtual",Boolean.toString(src.isProxy()));
            if(src.isProxy()) {
                xml.attr("sourcemaster",src.getMasterProperty());
            }
            xml.attr("targetid",binding.getTarget().getId());
            xml.attr("targetprop",binding.getTargetProperty().getName());
            DynamicNode nd = binding.getSource();
            if(nd.isMirror()) {
                xml.attr("mirror","true");
            }
            xml.end();
        }
        xml.end();

        xml.end();
        xml.close();
    }

    private static void nonvisualNodeToXML(SketchNode node, XMLWriter xml) {
        if(!node.isVisual() && node instanceof DynamicNode) {
            DynamicNode nd = (DynamicNode) node;
            u.p("spitting out " + nd);
            xml.start("node");
            if(nd.hasProperty("class")) {
                xml.attr("class",nd.getProperty("class").encode());
            } else {
                xml.attr("class","unknown");
            }
            xml
                    .attr("name", nd.getName())
                    .attr("id", nd.getId())
                    .attr("mirror",Boolean.toString(nd.isMirror()))
            ;
            for (Property prop : nd.getSortedProperties()) {
                xml.start("property");
                xml.attr("name", prop.getName());
                xml.attr("value", prop.encode());
                xml.attr("type", prop.getType().getName());
                xml.attr("exported",Boolean.toString(prop.isExported()));
                xml.attr("visible", Boolean.toString(prop.isVisible()));
                xml.attr("bindable", Boolean.toString(prop.isBindable()));
                xml.attr("compound",Boolean.toString(prop.isCompound()));
                xml.attr("list",Boolean.toString(prop.isList()));
                xml.attr("masterprop",""+prop.getMasterProperty());
                if(prop.getExportName() != null) {
                    xml.attr("exportname", prop.getExportName());
                }
                xml.end();
            }
            xml.end();
        }
        for(SketchNode child : node.children()) {
            nonvisualNodeToXML(child, xml);
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
            xml.attr("name", prop.getName());
            if(prop.getExportName() != null) {
                xml.attr("exportname", prop.getExportName());
            }
            if(prop.getType().isEnum()) {
                xml.attr("enum", "true");
            }
            xml.attr("value", prop.encode())
                    .attr("type", prop.getType().getName());
            xml.attr("exported",Boolean.toString(prop.isExported()));
            xml.attr("visible", Boolean.toString(prop.isVisible()));
            xml.attr("bindable", Boolean.toString(prop.isBindable()));
            xml.attr("compound",Boolean.toString(prop.isCompound()));
            xml.attr("list",Boolean.toString(prop.isList()));
            xml.attr("masterprop",""+prop.getMasterProperty());
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
                    nonvisualNodeToXML(nd2, xml);
                }
            }
        }
        xml.end();
        xml.end();
    }

}
