package com.joshondesign.treegui.modes.aminojava;

import org.joshy.gfx.event.BackgroundTask;
import org.joshy.gfx.event.ChangedEvent;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.util.ArrayListModel;

public class FlickrQuery {
    private String query;
    private ArrayListModel<String> results = new ArrayListModel<String>();
    private boolean active;


    public FlickrQuery() {
        setQuery("london");
        results.add("foo");
        setActive(false);
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

    public void execute() throws InterruptedException {
        setActive(true);
        results.clear();
        results.add(getQuery() + " one");
        results.add(getQuery() + " two");
        BackgroundTask<String, String> task = new BackgroundTask<String, String>() {
            @Override
            protected String onWork(String s) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onEnd(String result) {
                setActive(false);
            }
        };
        task.start();
    }

    public void setActive(boolean active) {
        this.active = active;
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.BooleanChanged, new Boolean(active),this));
    }

    public boolean isActive() {
        return active;
    }
}
