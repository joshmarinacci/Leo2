package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.StringUtils;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.DynamicNodeMode;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.node.control.Menu;
import org.joshy.gfx.util.OSUtil;
import org.joshy.gfx.util.u;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joshondesign.treegui.modes.aminolang.Defs.*;
import static org.joshy.gfx.util.u.p;

public class AminoLangMode extends DynamicNodeMode {

    public static Map<String, DynamicNode.DrawDelegate> drawMap = new HashMap<String, DynamicNode.DrawDelegate>();

    public AminoLangMode() {
        setId("com.joshondesign.modes.aminolang");
        add(new TreeNode<JAction>());

        TreeNode<SketchNode> symbols = new TreeNode<SketchNode>();
        symbols.setId("symbols");
        add(symbols);

        drawMap.put("VisualBase", VisualBaseDelegate);
        DynamicNode visualBase = parse(new Defs.VisualBase(), VisualBaseDelegate, null);
        /*
        for(Property prop : visualBase.getProperties()) {
            u.p("  " + prop.getName() + " " + prop.getType().getName() + " " + prop.isVisible() + " blah");
        }
        */
        visualBase.getProperty("width").setExportName("w");
        visualBase.getProperty("height").setExportName("h");
        visualBase.getProperty("translateX").setExportName("tx");
        visualBase.getProperty("translateY").setExportName("ty");

        drawMap.put("PushButton", PushButtonDelegate);
        symbols.add(parse(new Defs.PushButton(), PushButtonDelegate, visualBase));
        DynamicNode pb = findSymbol("PushButton");
        pb.getProperty("width").setDoubleValue(140);
        pb.getProperty("height").setDoubleValue(30);

        drawMap.put("ToggleButton", ToggleButtonDelegate);
        symbols.add(parse(new Defs.ToggleButton(), ToggleButtonDelegate, visualBase));
        DynamicNode tb = findSymbol("ToggleButton");
        tb.getProperty("width").setDoubleValue(100);
        tb.getProperty("height").setDoubleValue(30);

        drawMap.put("TextField", TextFieldDelegate);
        symbols.add(parse(new Defs.TextField(), TextFieldDelegate, visualBase));

        drawMap.put("TextArea", TextAreaDelegate);
        symbols.add(parse(new Defs.TextArea(), TextAreaDelegate, visualBase));

        drawMap.put("Slider", SliderDelegate);
        symbols.add(parse(new Defs.Slider(), SliderDelegate, visualBase));
        findSymbol("Slider").getProperty("height").setDoubleValue(25);

        drawMap.put("Label", LabelDelegate);
        symbols.add(parse(new Defs.Label(), LabelDelegate, visualBase));

        drawMap.put("Rect",RectDelegate);
        symbols.add(parse(new Defs.Rect(), RectDelegate, visualBase));

        drawMap.put("ImageView",ImageViewDelegate);
        symbols.add(parse(new Defs.ImageView(), ImageViewDelegate, visualBase));

        drawMap.put("ListView",Defs.ListViewDelegate);
        symbols.add(parse(new Defs.ListView(), ListViewDelegate, visualBase));

        drawMap.put("AnchorPanel",Defs.AnchorPanelDelegate);
        symbols.add(parse(new AnchorPanel(), AnchorPanelDelegate, visualBase));

        drawMap.put("Transition",Defs.ServiceBaseDelegate);
        symbols.add(parse(new Transition(), ServiceBaseDelegate, visualBase));


        drawMap.put("DynamicGroup",DynamicGroup.DynamicGroupDelegate);
    }

    @Override
    public String getName() {
        return "Amino Lang";
    }

    @Override
    public SketchDocument createEmptyDoc() {
        SketchDocument doc = new SketchDocument();
        doc.setModeId(this.getId());
        Layer layer = new Layer();
        layer.add(findSymbol("PushButton").duplicate(null));
        Page page = new Page();
        page.add(layer);
        doc.add(page);
        return doc;
    }

    @Override
    public void modifyFileMenu(Menu fileMenu, SketchDocument doc) {
        fileMenu.addItem("Test HTML", "T", new AminoLangJSONExport(doc,true));
        fileMenu.addItem("Export Font", new FontExportAction());
        fileMenu.addItem("Test in Browser", new BrowserExportAction(doc));
        fileMenu.addItem("Test on Desktop", new DesktopExportAction(doc));
        fileMenu.addItem("Test on Device",  new DeviceExportAction());
    }

    @Override
    public void modifyEditMenu(Menu editMenu, final SketchDocument doc) {
        super.modifyEditMenu(editMenu, doc);
        editMenu.addItem("Cut", "X", new AminoAction() {
            @Override
            public void execute() throws Exception {
                doc.getSelection().cut(doc);
            }
        });
        editMenu.addItem("Copy", "C", new AminoAction() {
            @Override
            public void execute() throws Exception {
                doc.getSelection().copy(doc);
            }
        });
        editMenu.addItem("Paste", "V", new AminoAction() {
            @Override
            public void execute() throws Exception {  doc.getSelection().paste(doc);  }
        });
    }

    @Override
    public Map<String, DynamicNode.DrawDelegate> getDrawMap() {
        return drawMap;
    }

    @Override
    public void filesDropped(List<File> files, Canvas canvas) {
    }

    @Override
    public SketchNode createEmptyGroup() {
        return new DynamicGroup();
    }



