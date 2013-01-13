package com.joshondesign.treegui.modes.aminojava;

import org.joshy.gfx.draw.FlatColor;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/11/13
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Property {

    private final String name;
    private final Class type;
    private Object value;
    private boolean exported = true;
    private String exportName;
    private boolean visible = true;
    private boolean bindable = false;

    public Property(String name, Class type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Property setExported(boolean exported) {
        this.exported = exported;
        return this;
    }

    public boolean isExported() {
        return exported;
    }

    public String getName() {
        return name;
    }

    public String encode() {
        if(type == String.class) return (String)value;
        if(type == Boolean.class) return ((Boolean)value).toString();
        if(type == Double.class) {
            if(value instanceof Integer) {
                return ((Integer)value).toString();
            }
            return ((Double)value).toString();
        }
        if(type == CharSequence.class) {
            return ((CharSequence)value).toString();
        }
        if(type == FlatColor.class) {
            return Integer.toHexString(((FlatColor)value).getRGBA());
        }
        if(type.isEnum()) {
            Object[] vals = type.getEnumConstants();
            return value.toString();
        }
        return null;
    }

    public Class getType() {
        return type;
    }

    public Property duplicate() {
        Property p = new Property(this.name,this.type,this.value);
        p.exportName = this.exportName;
        p.setVisible(this.isVisible());
        p.setExported(this.isExported());
        p.setBindable(this.isBindable());
        return p;
    }

    public double getDoubleValue() {
        if(type == Double.class) {
            if(value instanceof Double) {
                return ((Double)value).doubleValue();
            }
            if(value instanceof Integer) {
                return ((Integer)value).doubleValue();
            }
        }
        return 99;
    }

    public String getStringValue() {
        if(type == String.class) {
            return ((String)value);
        }
        if(type == CharSequence.class) {
            return ((CharSequence)value).toString();
        }
        return "ERROR";
    }

    public boolean getBooleanValue() {
        if(type == Boolean.class) {
            return ((Boolean)value);
        }
        return false;
    }

    public FlatColor getColorValue() {
        return (FlatColor) value;
    }

    public void setDoubleValue(double w) {
        this.value = new Double(w);
    }

    public Property setExportName(String exportName) {
        this.exportName = exportName;
        return this;
    }

    public String getExportName() {
        return exportName;
    }

    public void setStringValue(String text) {
        this.value = text;
    }

    public void setDoubleValue(String text) {
        this.value = Double.parseDouble(text);
    }

    public void setBooleanValue(boolean selected) {
        this.value = new Boolean(selected);
    }

    public Property setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setEnumValue(Object o) {
        this.value = o;
    }

    public void setColorValue(FlatColor value) {
        this.value = value;
    }

    public Enum getEnumValue() {
        return (Enum) this.value;
    }

    public Property setBindable(boolean bindable) {
        this.bindable = bindable;
        return this;
    }

    public boolean isBindable() {
        return bindable;
    }
}
