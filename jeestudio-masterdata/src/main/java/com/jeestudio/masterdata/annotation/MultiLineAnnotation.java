package com.jeestudio.masterdata.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiLineAnnotation {
    String maxlength();
    String maxline();
}
