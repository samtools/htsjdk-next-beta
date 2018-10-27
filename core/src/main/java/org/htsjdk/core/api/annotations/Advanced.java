package org.htsjdk.core.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a public API as Advanced.
 *
 * <p>Advanced signifies that public API is for advance-usage only, and typical usages are covered
 * by other API.
 *
 * <p>Examples:
 *
 * <ul>
 *     <li>
 *         Setters that might left the record in an inconsistent stage. Setters with validation
 *         and/or extra-steps might be used instead, unless necessary.
 *     </li>
 *     <li>
 *         Specialized classes that might be used in delegates or wrappers, but where the major
 *         functionality could be used through the interface.
 *     </li>
 *     <li>
 *         Methods containing unsafe operations, where a different method could do the same operation
 *         in a safer way in most of the cases.
 *     </li>
 * </ul>
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
public @interface Advanced {
}
