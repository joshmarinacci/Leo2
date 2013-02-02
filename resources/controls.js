

function Binder() {
    this.set = function(s,sp,t,tp) {
        this.source = s;
        this.sourceProp = sp;
        this.target = t;
        this.targetProp = tp;
        return this;
    }
    this.callback = function() {
        var binder = this;
        var getter = "get"
            +binder.sourceProp[0].toUpperCase()
            +binder.sourceProp.slice(1);
        if(!binder.source[getter]) {
            getter = "is"
            +binder.sourceProp[0].toUpperCase()
            +binder.sourceProp.slice(1);
        }
        var setter = "set"
            +binder.targetProp[0].toUpperCase()
            +binder.targetProp.slice(1);
        if(binder.val != binder.source[getter]()) {
            //console.log("doing " + getter + " to " + setter);
            binder.val = binder.source[getter]();
            binder.target[setter](binder.val);
            console.log(""+setter + " " + binder.val);
        }
    }
    return this;
}
Binder.extend(CallbackAnim);

function AminoControl() {
    AminoNode.call(this);
    this.typename = "AminoControl";
	this.x = 0;
	this.y = 0;
	this.w = 10;
	this.h = 10;
	this.setX = function(x) {
	    this.x = x;
	    this.setDirty();
	    return this;
	};
	this.getX = function() {
	    return this.x;
	}
	this.setY = function(y) {
	    this.y = y;
	    this.setDirty();
	    return this;
	};
	this.getY = function() {
	    return this.y;
	}
    this.contains = function(pt) {
        if(pt.x >= this.x && pt.x <= this.x + this.w) {
            if(pt.y >= this.y && pt.y<=this.y + this.h) {
                return true;
            }
        }
        return false;
    }
}
AminoControl.extend(AminoNode);

function PushButton() {
    AminoControl.call(this);
    this.typename = "PushButton";
    this.layoutDone = false;    
    
    this.text;
    this.setText = function(str) {
        this.text = str;
        this.layoutDone = false;
        return this;
    };
    
    this.doLayout = function(ctx) {
        this.w = ctx.measureText(this.text).width+5+5;
        this.h = ctx.measureText('M').width+5+5;
        this.layoutDone = true;
    }
    
    this.paint = function(ctx) {
        ctx.font = "15px sans-serif";
        if(!this.layoutDone) this.doLayout(ctx);
        ctx.fillStyle = "#ccc";
        ctx.fillRect(this.x,this.y,this.w,this.h);
        ctx.fillStyle = "black";
        ctx.fillText(this.text,this.x+5,this.y+this.h-5);
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.w,this.h);
    }
    
    var self = this;
    this.setup = function(root) {
        root.onPress(this,function() {
            if(self.cb) {
                self.cb();
            }
        });
        return this;
    }
    
    this.setCallback = function(cb) {
        this.cb = cb;
        return this;
    }

    return this;
}
PushButton.extend(AminoControl);


function ToggleButton() {
    AminoControl.call(this);
    this.typename = "ToggleButton";
    this.layoutDone = false;    
    
    this.text;
    this.setText = function(str) {
        this.text = str;
        this.layoutDone = false;
        return this;
    };
	
	this.selected = false;
	this.setSelected = function(s) {
	    this.selected = s;
	    this.setDirty();
	    return this;
	};
	this.getSelected = function() {
	    return this.selected;
	};
	
    this.doLayout = function(ctx) {
        this.w = ctx.measureText(this.text).width+5+5;
        this.h = ctx.measureText('M').width+5+5;
        this.layoutDone = true;
    }
    
    this.paint = function(ctx) {
        ctx.font = "15px sans-serif";
        if(!this.layoutDone) this.doLayout(ctx);
        ctx.fillStyle = "#ccc";
        if(this.getSelected()) {
            ctx.fillStyle = "#aaf";
        }
        ctx.fillRect(this.x,this.y,this.w,this.h);
        ctx.fillStyle = "black";
        ctx.fillText(this.text,this.x+5,this.y+this.h-5);
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.w,this.h);
    }
    
    var self = this;
    this.setup = function(root) {
        root.onPress(this,function() {
            self.setSelected(!self.getSelected());
        });
        return this;
    }

    return this;
}
ToggleButton.extend(AminoControl);

