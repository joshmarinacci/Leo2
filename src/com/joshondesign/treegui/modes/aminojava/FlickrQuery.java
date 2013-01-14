package com.joshondesign.treegui.modes.aminojava;

import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.util.ArrayListModel;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/13/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlickrQuery {
    private String query;
    private ArrayListModel<String> results = new ArrayListModel<String>();


    public FlickrQuery() {
        results.add("foo");
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public ListModel getResults() {
        return results;
    }

    public void setResults(ListModel results) {
        //this.results = results;
    }

    public void execute() {
        System.out.println("doing a flickr query");
        results.add("oregon one");
        results.add("oregon two");
    }
}
