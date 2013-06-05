package com.joshondesign.treegui.modes.aminolang;

import com.joshondesign.treegui.modes.aminojava.AminoParser;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.PopupMenuButton;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.control.FileDialog;
import org.joshy.gfx.util.u;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
* Created with IntelliJ IDEA.
* User: josh
* Date: 6/5/13
* Time: 11:27 AM
* To change this template use File | Settings | File Templates.
*/
class FontExportAction extends AminoAction {

    public FontExportAction() {
    }

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
    private void exportFont() {
        FileDialog fd = new FileDialog();
        fd.onCanceled(new Callback<FileDialog>() {
            public void call(FileDialog fileDialog) throws Exception {
                u.p("canceled");
            }
        });
        fd.onSucceeded(new Callback<FileDialog>() {
            public void call(FileDialog fileDialog) throws Exception {
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
                .set("version","2")
                .set("name",font.getFamily())
                .set("size",font.getSize2D());
        json.set("leading", metrics.getLeading());
        json.set("height",metrics.getHeight());
        json.set("ascent",metrics.getAscent());
        json.set("descent",metrics.getDescent());
        Rectangle2D maxbounds = metrics.getMaxCharBounds(g1);
        u.p("max bounds = " + maxbounds);


        char minchar = 32; //space
        char maxchar = 126; //tilde
        json.set("minchar",(int)minchar);
        json.set("maxchar",(int)maxchar);



        int maxCharWidth = (int) Math.ceil(maxbounds.getWidth());
        int maxCharHeight = (int) Math.ceil(maxbounds.getHeight());
        json.set("maxcharwidth",maxCharWidth);
        json.set("maxcharheight",maxCharHeight);



        int texw = 1024;
        int colcount = (int) Math.floor(texw / maxCharWidth);
        int rowcount = (maxchar - minchar)/colcount;
        json.set("colcount",colcount);
        json.set("rowcount",rowcount);
        u.p("col count = " + colcount + " row count = " + rowcount);
        u.p("max char width = " + maxCharWidth);
        u.p("max char height = " + maxCharHeight);
        int texh = rowcount * maxCharHeight;
        u.p("texture size = " + texw + " x " + texh);
        json.set("imagewidth", texw);
        json.set("imageheight", texh);




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






        img = new BufferedImage(texw, texh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(font);


        int[] xoffsets = new int[maxchar-minchar+1];
        int[] yoffsets = new int[maxchar-minchar+1];
        int x = 0;
        int y = 0;
        int c = 0;
        for(char i=minchar; i <= maxchar; i++) {
            g.drawString(i+"",x, y+metrics.getAscent());
            xoffsets[c] = x;
            yoffsets[c] = y;
            x += maxCharWidth;
            if(x > texw) {
                x = 0;
                y += maxCharHeight;
            }
            c++;
        }
        g.dispose();



        json.openArray("offsets");
        for(int i : xoffsets) {
            json.appendArray(i);
        }
        json.closeArray();
        json.openArray("yoffsets");
        for(int i : yoffsets) {
            json.appendArray(i);
        }
        json.closeArray();
        json.closeObject();
        u.p(json.toStringBuffer().toString());


        try {
            File pngFile =  new File(outFile.getParent(),outFile.getName()+".png");
            ImageIO.write(img, "png", pngFile);
            u.p("wrote out to: " + pngFile);

            File jsonFile = new File(outFile.getParent(),outFile.getName()+".json");
            u.stringToFile(json.toStringBuffer().toString(),jsonFile);
            u.p("write out to: " + jsonFile);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
