package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Group;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.VFlexBox;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 12/30/12
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Canvas extends Control {
    private TreeNode<SketchNode> target;
    private SketchNode selection;
    private PropsView propsView;
    private VFlexBox popup;
    private Point2D startDragPoint;
    private boolean dragging;
    private Point2D currentDragPoint;
    private VFlexBox popup2;
    private Binding currentBinding;
    private List<Binding> bindings = new ArrayList<Binding>();



    public Canvas() {
        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, new Callback<MouseEvent>() {
            private Point2D startPoint;
            public double startTX;
            public double startTY;
            public long lastClick;
            private Stack<TreeNode<SketchNode>> editStack = new Stack<TreeNode<SketchNode>>();

            public void call(MouseEvent mouseEvent) throws Exception {
                if (mouseEvent.getType() == MouseEvent.OpenContextMenu && getSelection() != null) {
                    showBindingMenu(mouseEvent.getPointInNodeCoords(Canvas.this));
                }
                if (mouseEvent.getType() == MouseEvent.MousePressed) {
                    long oldClick = lastClick;
                    lastClick = System.currentTimeMillis();
                    if(lastClick-oldClick < 250) {
                        SketchNode node = findNode(mouseEvent.getPointInNodeCoords(Canvas.this));
                        if(node == null) {
                            navigateUp();
                        }
                        if(node instanceof Group) {
                            navigateDown(node);
                        }
                    } else {
                        SketchNode node = findNode(mouseEvent.getPointInNodeCoords(Canvas.this));
                        setSelection(node);
                        if(node == null) return;
                        startDragGesture(mouseEvent);
                    }
                }
                if(mouseEvent.getType() == MouseEvent.MouseDragged && getSelection() != null) {
                    continueDragGesture(mouseEvent);
                }
            }

            private void continueDragGesture(MouseEvent mouseEvent) {
                Point2D pt = mouseEvent.getPointInNodeCoords(Canvas.this);
                getSelection().setTranslateX(startTX + (pt.getX()-startPoint.getX()));
                getSelection().setTranslateY(startTY + (pt.getY()-startPoint.getY()));
                setDrawingDirty();
            }

            private void startDragGesture(MouseEvent mouseEvent) {
                Point2D pt = mouseEvent.getPointInNodeCoords(Canvas.this);
                startPoint = pt;
                startTX = getSelection().getTranslateX();
                startTY = getSelection().getTranslateY();
            }
            private void navigateDown(SketchNode node) {
                setSelection(null);
                editStack.push(target);
                setTarget(node);
                setDrawingDirty();
            }
            private void navigateUp() {
                if(target instanceof  Group) {
                    setTarget(editStack.pop());
                    setSelection(null);
                    setDrawingDirty();
                }
            }
        });
    }



    private void showBindingMenu(Point2D pt) {

        currentBinding = new Binding();
        currentBinding.setSource(getSelection());

        if(popup == null) {
            popup = new VFlexBox();
            this.getParent().getStage().getPopupLayer().add(popup);
            popup.setFill(FlatColor.GRAY);
        }
        if(popup != null) {
            popup.removeAll();
            popup.setVisible(true);
        }
        populateWithBindableProperties(popup, getSelection());
        popup.setTranslateX(pt.getX() + this.getTranslateX());
        popup.setTranslateY(pt.getY() + this.getTranslateY());
    }

    private void populateWithBindableProperties(VFlexBox popup, SketchNode selection) {
        String[] props = {"translateX","translateY"};

        for(final String prop : props) {
            final Binding binding = findBinding(selection,prop);
            Button tx = new Button(prop) {
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
            };
            tx.setPrefWidth(100);
            Callback<MouseEvent> callback = new Callback<MouseEvent>() {
                public void call(MouseEvent mouseEvent) throws Exception {
                    if(mouseEvent.getType() == MouseEvent.MousePressed) {
                        startDragPoint = mouseEvent.getPointInNodeCoords(Canvas.this);
                        currentBinding.setSourceProperty(prop);
                    }
                    if(mouseEvent.getType() == MouseEvent.MouseDragged) {
                        dragging = true;
                        currentDragPoint = mouseEvent.getPointInNodeCoords(Canvas.this);
                        setDrawingDirty();
                    }
                    if(mouseEvent.getType() == MouseEvent.MouseReleased) {
                        dragging = false;
                        startDragPoint = null;
                        currentDragPoint = null;
                        setDrawingDirty();
                        SketchNode node = findNode(mouseEvent.getPointInNodeCoords(Canvas.this));
                        if(node == null) return;
                        showTargetPopup(node, mouseEvent);
                    }
                }
            };
            EventBus.getSystem().addListener(tx,MouseEvent.MouseAll, callback);
            popup.add(tx);
        }
    }

    private Binding findBinding(SketchNode selection, String prop) {
        for(Binding binding : bindings) {
            if(binding.getSource() == selection) {
                if(binding.getSourceProperty().equals(prop)) {
                    return binding;
                }
            }
        }
        return null;
    }

    private void showTargetPopup(final SketchNode node, MouseEvent mouseEvent) {
        if(popup2 == null) {
            popup2 = new VFlexBox();
            this.getParent().getStage().getPopupLayer().add(popup2);
            popup2.setFill(FlatColor.GRAY);
        }
        if(popup2 != null) {
            popup2.removeAll();
            popup2.setVisible(true);
        }
        Button tx = new Button("translateX");
        tx.onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                currentBinding.setTarget(node);
                currentBinding.setTargetProperty("translateX");
                bindings.add(currentBinding);
                currentBinding = null;
                popup2.setVisible(false);
                popup.setVisible(false);
            }
        });
        popup2.add(tx);
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
            gfx.drawLine(startDragPoint.getX(),startDragPoint.getY(), currentDragPoint.getX(),currentDragPoint.getY());
        }
    }

    private void drawSelectionOverlay(GFX gfx) {
        if(getSelection() == null) return;

        SketchNode s = getSelection();
        Bounds b = s.getInputBounds();
        gfx.translate(s.getTranslateX(),s.getTranslateY());

        gfx.setPaint(FlatColor.fromRGBInts(100,100,100));
        gfx.drawRect(b.getX(),b.getY(),b.getWidth(),b.getHeight());
        gfx.setPaint(FlatColor.fromRGBInts(200,200,200));
        gfx.drawRect(b.getX()-1,b.getY()-1,b.getWidth()+2,b.getHeight()+2);

        gfx.translate(-s.getTranslateX(), -s.getTranslateY());
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


    private SketchNode findNode(Point2D pt) {
        for(SketchNode n : this.target.children()) {
            if(n.contains(pt)) return n;
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }



    public void setTarget(TreeNode<SketchNode> level) {
        this.target = level;
    }

    public void setSelection(SketchNode selection) {
        this.selection = selection;
        this.getPropsView().setSelection(selection);
        setDrawingDirty();
    }

    public SketchNode getSelection() {
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
}
