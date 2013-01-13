package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.amino.ActionProp;
import com.joshondesign.treegui.modes.amino.AminoAdapter;
import com.joshondesign.treegui.modes.amino.TriggerProp;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/12/13
 * Time: 10:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class BindingUtils {
    public static void populateWithBindablePropertiesRegular(BindingBox popup, SketchNode node, Canvas canvas) {
        for(final String prop : AminoAdapter.getProps(node).keySet()) {
            if("id".equals(prop)) continue; //skip the ID property

            final Binding sourceBinding = findSourceBinding(node, prop, canvas);
            final Binding targetBinding = findTargetBinding(node, prop, canvas);
            popup.addProperty(canvas, node, prop, sourceBinding, targetBinding, true, true);
        }
    }
    public static void populateWithBindablePropertiesDynamic(BindingBox popup, DynamicNode node, Canvas canvas) {
        for(Property prop : node.getSortedProperties()) {
            if(!prop.isBindable()) continue;
            String name = prop.getName();
            final Binding sourceBinding = findSourceBinding(node, name, canvas);
            final Binding targetBinding = findTargetBinding(node, name, canvas);
            popup.addProperty(canvas, node, name, sourceBinding, targetBinding,  true, true);
        }
    }

    private static Binding findTargetBinding(SketchNode selection, String prop, Canvas canvas) {
        for(Binding binding : canvas.bindings) {
            if(binding.getTarget() == selection) {
                if(binding.getTargetProperty().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    private static Binding findSourceBinding(SketchNode selection, String prop, Canvas canvas) {
        for(Binding binding : canvas.bindings) {
            if(binding.getSource() == selection) {
                if(binding.getSourceProperty().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    public static void populateWithTargetPropertiesRegular(BindingBox popup2, SketchNode node, Binding currentBinding, Canvas canvas) {
        for(final String prop : AminoAdapter.getProps(node).keySet()) {
            if(prop.equals("id")) continue;
            final Binding sourceBinding = findSourceBinding(node, prop, canvas);
            final Binding targetBinding = findTargetBinding(node, prop, canvas);
            boolean canUse = true;
            if(currentBinding.getSourceType() == TriggerProp.class) {
                if(PropUtils.getPropertyType(node,prop) != ActionProp.class) {
                    canUse = false;
                }
            }
            popup2.addProperty(canvas, node, prop, sourceBinding, targetBinding, false, canUse);
        }

    }

    public static void populateWithTargetPropertiesDynamic(BindingBox popup2, DynamicNode node, Binding currentBinding, Canvas canvas) {
        for(Property prop : node.getSortedProperties()) {
            if(!prop.isBindable()) continue;
            String name = prop.getName();
            final Binding sourceBinding = findSourceBinding(node, name, canvas);
            final Binding targetBinding = findTargetBinding(node, name, canvas);
            boolean canUse = true;
            if(currentBinding.getSourceType() == TriggerProp.class) {
                if(prop.getType() != ActionProp.class) {
                    canUse = false;
                }
            }
            popup2.addProperty(canvas, node, name, sourceBinding, targetBinding,  false, canUse);
        }
    }
}
