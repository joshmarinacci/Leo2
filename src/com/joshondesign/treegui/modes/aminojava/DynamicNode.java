package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.MathUtils;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.awt.geom.Point2D;
import java.util.*;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/11/13
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DynamicNode extends SketchNode {
    private String name;
    private boolean visual;
    private boolean resizable = true;
    private Map<String, Property> properies = new HashMap<String, Property>();
    private DrawDelegate drawDelegate;
    private boolean container = false;
    private boolean custom;

    public DynamicNode() {
    }

    public void setDrawDelegate(DrawDelegate drawDelegate) {
        this.drawDelegate = drawDelegate;
    }

    public Property getProperty(String width) {
        return properies.get(width);
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    public void copyPropertiesFrom(DynamicNode parent) {
        for(Property prop : parent.getProperties()) {
            addProperty(prop.duplicate());
        }
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean isCustom() {
        return custom;
    }

    public static interface DrawDelegate {
        public void draw(GFX g, DynamicNode node);
    }


    @Override
    public SketchNode duplicate(SketchNode node) {
        if(node == null) {
            DynamicNode nd = new DynamicNode();
            nd.name = this.name;
            nd.visual = this.visual;
            nd.resizable = this.resizable;
            nd.drawDelegate = this.drawDelegate;
            nd.custom = this.custom;
            nd.setContainer(this.isContainer());
            for(Property p : this.getProperties()) {
                nd.addProperty(p.duplicate());
            }
            for(SketchNode ch : this.children()) {
                nd.add(ch.duplicate(null));
            }
            node = nd;
        }
        return super.duplicate(node);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVisual(boolean visual) {
        this.visual = visual;
    }

    public boolean isVisual() {
        return visual;
    }

    @Override
    public void draw(GFX g) {
        if(this.drawDelegate != null) {
            this.drawDelegate.draw(g,this);
        } else {
            g.setPaint(FlatColor.GREEN);
            g.fillRect(0,0,20,40);
        }
    }

    @Override
    public SketchNode setTranslateX(double translateX) {
        this.getProperty("translateX").setDoubleValue(translateX);
        return super.setTranslateX(translateX);
    }

    @Override
    public SketchNode setTranslateY(double translateY) {
        this.getProperty("translateY").setDoubleValue(translateY);
        return super.setTranslateY(translateY);
    }

    @Override
    public double getTranslateX() {
        return this.getProperty("translateX").getDoubleValue();
    }

    @Override
    public double getTranslateY() {
        return this.getProperty("translateY").getDoubleValue();
    }

    @Override
    public boolean contains(Point2D pt) {
        return getInputBounds().contains(MathUtils.transform(pt,-getTranslateX(),-getTranslateY()));
    }

    private boolean hasProperty(String name) {
        return this.properies.containsKey(name);
    }
    @Override
    public Bounds getInputBounds() {
        if(isResizable()) {
            double w = getProperty("width").getDoubleValue();
            double h = getProperty("height").getDoubleValue();
            return new Bounds(0,0,w,h);
        }
        if(hasProperty("width") && hasProperty("height")) {
            double w = getProperty("width").getDoubleValue();
            double h = getProperty("height").getDoubleValue();
            return new Bounds(0,0,w,h);
        }
        return new Bounds(0,0,40,40);
    }

    public void setResizable(boolean rs) {
        this.resizable = rs;
    }

    public boolean isResizable() {
        return this.resizable;
    }

    public DynamicNode addProperty(Property property) {
        this.properies.put(property.getName(),property);
        return this;
    }

    public Iterable<? extends Property> getProperties() {
        return properies.values();
    }

    public Iterable<? extends Property> getSortedProperties() {
        List<Property> props = new ArrayList<Property>();
        props.addAll(properies.values());
        Collections.sort(props, new Comparator<Property>() {
            public int compare(Property property, Property property2) {
                return property.getName().compareTo(property2.getName());
            }
        });
        return props;
    }

    @Override
    public boolean isContainer() {
        return this.container;
    }
}