function Label() {
    AminoControl.call(this);
    this.typename = "Label";
    this.layoutDone = false;    
    
    this.fontsize = 10;
    this.setFontsize = function(fs) {
        this.fontsize = fs;
        this.layoutDone = false;
        this.setDirty();
        return this;
    }
    this.w = 60;
    this.setWidth = function(width) {
        this.w = width;
        this.setDirty();
        return this;
    }
    this.h = 20;
	this.setHeight = function(height) {
	    this.h = height;
	    this.setDirty();
	    return this;
	};
    
    this.text;
    this.setText = function(str) {
        this.text = str;
        this.layoutDone = false;
        return this;
    };
    this.doLayout = function(ctx) {
        this.w = ctx.measureText(this.text).width+5+5;
        this.h = ctx.measureText('M').width+5+5;
        this.layoutDone = true;
    }
    
    this.paint = function(ctx) {
        ctx.font = this.fontsize+"pt sans-serif";
        //if(!this.layoutDone) this.doLayout(ctx);
        ctx.fillStyle = "#ccc";
//        ctx.fillRect(this.x,this.y,this.w,this.h);
        ctx.fillStyle = "black";
        ctx.fillText(this.text,this.x+5,this.y+this.h-5);
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.w,this.h);
    }
    
    this.setup = function(root) {
        root.onPress(this,function() {
            console.log("pressed");
        });
        return this;
    }

    return this;
}
Label.extend(AminoControl);




function Slider() {
    AminoControl.call(this);
    this.typename = "Slider";
    this.layoutDone = false;        	
	
    this.value = 0;
    this.setValue = function(v) {
        if(v > this.maxValue) v = this.maxValue;
        if(v < this.minValue) v = this.minValue;
        this.value = v;
        this.setDirty();
        return this;
    };
    this.getValue = function() {
        return this.value;
    };
    
    this.minValue = 0;
    this.setMinValue = function(v) {
        this.minValue = v;
        this.setDirty();
        return this;
    }
    this.getMinValue = function() {
        return this.minValue;
    }
    
    this.maxValue = 50;
    this.setMaxValue = function(v) {
        this.maxValue = v;
        this.setDirty();
        return this;
    }
    this.getMaxValue = function() {
        return this.maxValue;
    }
	
    this.contains = function(pt) {
        if(pt.x >= this.x && pt.x <= this.x + this.w) {
            if(pt.y >= this.y && pt.y<=this.y + this.h) {
                return true;
            }
        }
        return false;
    }
    
    this.setWidth = function(width) {
        this.w = width;
        this.setDirty();
        return this;
    }
    
    this.doLayout = function(ctx) {
        //this.w = 100;
        this.h = ctx.measureText('M').width+5+5;
        this.layoutDone = true;
    }
    
    this.paint = function(ctx) {
        ctx.font = "15px sans-serif";
        if(!this.layoutDone) this.doLayout(ctx);
        ctx.fillStyle = "#ccc";
        ctx.fillRect(this.x,this.y,this.w,this.h);
        
        ctx.fillStyle = "#aaf";
        ctx.fillRect(this.x+this.value,this.y,this.h,this.h);
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.w,this.h);
    }
    
    var self = this;
    this.setup = function(root) {
        root.onPress(this,function() {
        });
        root.onDrag(this,function(e) {
            self.setValue(e.point.x-self.getX());
        });
        return this;
    }
    return this;
}
Slider.extend(AminoControl);



