package com.joshondesign.treegui.examples.random;

import com.joshondesign.treegui.modes.aminojava.AminoParser;
import com.joshondesign.xml.Doc;
import com.joshondesign.xml.XMLParser;
import java.io.File;
import org.joshy.gfx.Core;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

public class Random {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().loadCSS(new File("test.css"));
        Core.setDebugCSS(new File("test.css"));
        Core.getShared().defer(new Runnable() {
            public void run() {
                init();
            }
        });

    }

    private static void init() {
        try {
            Doc xml = XMLParser.parse(Random.class.getResourceAsStream("random.xml"));
            Control root = (Control) AminoParser.parsePage(xml.root());
            Button doit = (Button) AminoParser.find("doit", root);
            final Textbox minBox = (Textbox) AminoParser.find("minBox", root);
            final Textbox maxBox = (Textbox) AminoParser.find("maxBox", root);
            final Label outLabel = (Label) AminoParser.find("outLabel", root);
            doit.onClicked(new Callback<ActionEvent>() {
                public void call(ActionEvent actionEvent) throws Exception {
                    int min = Integer.parseInt(minBox.getText());
                    int max = Integer.parseInt(maxBox.getText());
                    int val = (int) (min + Math.random()* (max-min));
                    outLabel.setText(Integer.toString(val));
                }
            });

            Stage stage = Stage.createStage();
            stage.setWidth(root.getPrefWidth());
            stage.setHeight(root.getPrefHeight() + 20);
            stage.setContent(root);
            //extra stuff
            EventBus.getSystem().addListener(SystemMenuEvent.Quit, quitHandler);
        } catch (Exception ex) {
            u.p(ex);
        }
    }

    public static Callback<SystemMenuEvent> quitHandler = new Callback<SystemMenuEvent>() {
        public void call(SystemMenuEvent actionEvent) throws Exception {
            System.exit(0);
        }
    };
}
