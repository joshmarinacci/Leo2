package com.joshondesign.treegui;

import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.leo2.SymbolRenderer;
import com.joshondesign.treegui.leo2.SymbolsDragHandler;
import com.joshondesign.treegui.leo2.TreeNodeListModel;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.amino.AminoJSMode;
import com.joshondesign.treegui.modes.aminojava.*;
import com.joshondesign.treegui.modes.sketch.SketchMode;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import javax.swing.JFrame;
import org.joshy.gfx.Core;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.stage.Stage;

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

    public static Callback<SystemMenuEvent> quitHandler = new Callback<SystemMenuEvent>() {
        public void call(SystemMenuEvent actionEvent) throws Exception {
            System.exit(0);
        }
    };

    private static void init() {
        try {
            //load up the new doc page
            File file = new File("resources/start.xml");
            Doc xml = XMLParser.parse(file);
            Control root = (Control) AminoParser.parsePage(xml.root());
            root.setTranslateX(0);
            root.setTranslateY(0);

            //bind quit action
            Button quitButton = (Button) AminoParser.find("quitButton", root);
            quitButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    System.exit(0);
                }
            });

            final TreeNode<Mode> modes = initModes();

            //bind new doc action
            Button newdocButton = (Button) AminoParser.find("newdocButton", root);
            newdocButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    doNewDoc(modes.get(1));
                }
            });

            Button newsketchButton = (Button) AminoParser.find("newsketchButton", root);
            newsketchButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    doNewDoc(modes.get(2));
                }
            });

            //edit self button
            ((Button)AminoParser.find("editselfButton",root)).onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    doNewDoc(modes.get(1), new File("resources/main.xml"));
                }
            });

            Button openButton = (Button) AminoParser.find("openButton", root);
            openButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    openDoc(modes.get(1));
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
            EventBus.getSystem().addListener(SystemMenuEvent.Quit, quitHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static TreeNode<Mode> initModes() {
        TreeNode<Mode> modes = new TreeNode<Mode>();
        modes.add(new AminoJSMode());
        modes.add(new AminoJavaMode());
        modes.add(new SketchMode());
        return modes;
    }

    private static void openDoc(final Mode mode) {
        AminoJavaXMLImport.Open open = new AminoJavaXMLImport.Open();
        open.onOpened(new Callback<SketchDocument>() {
            public void call(SketchDocument document) throws Exception {
                doNewDoc(mode, document);
            }
        });
        open.execute();
    }

    private static void doNewDoc(Mode mode) throws Exception {
        SketchDocument doc = mode.createEmptyDoc();
        doNewDoc(mode, doc);
    }

    private static void doNewDoc(Mode mode, File docFile) throws Exception {
        //init doc
        SketchDocument doc = null;
        doc = AminoJavaXMLImport.read(docFile);
        doc.setFile(docFile);
        doNewDoc(mode, doc);
    }

    private static void doNewDoc(Mode mode, SketchDocument doc) throws Exception {
        //init window

        File file = new File("resources/main.xml");
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


        if(doc.getFile() != null) {
            stage.setTitle(doc.getFile().getName());
        }

        canvasView.setDocument(doc);

        //setup the symbols view
        final TreeNode<SketchNode> symbols = mode.getSymbols();
        symbolsView.setModel(asListModel(symbols));
        symbolsView.setRenderer(new SymbolRenderer());
        EventBus.getSystem().addListener(symbolsView, MouseEvent.MouseAll,
                new SymbolsDragHandler(canvasView, symbolsView, doc));


        //setup the props view
        propsView.setPropFilter(new PropsView.PropFilter() {
            public boolean include(Object obj, String name) {
                return true;
            }
        });
        propsView.setDocument(doc);


        //set up the canvas
        canvasView.setMasterRoot(doc.get(0).get(0));
        canvasView.setEditRoot(doc.get(0).get(0));

        SelectionTool selectionTool = new SelectionTool(canvasView, doc, mode);
        canvasView.setTool(selectionTool);


        //set up  the buttons
        final SketchDocument finalDoc2 = doc;
        ((Button) AminoParser.find("runButton", root)).onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                new AminoJavaXMLExport.Test(canvasView, finalDoc2).execute();
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
        final SketchDocument finalDoc1 = doc;
        ((Button) AminoParser.find("deleteButton", root)).onClicked(new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                DocumentActions.deleteSelection(finalDoc1);
            }
        });


        stage.raiseToTop();

        Menubar menubar = new Menubar((JFrame) stage.getNativeWindow());
        Menu fileMenu = new Menu().setTitle("File");
        fileMenu.addItem("Open", asAction(new AminoJavaXMLImport(canvasView, finalDoc)));
        fileMenu.addItem("Save", asAction(new AminoJavaXMLExport.Save(canvasView, finalDoc)));
        fileMenu.addItem("Quit", asAction(quitHandler));
        menubar.add(fileMenu);

        Menu editMenu = new Menu().setTitle("Edit");
        menubar.add(editMenu);

        Menu nodeMenu = new Menu().setTitle("Node");
        mode.modifyNodeMenu(nodeMenu, doc);
        menubar.add(nodeMenu);
    }

    private static ListModel asListModel(TreeNode<SketchNode> symbols) {
        return new TreeNodeListModel(symbols);
    }

    private static AminoAction asAction(final JAction jAction) {
        return new AminoAction() {
            @Override
            public void execute() throws Exception {
                jAction.execute();
            }
        };
    }

    private static AminoAction asAction(final Callback quitHandler) {
        return new AminoAction() {
            @Override
            public void execute() throws Exception {
                quitHandler.call(null);
            }
        };
    }

}
