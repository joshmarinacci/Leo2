package com.joshondesign.treegui.modes.aminolang;

/**
* Created with IntelliJ IDEA.
* User: josh
* Date: 4/2/13
* Time: 6:52 PM
* To change this template use File | Settings | File Templates.
*/
public class JSONPrinter {
    private final StringBuffer sb;
    private boolean start;

    JSONPrinter() {
        this.sb = new StringBuffer();
        start = true;
    }

    public StringBuffer toStringBuffer() {
        return this.sb;
    }

    public JSONPrinter open() {
        doStart();
        sb.append("{");
        start = true;
        return this;
    }
    public JSONPrinter openArray(String key) {
        doStart();
        start = true;
        sb.append("\""+key+"\":[");
        return this;
    }
    public JSONPrinter closeArray() {
        sb.append("]");
        return this;
    }

    public JSONPrinter set(String key, String value) {
        doStart();
        sb.append("\""+key+"\":\""+value+"\"\n");
        return this;
    }

    private void doStart() {
        if(start) {
            start = false;
        } else {
            sb.append(",");
        }
    }

    public JSONPrinter set(String key, double value) {
        doStart();
        sb.append("\""+key+"\":"+value+"\n");
        return this;
    }

    public JSONPrinter set(String key, boolean value) {
        doStart();
        sb.append("\""+key+"\":"+value+"\n");
        return this;
    }

    public void close() {
        sb.append("}");
    }

}
