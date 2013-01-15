package com.joshondesign.treegui;

import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.leo2.SymbolRenderer;
import com.joshondesign.treegui.leo2.SymbolsDragHandler;
import com.joshondesign.treegui.leo2.TreeNodeListModel;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.amino.Rect;
import com.joshondesign.treegui.modes.aminojava.AminoJavaXMLExport;
import com.joshondesign.treegui.modes.aminojava.AminoJavaXMLImport;
import com.joshondesign.treegui.modes.aminojava.AminoParser;
import com.joshondesign.treegui.modes.aminojava.DocumentActions;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/13/13
 * Time: 6:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Leo2 {
    public static void main(String... args) throws Exception {
        Core.init();
        Core.getShared().defer(new Runnable() {
            public void run() {
                init();
            }
        });
    }

    private static void init() {
        try {
            //load up the new doc page
            File file = new File("start.xml");
            Doc xml = XMLParser.parse(file);
            Control root = (Control) AminoParser.parsePage(xml.root());
            root.setTranslateX(0);
            root.setTranslateY(0);

            //bind quit action
            Button quitButton = (Button) AminoParser.find("quitButton", root);
            quitButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    u.p("quitting");
                    System.exit(0);
                }
            });


            //bind new doc action
            Button newdocButton = (Button) AminoParser.find("newdocButton", root);
            newdocButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    doNewDoc(null);
                }
            });

            //edit self button
            ((Button)AminoParser.find("editselfButton",root)).onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    doNewDoc(new File("main.xml"));
                }
            });

            //create stage to match it's size
            Stage stage = Stage.createStage();
            double w = 600;
            double h = 400;
            if (root instanceof Control) {
                Control control = (Control) root;
                w = control.getPrefWidth();
                h = control.getPrefHeight();
            }
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setContent(root);
            //show the stage


            //extra stuff
            EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
                public void call(Event event) throws Exception {
                    System.exit(0);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doNewDoc(File docFile) throws Exception {
        //init modes
        TreeNode<Mode> modes = TreeGui.initModes();
        Mode mode = modes.get(1);

        //init window

        File file = new File("main.xml");
        Doc xml = XMLParser.parse(file);
        Control root = (Control) AminoParser.parsePage(xml.root());
        root.setTranslateX(0);
        root.setTranslateY(0);

        //create stage to match it's size
        Stage stage = Stage.createStage();
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.setContent(root);


        //get initial references
        final ListView symbolsView = (ListView) AminoParser.find("symbolsView", root);
        final Canvas canvasView = (Canvas) AminoParser.find("canvasView", root);
        final PropsView propsView = (PropsView) AminoParser.find("propsView", root);

        //init doc
        SketchDocument doc = null;
        if(docFile == null) {
            doc = initDoc();
        } else {
            doc = AminoJavaXMLImport.open(docFile, canvasView);
        }


        //setup the symbols view
        final TreeNode<SketchNode> symbols = mode.get(1);
        symbolsView.setModel(new TreeNodeListModel(symbols));
        symbolsView.setRenderer(new SymbolRenderer());
        EventBus.getSystem().addListener(symbolsView, MouseEvent.MouseAll,
                new SymbolsDragHandler(canvasView, symbolsView, doc));


        //setup the props view
        propsView.setPropFilter(new PropsView.PropFilter() {
            public boolean include(Object obj, String name) {
                return true;
            }
        });
        propsView.setSelection(doc.get(0).get(0).get(0));


        //set up the canvas
        canvasView.setMasterRoot(doc.get(0).get(0));
        canvasView.setEditRoot(doc.get(0).get(0));
        canvasView.setPropsView(propsView);


        //set up  the buttons
        ((Button) AminoParser.find("runButton", root)).onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
            }
        });
        final SketchDocument finalDoc = doc;
        ((Button) AminoParser.find("openButton", root)).onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                new AminoJavaXMLImport(canvasView, finalDoc).execute();
            }
        });
        ((Button) AminoParser.find("saveButton", root)).onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                new AminoJavaXMLExport.Save(canvasView, finalDoc).execute();
            }
        });
        ((Button) AminoParser.find("testButton", root)).onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
            }
        });
        ((Button) AminoParser.find("deleteButton", root)).onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                DocumentActions.deleteSelection(canvasView);
            }
        });


    }

    private static SketchDocument initDoc() {
        SketchDocument doc = new SketchDocument();
        Layer layer = new Layer();
        layer.add(new Rect().setFill(FlatColor.GREEN).setWidth(50).setHeight(50));
        Page page = new Page();
        page.add(layer);
        doc.add(page);
        return doc;
    }

}
