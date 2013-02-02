${tree}

function setupDraggable(node, root) {
    
    var sx = 0;
    var sy = 0;
    var stx = 0;
    var sty = 0;
    var dx = 0;
    var dy = 0;
    root.onPress(node,function(e) {
        sx = e.point.x;
        stx = node.getX();
        sty = node.getY();
        sy = e.point.y;
    });
    
    root.onDrag(node,function(e) {
        dx = e.point.x-sx;
        dy = e.point.y-sy;
        node.setX(stx + dx);
        node.setY(sty + dy);
    });

}

generated = {
    setup: function(root) {
        ${setup}
    }
}
