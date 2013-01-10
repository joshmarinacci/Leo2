package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Group;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.joshy.gfx.Core;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/4/13
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class SelectionTool extends CanvasTool {
    private final Canvas canvas;
    private Handle activeHandle;

    public SelectionTool(final Canvas canvas) {
        this.canvas = canvas;

        EventBus.getSystem().addListener(canvas, MouseEvent.MouseAll, new Callback<MouseEvent>() {
            private Point2D startPoint;
            public double startTX;
            public double startTY;
            public long lastClick;

            public void call(MouseEvent mouseEvent) throws Exception {
                Point2D pt = canvas.toEditRootCoords(mouseEvent.getPointInNodeCoords(canvas));
                if (mouseEvent.getType() == MouseEvent.OpenContextMenu && canvas.getSelection() != null) {
                    canvas.showBindingMenu(mouseEvent.getPointInNodeCoords(canvas));
                }
                if (mouseEvent.getType() == MouseEvent.MousePressed) {
                    Core.getShared().getFocusManager().setFocusedNode(canvas);
                    long oldClick = lastClick;
                    lastClick = System.currentTimeMillis();

                    if(lastClick-oldClick < 250) {
                        SketchNode node = canvas.findNode(pt);
                        if(node == null) {
                            canvas.navigateOutof();
                            return;
                        }
                        if(node.isContainer()) {
                            canvas.navigateInto(node);
                        }
                    } else {
                        Handle handle = canvas.findHandle(pt);
                        if(handle != null) {
                            startDragHandle(handle,mouseEvent);
                            return;
                        }
                        SketchNode node = canvas.findNode(pt);
                        if(!mouseEvent.isShiftPressed()) {
                            canvas.clearSelection();
                        }
                        if(node == null) return;
                        addToSelection(node);
                        startDragGesture(mouseEvent,pt);
                    }
                }
                if(mouseEvent.getType() == MouseEvent.MouseDragged && canvas.getSelection() != null) {
                    if(activeHandle != null) {
                        continueDragHandle(mouseEvent,pt);
                        return;
                    }
                    continueDragGesture(mouseEvent,pt);
                }
                if(mouseEvent.getType() == MouseEvent.MouseReleased) {
                    if(activeHandle != null) {
                        endDragHandle(mouseEvent,pt);
                    }
                }
            }

            private void startDragGesture(MouseEvent mouseEvent, Point2D pt) {
                if(canvas.getSelection().getSize() < 1) return;
                startPoint = pt;
                startTX = canvas.getSelection().get(0).getTranslateX();
                startTY = canvas.getSelection().get(0).getTranslateY();
            }

            private void continueDragGesture(MouseEvent mouseEvent, Point2D pt) {
                if(canvas.getSelection().getSize() < 1) return;
                double tx = startTX + (pt.getX() - startPoint.getX());
                double ty = startTY + (pt.getY() - startPoint.getY());
                tx = Math.floor(tx/10)*10;
                ty = Math.floor(ty/10)*10;
                canvas.getSelection().get(0).setTranslateX(tx);
                canvas.getSelection().get(0).setTranslateY(ty);
                canvas.redraw();
            }

            private void navigateDown(SketchNode node) {
            }
            private void navigateUp() {
            }
        });

        EventBus.getSystem().addListener(canvas, KeyEvent.KeyPressed, new Callback<KeyEvent>() {
            public void call(KeyEvent keyEvent) throws Exception {
                if(keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_DELETE || keyEvent.getKeyCode() == KeyEvent.KeyCode.KEY_BACKSPACE) {
                    u.p("deleting");
                    deleteSelection();
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
            }
        });
    }

    private void startDragHandle(Handle handle, MouseEvent mouseEvent) {
        activeHandle = handle;
    }

    private void continueDragHandle(MouseEvent mouseEvent, Point2D pt) {
        activeHandle.drag(mouseEvent, pt);
        canvas.redraw();
    }

    private void endDragHandle(MouseEvent mouseEvent, Point2D pt) {
        activeHandle = null;
    }


    private void addToSelection(SketchNode node) {
        canvas.addToSelection(node);
        canvas.rebuildHandles();
    }

    private void deleteSelection() {
        List<SketchNode> toMove = new ArrayList<SketchNode>();
        for(SketchNode child: canvas.getSelection().children()) {
            toMove.add(child);
        }
        for(SketchNode child : toMove){
            canvas.getEditRoot().remove(child);
        }
        canvas.clearSelection();
    }

    private void ungroupSelection() {
        if(canvas.getSelection().getSize() != 1) return;
        SketchNode node = canvas.getSelection().get(0);
        if(! (node instanceof Group)) return;

        Group group = (Group) node;
        List<SketchNode> toMove = new ArrayList<SketchNode>();
        for(SketchNode child: group.children()) {
            toMove.add(child);
        }
        canvas.clearSelection();
        for(SketchNode child: toMove) {
            group.remove(child);
            canvas.getEditRoot().add(child);
            canvas.addToSelection(child);
        }
        canvas.getEditRoot().remove(group);
    }

    private void groupSelection() {
        if(canvas.getSelection().getSize() > 1) {
            Group group = new Group();
            for(SketchNode node : canvas.getSelection().children()) {
                canvas.getEditRoot().remove(node);
                group.add(node);
            }
            canvas.getEditRoot().add(group);
            canvas.clearSelection();
            canvas.addToSelection(group);
        }
    }
}
