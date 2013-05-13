package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.Canvas;
import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.SketchDocument;
import com.joshondesign.treegui.docmodel.SketchNode;
import com.joshondesign.treegui.model.TreeNode;
import com.joshondesign.treegui.modes.DynamicNodeMode;
import com.joshondesign.treegui.modes.aminojava.AminoParser;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import static com.joshondesign.treegui.modes.aminolang.Defs.*;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.control.FileDialog;
import org.joshy.gfx.util.u;

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
        pb.getProperty("width").setDoubleValue(100);
        pb.getProperty("height").setDoubleValue(30);

        drawMap.put("ToggleButton", ToggleButtonDelegate);
        symbols.add(parse(new Defs.ToggleButton(), ToggleButtonDelegate, visualBase));
        DynamicNode tb = findSymbol("ToggleButton");
        tb.getProperty("width").setDoubleValue(100);
        tb.getProperty("height").setDoubleValue(30);

        drawMap.put("Slider", SliderDelegate);
        symbols.add(parse(new Defs.Slider(), SliderDelegate, visualBase));
        findSymbol("Slider").getProperty("height").setDoubleValue(25);

        drawMap.put("Label", LabelDelegate);
        symbols.add(parse(new Defs.Label(), LabelDelegate, visualBase));

        drawMap.put("Rect",RectDelegate);
        symbols.add(parse(new Defs.Rect(), RectDelegate, visualBase));

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

    private class FontExportAction extends AminoAction {
        @Override
        public CharSequence getDisplayName() {
            return "Export Font";
        }

        @Override
        public void execute() throws Exception {
            final Stage stage = Stage.createStage();

            Doc xml = XMLParser.parse(new File("resources/fontexport.xml"));
            Control root = (Control) AminoParser.parsePage(xml.root());

            Textbox size = (Textbox) AminoParser.find("fontSize", root);
            size.setText("30");

            final PopupMenuButton popup = (PopupMenuButton) AminoParser.find("fontName", root);
            String[] fonts = new String[]{"foo","bar","baz"};
            popup.setModel(org.joshy.gfx.node.control.ListView.createModel(fonts));


            /*
            final PopupMenuButton<Size> popup = (PopupMenuButton) AminoParser.find("sizeBox", root);
            Size[] sizes =  new Size[]{ new Size(320,480,Units.Pixels), new Size(1024,768,Units.Pixels), new Size(1280,800,Units.Pixels)};
            popup.setModel(org.joshy.gfx.node.control.ListView.createModel(sizes));
            popup.setTextRenderer(new ListView.TextRenderer<Size>() {
                public String toString(SelectableControl selectableControl, Size size, int i) {
                    return size.getWidth(Units.Pixels) + " x " + size.getHeight(Units.Pixels) + " px";
                }
            });
            */

            Button cancelButton = (Button) AminoParser.find("cancelButton", root);
            cancelButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    stage.hide();
                }
            });

            Button doneButton = (Button) AminoParser.find("exportButton", root);
            doneButton.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    stage.hide();
                    exportFont();
                }
            });

            double w = root.getPrefWidth();
            double h = root.getPrefHeight();
            stage.setWidth(w);
            stage.setHeight(h+20);
            stage.setContent(root);

        }
    }

    private void exportFont() {
        FileDialog fd = new FileDialog();
        fd.onCanceled(new Callback<FileDialog>() {
            public void call(FileDialog fileDialog) throws Exception {
                u.p("canceled");
            }
        });
        fd.onSucceeded(new Callback<FileDialog>() {
            public void call(FileDialog fileDialog) throws Exception {
                u.p("exporting to " + fileDialog.getSelectedFile().getAbsolutePath());
                exportFont("OpenSans", fileDialog.getSelectedFile());
            }
        });
        fd.showSaveDialog();
    }

    private void exportFont(String fontName, File outFile) {
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 40);
        BufferedImage img = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g1 = img.createGraphics();
        FontMetrics metrics = g1.getFontMetrics(font);
        FontRenderContext frc = g1.getFontRenderContext();

        JSONPrinter json = new JSONPrinter();
        json.openObject()
                .set("format","font")
                .set("version","1")
                .set("name",font.getFamily())
                .set("size",font.getSize2D());
        json.set("leading", metrics.getLeading());
        json.set("height",metrics.getHeight());
        json.set("ascent",metrics.getAscent());
        json.set("descent",metrics.getDescent());
        char minchar = 32; //space
        char maxchar = 126; //tilde
        json.set("minchar",(int)minchar);
        json.set("maxchar",(int)minchar);
        json.openArray("included");
        for(int i=0; i<256; i++) {
            if(i < minchar || i > maxchar) {
                json.appendArray(0);
            } else {
                json.appendArray(1);
            }
        }
        json.closeArray();
        json.openArray("widths");
        for(char i=minchar; i <= maxchar; i++) {
            int w = metrics.charWidth(i);
            json.appendArray(w);
        }
        json.closeArray();
        json.openArray("offsets");
        int total = 0;
        for(char i=minchar; i <= maxchar; i++) {
            int w = metrics.charWidth(i);
            json.appendArray(total);
            total += w;
        }
        json.closeArray();
        json.closeObject();
        u.p(json.toStringBuffer().toString());

        img = new BufferedImage(total + 10, metrics.getHeight()+10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(font);


        int x = 0;
        int y = metrics.getAscent()+5;
        for(char i=minchar; i <= maxchar; i++) {
            g.drawString(i+"",x, y);
            x+= metrics.charWidth(i);
        }
        g.dispose();
        try {
            File pngFile =  new File(outFile.getParent(),outFile.getName()+".png");
            ImageIO.write(img,"png",pngFile);
            u.p("wrote out to: " + pngFile);

            File jsonFile = new File(outFile.getParent(),outFile.getName()+".json");
            u.stringToFile(json.toStringBuffer().toString(),jsonFile);
            u.p("write out to: " + jsonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
