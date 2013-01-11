package com.joshondesign.treegui.modes.aminojava;

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
        return null;
    }

    public Class getType() {
        return type;
    }

    public Property duplicate() {
        Property p = new Property(this.name,this.type,this.value);
        p.setExported(this.isExported());
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

    public void setDoubleValue(double w) {
        this.value = new Double(w);
    }
}
