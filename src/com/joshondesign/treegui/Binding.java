package com.joshondesign.treegui;

import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;

public class Binding {
    private DynamicNode source;
    private Property sourceProperty;
    private DynamicNode target;
    private Property targetProperty;

    public void setSource(DynamicNode source) {
        this.source = source;
    }

    public DynamicNode getSource() {
        return source;
    }

    public void setSourceProperty(Property sourceProperty) {
        this.sourceProperty = sourceProperty;
    }

    public Property getSourceProperty() {
        return sourceProperty;
    }

    public void setTarget(DynamicNode target) {
        this.target = target;
    }

    public DynamicNode getTarget() {
        return target;
    }

    public void setTargetProperty(Property targetProperty) {
        this.targetProperty = targetProperty;
    }

    public Property getTargetProperty() {
        return targetProperty;
    }

    @Override
    public String toString() {
        return "Binding{" +
                "source=" + source +
                ", sourceProperty='" + sourceProperty + '\'' +
                ", target=" + target +
                ", targetProperty='" + targetProperty + '\'' +
                '}';
    }
}
