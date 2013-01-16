package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Group;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.modes.aminojava.DocumentActions;
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

public class SelectionTool extends CanvasTool {
    private final Canvas canvas;
    private Handle activeHandle;
    private final SketchDocument document;
    private final Mode mode;

    public SelectionTool(final Canvas canvas, final SketchDocument document, Mode mode) {
        this.canvas = canvas;
        this.document = document;
        this.mode = mode;

        EventBus.getSystem().addListener(canvas, MouseEvent.MouseAll, new MouseEventCallback(canvas, document));

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
            }
        });
    }

    private void startDragHandle(Handle handle, MouseEvent mouseEvent) {
        activeHandle = handle;
        handle.startDrag(mouseEvent);
    }

    private void continueDragHandle(MouseEvent mouseEvent, Point2D pt) {
        activeHandle.drag(mouseEvent, pt);
        canvas.redraw();
    }

    private void endDragHandle(MouseEvent mouseEvent, Point2D pt) {
        activeHandle.endDrag(mouseEvent,pt);
        activeHandle = null;
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
            Group group = new Group();
            for(SketchNode node : document.getSelection().children()) {
                document.findParent(node).remove(node);
                group.add(node);
            }
            canvas.getEditRoot().add(group);
            document.getSelection().clear();
            document.getSelection().add(group);
        }
    }

    private boolean moving;

    @Override
    public void drawOverlay(GFX gfx) {
        if(moving && document.getSelection().getSize() > 0) {

            double tx = document.getSelection().get(0).getTranslateX();
            double ty = document.getSelection().get(0).getTranslateY();
            Bounds bounds = document.getSelection().get(0).getInputBounds();

            gfx.setPaint(FlatColor.GRAY);
            gfx.translate(tx + bounds.getX2()+10, ty +bounds.getCenterY()-25/2);
            gfx.fillRoundRect(0,0,80, 30, 5, 5);
            gfx.setPaint(FlatColor.BLACK);
            gfx.drawText(tx + ", " + ty, Font.DEFAULT, 5,15);
            gfx.translate(-tx - bounds.getX2()-10, -ty -bounds.getCenterY()+25/2);
        }
    }

    private class MouseEventCallback implements Callback<MouseEvent> {
        private final Canvas canvas;
        private final SketchDocument document;
        private Point2D startPoint;
        public double startTX;
        public double startTY;
        public long lastClick;

        public MouseEventCallback(Canvas canvas, SketchDocument document) {
            this.canvas = canvas;
            this.document = document;
        }

        public void call(MouseEvent mouseEvent) throws Exception {
            Point2D pt = canvas.toEditRootCoords(mouseEvent.getPointInNodeCoords(canvas));
            if (mouseEvent.getType() == MouseEvent.OpenContextMenu && document.getSelection() != null) {
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
                        document.getSelection().clear();
                    }
                    if(node == null) return;
                    addToSelection(node);
                    startDragGesture(mouseEvent,pt);
                }
            }
            if(mouseEvent.getType() == MouseEvent.MouseDragged && document.getSelection() != null) {
                if(activeHandle != null) {
                    continueDragHandle(mouseEvent,pt);
                    return;
                }
                continueDragGesture(mouseEvent,pt);
            }
            if(mouseEvent.getType() == MouseEvent.MouseReleased) {
                if(activeHandle != null) {
                    endDragHandle(mouseEvent,pt);
                    return;
                }
                endDragGesture(mouseEvent, pt);
            }
        }

        private void startDragGesture(MouseEvent mouseEvent, Point2D pt) {
            if(document.getSelection().getSize() < 1) return;
            startPoint = pt;
            startTX = document.getSelection().get(0).getTranslateX();
            startTY = document.getSelection().get(0).getTranslateY();
            moving = true;
        }

        private void continueDragGesture(MouseEvent mouseEvent, Point2D pt) {
            if(document.getSelection().getSize() < 1) return;
            double tx = startTX + (pt.getX() - startPoint.getX());
            double ty = startTY + (pt.getY() - startPoint.getY());
            tx = Math.floor(tx/10)*10;
            ty = Math.floor(ty/10)*10;
            document.getSelection().get(0).setTranslateX(tx);
            document.getSelection().get(0).setTranslateY(ty);
            canvas.redraw();
        }

        private void endDragGesture(MouseEvent mouseEvent, Point2D pt) {
            moving = false;
            canvas.redraw();
        }

    }

}
