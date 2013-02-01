package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.util.u;

public class AminoAdapter {
    private static List<String> skipList;

    static {
        String[] skipListArray = {"class","inputBounds","size","constraint","visual","container","parent","resizable"};
        skipList = Arrays.asList(skipListArray);
    }
    public static String getScriptClass(DynamicNode node) {

//        if(node instanceof StringListModel) return "ListModel";
//        if(node instanceof ControlListModel) return "ListModel";
//        if(node instanceof Image) return "ImageView";
        u.p("node = " + node.getName());
        for(Property prop : node.getProperties()){
            u.p("   " + prop.getName() + " " + prop.getRawValue());
        }
        if(!node.hasProperty("class")) {
            u.p("warning. missing a class on a node " + node.getName());
        }
        Object val = node.getProperty("class").getRawValue();
        if(val instanceof Class) {
            return ((Class)val).getSimpleName();
        }
        if(val instanceof String) {
            return (String) val;
        }
        return null;
    }

    public static Map<String,Object> getProps(SketchNode node) {
        Map<String,Object> props = new HashMap<String, Object>();
        Class<SketchNode> clazz = (Class<SketchNode>) node.getClass();
        Method[] methods = clazz.getMethods();
        for(Method m : methods) {
            if(!m.getName().startsWith("get") && !m.getName().startsWith("is")) continue;
            if(m.getName().equals("get")) continue;

            String name = m.getName();
            if(name.startsWith("is")) {
                name = name.substring(2,3).toLowerCase()+name.substring(3);
            } else {
                if(name.startsWith("get")) {
                    name = name.substring(3,4).toLowerCase()+name.substring(4);
                }
            }

            try {
                //String name = m.getName().substring(3,4).toLowerCase()+m.getName().substring(4);
                u.p("name = " + name);
                if(skipList.contains(name)) continue;
                //if(node instanceof Slider && name.equals("width")) continue;
                if(node instanceof Slider && name.equals("height")) continue;
                if(node instanceof PushButton && name.equals("width")) continue;
                if(node instanceof PushButton && name.equals("height")) continue;
                if(!node.isVisual()) {
                    if(name.equals("translateX")) continue;
                    if(name.equals("translateY")) continue;
                }
                Object value = m.invoke(node);
                props.put(name,value);
            } catch (Throwable e) {
                u.p("failed at : " + m.getName());
                e.printStackTrace();
            }
        }
        return props;
    }

    public static boolean isDataModel(Binding binding) {
//        if(binding.getSource() instanceof StringListModel) {
//            return true;
//        }
//        if(binding.getSource() instanceof ControlListModel) {
//            return true;
//        }
        return false;
    }

    public static boolean useSetup(DynamicNode node) {
        if(node.getName().equals("Image")) return false;
        if(node.isVisual()) return true;
        return false;
    }

    public static boolean shouldExportProperty(DynamicNode node, Property prop) {
        if(!prop.isExported()) return false;
        String name = prop.getName();
        if("class".equals(name)) return false;
        if("this".equals(name)) return false;
        if("constraint".equals(name)) return false;
        if("resize".equals(name)) return false;
        if("trigger".equals(name)) return false;

        if(node.getName().equals("Image")) {
            if("width".equals(name)) return false;
            if("height".equals(name)) return false;
        }
        /*
        if(node instanceof FlickrQuery) {
            if("execute".equals(name)) return false;
            if("results".equals(name)) return false;
            if("active".equals(name)) return false;
        }

        if(node instanceof ControlListModel) {
            if("this".equals(name)) return false;
            if(name.startsWith("item")) return false;
        }
    */
        return true;
    }

    public static boolean shouldExportAsSetter(Binding binding) {
        if(binding.getSource().getName().equals("FlickrQuery")) {
            if(binding.getSourceProperty().getName().equals("results")) {
                return true;
            }
        }
//        if(binding.getSource() instanceof StringListModel) return true;
//        if(binding.getSource() instanceof ControlListModel) return true;
//        if(binding.getSource() instanceof FlickrQuery) {
//            if(binding.getSourceProperty().equals("results")) return true;
//        }
        return false;
    }

    public static boolean shouldExportAsTrigger(Binding binding) {
        if(binding.getSourceProperty().getType() == TriggerProp.class) return true;
        return false;
    }

    public static boolean shouldExportAsAdder(Binding binding) {
//        if(binding.getTarget() instanceof StringListModel) return true;
//        if(binding.getTarget() instanceof ControlListModel) return true;
        return false;
    }

    public static boolean shouldAddToScene(SketchNode node, List<Binding> bindings) {
        for(Binding binding : bindings) {
            if(binding.getSource() == node) {
                if(binding.getSourceProperty().equals("this")) {
                    return false;
                }
            }
        }
        return true;
    }
}
