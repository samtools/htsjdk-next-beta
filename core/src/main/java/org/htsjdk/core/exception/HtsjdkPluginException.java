package org.htsjdk.core.exception;

/**
 * Base class for exceptions resulting from ill-behaved codec plugins
 */
public class HtsjdkPluginException extends HtsjdkException {
    /**
     * Constructs an HTSJDK exception.
     *
     * @param message detailed message.
     */
    public HtsjdkPluginException(String message) {
        super(message);
    }

}
