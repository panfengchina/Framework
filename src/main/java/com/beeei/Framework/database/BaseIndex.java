package com.beeei.Framework.database;

import java.lang.annotation.*;

@Target(value = ElementType.ANNOTATION_TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BaseIndex {

    /**
     * (Optional) The name of the index.  Defaults to a provider-generated value.
     *
     * @return The index name
     */
    String name() default "";

    /**
     * (Required) The names of the columns to be included in the index.
     *
     * @return The names of the columns making up the index
     */
    String columns();

    /**
     * (Optional) Whether the index is unique.  Default is false.
     *
     * @return Is the index unique?
     */
    boolean unique() default false;
}