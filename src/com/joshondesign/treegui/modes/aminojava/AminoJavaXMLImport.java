package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.TreeGui;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import javax.xml.xpath.XPathExpressionException;
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

    public AminoJavaXMLImport(Canvas canvas) {
        super();
        this.canvas = canvas;
    }

    @Override
    public void execute() {
        File file = new File("foo.xml");
        u.p("loading"  + file.getAbsolutePath());
        try {
            Doc xml = XMLParser.parse(file);
            Page page = processPage(xml.root());
            canvas.setMasterRoot(page.get(0));
            canvas.setEditRoot(page.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Page processPage(Elem root) {
        Page page = new Page();

        //non visual
//        for(Elem nonvis : root.xpath("nonvisual")) {

//        }
        //visual
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
            u.p("prop = " + prop);
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
            String name = prop.attr("name");
            if(name.equals("prefWidth")) {
                name = "width";
            }
            if(name.equals("prefHeight")) {
                name = "height";
            }
            node.addProperty(new Property(name, type, val));
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
