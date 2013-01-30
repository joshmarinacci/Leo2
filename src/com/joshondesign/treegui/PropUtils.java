package com.joshondesign.treegui;

import java.lang.reflect.Method;

public class PropUtils {

    public static Method findGetter(Object src, String prop) throws NoSuchMethodException {
        try {
            String getterName = "get"+prop.substring(0,1).toUpperCase()+prop.substring(1);
            return src.getClass().getMethod(getterName);
        } catch (NoSuchMethodException e) {
            String getterName = "is"+prop.substring(0,1).toUpperCase()+prop.substring(1);
            return src.getClass().getMethod(getterName);
        }
    }

    public static Method findSetter(Object tgt, String tgtProp) throws NoSuchMethodException {
        Method getter = findGetter(tgt,tgtProp);
        String setterName = "set"+tgtProp.substring(0,1).toUpperCase()+tgtProp.substring(1);
        Method setter = tgt.getClass().getMethod(setterName, getter.getReturnType());
        return setter;
    }
}
