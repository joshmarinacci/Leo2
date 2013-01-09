package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.amino.AminoAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Focusable;

public class Canvas extends Control implements Focusable{
    private TreeNode<SketchNode> target;
    private TreeNode<SketchNode> selection = new TreeNode<SketchNode>();
    private PropsView propsView;
    BindingBox popup;
    BindingBox popup2;
    Point2D startDragPoint;
    boolean dragging;
    Point2D currentDragPoint;
    Binding currentBinding;
    List<Binding> bindings = new ArrayList<Binding>();
    private final SelectionTool selectionTool;


    public Canvas() {
        selectionTool = new SelectionTool(this);
    }



    void showBindingMenu(Point2D pt) {

        currentBinding = new Binding();
        currentBinding.setSource(getSelection().get(0));

        if(popup == null) {
            popup = new BindingBox();
            this.getParent().getStage().getPopupLayer().add(popup);
        }
        if(popup != null) {
            popup.reset();
            popup.setVisible(true);
        }
        populateWithBindableProperties(popup, getSelection().get(0));
        popup.setTranslateX(pt.getX() + this.getTranslateX());
        popup.setTranslateY(pt.getY() + this.getTranslateY());
    }

    private static class BindingButton extends Button {
        private final String prop;
        private Binding binding;

        public BindingButton(String prop, Binding binding) {
            super(prop);
            this.prop = prop;
            this.binding = binding;
            this.setPrefWidth(100);
        }

        public void setBinding(Binding binding) {
            this.binding = binding;
            setDrawingDirty();
        }



        @Override
        public void draw(GFX g) {
            g.setPaint(FlatColor.fromRGBInts(200,200,200));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
            g.setPaint(FlatColor.BLACK);
            Font.drawCenteredVertically(g, prop, Font.DEFAULT, 2, 0, getWidth(), getHeight(), false);
            if(binding == null) {
                g.drawOval(100-20,0,20,20);
            } else {
                g.fillOval(100 - 20, 0, 20, 20);
            }
        }
    }

    private void populateWithBindableProperties(BindingBox popup, SketchNode selection) {
        for(final String prop : AminoAdapter.getProps(selection).keySet()) {
            if("id".equals(prop)) continue; //skip the ID property

            final Binding sourceBinding = findSourceBinding(selection, prop);
            final Binding targetBinding = findTargetBinding(selection, prop);
            popup.addProperty(this, selection, prop, sourceBinding, targetBinding, true);
        }
    }

