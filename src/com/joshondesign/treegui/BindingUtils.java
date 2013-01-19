package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.amino.ActionProp;
import com.joshondesign.treegui.modes.amino.AminoAdapter;
import com.joshondesign.treegui.modes.amino.TriggerProp;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Prop;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.joshy.gfx.util.u;

public class BindingUtils {
    public static void populateWithBindablePropertiesRegular(BindingBox popup, SketchNode node, Canvas canvas, SketchDocument doc) {
        for(final String prop : AminoAdapter.getProps(node).keySet()) {
            if("id".equals(prop)) continue; //skip the ID property

            final Binding sourceBinding = findSourceBinding(node, prop, doc);
            final Binding targetBinding = findTargetBinding(node, prop, doc);
            popup.addProperty(canvas, doc, node, prop, sourceBinding, targetBinding, true, true);
        }
    }
    public static void populateWithBindablePropertiesDynamic(BindingBox popup, DynamicNode node, Canvas canvas, SketchDocument doc) {
        for(Property prop : node.getSortedProperties()) {
            if(!prop.isBindable()) continue;
            String name = prop.getName();
            final Binding sourceBinding = findSourceBinding(node, name, doc);
            final Binding targetBinding = findTargetBinding(node, name, doc);
            popup.addProperty(canvas, doc, node, name, sourceBinding, targetBinding,  true, true);
        }
    }

    private static Binding findTargetBinding(SketchNode selection, String prop, SketchDocument doc) {
        for(Binding binding : doc.getBindings()) {
            if(binding.getTarget() == selection) {
                if(binding.getTargetProperty().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    private static Binding findSourceBinding(SketchNode selection, String prop, SketchDocument doc) {
        for(Binding binding : doc.getBindings()) {
            if(binding.getSource() == selection) {
                if(binding.getSourceProperty().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    public static void populateWithTargetPropertiesRegular(BindingBox popup2, SketchNode node, Binding currentBinding, Canvas canvas, SketchDocument doc) {
        for(final String prop : AminoAdapter.getProps(node).keySet()) {
            if(prop.equals("id")) continue;
            final Binding sourceBinding = findSourceBinding(node, prop, doc);
            final Binding targetBinding = findTargetBinding(node, prop, doc);
            boolean canUse = true;
            if(currentBinding.getSourceType() == TriggerProp.class) {
                if(PropUtils.getPropertyType(node,prop) != ActionProp.class) {
                    canUse = false;
                }
            }
            popup2.addProperty(canvas, doc, node, prop, sourceBinding, targetBinding, false, canUse);
        }

    }

    public static void populateWithTargetPropertiesDynamic(BindingBox popup2, DynamicNode node, Binding currentBinding, Canvas canvas, SketchDocument doc) {
        for(Property prop : node.getSortedProperties()) {
            if(!prop.isBindable()) continue;
            String name = prop.getName();
            final Binding sourceBinding = findSourceBinding(node, name, doc);
            final Binding targetBinding = findTargetBinding(node, name, doc);
            boolean canUse = true;
            if(currentBinding.getSourceType() == TriggerProp.class) {
                if(prop.getType() != ActionProp.class) {
                    canUse = false;
                }
            }
            popup2.addProperty(canvas, doc, node, name, sourceBinding, targetBinding,  false, canUse);
        }
    }


    public static DynamicNode parseAnnotatedPOJO(Object obj, DynamicNode.DrawDelegate base) {
        DynamicNode node = new DynamicNode();
        node.setName("FlickrQuery");
        node.setResizable(false);
        node.setDrawDelegate(base);
        node.addProperty(new Property("translateX", Double.class, 0).setExported(false))
                .addProperty(new Property("translateY", Double.class, 0).setExported(false))
                .addProperty(new Property("width", Double.class, 90).setBindable(false).setExported(false))
                .addProperty(new Property("height", Double.class, 50).setBindable(false).setExported(false))
        ;
        try {
            for(Field field :obj.getClass().getFields()) {
                for(Annotation an : field.getAnnotations()) {
                    //u.p("  ann " + an);
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
                    node.addProperty(p);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return node;

    }
}
