package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/2/13
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropUtils {
    public static Class getPropertyType(Object object, String propname) {
        if(object instanceof DynamicNode) {
            DynamicNode node = (DynamicNode) object;
            return node.getProperty(propname).getType();
        }

        Class clazz = object.getClass();
        Method[] methods = clazz.getMethods();
        for(Method meth : methods) {
            if(meth.getName().startsWith("get")) {
                if(meth.getName().equals("get")) continue;
                String name = meth.getName().substring(3);
                name = name.substring(0,1).toLowerCase()+name.substring(1);
                if(name.equals(propname)) {
                    return meth.getReturnType();
                }
            }
        }
        return null;
    }


    public static boolean propertyEquals(SketchNode object, String prop, boolean value) {
        Class clazz = object.getClass();
        Method[] methods = clazz.getMethods();
        for(Method meth : methods) {
            String booleanGetter = "is"+prop.substring(0,1).toUpperCase() + prop.substring(1);
            if(meth.getName().equals(booleanGetter)) {
                try {
                    Boolean retval = (Boolean) meth.invoke(object);
                    u.p("got value : " + retval);
                    if(retval.booleanValue() == value) return true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}
