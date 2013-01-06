package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Group;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
            private Stack<TreeNode<SketchNode>> editStack = new Stack<TreeNode<SketchNode>>();

            public void call(MouseEvent mouseEvent) throws Exception {
                if (mouseEvent.getType() == MouseEvent.OpenContextMenu && canvas.getSelection() != null) {
                    canvas.showBindingMenu(mouseEvent.getPointInNodeCoords(canvas));
                }
                if (mouseEvent.getType() == MouseEvent.MousePressed) {
                    Core.getShared().getFocusManager().setFocusedNode(canvas);
                    long oldClick = lastClick;
                    lastClick = System.currentTimeMillis();
                    if(lastClick-oldClick < 250) {
                        SketchNode node = canvas.findNode(mouseEvent.getPointInNodeCoords(canvas));
                        if(node == null) {
                            navigateUp();
                        }
                        if(node instanceof Group) {
                            navigateDown(node);
                        }
                    } else {
                        Handle handle = canvas.findHandle(mouseEvent.getPointInNodeCoords(canvas));
                        if(handle != null) {
                            startDragHandle(handle,mouseEvent);
                            return;
                        }
                        SketchNode node = canvas.findNode(mouseEvent.getPointInNodeCoords(canvas));
                        if(!mouseEvent.isShiftPressed()) {
                            canvas.clearSelection();
                        }
                        if(node == null) return;
                        addToSelection(node);
                        startDragGesture(mouseEvent);
                    }
                }
                if(mouseEvent.getType() == MouseEvent.MouseDragged && canvas.getSelection() != null) {
                    if(activeHandle != null) {
                        continueDragHandle(mouseEvent);
                        return;
                    }
                    continueDragGesture(mouseEvent);
                }
                if(mouseEvent.getType() == MouseEvent.MouseReleased) {
                    if(activeHandle != null) {
                        endDragHandle(mouseEvent);
                    }
                }
            }

            private void startDragGesture(MouseEvent mouseEvent) {
                if(canvas.getSelection().getSize() < 1) return;
                Point2D pt = mouseEvent.getPointInNodeCoords(canvas);
                startPoint = pt;
                startTX = canvas.getSelection().get(0).getTranslateX();
                startTY = canvas.getSelection().get(0).getTranslateY();
            }

            private void continueDragGesture(MouseEvent mouseEvent) {
                if(canvas.getSelection().getSize() < 1) return;
                Point2D pt = mouseEvent.getPointInNodeCoords(canvas);
                canvas.getSelection().get(0).setTranslateX(startTX + (pt.getX() - startPoint.getX()));
                canvas.getSelection().get(0).setTranslateY(startTY + (pt.getY() - startPoint.getY()));
                canvas.redraw();
            }

            private void navigateDown(SketchNode node) {
                canvas.clearSelection();
                editStack.push(canvas.getTarget());
                canvas.setTarget(node);
                canvas.redraw();
            }
            private void navigateUp() {
                if(canvas.getTarget() instanceof  Group) {
                    canvas.setTarget(editStack.pop());
                    canvas.clearSelection();
                    canvas.redraw();
                }
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

    private void continueDragHandle(MouseEvent mouseEvent) {
        activeHandle.drag(mouseEvent, mouseEvent.getPointInNodeCoords(canvas));
        canvas.redraw();
    }

    private void endDragHandle(MouseEvent mouseEvent) {
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
            canvas.getTarget().remove(child);
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
            canvas.getTarget().add(child);
            canvas.addToSelection(child);
        }
        canvas.getTarget().remove(group);
    }

    private void groupSelection() {
        if(canvas.getSelection().getSize() > 1) {
            Group group = new Group();
            for(SketchNode node : canvas.getSelection().children()) {
                canvas.getTarget().remove(node);
                group.add(node);
            }
            canvas.getTarget().add(group);
            canvas.clearSelection();
            canvas.addToSelection(group);
        }
    }
}
