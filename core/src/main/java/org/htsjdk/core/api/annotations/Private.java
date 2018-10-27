package org.htsjdk.core.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a public API as Private.
 *
 * <p>Private signifies that public API should be considered as private code and not used by
 * client-code. This API will be changed without notice as its usage only supported when it is
 * internal.
 *
 * <p>Examples:
 *
 * <ul>
 *     <li>Internal packages ("*.internal").</li>
 *     <li>Public API only for sharing internaly but that should be located in non-internal packages.</li>
 *     <li>Public API only for testing (e.g., {@link VisibleForTesting}.</li>
 * </ul>
 *
 * @see VisibleForTesting
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.PACKAGE,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE
})
@Documented
public @interface Private {
}
