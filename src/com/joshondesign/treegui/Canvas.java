package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.*;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.tools.SelectionTool;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.ContextMenu;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Focusable;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.util.u;

public class Canvas extends Control implements Focusable, ScrollPane.ScrollingAware {
    BindingBox popup;
    BindingBox popup2;
    Point2D startDragPoint;
    boolean dragging;
    Point2D currentDragPoint;
    Binding currentBinding;
    private TreeNode<SketchNode> masterRoot;
    private TreeNode<SketchNode> editRoot;
    private ScrollPane scrollPane;
    private SketchDocument document;
    private double scrollX;
    private double scrollY;
    private CanvasTool currentTool;
    private Bounds maxBounds;
    private Bounds baseBounds = new Bounds(-100,-100,600+200,400+200);
    private Bounds totalBounds = new Bounds(-100,-100,600+200,400+200);
    private boolean boundsRecalcEnabled = false;
    private Mode mode;


    public void setMasterRoot(final TreeNode<SketchNode> masterRoot) {
        this.masterRoot = masterRoot;
        masterRoot.addListener(new TreeNode.TreeListener<SketchNode>() {
            public void added(SketchNode node) {
                setDrawingDirty();
            }

            public void removed(SketchNode node) {
                setDrawingDirty();
            }

            public void modified(SketchNode node) {
                setDrawingDirty();
                if(!boundsRecalcEnabled) return;
                recalcBounds();
            }

            public void selfModified(TreeNode self) {
                setDrawingDirty();
                if(!boundsRecalcEnabled) return;
                recalcBounds();
            }
        });
        setDrawingDirty();
    }

    public void recalcBounds() {
        Bounds tmp = MathUtils.unionBounds(masterRoot);
        maxBounds = new Bounds(tmp.getX()-100,tmp.getY()-100,tmp.getWidth()+200,tmp.getHeight()+200);
        //maxBounds = tmp;
        if (maxBounds.getX2() > baseBounds.getX2()
                || maxBounds.getY2() > baseBounds.getY2()
                || maxBounds.getX() < baseBounds.getX()
                || maxBounds.getY() < baseBounds.getY()
                ) {
            totalBounds = maxBounds.union(baseBounds);
            setLayoutDirty();
            setDrawingDirty();
        } else {
            totalBounds = baseBounds;
        }
    }

    public void setEditRoot(TreeNode<SketchNode> editRoot) {
        this.editRoot = editRoot;
        scanForMirrors();
        setDrawingDirty();
    }

    private void scanForMirrors() {
        for(SketchNode node : editRoot.children()) {
            if(node instanceof DynamicNode) {
                DynamicNode dnode = (DynamicNode) node;
                if(dnode.isMirror()) {
                    u.p("refreshing the mirror");
                    dnode.refreshMirror(document);
                }
            }
        }

    }

    public TreeNode<SketchNode> getEditRoot() {
        return editRoot;
    }

