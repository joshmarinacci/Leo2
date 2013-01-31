package com.joshondesign.treegui.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Metadata {
    boolean visual() default true;
    String exportClass() default "";
}