function CheckButton() {
    AminoControl.call(this);
    this.typename = "CheckButton";
    this.layoutDone = false;    
    
    this.text;
    this.setText = function(str) {
        this.text = str;
        this.layoutDone = false;
        return this;
    };
	
	this.selected = false;
	this.setSelected = function(s) {
	    this.selected = s;
	    this.setDirty();
	    return this;
	};
	this.getSelected = function() {
	    return this.selected;
	};
	
    this.doLayout = function(ctx) {
        this.h = ctx.measureText('M').width+5+5;
        this.w = ctx.measureText(this.text).width+5+5 + this.h;
        this.layoutDone = true;
    }
    
    this.paint = function(ctx) {
        ctx.font = "15px sans-serif";
        if(!this.layoutDone) this.doLayout(ctx);
        ctx.fillStyle = "#ccc";
        if(this.getSelected()) {
            ctx.fillStyle = "#aaf";
        }
        ctx.fillRect(this.x,this.y,this.h,this.h);
        ctx.fillStyle = "black";
        ctx.fillText(this.text,this.x+5+this.h,this.y+this.h-5);
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.h,this.h);
    }
    
    var self = this;
    this.setup = function(root) {
        root.onPress(this,function() {
            self.setSelected(!self.getSelected());
        });
        return this;
    }
    return this;
}
CheckButton.extend(AminoControl);

function Textbox() {
    AminoControl.call(this);
    this.text;
    this.layoutDone = false;
    this.setText = function(str) {
        this.text = str;
        this.layoutDone = false;
        this.setDirty();
        return this;
    };
    this.getText = function() {
        return this.text;
    };
	this.setWidth = function(width) {
	    this.w = width;
	    this.setDirty();
	    return this;
	};
	this.getWidth = function() {
	    return this.w;
	}
	this.setHeight = function(height) {
	    this.h = height;
	    this.setDirty();
	    return this;
	};
	this.getHeight = function() {
	    return this.h;
	}
    this.doLayout = function(ctx) {
        //this.w = ctx.measureText(this.text).width+5+5;
        this.h = ctx.measureText('M').width+5+5;
        this.layoutDone = true;
    }
    this.paint = function(ctx) {
        ctx.font = "15px sans-serif";
        if(!this.layoutDone) this.doLayout(ctx);
        ctx.fillStyle = "#ccc";
        if(this.hasFocus) {
            ctx.fillStyle = "#fff";
        }
        ctx.fillRect(this.x,this.y,this.w,this.h);
        ctx.fillStyle = "black";
        ctx.fillText(this.text,this.x+5,this.y+this.h-5);
        if(this.hasFocus) {
            var tw = ctx.measureText(this.text).width;
            ctx.fillStyle = "red";
            ctx.fillRect(this.x+5+tw+1, this.y+3, 2,this.h-6);
        }
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.w,this.h);
    }
    this.hasFocus = false;
    var self = this;
    this.setup = function(root) {
        root.onPress(this,function() {
            console.log("set to active text");
            if(!self.hasFocus) {
                self.hasFocus = true;
                self.setDirty();
                window.addEventListener('keydown',function(e) {
                    //delete
                    if(e.keyCode == 46 || e.keyCode == 8) {
                        var t= self.getText();
                        t = t.substring(0,t.length-1);
                        self.setText(t);
                    }
                    
                    //console.log(e);
                });
                window.addEventListener('keyup',function(e) {
                });
                window.addEventListener('keypress',function(e) {
                    self.setText(self.getText()+String.fromCharCode(e.charCode));
                    //console.log(e);
                });
            }
        });
        return this;
    }
    this.w = 100;
    this.h = 20;
    return this;
}
Textbox.extend(AminoControl);


