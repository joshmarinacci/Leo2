package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Focusable;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.util.u;

public class Canvas extends Control implements Focusable, ScrollPane.ScrollingAware {
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
    private TreeNode<SketchNode> masterRoot;
    private TreeNode<SketchNode> editRoot;
    private ScrollPane scrollPane;


    public void setMasterRoot(TreeNode<SketchNode> masterRoot) {
        this.masterRoot = masterRoot;
        setDrawingDirty();
    }

    public void setEditRoot(TreeNode<SketchNode> editRoot) {
        this.editRoot = editRoot;
        setDrawingDirty();
    }

    public TreeNode<SketchNode> getEditRoot() {
        return editRoot;
    }

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
        SketchNode node = getSelection().get(0);
        if(node instanceof DynamicNode) {
            BindingUtils.populateWithBindablePropertiesDynamic(popup, (DynamicNode) node, this);
        } else {
            BindingUtils.populateWithBindablePropertiesRegular(popup, node, this);
        }
        popup.setTranslateX(pt.getX() + this.getTranslateX());
        popup.setTranslateY(pt.getY() + this.getTranslateY());
    }


    void showTargetPopup(final SketchNode node, MouseEvent mouseEvent) {
        if(popup2 == null) {
            popup2 = new BindingBox();
            this.getParent().getStage().getPopupLayer().add(popup2);
        }
        if(popup2 != null) {
            popup2.reset();
            popup2.setVisible(true);
        }
        if(node instanceof DynamicNode) {
            BindingUtils.populateWithTargetPropertiesDynamic(popup2, (DynamicNode)node, currentBinding, this);
        } else {
            BindingUtils.populateWithTargetPropertiesRegular(popup2, node, currentBinding, this);

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
        drawDocumentBounds(gfx, masterRoot);
        drawMasterRoot(gfx, masterRoot);
        drawBindings(gfx);
        drawSelectionOverlay(gfx);
        drawActiveBindingLineOverlay(gfx);
        drawHandles(gfx);
        drawGroupEditOverlay(gfx);
        //drawDebug(gfx);
    }

    private void drawDocumentBounds(GFX gfx, TreeNode<SketchNode> masterRoot) {
        gfx.setPaint(FlatColor.WHITE);
        gfx.fillRect(0,0,getWidth(),getHeight());
        if(masterRoot instanceof Layer) {
            gfx.setPaint(FlatColor.GRAY);
            gfx.drawRect(0,0,600,400);
        }
    }


    private void drawMasterRoot(GFX gfx, TreeNode<SketchNode> root) {
        if(root == null) return;
        for(SketchNode n : root.children()) {
            drawNode(gfx, n);
        }
    }

    private void drawNode(GFX gfx, SketchNode n) {
        gfx.translate(n.getTranslateX(), n.getTranslateY());
        n.draw(gfx);
        for(SketchNode c : n.children()) {
            drawNode(gfx,c);
        }
        gfx.translate(-n.getTranslateX(), -n.getTranslateY());
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

    private void drawSelectionOverlay(GFX gfx) {
        if(getSelection().getSize() < 1) return;
        Point2D pt = new Point2D.Double(0,0);
        if(getEditRoot() instanceof SketchNode) {
            SketchNode sn = (SketchNode) getEditRoot();
            pt = MathUtils.transform(pt,sn.getTranslateX(),sn.getTranslateY());
        }
        Bounds b = MathUtils.unionBounds(getSelection());
        b = MathUtils.transform(b,pt);
        gfx.setPaint(FlatColor.fromRGBInts(100,100,100));
        gfx.drawRect(b.getX(),b.getY(),b.getWidth(),b.getHeight());
        gfx.setPaint(FlatColor.fromRGBInts(200,200,200));
        gfx.drawRect(b.getX() - 1, b.getY() - 1, b.getWidth() + 2, b.getHeight() + 2);
    }


    private void drawHandles(GFX gfx) {
        Point2D pt = new Point2D.Double(0,0);
        if(getEditRoot() instanceof SketchNode) {
            SketchNode sn = (SketchNode) getEditRoot();
            pt = MathUtils.transform(pt,sn.getTranslateX(),sn.getTranslateY());
        }
        gfx.translate(pt.getX(),pt.getY());
        for(Handle handle : this.handles) {
            handle.draw(gfx);
        }
        gfx.translate(-pt.getX(),-pt.getY());
    }


    private void drawActiveBindingLineOverlay(GFX gfx) {
        if(startDragPoint != null && currentDragPoint != null && dragging) {
            gfx.setPaint(FlatColor.RED);
            gfx.drawLine(startDragPoint.getX(),startDragPoint.getY(),
                    currentDragPoint.getX(),currentDragPoint.getY());
        }
    }


    private void drawGroupEditOverlay(GFX gfx) {
        if(editRoot != masterRoot && editRoot instanceof SketchNode) {
            SketchNode root = (SketchNode) editRoot;
            Bounds bounds = root.getInputBounds();
            bounds = MathUtils.transform(bounds,root.getTranslateX(),root.getTranslateY());
            gfx.setPaint(FlatColor.hsb(0,1,1,0.4));
            gfx.fillRect(0,0,bounds.getX(),getHeight());
            gfx.fillRect(bounds.getX2(),0,getWidth()-bounds.getX2(),getHeight());
            gfx.fillRect(bounds.getX(),0,bounds.getWidth(),bounds.getY());
            gfx.fillRect(bounds.getX(),bounds.getY2(),bounds.getWidth(),getHeight()-bounds.getY2());
        }
    }

    private void drawDebug(GFX gfx) {
        gfx.setPaint(FlatColor.hsb(0, 0, 0.7));
        gfx.fillRect(0, 0, 400, 20);
        gfx.setPaint(FlatColor.BLACK);
        gfx.drawText("edit root: " + getEditRoot().getClass().getName(), Font.DEFAULT, 5, 15);
    }


    SketchNode findNode(Point2D pt) {
        for(SketchNode n : this.editRoot.reverseChildren()) {
            if(n.contains(pt)) return n;
        }
        return null;
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
        this.handles.clear();
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
            if(node instanceof DynamicNode) {
                DynamicNode dnode = (DynamicNode) node;
                if(dnode.isResizable()) {
                    handles.add(new DynamicResizeHandle((DynamicNode) node));
                }
            }
        }
    }

    public Handle findHandle(Point2D pointInNodeCoords) {
        for(Handle handle : this.handles) {
            if(handle.contains(pointInNodeCoords)) return handle;
        }
        return null;
    }

    private Stack<TreeNode<SketchNode>> editStack = new Stack<TreeNode<SketchNode>>();

    public void navigateInto(SketchNode node) {
        editStack.push(getEditRoot());
        clearSelection();
        setEditRoot(node);
        redraw();
    }

    public void navigateOutof() {
        setEditRoot(editStack.pop());
        clearSelection();
        redraw();
    }

    public Point2D toEditRootCoords(Point2D pointInNodeCoords) {
        if(this.editRoot == this.masterRoot) return pointInNodeCoords;
        if(this.editRoot instanceof SketchNode) {
            SketchNode node = (SketchNode) editRoot;
            return MathUtils.transform(pointInNodeCoords,
                    -node.getTranslateX(),-node.getTranslateY());
        }
        u.p("possible error in toEditRootCoords. shouldn't get here");
        return  pointInNodeCoords;
    }



    /* ======== scrolling aware implementation ========== */

    public double getFullWidth(double w, double h) {
        return 600;
    }

    public double getFullHeight(double v, double v2) {
        return 400;
    }

    public void setScrollX(double v) {
    }

    public void setScrollY(double v) {
    }

    public void setScrollParent(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }
}
