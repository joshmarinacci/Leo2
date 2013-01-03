package com.joshondesign.treegui;

import com.joshondesign.treegui.actions.JAction;
import com.joshondesign.treegui.docmodel.Layer;
import com.joshondesign.treegui.docmodel.Page;
import com.joshondesign.treegui.docmodel.ResizableRectNode;
import com.joshondesign.treegui.docmodel.SketchNode;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import org.joshy.gfx.util.OSUtil;

public class HTMLBindingExport extends JAction {

    public Canvas canvas;
    public Page page;

    @Override
    public void execute() {
        try {
            //File file = File.createTempFile("foo",".html");
            File file = new File("/Users/josh/projects/Leo/t2/foo.html");
            //file.deleteOnExit();

            PrintWriter out = new PrintWriter(new FileWriter(file));
            //PrintWriter out = new PrintWriter(System.out);
            out.println("<html><head>");
            out.println("<script src='amino.js'></script>");
            out.println("</head>");
            out.println("<body>");
            out.println("<canvas id='canvas' width='500' height='400'></canvas>");

            out.println("<script language='Javascript'>");

            out.println("var engine = new Amino();");
            out.println("var root = engine.addCanvas('canvas');");
            PropWriter w = new PropWriter(out);
            for(Layer layer : page.children()) {
                w.p("//layer");
                w.indent();
                for(SketchNode node : layer.children()) {
                    w.newObjVarOpen(node.getId(), "Transform").indent();
                    if(node instanceof ResizableRectNode) {
                        ResizableRectNode rect = (ResizableRectNode) node;
                        w.newObj("Rect")
                            .indent()
                            .p(".set(0,0," + rect.getWidth() + "," + rect.getHeight() + ")")
                            .prop("fill", "red")
                            .outdent();
                    } else {
                        w.newObj("Rect")
                            .indent()
                            .p(".set(0,0,100,100)")
                            .prop("fill", "red")
                            .outdent();
                    }
                    w.newObjVarClose();

                    w.p("translateX", node.getTranslateX());
                    w.p("translateY", node.getTranslateY());
                    w.p(";").outdent();

                    w.p("root.add(" + node.getId() + ");");

                    if(PropUtils.propertyEquals(node,"draggable",true)) {
                        w.p("root.onPress("
                                +node.getId()
                                +",function() {" +
                                node.getId()
                                +".setTranslateX("
                                +node.getId()
                                +".getTranslateX()+10);"+
                                "});");
                    }
                }
                w.outdent();
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
            out.flush();
            //http://projects.joshy.org/Amino3/1.1/amino.js
            out.close();
            //OSUtil.openBrowser(file.toURI().toURL().toString());
            OSUtil.openBrowser("http://localhost/~josh/projects/Leo/t2/foo.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getShortName() {
        return "Export HTML";
    }

    private class PropWriter {
        private final PrintWriter out;
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        private int indent;

        public PropWriter(PrintWriter out) {
            this.out = out;
            decimalFormat.setMaximumFractionDigits(2);
            decimalFormat.setMinimumFractionDigits(0);
        }

        public PropWriter prop(String name, String value) {
            pindent();
            out.print(".set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            out.print("("+"'"+value+"'"+")");
            out.println();
            return this;
        }

        private void pindent() {
            for(int i=0; i<indent; i++) {
                out.print("    ");
            }
        }

        public PropWriter p(String name, double value) {
            pindent();
            out.print(".set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            out.print("("+""+value+""+")");
            out.println();
            return this;
        }

        public PropWriter indent() {
            this.indent++;
            return this;
        }

        public PropWriter outdent() {
            this.indent--;
            return this;
        }

        public PropWriter pwen() {
            pindent();
            out.println(")");
            return this;
        }

        public PropWriter pnew(String name) {
            pindent();
            out.println(" new "+name+"(");
            return this;
        }

        public PropWriter p(String s) {
            pindent();
            out.println(s);
            return this;
        }

        public PropWriter newObj(String name) {
            pindent();
            out.println(" new "+name+"()");
            return this;
        }

        public PropWriter newObjVarOpen(String id, String name) {
            pindent();
            out.println("var " + id + " = new " + name + "(");
            return this;
        }

        public PropWriter newObjVarClose() {
            pindent();
            out.println(")");
            return this;
        }
    }
}