    public Canvas() {

        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, new Callback<MouseEvent>(){
            public void call(MouseEvent mouseEvent) throws Exception {
                if(currentTool == null) return;
                Point2D pt = toEditRootCoords(mouseEvent.getPointInNodeCoords(Canvas.this));
                if (mouseEvent.getType() == MouseEvent.OpenContextMenu && document.getSelection() != null) {
                    if(mouseEvent.isAltPressed()) {
                        showBindingMenu(mouseEvent.getPointInNodeCoords(Canvas.this));
                    } else {
                        showContextMenu(mouseEvent.getPointInNodeCoords(Canvas.this));
                    }
                    return;
                }

                if (mouseEvent.getType() == MouseEvent.MousePressed) {
                    currentTool.pressed(new CanvasMouseEvent(Canvas.this, pt, mouseEvent));
                }
                if(mouseEvent.getType() == MouseEvent.MouseDragged) {
                    currentTool.dragged(new CanvasMouseEvent(Canvas.this, pt, mouseEvent));
                }
                if(mouseEvent.getType() == MouseEvent.MouseReleased) {
                    currentTool.released(new CanvasMouseEvent(Canvas.this, pt, mouseEvent));
                }
            }
        });

    }

    public void setBoundsRecalcEnabled(boolean boundsRecalcEnabled) {
        this.boundsRecalcEnabled = boundsRecalcEnabled;
    }

    public boolean isBoundsRecalcEnabled() {
        return boundsRecalcEnabled;
    }

    public TreeNode<SketchNode> getMasterRoot() {
        return masterRoot;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }


    public static class CanvasMouseEvent {

        public final Point2D pt;
        public final MouseEvent mouseEvent;
        public Canvas canvas;

        public CanvasMouseEvent(Canvas canvas, Point2D pt, MouseEvent mouseEvent) {
            this.canvas = canvas;
            this.pt = pt;
            this.mouseEvent = mouseEvent;
        }
    }



    private void showContextMenu(Point2D pointInNodeCoords) {
        ContextMenu popup = new ContextMenu();
        List<AminoAction> actions = mode.getContextMenuActions(document, document.getSelection());
        popup.addActions(actions.toArray(new AminoAction[0]));
        popup.show(this,pointInNodeCoords);
    }

    void showBindingMenu(Point2D pt) {

        currentBinding = new Binding();
        currentBinding.setSource((DynamicNode) document.getSelection().get(0));

        if(popup == null) {
            popup = new BindingBox();
            this.getParent().getStage().getPopupLayer().add(popup);
        }
        if(popup != null) {
            popup.reset();
            popup.setVisible(true);
        }
        SketchNode node = document.getSelection().get(0);
        if(node instanceof DynamicNode) {
            BindingUtils.populateWithBindablePropertiesDynamic(popup, (DynamicNode) node, this, document);
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
            BindingUtils.populateWithTargetPropertiesDynamic(popup2, (DynamicNode) node, currentBinding, this, document);
        }
        Point2D pt = mouseEvent.getPointInNodeCoords(Canvas.this);
        popup2.setTranslateX(pt.getX() + this.getTranslateX());
        popup2.setTranslateY(pt.getY() + this.getTranslateY());
    }

    @Override
    public void doLayout() {
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


        double dx = scrollX-totalBounds.getX();
        double dy = scrollY-totalBounds.getY();
        gfx.translate(dx,dy);

        Size size = document.getMasterSize();
        Bounds docBounds = new Bounds(0,0,size.getWidth(Units.Pixels), size.getHeight(Units.Pixels));
        drawDocumentBounds(gfx, masterRoot, docBounds);
        drawMasterRoot(gfx, masterRoot);
        drawBindings(gfx);
        drawSelectionOverlay(gfx);
        drawActiveBindingLineOverlay(gfx);
        drawHandles(gfx);
        drawGroupEditOverlay(gfx);
        //drawDebug(gfx);

        drawToolOverlay(gfx);

        gfx.translate(-dx,-dy);

        //drawMaxBounds(gfx,maxBounds);
    }

    private void drawMaxBounds(GFX gfx, Bounds maxBounds) {
        if(maxBounds == null) return;
        gfx.setPaint(FlatColor.PURPLE);
        if(!masterRoot.children().iterator().hasNext()) return;
        gfx.drawRect(maxBounds.getX(),maxBounds.getY(),maxBounds.getWidth(),maxBounds.getHeight());
    }

    private void drawToolOverlay(GFX gfx) {
        Point2D pt = new Point2D.Double(0,0);
        pt = editRootToMasterRootCoords(pt, editRoot);
        gfx.translate(pt.getX(),pt.getY());
        if(currentTool != null) {
            currentTool.drawOverlay(gfx);
        }
        gfx.translate(-pt.getX(),-pt.getY());
    }

    private void drawDocumentBounds(GFX gfx, TreeNode<SketchNode> masterRoot, Bounds docBounds) {
        gfx.setPaint(FlatColor.WHITE);
        gfx.fillRect(0,0,docBounds.getWidth(), docBounds.getHeight());
        if(masterRoot instanceof Layer) {
            gfx.setPaint(FlatColor.GRAY);
            gfx.drawRect(0,0,docBounds.getWidth(), docBounds.getHeight());
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
        for(Binding binding : document.getBindings()) {
            Bounds sb = binding.getSource().getInputBounds();
            Bounds tb = binding.getTarget().getInputBounds();
            Path2D.Double pth = calculateBindingPath(sb,tb, binding);
            gfx.setPaint(FlatColor.BLACK);
            gfx.setStrokeWidth(4);
            gfx.drawPath(pth);
            gfx.setPaint(FlatColor.GRAY);
            gfx.setStrokeWidth(2);
            gfx.drawPath(pth);
            gfx.setStrokeWidth(1);
        }
    }

    private Path2D.Double calculateBindingPath(Bounds sb, Bounds tb, Binding binding) {
        Path2D.Double pth = new Path2D.Double();

        //bottom center of start
        Point2D start = toRootCoords(pt(sb.getX(), sb.getY()), binding.getSource());
        Point2D end =   toRootCoords(pt(tb.getX(), tb.getY()), binding.getTarget());

        Bounds a = new Bounds(start.getX(),start.getY(), sb.getWidth(), sb.getHeight());
        Bounds b = new Bounds(end.getX(),end.getY(),tb.getWidth(), tb.getHeight());
        //top center of end

        double offy = 60;


        if(a.getY() > b.getY2()) {
            Bounds c = a;
            a = b;
            b = c;
        }

        if(b.getX() - a.getX2() > b.getY() - a.getY2()) {
            pth.moveTo(a.getX2(),a.getCenterY());
            pth.curveTo(
                    a.getX2()+offy,a.getCenterY(),
                    b.getX()-offy,b.getCenterY(),
                    b.getX(),b.getCenterY()
            );
            return pth;
        } else {
            //swap
            pth.moveTo(a.getCenterX(), a.getY2());
            pth.curveTo(a.getCenterX(), a.getY2()+offy,
                    b.getCenterX(),b.getY()-offy,
                    b.getCenterX(),b.getY()
                    );
            return pth;
        }
    }

    private Point2D pt(double x, double y) {
        return new Point2D.Double(x,y);
    }

    private Point2D toRootCoords(Point2D start, SketchNode source) {
        start = new Point2D.Double(
                start.getX()+source.getTranslateX(),
                start.getY()+source.getTranslateY());
        if(source == getEditRoot()) {
            return start;
        }

        if(!(source.getParent() instanceof SketchNode)) {
            return start;
        }
        SketchNode parent = (SketchNode) source.getParent();
        return toRootCoords(start, parent);
    }

    private void drawSelectionOverlay(GFX gfx) {
        if(document.getSelection().getSize() < 1) return;
        Point2D pt = new Point2D.Double(0,0);
        pt = editRootToMasterRootCoords(pt, editRoot);
        gfx.translate(pt.getX(),pt.getY());
        Bounds b = MathUtils.unionBounds(document.getSelection());
        gfx.setPaint(FlatColor.fromRGBInts(100,100,100));
        gfx.drawRect(b.getX(),b.getY(),b.getWidth(),b.getHeight());
        gfx.setPaint(FlatColor.fromRGBInts(200,200,200));
        gfx.drawRect(b.getX() - 1, b.getY() - 1, b.getWidth() + 2, b.getHeight() + 2);
        gfx.translate(-pt.getX(),-pt.getY());
    }


    private void drawHandles(GFX gfx) {
        Point2D pt = new Point2D.Double(0,0);
        pt = editRootToMasterRootCoords(pt, editRoot);
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
            Point2D pt = new Point2D.Double(0,0);
            pt = editRootToMasterRootCoords(pt, editRoot);
            SketchNode root = (SketchNode) editRoot;
            Bounds bounds = root.getInputBounds();
            bounds = MathUtils.transform(bounds,pt.getX(),pt.getY());
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


    public SketchNode findNode(Point2D pt) {
        for(SketchNode n : this.editRoot.reverseChildren()) {
            if(n.contains(pt)) return n;
        }
        return null;
    }

    public SketchNode findNodeSkipping(Point2D pt, SketchNode node) {
        for(SketchNode n : this.editRoot.reverseChildren()) {
            if(n.contains(pt) && n != node) return n;
        }
        return null;
    }



    /*
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
    */


    public void redraw() {
        setDrawingDirty();
    }


    public boolean isFocused() {
        return true;
    }

    List<Handle> handles = new ArrayList<Handle>();
    public void rebuildHandles() {
        this.handles.clear();
        for(SketchNode node : document.getSelection().children()) {
            if(node instanceof ResizableRectNode) {
                handles.add(new ResizeHandle((ResizableRectNode) node));
            }
            if(node instanceof DynamicNode) {
                DynamicNode dnode = (DynamicNode) node;
                if(dnode.getResize() == Resize.Any) {
                    handles.add(new DynamicResizeHandle((DynamicNode) node, document));
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
        document.getSelection().clear();
        setEditRoot(node);
        redraw();
    }

    public void navigateOutof() {
        setEditRoot(editStack.pop());
        document.getSelection().clear();
        redraw();
    }

    public Point2D toEditRootCoords(Point2D pt) {
        pt = toEditRootCoords(pt, this.editRoot);
        return MathUtils.transform(pt, -scrollX+totalBounds.getX(), -scrollY+totalBounds.getY());
    }

    private Point2D toEditRootCoords(Point2D point, TreeNode<SketchNode> node) {
        if(node == masterRoot) return point;
        SketchNode sn = (SketchNode) node;
        point = MathUtils.transform(point, -sn.getTranslateX(), -sn.getTranslateY());
        return toEditRootCoords(point, sn.getParent());
    }

    private Point2D editRootToMasterRootCoords(Point2D point, TreeNode root) {
        if(root == masterRoot) return point;
        SketchNode sn = (SketchNode) root;
        point = MathUtils.transform(point, sn.getTranslateX(), sn.getTranslateY());
        return editRootToMasterRootCoords(point, sn.getParent());
    }




    /* ======== scrolling aware implementation ========== */

    public double getFullWidth(double w, double h) {
        return totalBounds.getWidth();
    }

    public double getFullHeight(double v, double v2) {
        return totalBounds.getHeight();
    }

    public void setScrollX(double v) {
        this.scrollX = v;
    }

    public void setScrollY(double v) {
        this.scrollY = v;
    }

    public void setScrollParent(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }





    /* ============ more stuff ================== */
    public void setDocument(SketchDocument document) {
        this.document = document;
        document.getSelection().addListener(new TreeNode.TreeListener() {
            public void added(Object node) {
                redraw();
            }

            public void removed(Object node) {
                redraw();
            }

            public void modified(Object node) {
                redraw();
            }

            public void selfModified(TreeNode self) {
                redraw();
            }
        });
        document.addListener(new TreeNode.TreeListener<Page>() {
            public void added(Page node) {       }
            public void removed(Page node) {     }
            public void modified(Page node) {    }
            public void selfModified(TreeNode self) {
                redraw();
            }
        });
    }

    public void setTool(SelectionTool selectionTool) {
        this.currentTool = selectionTool;
    }
}
