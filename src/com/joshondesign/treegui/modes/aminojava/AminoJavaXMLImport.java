package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.TreeGui;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import javax.xml.xpath.XPathExpressionException;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/12/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class AminoJavaXMLImport extends JAction {
    private final Canvas canvas;
    private final SketchDocument doc;

    public AminoJavaXMLImport(Canvas canvas, SketchDocument doc) {
        super();
        this.canvas = canvas;
        this.doc = doc;
    }

    @Override
    public void execute() {
        File file = new File("foo.xml");
        try {
            Doc xml = XMLParser.parse(file);
            Page page = processPage(xml.root());
            doc.clear();
            doc.add(page);
            canvas.setMasterRoot(page.get(0));
            canvas.setEditRoot(page.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Page processPage(Elem root) throws XPathExpressionException, ClassNotFoundException {
        Page page = new Page();

        Layer layer = new Layer();
        page.add(layer);

        //non visual
        for(Elem nonvis : root.xpath("nonvisual/node")) {
            DynamicNode node = process(nonvis);
            layer.add(node);
        }
        //visual

        for(Elem vis : root.xpath("visual/node")) {
            DynamicNode node = process(vis);
            layer.add(node);
        }
        //bind them together
        return page;
    }

    private DynamicNode process(Elem xml) throws XPathExpressionException, ClassNotFoundException {
        DynamicNode node = new DynamicNode();
        node.setName(xml.attr("name"));
        node.addProperty(new Property("class", String.class, xml.attr("class")));
        node.setVisual(xml.attrEquals("visual", "true"));
        node.setContainer(xml.attrEquals("container", "true"));
        node.setResizable(xml.attrEquals("resizable", "true"));
        node.setCustom(xml.attrEquals("custom", "true"));
        for(Elem prop : xml.xpath("property")) {
            Object val = null;
            Class type = Class.forName(prop.attr("type"));
            String sval = prop.attr("value");
            if(type == Double.class) {
                val = (Double)Double.parseDouble(sval);
            }
            if(type == Boolean.class) {
                val = (Boolean)Boolean.parseBoolean(sval);
            }
            if(type == String.class) {
                val = (String)sval;
            }
            if(type == CharSequence.class) {
                val = (CharSequence)sval;
            }
            if(type == FlatColor.class) {
                val = new FlatColor(sval);
            }
            if(prop.attrEquals("enum","true")) {
                val = Enum.valueOf(type, sval);
            }
            String name = prop.attr("name");
            if(name.equals("prefWidth")) {
                name = "width";
            }
            if(name.equals("prefHeight")) {
                name = "height";
            }
            Property property = new Property(name, type, val);
            node.addProperty(property);
        }
        u.p("restored node " + node.getName());
        for(Property p : node.getSortedProperties()) {
            u.p("   " + p.getName() + " " + p.getType());
        }


        node.setDrawDelegate(TreeGui.drawMap.get(node.getName()));
        for(Elem echild : xml.xpath("children/node")) {
            node.add(process(echild));
        }
        return node;
    }

    @Override
    public String getShortName() {
        return "Open";
    }
}
