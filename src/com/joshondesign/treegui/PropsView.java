package com.joshondesign.treegui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.FocusEvent;
import org.joshy.gfx.node.control.Checkbox;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.Textarea;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.GridBox;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/30/12
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropsView extends GridBox {
    private PropFilter filter;
    private Callback<Void> updateCallback;

    public PropsView() {
    }

    @Override
    public void reset() {
        removeAll();
        super.reset();
        setPadding(1);
        setPrefWidth(270);
        createColumn(50, Align.Right);
        createColumn(200, Align.Fill);
        debug(false);
    }

    public void setSelection(final Object object) {
        reset();
        if(object == null) return;
        List<Prop> props = findGetters(object);
        Collections.sort(props, new Comparator<Prop>() {
            public int compare(Prop a, Prop b) {
                if(a.name.equals("id")) return -1;
                return a.name.compareTo(b.name);
            }
        });

        for(final Prop prop : props) {
            if(filter != null) {
                if(!filter.include(object, prop.name)) continue;
            }
            if(prop.getter.getReturnType() == boolean.class) {
                addBooleanProperty(prop, object);
            }
            if(prop.getter.getReturnType() == double.class) {
                addDoubleProperty(prop, object);
            }

            if(prop.getter.getReturnType() == String.class) {
                addStringProperty(prop, object);
            }

            if(prop.getter.getReturnType() == List.class) {
                addListProperty(prop, object);
            }
        }
    }

    private void addListProperty(final Prop prop, Object object) {
        addControl(new Label(prop.name));
        final Textarea ta = new Textarea();
        ta.setText("" + prop.getStringValue());
        ta.setPrefWidth(100);
        ta.setPrefHeight(100);
        EventBus.getSystem().addListener(ta, FocusEvent.Lost, new Callback<FocusEvent>() {
            public void call(FocusEvent focusEvent) throws Exception {
                prop.setValue(ta.getText());
                if (updateCallback != null) {
                    updateCallback.call(null);
                }
            }
        });
        addControl(ta);
        nextRow();
    }

    private void addStringProperty(final Prop prop, Object object) {
        addControl(new Label(prop.name));
        final Textbox tb = new Textbox();
        tb.setText("" + prop.getStringValue());
        tb.setPrefWidth(100);
        EventBus.getSystem().addListener(tb, FocusEvent.Lost, new Callback<FocusEvent>() {
            public void call(FocusEvent focusEvent) throws Exception {
                prop.setValue(tb.getText());
                if (updateCallback != null) {
                    updateCallback.call(null);
                }
            }
        });
        tb.onAction(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                prop.setValue(tb.getText());
                if (updateCallback != null) {
                    updateCallback.call(null);
                }
            }
        });
        addControl(tb);
        this.nextRow();
    }

    private void addDoubleProperty(final Prop prop, Object object) {
        addControl(new Label(prop.name));
        final Textbox tb = new Textbox();
        tb.setText(""+prop.getDoubleValue());
        tb.setPrefWidth(100);
        EventBus.getSystem().addListener(tb, FocusEvent.Lost, new Callback<FocusEvent>() {
            public void call(FocusEvent focusEvent) throws Exception {
                prop.setValue(tb.getText());
                if (updateCallback != null) {
                    updateCallback.call(null);
                }
            }
        });
        tb.onAction(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                prop.setValue(tb.getText());
                if (updateCallback != null) {
                    updateCallback.call(null);
                }
            }
        });
        addControl(tb);
        this.nextRow();
    }

    private void addBooleanProperty(final Prop prop, final Object object) {
        final Checkbox cb = new Checkbox(prop.name);
        EventBus.getSystem().addListener(cb, FocusEvent.Lost, new Callback<FocusEvent>() {
            public void call(FocusEvent focusEvent) throws Exception {
                if(prop.setter != null) {
                    prop.setter.invoke(object,cb.isSelected());
                }
                if(updateCallback != null) {
                    updateCallback.call(null);
                }
            }
        });
        cb.onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                if(prop.setter != null) {
                    prop.setter.invoke(object,cb.isSelected());
                }
            }
        });
        try {
            Object value = prop.getter.invoke(object);
            Boolean bool = (Boolean) value;
            cb.setSelected(bool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        skip();
        addControl(cb);
        nextRow();
    }

    private List<Prop> findGetters(Object object) {
        Class clazz = object.getClass();
        Method[] methods = clazz.getMethods();
        List<Prop> meths = new ArrayList<Prop>();
        for(Method meth : methods) {
            if(meth.getName().startsWith("get")) {
                meths.add(new Prop(meth,clazz,object));
            }
            if(meth.getName().startsWith("is")) {
                meths.add(new Prop(meth,clazz,object));
            }
        }
        return meths;
    }

    public void setPropFilter(PropFilter propFilter) {
        this.filter = propFilter;
    }

    public void onUpdate(Callback<Void> callback) {
        this.updateCallback = callback;
    }

    public static interface PropFilter {
        public boolean include(Object object, String name);
    }

    private static class Prop {
        private String name;
        private Method getter;
        private Object obj;
        private Method setter;
        private String doubleValue;

        public Prop(Method meth, Class clazz, Object obj) {
            this.obj = obj;
            final String name = meth.getName();
            this.name = meth.getName();
            this.getter = meth;
            if(name.startsWith("is")) {
                this.name = name.substring(2,3).toLowerCase() + name.substring(3);
            }
            if(name.startsWith("get") && name.length() > 3) {
                this.name = name.substring(3,4).toLowerCase() + name.substring(4);
            }

            String setterName = "set"+this.name.substring(0,1).toUpperCase()+this.name.substring(1);
            Method[] methods = clazz.getMethods();
            for(Method m : methods) {
                if(m.getName().equals(setterName)) {
                    this.setter = m;
                    break;
                }
            }
        }

        public Double getDoubleValue() {
            Object value = null;
            try {
                value = this.getter.invoke(this.obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return (Double)value;
        }

        public String getStringValue() {
            Object value = null;
            try {
                value = this.getter.invoke(this.obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            if(value instanceof List) {
                StringBuffer sb = new StringBuffer();
                for(Object v : ((List)value)) {
                    sb.append(v);
                    sb.append("\n");
                }
                return sb.toString();
            }
            return (String)value;
        }

        public void setValue(String text) {
            if(this.getter.getReturnType() == double.class) {
                try {
                    this.setter.invoke(this.obj, Double.parseDouble(text));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if(this.getter.getReturnType() == String.class) {
                try {
                    this.setter.invoke(this.obj, text);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if(this.getter.getReturnType() == List.class) {
                String[] strings = text.split("\n");
                List<String> list = Arrays.asList(strings);
                try {
                    this.setter.invoke(this.obj, list);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
}
