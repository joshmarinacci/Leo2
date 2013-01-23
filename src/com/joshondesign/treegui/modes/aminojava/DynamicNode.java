package com.joshondesign.treegui.modes.aminojava;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.MathUtils;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.awt.geom.Point2D;
import java.util.*;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

public class DynamicNode extends SketchNode {
    private String name;
    private boolean visual;
    private boolean resizable = true;
    private Map<String, Property> properies = new HashMap<String, Property>();
    private DrawDelegate drawDelegate;
    private boolean container = false;
    private boolean custom;
    private boolean positionLocked = false;
    private boolean mirror;
    private String mirrorTarget;

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

    public DynamicNode setPositionLocked(boolean locked) {
        this.positionLocked = locked;
        return this;
    }

    public boolean isPositionLocked() {
        return positionLocked;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    public boolean isMirror() {
        return mirror;
    }

    public void setMirrorTarget(String mirrorTarget) {
        this.mirrorTarget = mirrorTarget;
    }

    public String getMirrorTarget() {
        return mirrorTarget;
    }

    public void refreshMirror(SketchDocument document) {
        if(!isMirror()) return;

        DynamicNode par = (DynamicNode) ((DynamicNode) getParent()).getParent();
        if(par == null) return;
        Property tprop = par.getProperty(getMirrorTarget());
        if(!tprop.isList()) return;
        //find the thing it is bound to
        Binding binding = findBindingForTarget(document.getBindings(),par,tprop);
        if(binding == null) return;
        DynamicNode source = ((DynamicNode)binding.getSource());
        DynamicNode proto = source.getProperty(binding.getSourceProperty()).getItemPrototype();
        for(Property prop : proto.getProperties()) {
            if(prop.isBindable()) {
                addProperty(prop.duplicate());
            }
        }
    }

    private Binding findBindingForTarget(List<Binding> bindings, DynamicNode par, Property tprop) {
        for(Binding binding : bindings) {
            if(binding.getTarget() == par) {
                if(binding.getTargetProperty().equals(tprop.getName())) {
                    return binding;
                }
            }
        }
        return null;
    }

    public void markPropertyChanged() {
        markModified(this);
        if(getParent() != null) {
            getParent().markModified(this);
        }
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
            nd.positionLocked = this.positionLocked;
            nd.mirror = this.mirror;
            nd.mirrorTarget = this.mirrorTarget;

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

    boolean hasProperty(String name) {
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
        property.setNode(this);
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
