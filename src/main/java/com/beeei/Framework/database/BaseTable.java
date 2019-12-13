package com.beeei.Framework.database;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BaseTable {

    /**
     * (Optional) The name of the table.
     * <p/>
     * Defaults to the entity name.
     */
    String name() default "";

    /**
     * (Optional) Indexes for the table. These are only used if table generation is in effect.  Defaults to no
     * additional indexes.
     * @return The indexes
     */
    BaseIndex[] indexes() default {};

}