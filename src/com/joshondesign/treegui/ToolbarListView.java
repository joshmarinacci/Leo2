package com.joshondesign.treegui;


import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.model.TreeNode;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.layout.HFlexBox;

public class ToolbarListView extends HFlexBox {
    private TreeNode<JAction> model;

    public ToolbarListView() {

    }

    @Override
    public void doPrefLayout() {
        super.doPrefLayout();
    }

    public void setModel(TreeNode<JAction> model) {
        this.model = model;
        this.removeAll();
        for(final JAction action : model.children()) {
            this.add(new Button(action.getShortName()).onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    action.execute();
                }
            }));
        }
    }

    public TreeNode getModel() {
        return model;
    }
}
