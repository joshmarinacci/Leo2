package com.joshondesign.treegui.modes.aminojs;

import com.joshondesign.treegui.Binding;
import com.joshondesign.treegui.StringUtils;
import com.joshondesign.treegui.actions.XMLExport;
import com.joshondesign.treegui.docmodel.*;
import com.joshondesign.treegui.modes.aminojava.DynamicNode;
import com.joshondesign.treegui.modes.aminojava.Property;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.util.OSUtil;
import org.joshy.gfx.util.u;
import static org.joshy.gfx.util.u.p;

public class HTMLBindingExport extends AminoAction {

    private static final boolean USE_OMETA = true;
    public SketchDocument document;
    public Page page;
    private final boolean useRandomFile;
    private int imageCounter = 0;

    public HTMLBindingExport(SketchDocument document, boolean useRandomFile) {
        this.document = document;
        this.page = document.get(0);
        this.useRandomFile = useRandomFile;
    }

    @Override
    public void execute() {

        if(USE_OMETA) {
            try {
                exportWithOmeta();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            File dir = new File("/Users/josh/projects/Leo/t2");
            File html = new File(dir, "foo.html");
            File templatedir = new File("resources/");
            if(useRandomFile) {

            } else {

                //get a file to write to
                if(document.getExportFile() != null) {
                    html = document.getExportFile();
                    dir = html.getParentFile();
                } else {
                    java.awt.FileDialog fd = new java.awt.FileDialog((Frame) null);
                    fd.setMode(FileDialog.SAVE);
                    fd.setVisible(true);
                    if(fd.getFile() == null) {
                        return;
                    }
                    String filename = fd.getFile();
                    if(!filename.toLowerCase().endsWith(".html")) {
                        filename += ".html";
                    }
                    html = new File(fd.getDirectory(),filename);
                    dir = html.getParentFile();
                }
            }

            //File file = File.createTempFile("foo",".html");
            //file.deleteOnExit();
            StringWriter treeContent = new StringWriter();
            PrintWriter treeOut = new PrintWriter(treeContent);
            PropWriter treeWriter = new PropWriter(treeOut);
            StringWriter setupContent = new StringWriter();
            for(Layer layer : page.children()) {
                treeWriter.p("//layer");
                treeWriter.indent();
                for(SketchNode node : layer.children()) {
                    DynamicNode dnode = (DynamicNode) node;
                    exportNode(treeWriter, dnode, true, dir);
                    if(node.isVisual() && AminoAdapter.shouldAddToScene(node, document.getBindings())) {
                        setupContent.append("root.add(" + node.getId() + ");\n");
                    }
                    if(AminoAdapter.useSetup(dnode)) {
                        setupContent.append(node.getId() + ".setup(root);\n");
                    }
                    doExtensions(setupContent, dnode);
                }
                treeWriter.outdent();
            }

            for(Binding binding : document.getBindings()) {
                exportBinding(new PrintWriter(setupContent),binding);
            }

            treeOut.close();
            setupContent.close();

            Map<String,String> subs = new HashMap<String,String>();
            subs.put("tree",treeContent.toString());
            subs.put("setup", setupContent.toString());

            if(!html.exists()) {
                StringUtils.applyTemplate(new File(templatedir, "index_template.html"), html, subs);
            }
            File js = new File(dir,"generated.js");
            StringUtils.applyTemplate( new File(templatedir, "generated_template.js"), js, subs);
            StringUtils.copyFile(new File(templatedir, "amino.js"), new File(dir, "amino.js"));
            StringUtils.copyFile(new File(templatedir, "jquery.js"), new File(dir, "jquery.js"));
            StringUtils.copyFile(new File(templatedir,"controls.js"), new File(dir,"controls.js"));


            File trimfile = new File("/Users/josh/");
            String partialPath = dir.getAbsolutePath().substring((int)trimfile.getAbsolutePath().length());
            OSUtil.openBrowser("http://localhost/~josh/"+partialPath+"/"+html.getName());

            document.setExportFile(html);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportWithOmeta() throws IOException, URISyntaxException, InterruptedException {
        p("using ometa");

        //export to XML file
        File xmlFile = new File("foo.xml");
        toXML(xmlFile);
        //invoke converter
        execAndWait("/usr/local/bin/node");
        //apply template to make final file
        File genJSFile = new File("/Users/josh/projects/Leo2/foo.html");
        File dir = new File("/Users/josh/projects/Leo/t2");
        File html = new File(dir, "foo.html");
        File templatedir = new File("resources/");
        Map<String,String> subs = new HashMap<String,String>();

        String str = fileToString(genJSFile);
        subs.put("tree", str.toString());

        html.delete();
        if(!html.exists()) {
            StringUtils.applyTemplate(new File(templatedir, "index_aminolang_template.html"), html, subs);
            p("wrote out template.html to " + html.getAbsolutePath());
        }
        File js = new File(dir,"generated.js");
        StringUtils.applyTemplate( new File(templatedir, "generated_aminolang_template.js"), js, subs);
        p("wrote out: " + js.getAbsolutePath());
        //open browser
        File trimfile = new File("/Users/josh/");
        String partialPath = dir.getAbsolutePath().substring((int)trimfile.getAbsolutePath().length());
        OSUtil.openBrowser("http://localhost/~josh/"+partialPath+"/"+html.getName());
    }

    private void toXML(File xmlFile) throws FileNotFoundException, URISyntaxException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(xmlFile));
        XMLExport.exportToXML(pw,document);
        p("wrote to file" + xmlFile.getAbsolutePath());
    }

    private String fileToString(File genJSFile) throws IOException {
        return u.fileToString(new FileInputStream(genJSFile));
    }

    private void execAndWait(String s) throws InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(s,"convert_xml_to_js.js");
        pb.directory(new File("/Users/josh/projects/compilerclass/guitest2/"));
        try {
            Process proc = pb.start();
            proc.waitFor();
            p("processing");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void doExtensions(StringWriter setupContent, DynamicNode dnode) {
        if(dnode.hasProperty("draggable")) {
            if(dnode.getProperty("draggable").getBooleanValue()) {
                setupContent.append("setupDraggable("+dnode.getId()+",root);\n");
            }
        }
    }

    private void exportBinding(PrintWriter out, Binding binding) {
        if(AminoAdapter.shouldExportAsSetter(binding)) {
            String prop = binding.getTargetProperty().getName();
            out.println(
                    binding.getTarget().getId() +
                            ".set" + prop.substring(0,1).toUpperCase()
                            +prop.substring(1)+"("
                            +binding.getSource().getId()
                            +");"
            );
            return;
        }

        if(AminoAdapter.shouldExportAsAdder(binding)) {
            out.print(binding.getTarget().getId()+".add(");
            out.print(binding.getSource().getId());
            out.println(");");
            return;
        }

        if(AminoAdapter.shouldExportAsTrigger(binding)) {
            out.println(
                    binding.getSource().getId()+".setCallback("
                    +"function(){\n");
                out.println("    "+binding.getTarget().getId()+".execute();");
            out.println("});");
            return;
        }

        if(binding.getSource().getName().equals("ListView")) {
            if(binding.getSourceProperty().isProxy()) {
                p("it's a proxy! " + binding.getSourceProperty().getName());
                p("master = " + binding.getSourceProperty().getMasterProperty());
                String n = binding.getTargetProperty().getName();
                String setterName = "set"+n.substring(0,1).toUpperCase()+n.substring(1);
                out.println(
                    binding.getSource().getId()+".listen(function(e){\n"
                    +"  var val = e.getSelectedObject()."+binding.getSourceProperty().getName()+";\n"
                    +"  "+binding.getTarget().getId()+"."+setterName+"(val);\n"
                    +"});\n"
                );
                return;
            }
        }

        out.println(
                "var binder = new Binder()"
                        +".set("+binding.getSource().getId()+",'"+binding.getSourceProperty().getName()+"',"
                        +binding.getTarget().getId()+",'"+binding.getTargetProperty().getName()+"');\n" +
                        "engine.addAnim(binder);\n"+
                        "binder.start();\n");
    }

    private void exportNode(PropWriter w, DynamicNode node, boolean includeVar, File dir) {
        p("doing node: " + node);
        String[] visualOnlyProps = new String[]{"x","y","width","height"};
        List<String> resizeOnlyProps = Arrays.asList("width", "height");

        if(includeVar) {
            w.newObj(node.getId(), AminoAdapter.getScriptClass(node));
        } else {
            w.newObj(AminoAdapter.getScriptClass(node));
        }
        w.indent();
        for(Property prop : node.getProperties()) {
            if(!AminoAdapter.shouldExportProperty(node,prop)) continue;
            p("writing: " + prop.getName() + " " + prop.getRawValue() + " exported = " + prop.isExported());
            String key = prop.getName();
            if(key.equals("translateX")) key = "x";
            if(key.equals("translateY")) key = "y";
            if(!node.isVisual() && Arrays.asList(visualOnlyProps).contains(key)) continue;
            if(key.equals("width") && !canResizeHorizontal(node)) continue;
            if(key.equals("height") && !canResizeVertical(node)) continue;
            if(key.equals("draggable")) continue;
            if(key.equals("src") && node.getName().equals("Image")) {
                try {
                    URL srcURL = new URL(prop.getStringValue());
                    File dstFile = calcUniqueImageName(dir);
                    StringUtils.copyFile(srcURL, dstFile);
                    w.prop(key, dstFile.getName());
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            w.prop(key, prop.getRawValue());
        }

        for(SketchNode child : node.children()) {
            w.p(".add(");
            exportNode(w, (DynamicNode) child, false, dir);
            w.p(")");
        }
        if(includeVar) {
            w.p(";");
        }
        w.outdent();
    }

    private File calcUniqueImageName(File dir) {
        imageCounter++;
        return new File(dir,"_image_"+imageCounter+".png");
    }

    private boolean canResizeVertical(DynamicNode node) {
        if(node.getResize() == Resize.Any) return true;
        if(node.getResize() == Resize.VerticalOnly) return true;
        return false;
    }

    private boolean canResizeHorizontal(DynamicNode node) {
        if(node.getResize() == Resize.Any) return true;
        if(node.getResize() == Resize.HorizontalOnly) return true;
        return false;
    }


    @Override
    public CharSequence getDisplayName() {
        return "Export Webpage";
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

        public PropWriter prop(String name, Object value) {
            pindent();
            out.print(".set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            out.print("("+valueToString(value)+")");
            out.println();
            return this;
        }

        private String valueToString(Object value) {
            if(value instanceof String) {
                return '"'+((String)value)+'"';
            }
            if(value instanceof FlatColor) {
                FlatColor fc = (FlatColor) value;
                return '"'+"#"+Integer.toHexString(fc.getRGBA()).substring(2) + '"';
            }
            if(value instanceof List) {
                StringBuffer sb = new StringBuffer();
                sb.append("[");
                boolean first = true;
                for(Object o : ((List)value)) {
                    if(!first) sb.append(",");
                    sb.append('"'+o.toString()+'"');
                    first = false;
                }
                sb.append("]");
                return sb.toString();
            }
            if(value == null) return "null";
            return value.toString();
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

        public PropWriter newObj(String id, String name) {
            pindent();
            out.println("var " + id + " = new " + name + "()");
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
