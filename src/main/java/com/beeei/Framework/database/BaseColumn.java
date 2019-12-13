package com.beeei.Framework.database;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BaseColumn {

    String columnName() default "";

    String columnType() default "";

    String columnDefault() default "";

    boolean isNullable() default true;

    String columnComment() default "";
}
