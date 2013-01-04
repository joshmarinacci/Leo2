package com.joshondesign.treegui.modes.amino;

import com.joshondesign.treegui.docmodel.SketchNode;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/4/13
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class AminoAdapter {
    private static List<String> skipList;

    static {
        String[] skipListArray = {"class","inputBounds","size","constraint","translateX","translateY"};
        skipList = Arrays.asList(skipListArray);
    }
    public static String getScriptClass(SketchNode node) {
        if(node instanceof Button) return "Button";
        if(node instanceof Rect) return "Rect";
        if(node instanceof Slider) return "Slider";
        return "Rect";
    }

    public static Map<String,Object> getProps(SketchNode node) {
        Map<String,Object> props = new HashMap<String, Object>();
        Class<SketchNode> clazz = (Class<SketchNode>) node.getClass();
        Method[] methods = clazz.getMethods();
        for(Method m : methods) {
            if(!m.getName().startsWith("get")) continue;

            try {
                String name = m.getName().substring(3,4).toLowerCase()+m.getName().substring(4);
                u.p("prop name = " + name);
                if(skipList.contains(name)) continue;
                if(node instanceof Slider && name.equals("width")) continue;
                if(node instanceof Slider && name.equals("height")) continue;
                Object value = m.invoke(node);
                props.put(name,value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return props;
    }
}
