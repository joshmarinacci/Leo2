package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchNode;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/1/13
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Binding {
    private SketchNode source;
    private String sourceProperty;
    private SketchNode target;
    private String targetProperty;

    public void setSource(SketchNode source) {
        this.source = source;
    }

    public SketchNode getSource() {
        return source;
    }

    public void setSourceProperty(String sourceProperty) {
        this.sourceProperty = sourceProperty;
    }

    public String getSourceProperty() {
        return sourceProperty;
    }

    public void setTarget(SketchNode target) {
        this.target = target;
    }

    public SketchNode getTarget() {
        return target;
    }

    public void setTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
    }

    public String getTargetProperty() {
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
