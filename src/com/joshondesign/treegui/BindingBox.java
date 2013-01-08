package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.SketchNode;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/7/13
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class BindingBox extends VFlexBox {
    public BindingBox() {
        setFill(FlatColor.GRAY);
        this.setPrefWidth(120);
        this.setPrefHeight(200);
        add(new Button("Bindings"), 1);
    }


    public void addProperty(final Canvas canvas, final SketchNode node,
                            final String prop,
                            final Binding sourceBinding, Binding targetBinding, boolean isSource) {
        HFlexBox row = new HFlexBox();
        BindingStateButton targetButton = new BindingStateButton();
        if(targetBinding != null) {
            targetButton.setSelected(true);
        }
        final BindingStateButton sourceButton = new BindingStateButton();
        if(sourceBinding != null) {
            sourceButton.setSelected(true);
        }
        row.add(targetButton);
        row.add(new Label(prop));
        row.add(sourceButton);
        add(row, 1);

        if(isSource) {
            Callback<MouseEvent> callback = new Callback<MouseEvent>() {
                public void call(MouseEvent mouseEvent) throws Exception {
                    if(mouseEvent.getType() == MouseEvent.MousePressed) {
                        canvas.startDragPoint = mouseEvent.getPointInNodeCoords(canvas);
                        if(sourceBinding != null) {
                            sourceButton.setSelected(false);
                            canvas.getBindings().remove(sourceBinding);
                        }
                        canvas.currentBinding.setSourceProperty(prop);
                    }
                    if(mouseEvent.getType() == MouseEvent.MouseDragged) {
                        canvas.dragging = true;
                        canvas.currentDragPoint = mouseEvent.getPointInNodeCoords(canvas);
                        setDrawingDirty();
                    }
                    if(mouseEvent.getType() == MouseEvent.MouseReleased) {
                        canvas.dragging = false;
                        canvas.startDragPoint = null;
                        canvas.currentDragPoint = null;
                        setDrawingDirty();
                        SketchNode node = canvas.findNode(mouseEvent.getPointInNodeCoords(canvas));
                        if(node == null) return;
                        canvas.showTargetPopup(node, mouseEvent);
                    }
                }
            };
            EventBus.getSystem().addListener(sourceButton, MouseEvent.MouseAll, callback);
        } else {
            targetButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    Binding currentBinding = canvas.currentBinding;
                    canvas.currentBinding.setTarget(node);
                    currentBinding.setTargetProperty(prop);
                    u.p("added a binding: " + currentBinding);
                    canvas.bindings.add(currentBinding);
                    canvas.currentBinding = null;
                    canvas.popup2.setVisible(false);
                    canvas.popup.setVisible(false);
                }
            });
        }
    }

    public void reset() {
        removeAll();
    }

    private class BindingStateButton extends Button {
        public BindingStateButton() {
            setPrefWidth(20);
            setPrefHeight(20);
        }

        public void draw(GFX g) {
            g.setPaint(FlatColor.BLACK);
            if(isSelected()) {
                g.fillOval(0,0,20,20);
            } else {
                g.drawOval(0,0,20,20);
            }
        }
    }
}