function PlainPanel() {
    AminoControl.call(this);
    this.childs = [];
    this.typename = "PlainPanel";
	this.setWidth = function(width) {
	    this.w = width;
	    this.setDirty();
	    return this;
	};
	this.getWidth = function() {
	    return this.w;
	}
	this.setHeight = function(height) {
	    this.h = height;
	    this.setDirty();
	    return this;
	};
	this.getHeight = function() {
	    return this.h;
	}
	this.title = "PlainPanel";
	this.setTitle = function(title) {
	    this.title = title;
	    this.setDirty();
	    return this;
	}
	
    this.paint = function(ctx) {
        ctx.fillStyle = "#eee";
        ctx.fillRect(this.x,this.y,this.w,this.h);
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.w,this.h);
        
        for(var i=0; i<this.childs.length; i++) {
            ctx.save();
            ctx.translate(this.x,this.y);
            this.childs[i].paint(ctx);
            ctx.restore();
        }
        
    }
    
    this.add = function(child) {
        this.childs.push(child);
        child.setParent(this);
        this.setDirty();
        return this;
    }
    
	var self = this;
    this.setup = function(root) {
        return this;
    }
    return this;
}
PlainPanel.extend(AminoControl);

function ListView() {
    AminoControl.call(this);
    this.cb = [];
    this.typename = "ListView";
	this.setWidth = function(width) {
	    this.w = width;
	    this.setDirty();
	    return this;
	};
	this.getWidth = function() {
	    return this.w;
	};
	this.setHeight = function(height) {
	    this.h = height;
	    this.setDirty();
	    return this;
	};
	this.getHeight = function() {
	    return this.h;
	};
	
	this.listModel = ['a','b','c'];
	this.setListModel = function(m)  {
	    this.listModel = m;
	    if(m.listen) {
	        var self = this;
	        m.listen(function() {
                self.setDirty();
	        });
	    }
	    this.setDirty();
	    return this;
	};
    this.setData = function(data) {
        return this.setListModel(data);
    }
    
    this.index = 0;
    this.getSelectedIndex = function() {
        return this.index;
    }
    this.setSelectedIndex = function(index) {
        var i = index;
        if(this.listModel.items) {
            console.log("len = " + this.listModel.items.length);
            if(i > this.listModel.items.length-1) {
                i = this.listModel.items.length-1;
            }
        }
        console.log("setting index to : " + i);
        this.index = i;
        this.fireChange();
        return this;
    }
    this.getSelectedObject = function() {
        return this.listModel.get(this.index);
    }
    this.fireChange = function() {
        for(var i=0; i<this.cb.length; i++) {
            this.cb[i](this);
        }
        this.setDirty();
    }
        
    this.rh = 19;
    
	this.listen = function(cb) {
	    this.cb.push(cb);
        return this;
    }

    this.paint = function(ctx) {
        ctx.font = "15px sans-serif";
        ctx.fillStyle = "#eee";
        ctx.fillRect(this.x,this.y,this.w,this.h);
        
        ctx.fillStyle = "red";
        ctx.fillRect(this.x,this.y+this.index*this.rh, this.w, this.rh);
        
        ctx.fillStyle = "black";
        for(var i=0; i<this.listModel.length; i++) {
            ctx.fillText(this.listModel[i],
                this.x+5,this.y+i*this.rh+20);
        }
        if(this.listModel.items) {
            for(var i=0; i<this.listModel.items.length; i++) {
                ctx.fillText(this.listModel.items[i],
                    this.x+5,this.y+i*this.rh+20);
            }
        }
        
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.w,this.h);
    }
	var self = this;
    this.setup = function(root) {
        root.onPress(this,function(e) {
            console.log("clicked on listview at : ");
            console.log(e);
            var y = e.point.y - self.y;
            console.log("y = " + y + " row height = " + self.rh);
            var n = parseInt(y / self.rh,10);
            console.log("row = " + n);
            self.setSelectedIndex(n);
        });
        return this;
    }
    return this;
}
ListView.extend(AminoControl);


