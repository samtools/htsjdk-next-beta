package org.htsjdk.core.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a public API as Beta.
 *
 * <p>Beta signifies that public API could be change without notice in future releases, but
 * <em>not removed</em>. Removal of {@link Beta} annotation signifies that the public API is
 * stable and changes suppose a major version bump.
 *
 * @see Experimental
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
public @interface Beta {
}
