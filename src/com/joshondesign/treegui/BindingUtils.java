package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.amino.ActionProp;
import com.joshondesign.treegui.modes.amino.AminoAdapter;
import com.joshondesign.treegui.modes.amino.TriggerProp;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;

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
}