function TabPanel() {
    AminoControl.call(this);
    this.childs = [];
    this.th = 20; //tab height
    this.selectedIndex = 0;
    
    this.typename = "TabPanel";
	this.setWidth = function(width) {
	    this.w = width;
	    this.setDirty();
	    return this;
	};
	this.getWidth = function() {
	    return this.w;
	}
	this.setHeight = function(height) {
	    this.h = height;
	    this.setDirty();
	    return this;
	};
	this.getHeight = function() {
	    return this.h;
	}
    this.paint = function(ctx) {
        ctx.fillStyle = "#eee";
        ctx.fillRect(this.x,this.y,this.w,this.h);
        ctx.strokeStyle = "black";
        ctx.lineWidth = 2;
        ctx.strokeRect(this.x,this.y,this.w,this.h);
        
        ctx.translate(this.x,this.y);
        //draw the tabs
        var x = 0;
        var h = this.th;//ctx.measureText('M').width;
        
        var childs = this.getChildren();
        for(var i=0; i<childs.length; i++) {
            var ch = childs[i];
            var title = "foolongtitle";
            if(ch.title) {
                title = ch.title;
            }
            
            var w = ctx.measureText(ch.title).width;
            ctx.fillStyle = "red";
            if(i == this.selectedIndex) {
                ctx.fillStyle = "white";
            }
            ctx.fillRect(x,0,w+10,h+10);
            ctx.fillStyle = "black";
            ctx.fillText(title,x+5,15);
            ctx.strokeStyle = "black";
            ctx.lineWidth = 2;
            ctx.strokeRect(x,0,w+10,h+10);
            x = x + w + 10;
            ch._lastw = w + 10;
        }
        //draw the line at the bottom of the tabs
        ctx.beginPath();
        ctx.moveTo(0,h+10);ctx.lineTo(this.w,h+10);
        ctx.stroke();
        
        //draw the visible child
        ctx.translate(0,h+10);
        var tab = childs[this.selectedIndex];
        if(tab && tab.paint) {
            tab.paint(ctx);
        }
        ctx.translate(0,-(h+10));
        ctx.translate(-this.x,-this.y);
    }
    this.add = function(child) {
        this.childs.push(child);
        child.setParent(this);
        if(child.setWidth) {
            child.setWidth(this.getWidth());
        }
        if(child.setHeight) child.setHeight(this.getHeight()-this.th-10);
        this.setDirty();
        return this;
    };
    
    this.childrenModel = [];
    this.getChildren = function() {
        if(this.childrenModel &&
            this.childrenModel.items &&
            this.childrenModel.items.length > 0) {
            return this.childrenModel.items;
        }
        return this.childs;
    }
    this.setListModel = function(m) {
        this.childrenModel = m;
        this.selectedIndex = 0;
        console.log("selected = " + this.selectedIndex);
        console.log(m);
        this.setupSelectedChild();
	    if(m.listen) {
	        var self = this;
	        m.listen(function() {
                console.log("updating");
                self.setDirty();
	        });
	    }
	    this.setDirty();
	    return this;
	}
        
	this.setupSelectedChild = function() {
        var c = this.getChildren()[this.selectedIndex];
        if(c) {
            c.setParent(this);
            if(c.setX) c.setX(0);
            if(c.setY) c.setY(0);
            if(c.setWidth) {
                c.setWidth(this.getWidth());
            }
            if(c.setHeight) {
                c.setHeight(this.getHeight()-this.th-10);
            }
        }	    
    }
    
	var self = this;
    this.setup = function(root) {
        root.onPress(this,function(e) {
            if(e.point.y-self.y < self.th) {
                var x = 0;
                var px = e.point.x-self.x;
                for(var i=0; i<self.getChildren().length; i++) {
                    var ch = self.getChildren()[i];
                    if(px > x && px < x+ch._lastw) {
                        self.selectedIndex = i;
                        self.setupSelectedChild();
                        self.setDirty();
                        break;
                    }
                    x += ch._lastw;
                }
            }
        });
        return this;
    }
    return this;
}
TabPanel.extend(AminoControl);


