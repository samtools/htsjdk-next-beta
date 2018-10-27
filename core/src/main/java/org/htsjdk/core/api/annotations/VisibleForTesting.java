package org.htsjdk.core.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates a public API as visible for testing.
 *
 * <p>Visible for testing signifies that public API is visible only for testing purposes; thus,
 * they are not supposed to be used by client-code.
 *
 * <p>{@link VisibleForTesting} indicates the reason for make public a private API, which should
 * be also marked as {@link Private}.
 *
 * @see Private
 */
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface VisibleForTesting {
}
