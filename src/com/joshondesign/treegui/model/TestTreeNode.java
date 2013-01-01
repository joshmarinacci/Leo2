package com.joshondesign.treegui.model;

import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.ResizableRectNode;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.layout.FlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestTreeNode {
    @Test
    public void basicAPITests() {
        //create a tree with a single node. size is 0.
        TreeNode node = new TreeNode();
        assertEquals(0,node.getSize());

        //create a tree with a single root and 3 children  with chaining
        TreeNode n2 = new TreeNode();
        n2.add(new TreeNode()).add(new TreeNode()).add(new TreeNode());
        assertEquals(3,n2.getSize());

        //root + 3 children with varargs
        TreeNode n3 = new TreeNode();
        n3.add(new TreeNode(),new TreeNode(), new TreeNode());
        assertEquals(3,n3.getSize());


        //tree w/ three levels
        TreeNode n4 = new TreeNode();
        n4.add(new TreeNode()).add(new TreeNode().add(new TreeNode()));
        assertEquals(n4.getSize(),2);
        assertEquals(n4.get(1).getSize(),1);




        //insertion and deletion
        TreeNode n6 = new TreeNode();
        TreeNode child1 = new TreeNode();
        n6.add(new TreeNode());
        assertEquals(n6.getSize(), 1);
        n6.add(child1);
        assertEquals(n6.getSize(), 2);
        n6.remove(0);
        assertEquals(n6.getSize(), 1);
        n6.remove(child1);
        assertEquals(n6.getSize(), 0);

    }

    @Test
    public void DocModel() {
        SketchDocument doc = new SketchDocument();
        doc.add(new Page(),new Page(),new Page());
        doc.get(0).add(new Layer());
        Layer layer = doc.get(0).get(0);
        layer.add(new ResizableRectNode(){
            @Override
            public void draw(GFX g) {

            }
        }.setWidth(100).setHeight(100).setTranslateX(100));
    }

    @Test
    public void iteration() {
        //iterate over list
        TreeNode<TreeNode> n5 = new TreeNode<TreeNode>();
        n5.add(new TreeNode(),new TreeNode(),new TreeNode());
        n5.add(new TreeNode().add(new TreeNode()).add(new TreeNode()));

        //go through current level of children. like a list
        int counter1 = 0;
        for(TreeNode child : n5.children()) {
            dummyop(child);
            counter1++;
        }
        assertEquals(counter1,4);

        //go through all children
        int counter2 = 0;
        for(TreeNode child : n5.inOrderTraversal()) {
            dummyop(child);
            counter2++;
        }
        assertEquals(counter2,7);

        //go through all children in reverse
        int counter3 = 0;
        for(TreeNode child : n5.reverseInOrderTraversal()) {
            dummyop(child);
            counter3++;
        }
        assertEquals(counter3,7);
    }

    @Test
    public void listeners() {
        //test listener for list item add/remove/modify
        TreeNode n7 = new TreeNode();
        final int[] addCounter1 = {0};
        final int[] removeCounter1 = {0};
        final int[] modifyCounter1 = {0};
        n7.addListener(new TreeNode.TreeListener() {
            public void added(TreeNode node) {
                addCounter1[0]++;
            }

            public void removed(TreeNode node) {
                removeCounter1[0]++;
            }

            public void modified(TreeNode node) {
                modifyCounter1[0]++;
            }
        });

        n7.add(new TreeNode());
        TreeNode child2 = new TreeNode();
        n7.add(child2);
        n7.add(new TreeNode());
        n7.add(new TreeNode());
        n7.remove(child2);
        n7.add(child2);
        n7.markModified(child2);
        n7.add(new TreeNode());
        n7.remove(child2);

        assertEquals(6,addCounter1[0]);
        assertEquals(2,removeCounter1[0]);
        assertEquals(1,modifyCounter1[0]);

    }

    @Test
    public void Filter() throws Exception {
        //create a filter wrapper. just returns a subset of a list level. verify results programmatically.

        TreeNode root = new TreeNode();
        root.add(new StringTreeNode("foo"));
        FilterTreeNode filter = new FilterTreeNode();
        assertEquals(filter.getSize(),1);
        root.add(new StringTreeNode("bar"), new StringTreeNode("foobar"));
        assertEquals(filter.getSize(), 3);

        //add filter to only show strings with foo in them
        filter.setFilter(new FilterTreeNode.Filter() {
            public boolean include(TreeNode node) {
                if (((StringTreeNode) node).getString().contains("foo")) return true;
                return true;
            }
        });
        assertEquals(filter.getSize(), 2);

        //add new nodes, only one will make it through the filter
        root.add(new StringTreeNode("bar"), new StringTreeNode("foobar"));
        assertEquals(filter.getSize(), 3);

        //remove the first, which is a foo. filter list should show one less
        root.remove(0);
        assertEquals(filter.getSize(), 2);

        //remove the first, which is now a bar. filter should be the same size
        root.remove(0);
        assertEquals(filter.getSize(),2);
    }


    public static void main(String ... args) {
        new TestTreeNode().ListView();
    }
    @Test
    public void ListView() {
        try {
            Core.init();
            ListViewImpl();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    //test updates to the list
    //test modifications to the sublists (should affect the list, for repaints)
    //test updates to the sublists (shouldn't affect the list)
    public void ListViewImpl() throws Exception {
        Core.getShared().defer(new Runnable() {
            public void run() {
                EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
                    public void call(Event event) throws Exception {
                        System.exit(0);
                    }
                });

                final TreeNode root = new TreeNode();
                root.add(new TreeNode());
                root.add(new TreeNode());
                TreeNodeListView view = new TreeNodeListView();
                view.setPrefWidth(200);
                view.setTreeNodeModel(root);
                Button add = new Button("add").onClicked(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        root.add(new TreeNode());
                        u.p("total tree count = " + Trees.getTotalTreeCount(root));
                    }
                });
                Button remove = new Button("remove").onClicked(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        root.remove(0);
                        u.p("total tree count = " + Trees.getTotalTreeCount(root));
                    }
                });
                Button addSublist = new Button("sublist add").onClicked(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        root.get(0).add(new TreeNode());
                        u.p("total tree count = " + Trees.getTotalTreeCount(root));
                    }
                });
                Button removeSublist = new Button("sublist remove").onClicked(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        root.get(0).remove(0);
                        u.p("total tree count = " + Trees.getTotalTreeCount(root));
                    }
                });
                Button modifyItem = new Button("modify item").onClicked(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        u.p("total tree count = " + Trees.getTotalTreeCount(root));
                    }
                });
                Button modifySublist = new Button("modify sublist").onClicked(new Callback<ActionEvent>() {
                    public void call(ActionEvent actionEvent) throws Exception {
                        u.p("total tree count = " + Trees.getTotalTreeCount(root));
                    }
                });

                Stage stage = Stage.createStage();
                stage.setContent(new VFlexBox().setBoxAlign(FlexBox.Align.Stretch)
                        .add(view, 1)
                        .add(add, 0)
                        .add(modifyItem, 0)
                        .add(remove, 0)
                        .add(addSublist, 0)
                        .add(modifySublist, 0)
                        .add(removeSublist, 0)
                );
            }
        });

    }

    private static class StringTreeNode extends TreeNode {
        private String string;
        public StringTreeNode(String foo) {
            this.string = foo;
        }
        public String getString() {
            return string;
        }
    }


    //test listener for whole tree. modify subnodes. prove that list listener isn't affected
    //create a listview wrapping a tree level
    //create a tree filter wraper. how does this work?
    //create a sorted wrapper. define sort criteria. verify sorting is preserved when contents are updated
    /*
        create list. just a node w/ N children
        create tree of strings
        create tree of ints
        create tree of objects with properties. simple node w/ x/y/visible
        iterate over tree, skipping non-visible items.
        create a tree. create list wrapper for one child. confirm add/delete updates.
        create two lists. create union wrapper. confirm updates
        filter list wrapper. confirm all updates work.
        filter tree wrapper
        sorted list wrapper
    */

    private static void dummyop(TreeNode child) {
        //To change body of created methods use File | Settings | File Templates.
    }
}
