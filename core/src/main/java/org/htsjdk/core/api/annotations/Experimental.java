package org.htsjdk.core.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a public API as Experimental.
 *
 * <p>Experimental signifies that public API could be change or <em>removed</em> without notice
 * in future releases. A {@link Experimental} API could be upgraded to {@link Beta} if not removal
 * is predicted but API is unstable.
 *
 * @see Beta
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE
})
@Documented
public @interface Experimental {
}
