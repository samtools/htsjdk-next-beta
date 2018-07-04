package org.htsjdk.core.api;

/**
 * Represents the location of a sequence with respect to another sequence (reference).
 *
 * <p>
 * This interface represents a location or "mapping" with respect to a reference sequence. Examples
 * of this abstraction are features located on the genome or alignment between two sequences (query
 * with respect to a reference).
 * </p>
 *
 * @implSpec note that 0-length intervals are allowed for intervals with undefined end.
 */
public interface Locatable {

    /**
     * Represents the reference name for unplaced locations.
     *
     * @see #getRefName()
     */
    public static final String UNPLACED_REF_NAME = "*";

    /**
     * Gets the reference name where this location is placed.
     *
     * <p>
     * {@link Locatable} should either be placed on a reference sequence or being unplaced
     * ({@link #UNPLACED_REF_NAME}).
     * </p>
     *
     * @return name of the reference for the location; {@link #UNPLACED_REF_NAME} if unplaced.
     * Never {@code null}
     */
    String getRefName();

    /**
     * Gets the start position on the reference (1-based).
     *
     * <p>
     * {@link Locatable} should have a non-negative location on the reference sequence; if unplaced
     * ({@code getReferenceName() == UNPLACED_REF_NAME}), position is undefined.
     * </p>
     *
     * @return 1-based start position on the reference; undefined if {@link #getRefName()}
     * returns {@link #UNPLACED_REF_NAME}.
     */
    long getStart();

    /**
     * Gets the end position on the reference (1-based fully-closed).
     *
     * @return 1-based fully-closed if present; {@code getStart() - 1} otherwise.
     *
     * @implSpec if present, end position should be downstream te start position ({@code getStart()
     * > getEnd()}); otherwise, {@code getEnd() == getStart() - 1 }.
     * @see #getStart()
     */
    long getEnd();

    /**
     * Gets the number of bases of reference covered by this location.
     *
     * <p>
     * Returns the number of bases on te reference from the start to the end (independently of the
     * alignment, if any). 0-lenght intervals represents the special case of undetermined eng.
     * </p>
     *
     * @return number of bases of reference; 0 for undefined end.
     *
     * @implNote default implementation uses {@code getEnd() - getStart() + 1} to compute
     * the length on the reference.
     * @implSpec return value should be the same as the default implementation, but subclasses could
     * override to more efficient implementations.
     * @see #getStart()
     * @see #getEnd()
     */
    default long getLengthOnReference() {
        return getEnd() - getStart() + 1;
    }

    /**
     * Determines whether this location overlaps with other.
     *
     * @param other location to test.
     *
     * @return {@code true} if this location overlaps other; {@code false} otherwise.
     *
     * @implNote default implementation returns {@code withinDistanceOf(other, 0)}.
     * @implSpec should return {@code false} if {@code referenceMatch(other) == false}.
     * @see #withinDistanceOf(Locatable, int)
     */
    // TODO: default implementation?
    default boolean overlaps(Locatable other) {
        return withinDistanceOf(other, 0);
    }

    /**
     * Determines whether this location is within a {@code distance} of other (overlap with margin).
     *
     * @param other    location to test.
     * @param distance number of bases between the intervals to use as padding for test the
     *                 overlap.
     *
     * @return {@code true} if this location overlaps other with the provided margin; {@code false}
     * otherwise.
     *
     * @implSpec should return {@code false} if {@code referenceMatch(other) == false}.
     */
    // TODO: default implementation?
    boolean withinDistanceOf(Locatable other, int distance);

    /**
     * Determines whether this location contains entirely other.
     *
     * <p>
     * A location contains the entire location represented by other when it contains all positions
     * spanned by the other (or more).
     * </p>
     *
     * @param other location to test.
     *
     * @return {@code true} if this location contains the other; {@code false} otherwise.
     *
     * @implSpec should return {@code false} if {@code referenceMatch(other) == false}.
     */
    // TODO: default implementation?
    boolean contains(Locatable other);

    /**
     * Determines if this location is on the same reference as other.
     *
     * @return {@code true} if both locations are in the same reference; {@code false} otherwise.
     *
     * @implNote default implementation compares {@link #getRefName()} values.
     * @implSpec should return the same value as {@code this.getRefName().equals(other.getRefName())}.
     * @see #getRefName()
     */
    // TODO: default implementation?
    boolean referenceMatch(Locatable other);
}