    private Binding findTargetBinding(SketchNode selection, String prop) {
        for(Binding binding : bindings) {
            if(binding.getTarget() == selection) {
                if(binding.getTargetProperty().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    private Binding findSourceBinding(SketchNode selection, String prop) {
        for(Binding binding : bindings) {
            if(binding.getSource() == selection) {
                if(binding.getSourceProperty().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    void showTargetPopup(final SketchNode node, MouseEvent mouseEvent) {
        if(popup2 == null) {
            popup2 = new BindingBox();
            this.getParent().getStage().getPopupLayer().add(popup2);
            //popup2.setFill(FlatColor.GRAY);
        }
        if(popup2 != null) {
            popup2.reset();
            popup2.setVisible(true);
        }
        for(final String prop : AminoAdapter.getProps(node).keySet()) {
            if(prop.equals("id")) continue;
            final Binding sourceBinding = findSourceBinding(node, prop);
            final Binding targetBinding = findTargetBinding(node, prop);
            popup2.addProperty(this, node, prop, sourceBinding, targetBinding, false);
        }
        Point2D pt = mouseEvent.getPointInNodeCoords(Canvas.this);
        popup2.setTranslateX(pt.getX() + this.getTranslateX());
        popup2.setTranslateY(pt.getY() + this.getTranslateY());
    }

    @Override
    public void doLayout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doPrefLayout() {
        this.setWidth(getPrefWidth());
        this.setHeight(getPrefHeight());
    }

    @Override
    public void doSkins() {
    }

    @Override
    public void setLayoutDirty() {
        super.setLayoutDirty();
    }

    @Override
    public void draw(GFX gfx) {
        gfx.setPaint(FlatColor.fromRGBInts(230,230,230));
        gfx.fillRect(0,0,getWidth(),getHeight());
        drawTarget(gfx,target);
        drawBindings(gfx);
        drawSelectionOverlay(gfx);
        drawActiveBindingLineOverlay(gfx);
        drawHandles(gfx);
    }

    private void drawHandles(GFX gfx) {
        for(Handle handle : this.handles) {
            handle.draw(gfx);
        }
    }

    private void drawBindings(GFX gfx) {
        for(Binding binding : bindings) {
            gfx.setPaint(FlatColor.BLACK);
            Bounds sbounds = binding.getSource().getInputBounds();
            Bounds tbounds = binding.getTarget().getInputBounds();
            gfx.drawLine(
                    binding.getSource().getTranslateX() + sbounds.getCenterX(),
                    binding.getSource().getTranslateY() + sbounds.getCenterY(),
                    binding.getTarget().getTranslateX() + tbounds.getCenterX(),
                    binding.getTarget().getTranslateY() + tbounds.getCenterY()
            );
        }
    }

    private void drawActiveBindingLineOverlay(GFX gfx) {
        if(startDragPoint != null && currentDragPoint != null && dragging) {
            gfx.setPaint(FlatColor.RED);
            gfx.drawLine(startDragPoint.getX(),startDragPoint.getY(),
                    currentDragPoint.getX(),currentDragPoint.getY());
        }
    }

    private void drawSelectionOverlay(GFX gfx) {
        if(getSelection().getSize() < 1) return;
        Bounds b = MathUtils.unionBounds(getSelection());
        gfx.setPaint(FlatColor.fromRGBInts(100,100,100));
        gfx.drawRect(b.getX(),b.getY(),b.getWidth(),b.getHeight());
        gfx.setPaint(FlatColor.fromRGBInts(200,200,200));
        gfx.drawRect(b.getX()-1,b.getY()-1,b.getWidth()+2,b.getHeight()+2);
    }

    private void drawTarget(GFX gfx, TreeNode<SketchNode> target) {
        for(SketchNode n : this.target.children()) {
            drawNode(gfx, n);
        }
    }

    private void drawNode(GFX gfx, SketchNode n) {
        gfx.translate(n.getTranslateX(),n.getTranslateY());
        n.draw(gfx);
        for(SketchNode c : n.children()) {
            drawNode(gfx,c);
        }
        gfx.translate(-n.getTranslateX(),-n.getTranslateY());
    }


    SketchNode findNode(Point2D pt) {
        for(SketchNode n : this.target.children()) {
            if(n.contains(pt)) return n;
        }
        return null;
    }



    public void setTarget(TreeNode<SketchNode> level) {
        this.target = level;
    }

    public void addToSelection(SketchNode node) {
        this.selection.add(node);
        this.getPropsView().setSelection(node);
        setDrawingDirty();
    }
    public void clearSelection() {
        if(popup != null && popup.isVisible()) {
            popup.setVisible(false);
        }
        if(popup2 != null && popup2.isVisible()) {
            popup2.setVisible(false);
        }
        this.selection.clear();
        setDrawingDirty();
    }

    public TreeNode<SketchNode> getSelection() {
        return selection;
    }

    public void setPropsView(PropsView propsView) {
        this.propsView = propsView;
    }

    public PropsView getPropsView() {
        return propsView;
    }

    public List<Binding> getBindings() {
        return bindings;
    }

    public void redraw() {
        setDrawingDirty();
    }

    public TreeNode<SketchNode> getTarget() {
        return target;
    }

    public boolean isFocused() {
        return true;
    }

    List<Handle> handles = new ArrayList<Handle>();
    public void rebuildHandles() {
        this.handles.clear();
        for(SketchNode node : this.getSelection().children()) {
            if(node instanceof ResizableRectNode) {
                handles.add(new ResizeHandle((ResizableRectNode) node));
            }
        }
    }

    public Handle findHandle(Point2D pointInNodeCoords) {
        for(Handle handle : this.handles) {
            if(handle.contains(pointInNodeCoords)) return handle;
        }
        return null;
    }
}
