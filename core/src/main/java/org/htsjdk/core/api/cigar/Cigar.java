package org.htsjdk.core.api.cigar;

import java.util.stream.StreamSupport;

/**
 * Represents a pairwise alignment using the CIGAR format.
 *
 * <p>The CIGAR (Compact Idiosyncratic Gapped Alignment Report) format is used in the
 * <a href="http://samtools.github.io/hts-specs/SAMv1.pdf">SAM specifications</a> to represent
 * alignments between a read and a reference genome, but it could also represent other pairwise
 * alignments. Here we use a nomenclature based on the alignments between a read an an reference
 * (e.g., {@link #getReferenceLength()} and {@link #getReadLength()}).
 *
 * <p>As a run-length encoded format, each element ({@link CigarElement}) of the CIGAR contains the
 * length of a concrete alignment operator ({@link CigarOperator}).
 *
 * <h2>Example
 *
 * <p>{@code 10M1D25M}:
 *
 * <ol>
 *     <li>Match or mismatch ({@link CigarOperator#M}) for 10 bases.</li>
 *     <li>Deletion ({@link CigarOperator#D}) of 1 bases.</li>
 *     <li>Match or mismatch ({@link CigarOperator#M}) for 25 bases.</li>
 * </ol>
 *
 * @implSpec an empty {@link Cigar} represents an undefined alignment.
 */
public interface Cigar extends Iterable<CigarElement> {

    /**
     * Gets the number of elements on this CIGAR.
     *
     * @return number of elements on the cigar.
     */
    public int size();

    /**
     * Checks if this {@link Cigar} does not contain any element.
     *
     * @return {@code true} if the CIGAR is empty; {@code false} otherwise.
     * @implNote default implementation checks if {@link #size()} returns {@code 0}.
     * @implSpec return value should be equivalent to default implementation.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Gets the CIGAR element at position {@code i}.
     *
     * @param i index for the CIGAR element (0-based).
     * @return the element at position {@code i}.
     * @throws IndexOutOfBoundsException if the index does not fit into the cigar elements.
     */
    // TODO: should we throw an HtsjdkException
    public CigarElement getCigarElement(final int i);

    /**
     * Returns the CIGAR element at the first position.
     *
     * @return the element at position {@code 0}.
     * @implNote default implementation returns {@code getCigarElement(0)}.
     * @implSpec return value should be equivalent to default implementation.
     */
    default CigarElement getFirstCigarElement() {
        return getCigarElement(0);
    }

    /**
     * Returns the CIGAR element at the last position.
     *
     * @return the element at position {@code 0}.
     * @implNote default implementation returns {@code getCigarElement(size() - 1)}.
     * @implSpec return value should be equivalent to default implementation.
     */
    default CigarElement getLastCigarElement() {
        return getCigarElement(this.size() - 1);
    }

    /**
     * Gets the length on the reference for this CIGAR (excluding padding).
     *
     * @return number of reference bases that the cigar covers.
     */
    default int getReferenceLength() {
        return StreamSupport.stream(spliterator(), false)
                .filter(CigarElement::consumesReferenceBases)
                .mapToInt(CigarElement::getLength).sum();
    }

    /**
     * Gets the length on the reference for this CIGAR (including padding).
     *
     * @return number of reference bases that the cigar covers with padding.
     */
    default int getPaddedReferenceLength() {
        return StreamSupport.stream(spliterator(), false)
                .filter(e -> e.consumesReferenceBases() || e.getOperator() == CigarOperator.P)
                .mapToInt(CigarElement::getLength).sum();
    }

    /**
     * Gets the length on the read for this CIGAR.
     *
     * @return number of read bases that the cigar covers.
     */
    default int getReadLength() {
        return StreamSupport.stream(spliterator(), false)
                .filter(CigarElement::consumesReadBases)
                .mapToInt(CigarElement::getLength).sum();
    }

    /**
     * Checks if the CIGAR contains a concrete operator.
     *
     * @param operator operator to check for.
     * @return {@code true} if the operator is found in at least one element; {@code false} otherwise.
     */
    default boolean containsOperator(final CigarOperator operator) {
        return StreamSupport.stream(spliterator(), false)
                .anyMatch(element -> element.getOperator() == operator);
    }
    
    /** returns true if the cigar string starts With a clipping operator */
    /**
     * Checks if the CIGAR is left-clipped.
     *
     * @return {@code true} if the first element represents a clip; {@code false} otherwise.
     */
    default boolean isLeftClipped() {
        return getFirstCigarElement().getOperator().isClipping();
    }

    /**
     * Checks if the CIGAR is right-clipped.
     *
     * @return {@code true} if the last element represents a clip; {@code false} otherwise.
     */
    default boolean isRightClipped() {
        return getLastCigarElement().getOperator().isClipping();
    }

    /**
     * Checks if the CIGAR is clipped in any extreme.
     *
     * @return {@code true} if at least one extreme represents a clip; {@code false} otherwise.
     */
    default boolean isClipped() {
        return isLeftClipped() || isRightClipped();
    }
}
