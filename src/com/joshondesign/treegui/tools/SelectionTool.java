package com.joshondesign.treegui.tools;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.CanvasTool;
import com.joshondesign.treegui.Handle;
import com.joshondesign.treegui.Mode;
import com.joshondesign.treegui.docmodel.Group;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.aminojava.DocumentActions;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminolang.DynamicGroup;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.util.u;

public class SelectionTool extends CanvasTool {
    private final Canvas canvas;
    private Handle activeHandle;
    private final SketchDocument document;
    private final Mode mode;
    private boolean moving;
    public long lastClick;
    private Point2D startPoint;
    public double startTX;
    public double startTY;
    private long lastMove;
    private Thread timerThread;
    private boolean timerGoing = false;
    private final SnappingManager snappingmanager;
    private SnappingManager.Snapper currentVSnapper;
    private double currentVPoint;
    private SnappingManager.Snapper currentHSnapper;
    private double currentHPoint;

    public SelectionTool(final Canvas canvas, final SketchDocument document, Mode mode) {
        this.canvas = canvas;
        this.document = document;
        this.mode = mode;


        EventBus.getSystem().addListener(canvas, KeyEvent.KeyPressed, new Callback<KeyEvent>() {
            public void call(KeyEvent keyEvent) throws Exception {
                if(keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_DELETE || keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_BACKSPACE) {
                    DocumentActions.deleteSelection(document);
                }
                if(keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_G) {
                    if(keyEvent.isSystemShortcut()) {
                        if(keyEvent.isShiftPressed()) {
                            ungroupSelection();
                        } else {
                            groupSelection();
                        }
                    }
                }
                if(keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_LEFT_ARROW) { translateSelection(-1,0); }
                if(keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_RIGHT_ARROW) { translateSelection(1,0); }
                if(keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_UP_ARROW) { translateSelection(0,-1); }
                if(keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_DOWN_ARROW) { translateSelection(0,1); }
            }
        });
        snappingmanager = new SnappingManager();
    }


    @Override
    public void pressed(Canvas.CanvasMouseEvent event) {
        Core.getShared().getFocusManager().setFocusedNode(event.canvas);
        long oldClick = lastClick;
        lastClick = System.currentTimeMillis();

        if(lastClick-oldClick < 250) {
            SketchNode node = event.canvas.findNode(event.pt);
            if(node == null) {
                canvas.navigateOutof();
                return;
            }
            if(node.isContainer()) {
                canvas.navigateInto(node);
            }
        } else {
            Handle handle = event.canvas.findHandle(event.pt);
            if(handle != null) {
                startDragHandle(handle,event.mouseEvent);
                return;
            }
            SketchNode node = event.canvas.findNode(event.pt);
            if(!event.mouseEvent.isShiftPressed()) {
                document.getSelection().clear();
            }
            if(node == null) return;
            addToSelection(node);
            startDragGesture(event.pt);
        }
    }

    @Override
    public void dragged(Canvas.CanvasMouseEvent event) {
        if(activeHandle != null) {
            continueDragHandle(event.mouseEvent,event.pt);
            return;
        }
        continueDragGesture(event.pt);
    }

    @Override
    public void released(Canvas.CanvasMouseEvent event) {
        event.canvas.recalcBounds();
        if(activeHandle != null) {
            endDragHandle(event.mouseEvent,event.pt);
            return;
        }
        endDragGesture(event.pt);
    }

    private void startDragHandle(Handle handle, MouseEvent mouseEvent) {
        activeHandle = handle;
        handle.startDrag(mouseEvent);
    }

    private void continueDragHandle(MouseEvent mouseEvent, Point2D pt) {
        activeHandle.drag(mouseEvent, pt);
        currentVSnapper = null;
        currentHSnapper = null;
        SketchNode target = activeHandle.getNode();
        Bounds b = target.getInputBounds();
        Point2D pt2 = new Point2D.Double(pt.getX()-b.getWidth(), pt.getY()-b.getHeight());
        /*
        boolean vSnapped = false;
        for(SnappingManager.Snapper snapper : snappingmanager.getVSnappers()) {
            if(snapper.canSnap(pt2,document,target)) {
                double tempx = target.getTranslateX();
                currentVPoint = snapper.snap(pt2,document,target);
                target.setTranslateX(tempx);
                target.setWidth(currentVPoint-target.getTranslateX());
                vSnapped = true;
                currentVSnapper = snapper;
                break;
            }
        }
        boolean hSnapped = false;
        for(SnappingManager.Snapper snapper : snappingmanager.getHSnappers()) {
            if(snapper.canSnap(pt2,document,target)) {
                double temp = target.getTranslateY();
                currentHPoint = snapper.snap(pt2,document,target);
                target.setTranslateY(temp);
                target.setHeight(currentHPoint-target.getTranslateY());
                hSnapped = true;
                currentHSnapper = snapper;
                break;
            }
        }*/
        canvas.redraw();
    }

    private void endDragHandle(MouseEvent mouseEvent, Point2D pt) {
        activeHandle.endDrag(mouseEvent,pt);
        activeHandle = null;
        currentVSnapper = null;
        currentHSnapper = null;
    }

    private void addToSelection(SketchNode node) {
        document.getSelection().add(node);
        canvas.rebuildHandles();
    }