function Spinner() {
    AminoControl.call(this);
    this.typename = "Spinner";
    this.layoutDone = false;    
    
    
    this.active = false;
    
    this.anim = new PropAnim(this,"rotation",0,90,500).setLoop(-1);
    this.setActive = function(active) {
        this.active = active;
        this.setDirty();
        if(this.active) {
            this.anim.start(); 
        } else {
            if(this.anim.playing) {
                this.anim.stop();
            }
        }
        return this;
    }
    
    this.isActive = function() {
        return this.active;
    }
    
    this.doLayout = function(ctx) {
        this.layoutDone = true;
    }
    
    this.rotation = 0;
    
    this.setRotation = function(rotation) {
        this.rotation = rotation;
        this.setDirty();
        return this;
    }
    
	this.setWidth = function(width) {
	    this.w = width;
	    this.setDirty();
	    return this;
	};
	this.getWidth = function() {
	    return this.w;
	}
	this.setHeight = function(height) {
	    this.h = height;
	    this.setDirty();
	    return this;
	};
	this.getHeight = function() {
	    return this.h;
	}
    
    this.paint = function(ctx) {
//        ctx.font = "15px sans-serif";
//        if(!this.layoutDone) this.doLayout(ctx);
        if(!this.isActive()) return;

        ctx.fillStyle = "#ccc";
        ctx.save();
        ctx.translate(this.x+this.w/2,this.y+this.h/2);
        ctx.rotate(this.rotation/3.14*180);
        var z = this.w/2;
        ctx.strokeRect(-z,-z,z*2,z*2);
        ctx.restore();
        
        ctx.save();
        ctx.translate(this.x+this.w/2,this.y+this.h/2);
        ctx.rotate(-this.rotation/3.14*180);
        var z = this.w*3/8;
        ctx.strokeRect(-z,-z,z*2,z*2);
        ctx.restore();
    }
    
    this.setup = function(root) {
        root.onPress(this,function() {
            console.log("pressed");
        });
        root.addAnim(this.anim);
        return this;
    }

    return this;
}
Spinner.extend(AminoControl);


function ListModel() {
    this.items = [];
    this.id = "foo model";
    this.setId = function(id) {
        this.id = id;
        return this;
    }
    this.add = function(item) {
        this.items.push(item);
        this.fireChange();
        return this;
    }
    this.get = function(n) {
        return this.items[n];
    }
    this.setData = function(data) {
        this.items = data;
        this.fireChange();
        return this;
    }
    this.listen = function(cb) {
        this.cb = cb;
        return this;
    }
    this.clear = function() {
        this.items = [];
        this.fireChange();
        return this;
    }
    this.fireChange = function() {
        if(this.cb) {
            this.cb(this);
        }
    }
    return this;
}

function FlickrQuery() {
    ListModel.call(this);
    var self = this;
    this.query = "london";
    this.setQuery = function(query) {
        this.query = query;
        return this;
    }
    this.active = false;
    this.isActive = function() {
        return this.active;
    }
    this.setActive = function(a) {
        this.active = a;
        this.fireChange();
        return this;
    }
    this.execute = function() {
        this.active = true;
        self.clear();
        $.getJSON('http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20flickr.photos.search%20where%20has_geo%3D%22true%22%20and%20text%3D%22'+this.query+'%22%20and%20api_key%3D%2292bd0de55a63046155c09f1a06876875%22%3B&diagnostics=true&format=json',
            function(data) {
                console.log("got back data");
                console.log(data.query.results.photo);
                console.log('photo count = ' + data.query.results.photo.length);
                var photos = data.query.results.photo;
                function addit() {
                    if(photos.length < 1) {
                        self.active = false;
                        return;
                    }
                    var p = photos.shift();
                    var url = "http://farm"+p.farm+
                    ".staticflickr.com/"+p.server+
                    "/"+p.id+"_"+p.secret+".jpg";
                    self.add({
                        title: p.title,
                        orig: p,
                        url: url,
                    });
                    console.log("added",p);
                    setTimeout(addit,200);
                }
                addit();
            });
    }
}
FlickrQuery.extend(ListModel);


