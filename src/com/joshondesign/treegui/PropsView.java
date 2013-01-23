package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.GridBox;
import org.joshy.gfx.util.ArrayListModel;

public class PropsView extends GridBox implements TreeNode.TreeListener {
    private PropFilter filter;
    private Callback<Void> updateCallback;
    private SketchDocument document;
    private DynamicNode lastObject;
    private List<Callback> listeners = new ArrayList<Callback>();

    public PropsView() {
    }

    @Override
    public void reset() {
        if(lastObject != null) {
            lastObject.removeListener(this);
        }
        if(listeners != null) {
            listeners.clear();
        }

        removeAll();
        super.reset();
        setPadding(5);
        createColumn(90, Align.Right);
        createColumn(130, Align.Fill);
        debug(false);
    }

    public void setSelection(final Object object) {
        reset();
        if(object == null) return;

        if(object instanceof DynamicNode) {
            setSelectionDynamicNode((DynamicNode)object);
            return;
        }

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

            if(prop.getter.getReturnType() == FlatColor.class) {
                addColorProperty(prop, object);
            }

            if(prop.getter.getReturnType() == List.class) {
                addListProperty(prop, object);
            }
        }
    }

    private void setSelectionDynamicNode(final DynamicNode node) {
        lastObject = node;
        node.addListener(this);

        addControl(new Label("id"));
        final Textbox idtb = new Textbox();
        idtb.setText("" + node.getId());
        idtb.setPrefWidth(100);
        EventBus.getSystem().addListener(idtb, FocusEvent.Lost, new Callback<FocusEvent>() {
            public void call(FocusEvent focusEvent) throws Exception {
                node.setId(idtb.getText());
                if (updateCallback != null) {
                    updateCallback.call(null);
                }
            }
        });
        idtb.onAction(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                node.setId(idtb.getText());
                if (updateCallback != null) {
                    updateCallback.call(null);
                }
            }
        });
        addControl(idtb);
        nextRow();


        for(final Property prop : node.getSortedProperties()) {
            if(!prop.isVisible()) continue;

            //do booleans first. they don't get separate labels
            if(prop.getType().isAssignableFrom(Boolean.class)) {
                skip();
                final Checkbox cb = new Checkbox(prop.getName());
                EventBus.getSystem().addListener(cb, FocusEvent.Lost, new Callback<FocusEvent>() {
                    public void call(FocusEvent focusEvent) throws Exception {
                        prop.setBooleanValue(cb.isSelected());
                        if (updateCallback != null) {
                            updateCallback.call(null);
                        }
                    }
                });
                cb.onClicked(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        prop.setBooleanValue(cb.isSelected());
                    }
                });
                cb.setSelected(prop.getBooleanValue());
                addControl(cb);
                nextRow();
                final Property property = prop;
                addListener(new Callback() {
                    public void call(Object o) throws Exception {
                        cb.setSelected(property.getBooleanValue());
                    }
                });
                continue;
            }


            //all non-booleans get separate labels
            addControl(new Label(prop.getDisplayName()));
            if(prop.getType().isAssignableFrom(String.class)) {
                final Textbox tb = new Textbox();
                tb.setText("" + prop.getStringValue());
                tb.setPrefWidth(100);
                EventBus.getSystem().addListener(tb, FocusEvent.Lost, new Callback<FocusEvent>() {
                    public void call(FocusEvent focusEvent) throws Exception {
                        prop.setStringValue(tb.getText());
                        //prop.setValue(tb.getText());
                        if (updateCallback != null) {
                            updateCallback.call(null);
                        }
                    }
                });
                tb.onAction(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        prop.setStringValue(tb.getText());
                        if (updateCallback != null) {
                            updateCallback.call(null);
                        }
                    }
                });
                final Property property = prop;
                addListener(new Callback() {
                    public void call(Object o) throws Exception {
                        tb.setText("" + property.getStringValue());
                    }
                });
                addControl(tb);
            }
            if(prop.getType().isAssignableFrom(Double.class)) {
                final Textbox tb = new Textbox();
                tb.setText(""+prop.getDoubleValue());
                tb.setPrefWidth(100);
                EventBus.getSystem().addListener(tb, FocusEvent.Lost, new Callback<FocusEvent>() {
                    public void call(FocusEvent focusEvent) throws Exception {
                        prop.setDoubleValue(tb.getText());
                        if (updateCallback != null) {
                            updateCallback.call(null);
                        }
                    }
                });
                tb.onAction(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        prop.setDoubleValue(tb.getText());
                        if (updateCallback != null) {
                            updateCallback.call(null);
                        }
                    }
                });
                final Property property = prop;
                addListener(new Callback() {
                    public void call(Object o) throws Exception {
                        tb.setText("" + property.getDoubleValue());
                    }
                });
                addControl(tb);
            }


            if(prop.getType().isEnum()) {
                Object[] vals = prop.getType().getEnumConstants();
                PopupMenuButton<Object> popup = new PopupMenuButton<Object>();
                final ArrayListModel<Object> list = new ArrayListModel<Object>();
                list.addAll(Arrays.asList(vals));
                popup.setModel(list);
                Enum en = prop.getEnumValue();
                popup.setSelectedIndex(en.ordinal());
                addControl(popup);

                EventBus.getSystem().addListener(popup,SelectionEvent.Changed, new Callback<SelectionEvent>() {
                    public void call(SelectionEvent selectionEvent) throws Exception {
                        int index = selectionEvent.getView().getSelectedIndex();
                        prop.setEnumValue(list.get(index));
                    }
                });

            }

            if(prop.getType() == FlatColor.class) {
                final SwatchColorPicker cp = new SwatchColorPicker();
                cp.onColorSelected(new Callback<ChangedEvent>() {
                    public void call(ChangedEvent changedEvent) throws Exception {
                        prop.setColorValue((FlatColor) changedEvent.getValue());
                    }
                });
                cp.setSelectedColor(prop.getColorValue());
                addControl(cp);
            }

            if(prop.getType() == List.class) {
                final Textarea ta = new Textarea();
                ta.setText("" + prop.getStringValue());
                ta.setPrefWidth(100);
                ta.setPrefHeight(100);
                addControl(ta);
            }
            nextRow();
        }
    }

    private void addListener(Callback callback) {
        this.listeners.add(callback);
    }

    private void addColorProperty(final Prop prop, Object object) {
        addControl(new Label(prop.name));
        final SwatchColorPicker cp = new SwatchColorPicker();
        cp.onColorSelected(new Callback<ChangedEvent>() {
            public void call(ChangedEvent changedEvent) throws Exception {
                prop.setValue((FlatColor)changedEvent.getValue());
            }
        });
        cp.setSelectedColor(prop.getColorValue());
        addControl(cp);
        this.nextRow();
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
                if (prop.setter != null) {
                    prop.setter.invoke(object, cb.isSelected());
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

    public void setDocument(SketchDocument document) {
        this.document = document;
        document.getSelection().addListener(new TreeNode.TreeListener() {
            public void added(TreeNode node) {
                setSelection(node);
                setDrawingDirty();
            }

            public void removed(TreeNode node) {
                setDrawingDirty();
            }

            public void modified(TreeNode node) {
                setSelection(node);
                setDrawingDirty();
            }
        });
    }

    public void added(TreeNode node) {
    }

    public void removed(TreeNode node) {
    }

    public void modified(TreeNode node) {
        updateControls();
    }

    private void updateControls() {
        for(Callback l : listeners) {
            try {
                l.call(null);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
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
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return (Double)value;
        }

        public FlatColor getColorValue() {
            Object value = null;
            try {
                value = this.getter.invoke(this.obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return (FlatColor)value;
        }

        public String getStringValue() {
            Object value = null;
            try {
                value = this.getter.invoke(this.obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
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

        public void setValue(FlatColor value) {
            if(this.getter.getReturnType() == FlatColor.class) {
                try {
                    this.setter.invoke(this.obj, value);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
}
