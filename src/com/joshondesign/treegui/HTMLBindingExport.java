package com.joshondesign.treegui;

import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.joshy.gfx.util.OSUtil;

public class HTMLBindingExport extends JAction<Page> {

    public Canvas canvas;

    @Override
    public void execute(Page data) {
        try {
            //File file = File.createTempFile("foo",".html");
            File file = new File("/Users/josh/projects/Leo/t2/foo.html");
            //file.deleteOnExit();

            PrintWriter out = new PrintWriter(new FileWriter(file));
            out.println("<html><head>");
            out.println("<script src='amino.js'></script>");
            out.println("</head>");
            out.println("<body>");
            out.println("<canvas id='canvas' width='500' height='400'></canvas>");

            out.println("<script language='Javascript'>");

            out.println("var engine = new Amino();");
            out.println("var root = engine.addCanvas('canvas');");
            for(Layer layer : data.children()) {
                for(SketchNode node : layer.children()) {
                    out.println("var "+node.getId()+" = ");
                    out.println("  new Transform(");
                    if(node instanceof ResizableRectNode) {
                        ResizableRectNode rect = (ResizableRectNode) node;
                        out.println("  new Rect().set(0,0,"+rect.getWidth()+","+rect.getHeight()+").setFill('red')");
                    } else {
                        out.println("  new Rect().set(0,0,100,100).setFill('red')");
                    }
                    out.println("  )");
                    out.println("  .setTranslateX("+node.getTranslateX()+")");
                    out.println("  .setTranslateY("+node.getTranslateY()+")");
                    out.println("  ;");
                    out.println("root.add("+node.getId()+");");

                    if(PropUtils.propertyEquals(node,"draggable",true)) {
                        out.println("root.onPress("+node.getId()+",function() {\n" +
                                node.getId()+".setTranslateX("+node.getId()+".getTranslateX()+10);\n"+
                                "});\n");
                    }
                }
            }


            for(Binding binding : canvas.getBindings()) {
                out.println(
                    "var binder = new Binder()"
                            +".set("+binding.getSource().getId()+",'"+binding.getSourceProperty()+"',"
                            +binding.getTarget().getId()+",'"+binding.getTargetProperty()+"');\n" +
                    "engine.addAnim(binder);\n"+
                    "binder.start();\n");
            }

            out.println("console.log('running');");
            out.println("engine.start();");

            out.println("</script>");
            out.println("</body>");
            out.println("</html>");
            //http://projects.joshy.org/Amino3/1.1/amino.js
            out.close();
            //OSUtil.openBrowser(file.toURI().toURL().toString());
            OSUtil.openBrowser("http://localhost/~josh/projects/Leo/t2/foo.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