    private class BrowserExportAction extends AminoAction {
        private final AminoLangJSONExport delegate;
        private BrowserExportAction(SketchDocument doc) {
            delegate = new AminoLangJSONExport(doc,false);
        }
        @Override
        public void execute() throws Exception {
            File dir = StringUtils.createTempDir();
            u.p("dir = ");
            u.p(dir);

            //copy js files
            File srcdir = new File("/Users/josh/projects/aminolang/src/jscanvas");
            StringUtils.copyFileToDir(new File(srcdir,"init.js"), dir);
            StringUtils.copyFileToDir(new File(srcdir,"handcoded7.js"), dir);
            StringUtils.copyFileToDir(new File(srcdir,"monkeypatch.js"), dir);
            File src2 = new File("/Users/josh/projects/aminolang/build/jscanvas");
            StringUtils.copyFileToDir(new File(src2,"out.js"),dir);
            File src3 = new File("/Users/josh/projects/aminolang/tests/");
            StringUtils.copyFileToDir(new File(src3,"jquery.js"),dir);

            //create the json file
            String str = delegate.exportTree(dir);
            File testjson = new File(dir,"test.json");
            u.stringToFile(str, testjson);

            //create js file
            StringBuffer sb = new StringBuffer();
            sb.append("<html>\n" +
                    "<head>\n" +
                    "<script src='init.js'></script>\n" +
                    "<script src='out.js'></script>\n" +
                    "<script src='handcoded7.js'></script>\n" +
                    "<script src='monkeypatch.js'></script>\n" +
                    "<script src='jquery.js'></script>\n" +
                    "<style type=\"text/css\">\n" +
                    "canvas { border: 1px solid black; }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<canvas id='mycanvas' width='600' height='300'></canvas>\n" +
                    "<script language=\"JavaScript\">\n" +
                    "Corex.start(function(core) {\n" +
                    "    var stage = core.createStage('mycanvas');\n" +

                    "$.getJSON('test.json',undefined, function(sceneRaw) {\n"+
                    "  var root = new SceneParser().parse(sceneRaw);"+
                    "  stage.setRoot(root);\n"
                    +"});\n"
                    +"});\n"
                    +"</script></body></html>"
                    );
            File testapp = new File(dir,"test.html");
            u.stringToFile(sb.toString(), testapp);

            OSUtil.openBrowser(testapp.toURI().toASCIIString());

        }
    }
    private class DesktopExportAction extends AminoAction {
        private final AminoLangJSONExport delegate;

        private DesktopExportAction(SketchDocument doc) {
            delegate = new AminoLangJSONExport(doc,false);
        }

        @Override
        public void execute() throws Exception {
            File dir = StringUtils.createTempDir();
            u.p("dir = ");
            u.p(dir);


            //copy amino.js
            File srcdir = new File("/Users/josh/projects/aminolang/src/node");
            StringUtils.copyFileToDir(new File(srcdir,"amino.js"), dir);
            StringUtils.copyFileToDir(new File(srcdir,"out.js"), dir);

            //copy aminonode
            File binfile = new File("/Users/josh/projects/aminolang/build/Release/amino.node");
            StringUtils.copyFileToDir(binfile, dir);


            File srcdir2 = new File("/Users/josh/projects/aminolang/tests/");
            StringUtils.copyFileToDir(new File(srcdir2,"test1.json"), dir);
            StringUtils.copyFileToDir(new File(srcdir2,"test1.png"), dir);

            //create the json file
            String str = delegate.exportTree(dir);
            File testjson = new File(dir,"test.json");
            u.stringToFile(str, testjson);

            //create js file
            StringBuffer sb = new StringBuffer();
            sb.append("var fs = require('fs');\n");
            sb.append("var amino = require('./amino.js');\n");
            sb.append("var core = amino.getCore();\n" +
                    "core.setDevice(\"mac\");\n");
            sb.append("var stage = core.createStage();\n");
            sb.append("var filedata = fs.readFileSync('"+testjson.getAbsolutePath()+"');\n" +
                    "var jsonfile = JSON.parse(filedata);\n" +
                    "var root = new amino.SceneParser().parse(core,jsonfile);\n" +
                    "stage.setRoot(root);\n");
            sb.append("setTimeout(function(){core.start();},1000);");
            File testapp = new File(dir,"test.js");
            u.stringToFile(sb.toString(), testapp);


            execAndWait("/usr/local/bin/node", dir, testapp.getAbsolutePath());
        }

        private Process proc;
        private void execAndWait(final String bin, final File wd, final String arg) throws InterruptedException {
            if(proc != null) {
                proc.destroy();
                proc = null;
            }
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    ProcessBuilder pb = new ProcessBuilder(bin,arg);
                    pb.directory(wd);
                    try {
                        proc = pb.start();
                        dumpInThread(proc.getErrorStream());
                        dumpInThread(proc.getInputStream());
                        p("running javascript");
                        proc.waitFor();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            });
            thread.start();
        }

        private void dumpInThread(final InputStream in) {
            new Thread(new Runnable() {
                public void run() {
                    byte[] buffer = new byte[1024];
                    try {
                        int len = in.read(buffer);
                        while(len != -1) {
                            System.out.write(buffer,0,len);
                            len = in.read(buffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }).start();
        }

    }
    private class DeviceExportAction extends AminoAction {

        @Override
        public void execute() throws Exception {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
