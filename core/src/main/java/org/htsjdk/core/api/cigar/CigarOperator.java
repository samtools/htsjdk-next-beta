package org.htsjdk.core.api.cigar;

/**
 * Operations supported in the {@link Cigar} format.
 *
 * <p>Includes both standard ({@link #M}, {@link #I}, {@link #D}) and extended CIGAR elements
 *
 * @see Cigar
 */
public enum CigarOperator {
    /** Match or mismatch */
    M(true, true),
    /** Insertion vs. the reference. */
    I(true, false),
    /** Deletion vs. the reference. */
    D(false, true),
    /** Skipped region from the reference. */
    N(false, true),
    /** Soft clip. */
    S(true, false),
    /** Hard clip. */
    H(false, false),
    /** Padding. */
    P(false, false),
    /** Matches the reference. */
    EQ(true, true),
    /** Mismatches the reference. */
    X(true, true);

    private final boolean consumesReadBases;
    private final boolean consumesReferenceBases;

    /**
     * Default constructor.
     *
     * @param consumesReadBases      {@code true} if it consumes read bases.
     * @param consumesReferenceBases {@code true} if it consumes reference bases.
     */
    CigarOperator(boolean consumesReadBases, boolean consumesReferenceBases) {
        this.consumesReadBases = consumesReadBases;
        this.consumesReferenceBases = consumesReferenceBases;
    }

    /**
     * Checks if the operator "consume" bases from the reads.
     *
     * @return {@code true} if the operator "consume" bases; {@code false} otherwise.
     */
    public boolean consumesReadBases() {
        return consumesReadBases;
    }

    /**
     * Checks if the operator "consume" bases from the reference.
     *
     * @return {@code true} if the operator "consume" bases; {@code false} otherwise.
     */
    public boolean consumesReferenceBases() {
        return consumesReferenceBases;
    }

    /**
     * Checks if the operator represents a clip (hard or soft).
     *
     * <p>Operators representing clips are {@link #S} and {@link #H}.
     *
     * @return {@code true} if the operator represent a clip; {@code false} otherwise.
     */
    public boolean isClipping() {
        return this == S || this == H;
    }

    /**
     * Checks if the operator represents an indel (insertion or deletion).
     *
     * <p>Operators representing indels are {@link #I} and {@link #D}.
     *
     * @return {@code true} if the operator represent an indel; {@code false} otherwise.
     */
    public boolean isIndel() {
        return this == I || this == D;
    }

    /**
     * Checks if the operator represents an alignment (match or mismatch).
     *
     * <p>Operators representing clips are {@link #M}, {@link #X} and {@link #EQ}.
     *
     * @return {@code true} if the operator represent an alignment; {@code false} otherwise.
     */
    public boolean isAlignment() {
        return this == M || this == X || this == EQ;
    }
}
