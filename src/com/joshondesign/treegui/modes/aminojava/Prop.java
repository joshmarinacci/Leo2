package com.joshondesign.treegui.modes.aminojava;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
* Created with IntelliJ IDEA.
* User: josh
* Date: 1/18/13
* Time: 8:50 PM
* To change this template use File | Settings | File Templates.
*/
@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {
    boolean bindable() default true;
    boolean visible() default true;
    boolean exported() default true;
}
