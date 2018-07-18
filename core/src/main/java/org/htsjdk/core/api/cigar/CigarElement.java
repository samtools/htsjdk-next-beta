package org.htsjdk.core.api.cigar;

/**
 * Represents each element of a {@link Cigar}.
 *
 * <p>As a run-length encoded format, each element of the CIGAR contains the
 * length ({@link #getLength()}) of a concrete alignment operator ({@link #getOperator()}).
 *
 * @see CigarOperator
 */
public interface CigarElement {

    /**
     * Gets the length of the element.
     *
     * @return element length.
     */
    public int getLength();

    /**
     * Gets the operator of the element.
     *
     * @return element operator.
     */
    public CigarOperator getOperator();

    /**
     * Checks if the element's operator "consume" bases from the reads.
     *
     * @return {@code true} if the operator "consume" bases; {@code false} otherwise.
     *
     * @implNote default implementation returns {@code getOperator().consumesReadBases()}.
     * @implSpec return value should be equivalent to the default implementation.
     */
    default boolean consumesReadBases() {
        // sugar syntax
        return getOperator().consumesReadBases();
    }

    /**
     * Checks if the element's operator "consume" bases from the reference.
     *
     * @return {@code true} if the operator "consume" bases; {@code false} otherwise.
     *
     * @implNote default implementation returns {@code getOperator().consumesReferenceBases()}.
     * @implSpec return value should be equivalent to the default implementation.
     */
    default boolean consumesReferenceBases() {
        // sugar syntax
        return getOperator().consumesReferenceBases();
    }

    /**
     * Checks if the element's operator represents a clip (hard or soft).
     *
     * @return {@code true} if the operator represent a clip; {@code false} otherwise.
     *
     * @see CigarOperator#isIndel()
     */
    default boolean isClipping() {
        return getOperator().isClipping();
    }

    /**
     * Checks if the element's operator represents an indel (insertion or deletion).
     *
     * @return {@code true} if the operator represent an indel; {@code false} otherwise.
     *
     * @see CigarOperator#isIndel()
     */
    default boolean isIndel() {
        return getOperator().isIndel();
    }

    /**
     * Checks if the element's operator represents an alignment (match or mismatch).
     *
     * @return {@code true} if the operator represent an alignment; {@code false} otherwise.
     *
     * @see CigarOperator#isIndel()
     */
    default boolean isAlignment() {
        return getOperator().isAlignment();
    }
}