    private void ungroupSelection() {
        if(document.getSelection().getSize() != 1) return;
        SketchNode node = document.getSelection().get(0);
        if(! (node instanceof Group)) return;

        Group group = (Group) node;
        List<SketchNode> toMove = new ArrayList<SketchNode>();
        for(SketchNode child: group.children()) {
            toMove.add(child);
        }
        document.getSelection().clear();
        for(SketchNode child: toMove) {
            group.remove(child);
            canvas.getEditRoot().add(child);
            document.getSelection().add(child);
        }
        canvas.getEditRoot().remove(group);
    }

    private void groupSelection() {
        if(document.getSelection().getSize() > 1) {
            SketchNode group = mode.createEmptyGroup();
            if(group == null) {
                u.p("WARNING. Mode returned a null group");
                return;
            }
            for(SketchNode node : document.getSelection().children()) {
                document.findParent(node).remove(node);
                group.add(node);
            }
            if(group instanceof DynamicGroup) {
                ((DynamicGroup)group).normalize();
            }
            canvas.getEditRoot().add(group);
            document.getSelection().clear();
            document.getSelection().add(group);
        }
    }

    private void translateSelection(double x, double y) {
        for(SketchNode node : document.getSelection().children()) {
            node.setTranslateX(node.getTranslateX()+x);
            node.setTranslateY(node.getTranslateY()+y);
        }
    }

    private void startDragGesture(Point2D pt) {
        if(document.getSelection().getSize() < 1) return;
        startPoint = pt;
        startTX = document.getSelection().get(0).getTranslateX();
        startTY = document.getSelection().get(0).getTranslateY();
        moving = true;
    }

    private void continueDragGesture(Point2D pt) {
        currentVSnapper = null;
        currentHSnapper = null;
        if(document.getSelection().getSize() < 1) return;
        startTimeout();
        double tx = startTX + (pt.getX() - startPoint.getX());
        double ty = startTY + (pt.getY() - startPoint.getY());

        //don't move locked nodes
        SketchNode target = document.getSelection().get(0);
        if(target instanceof DynamicNode) {
            if(((DynamicNode) target).isPositionLocked()) return;
        }

        //check all of the snappers first
        Point2D pt2 = new Point2D.Double(tx,ty);
        boolean vSnapped = false;
        for(SnappingManager.Snapper snapper : snappingmanager.getVSnappers()) {
            if(snapper.canSnap(pt2,document,target)) {
                currentVPoint = snapper.snap(pt2,document,target);
                vSnapped = true;
                currentVSnapper = snapper;
                break;
            }
        }

        boolean hSnapped = false;
        for(SnappingManager.Snapper snapper : snappingmanager.getHSnappers()) {
            if(snapper.canSnap(pt2,document,target)) {
                currentHPoint = snapper.snap(pt2,document,target);
                hSnapped = true;
                currentHSnapper = snapper;
                break;
            }
        }

        if(!vSnapped) {
            target.setTranslateX(tx);
        }
        if(!hSnapped) {
            target.setTranslateY(ty);
        }
        canvas.redraw();
    }

    private void stopTimeout() {
        timerGoing = false;
    }

    private void startTimeout() {
        if(timerThread == null) {
            timerThread = new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            Thread.sleep(100);
                            if(timerGoing && System.currentTimeMillis()-lastMove > 1000) {
                                u.p("hit the timerThread");
                                //get back on the main thread
                                Core.getShared().defer(new Runnable() {
                                    public void run() {
                                        dragPaused();
                                    }
                                });
                                timerGoing = false;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            timerThread.start();
        }
        lastMove = System.currentTimeMillis();
        timerGoing = true;
    }

    private void dragPaused() {
        //check the selection
        SketchNode node = document.getSelection().get(0);
        Point2D pt = new Point2D.Double(node.getTranslateX(),node.getTranslateY());
        SketchNode under = canvas.findNodeSkipping(pt, node);
        if(under != null && under.isContainer()) {
            canvas.navigateInto(under);
            node.getParent().remove(node);
            under.add(node);
            document.getSelection().clear().add(node);
            return;
        }

        if(under == null && canvas.getEditRoot() != canvas.getMasterRoot()) {
            if(canvas.getEditRoot() instanceof SketchNode) {
                SketchNode root = (SketchNode) canvas.getEditRoot();
                if(!root.getInputBounds().contains(pt)) {
                    canvas.navigateOutof();
                    node.getParent().remove(node);
                    TreeNode<SketchNode> eroot = canvas.getEditRoot();
                    eroot.add(node);
                    document.getSelection().clear().add(node);
                }
            }
        }
    }

    private void endDragGesture(Point2D pt) {
        stopTimeout();
        moving = false;
        currentVSnapper = null;
        currentHSnapper = null;
        canvas.redraw();
    }

    @Override
    public void drawOverlay(GFX gfx) {
        if(moving && document.getSelection().getSize() > 0) {

            double tx = document.getSelection().get(0).getTranslateX();
            double ty = document.getSelection().get(0).getTranslateY();
            Bounds bounds = document.getSelection().get(0).getInputBounds();

            gfx.setPaint(FlatColor.GRAY);
            gfx.translate(tx + bounds.getX2() + 10, ty + bounds.getCenterY() - 25 / 2);
            gfx.fillRoundRect(0,0,80, 30, 5, 5);
            gfx.setPaint(FlatColor.BLACK);
            gfx.drawText(tx + ", " + ty, Font.DEFAULT, 5, 15);
            gfx.translate(-tx - bounds.getX2() - 10, -ty - bounds.getCenterY() + 25 / 2);
        }

        if(currentVSnapper != null) {
            currentVSnapper.drawSnap(gfx, currentVPoint);
        }
        if(currentHSnapper != null) {
            currentHSnapper.drawSnap(gfx, currentHPoint);
        }
    }
}
