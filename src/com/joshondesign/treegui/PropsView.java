package com.joshondesign.treegui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.control.Checkbox;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/30/12
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropsView extends VFlexBox {
    private PropFilter filter;
    private Callback<Void> updateCallback;

    public PropsView() {
    }

    public void setSelection(final Object object) {
        u.p("setting the selection to: " + object);
        List<Prop> props = findGetters(object);
        u.p(props);

        this.children.clear();
        for(final Prop prop : props) {
            if(filter != null) {
                if(!filter.include(object, prop.name)) continue;
            }
            if(prop.getter.getReturnType() == boolean.class) {
                final Checkbox cb = new Checkbox(prop.name);
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
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                this.add(cb);
            }
            if(prop.getter.getReturnType() == double.class) {
                HFlexBox box = new HFlexBox();
                box.add(new Label(prop.name));
                final Textbox tb = new Textbox();
                tb.setText(""+prop.getDoubleValue());
                tb.setPrefWidth(100);
                tb.onAction(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        prop.setValue(tb.getText());
                        if(updateCallback != null) {
                            updateCallback.call(null);
                        }
                    }
                });
                box.add(tb);
                this.add(box);
            }
        }
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
                u.p("name = " + name);
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
        }
    }
}
