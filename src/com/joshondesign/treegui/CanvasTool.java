package com.joshondesign.treegui;

import org.joshy.gfx.draw.GFX;

public abstract class CanvasTool {
    public abstract void drawOverlay(GFX gfx);
    public abstract void pressed(Canvas.CanvasMouseEvent event);
    public abstract void dragged(Canvas.CanvasMouseEvent event);
    public abstract void released(Canvas.CanvasMouseEvent event);
}
