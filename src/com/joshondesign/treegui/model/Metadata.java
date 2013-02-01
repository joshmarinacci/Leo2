package com.joshondesign.treegui.model;

import com.joshondesign.treegui.docmodel.Resize;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Metadata {
    String name() default "unnamed";
    boolean visual() default true;
    String exportClass() default "";
    Resize resize() default Resize.Any;
    boolean container() default false;
}