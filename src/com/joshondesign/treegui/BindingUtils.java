package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Resize;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.Metadata;
import com.joshondesign.treegui.model.Prop;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import com.joshondesign.treegui.modes.aminojs.ActionProp;
import com.joshondesign.treegui.modes.aminojs.TriggerProp;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.joshy.gfx.util.u;

public class BindingUtils {

    public static void populateWithBindablePropertiesDynamic(BindingBox popup, DynamicNode node, Canvas canvas, SketchDocument doc) {
        for(Property prop : node.getSortedProperties()) {
            if(!prop.isBindable()) continue;
            String name = prop.getName();

            if(prop.isCompound()) {
                u.p("a compound prop. exploding");
                if(!node.hasProperty(prop.getMasterProperty())) {
                    u.p("WARNING: master property " + prop.getMasterProperty() + " is missing!");
                }
                Property master = node.getProperty(prop.getMasterProperty());
                u.p("master = " + master.getName());
                u.p("value = " + master.getRawValue());
                Binding binding = findTargetBinding(node, master.getName(), doc);
                u.p("current binding = " + binding);
                if(binding == null) continue;
                Property srcProp = binding.getSource().getProperty(binding.getSourceProperty().getName());
                u.p("src prop = " + srcProp);
                if(srcProp.isList()) {
                    u.p("master is bound to a list prop. we can get the prototype");
                    DynamicNode proto = srcProp.getItemPrototype();
                    u.p("proto = " + proto);
                    for(Property p2 :proto.getSortedProperties()) {
                        u.p("p2 = " + p2.getName());
                        if(p2.isBindable()) {
                            Property px = new Property(p2.getName(),p2.getType(),p2.getRawValue());
                            px.setProxy(true);
                            px.setMasterProperty(prop.getName());
                            popup.addProperty(canvas, doc, node, px, null, null, true, true);
                        }
                    }
                    continue;
                }
            }

            final Binding sourceBinding = findSourceBinding(node, name, doc);
            final Binding targetBinding = findTargetBinding(node, name, doc);
            popup.addProperty(canvas, doc, node, prop, sourceBinding, targetBinding,  true, true);
        }
    }

    private static Binding findTargetBinding(DynamicNode selection, String prop, SketchDocument doc) {
        for(Binding binding : doc.getBindings()) {
            if(binding.getTarget() == selection) {
                if(binding.getTargetProperty().getName().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    private static Binding findSourceBinding(SketchNode selection, String prop, SketchDocument doc) {
        for(Binding binding : doc.getBindings()) {
            if(binding.getSource() == selection) {
                if(binding.getSourceProperty().getName().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    public static void populateWithTargetPropertiesDynamic(BindingBox popup2, DynamicNode node, Binding currentBinding, Canvas canvas, SketchDocument doc) {
        for(Property prop : node.getSortedProperties()) {
            if(!prop.isBindable()) continue;
            String name = prop.getName();
            final Binding sourceBinding = findSourceBinding(node, name, doc);
            final Binding targetBinding = findTargetBinding(node, name, doc);
            boolean canUse = true;
            if(currentBinding.getSourceProperty().getType() == TriggerProp.class) {
                if(prop.getType() != ActionProp.class) {
                    canUse = false;
                }
            }
            popup2.addProperty(canvas, doc, node, prop, sourceBinding, targetBinding,  false, canUse);
        }
    }


    public static DynamicNode parseAnnotatedPOJO(Object obj, DynamicNode.DrawDelegate base) {
        DynamicNode node = new DynamicNode();
        node.setName(obj.getClass().getSimpleName());
        node.setResize(Resize.Any);
        node.setDrawDelegate(base);
        node
                .addProperty(new Property("width", Double.class, 90).setBindable(false).setExported(false))
                .addProperty(new Property("height", Double.class, 50).setBindable(false).setExported(false))
        ;
        try {
            u.p("doing " + node.getName());
            parseFields(obj.getClass().getFields(), obj, node);
            parseMethods(obj.getClass().getMethods(), obj, node);
            parseClassInfo(obj.getClass(), obj, node);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return node;

    }

    private static void parseClassInfo(Class aClass, Object obj, DynamicNode node) {
        if(aClass.isAnnotationPresent(Metadata.class)) {
            Metadata info = (Metadata) aClass.getAnnotation(Metadata.class);
            node.setVisual(info.visual());
            node.setResize(info.resize());
            node.setContainer(info.container());
            if(!info.exportClass().trim().equals("")) {
                node.addProperty(new Property("class", String.class, info.exportClass()));
            } else {
                node.addProperty(new Property("class", Class.class, aClass));
            }
            node.setName(info.name());
            if(info.name().equals("unnamed")) {
                node.setName(aClass.getSimpleName());
            }
        }
    }

    private static void parseMethods(Method[] methods, Object obj, DynamicNode node) throws InvocationTargetException, IllegalAccessException {
        for(Method method : methods) {
            if(method.isAnnotationPresent(Prop.class)) {
                Prop prop = method.getAnnotation(Prop.class);
                String name = method.getName();
                if(name.startsWith("is")) {
                    name = name.substring(2,3).toLowerCase() + name.substring(3);
                }
                if(name.startsWith("get")) {
                    name = name.substring(3,4).toLowerCase() + name.substring(4);
                }
                u.p("method = " + method.getName() + " type = " + method.getReturnType());
                Property p = new Property(name, method.getReturnType(), method.invoke(obj));
                p.setBindable(prop.bindable());
                p.setVisible(prop.visible());
                p.setExported(prop.exported());
                p.setCompound(prop.compound());
                p.setList(prop.list());
                p.setMasterProperty(prop.master());
                node.addProperty(p);
            }
        }
    }

    private static void parseFields(Field[] fields, Object obj, DynamicNode node) throws IllegalAccessException {
        for(Field field : fields) {
            for(Annotation an : field.getAnnotations()) {
                u.p("  ann " + an);
            }

            if(field.isAnnotationPresent(Prop.class)) {
                Prop prop = field.getAnnotation(Prop.class);
                u.p("field = " + field.getName() + " type = " + field.getType() + " value = " + field.get(obj));
                    u.p("  bindable = " + prop.bindable());
                String name = field.getName();
                if(name.equals("clazz")) {
                    name = "class";
                }
                Property p = new Property(name, field.getType(), field.get(obj));

                p.setBindable(prop.bindable());
                p.setVisible(prop.visible());
                p.setExported(prop.exported());
                p.setCompound(prop.compound());
                p.setList(prop.list());
                p.setMasterProperty(prop.master());
                node.addProperty(p);
            }
        }
    }

    public static Binding createBinding(DynamicNode source, String sname,
                                        DynamicNode target, String tname) {
        Binding binding = new Binding();
        binding.setSource(source);
        binding.setSourceProperty(source.getProperty(sname));
        binding.setTarget(target);
        binding.setTargetProperty(target.getProperty(tname));
        return binding;
    }
}
