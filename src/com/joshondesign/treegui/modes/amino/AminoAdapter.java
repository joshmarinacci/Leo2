package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.docmodel.Group;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.util.u;

public class AminoAdapter {
    private static List<String> skipList;

    static {
        String[] skipListArray = {"class","inputBounds","size","constraint","visual"};
        skipList = Arrays.asList(skipListArray);
    }
    public static String getScriptClass(SketchNode node) {
        if(node instanceof CheckButton) return "CheckButton";
        if(node instanceof ToggleButton) return "ToggleButton";
        if(node instanceof Spinner) return "Spinner";
        if(node instanceof PushButton) return "PushButton";
        if(node instanceof Rect) return "Rect";
        if(node instanceof Slider) return "Slider";
        if(node instanceof Group) return "Group";
        if(node instanceof ListView) return "ListView";
        if(node instanceof StringListModel) return "ListModel";
        if(node instanceof Textbox) return "Textbox";
        if(node instanceof Image) return "ImageView";
        if(node instanceof FlickrQuery) return "FlickrQuery";
        return "Rect";
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
                if(node instanceof StringListModel) {
                    if(name.equals("this")) continue;
                    if(name.equals("x")) continue;
                    if(name.equals("y")) continue;
                }
                if(node instanceof Image) {
                    if(name.equals("width")) continue;
                    if(name.equals("height")) continue;
                }
                if(node instanceof FlickrQuery) {
                    //if(name.equals("results")) continue;
                    if(name.equals("query")) continue;
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
        if(binding.getSource() instanceof StringListModel) {
            return true;
        }
        return false;
    }

    public static boolean useSetup(SketchNode node) {
        if(node instanceof Slider) return true;
        if(node instanceof PushButton) return true;
        if(node instanceof CheckButton) return true;
        if(node instanceof ToggleButton) return true;
        if(node instanceof Textbox) return true;
        return false;
    }

    public static boolean shouldExportProperty(SketchNode node, String name) {
        if(node instanceof FlickrQuery) {
            if("execute".equals(name)) return false;
            if("results".equals(name)) return false;
        }

        if(node instanceof PushButton) {
            if("trigger".equals(name)) return false;
        }

        return true;
    }

    public static boolean shouldExportAsSetter(Binding binding) {
        if(binding.getSource() instanceof StringListModel) return true;
        if(binding.getSource() instanceof FlickrQuery) {
            if(binding.getSourceProperty().equals("results")) return true;
        }
        return false;
    }

    public static boolean shouldExportAsTrigger(Binding binding) {
        if(binding.getSource() instanceof PushButton) {
            if(binding.getSourceProperty().equals("trigger")) {
                return true;
            }
        }

        return false;
    }
}
