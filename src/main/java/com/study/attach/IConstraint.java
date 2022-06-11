package com.study.attach;

/**
 * A constraint on allowed instances of an existing type {@code T}, including constrained
 * conversions to and from persistable and interactive strings.
 *
 * @param <T>
 *            the type of values that the constraint operates on
 */
/*
 * FIXME: Separate persistence to a has-a, rather than a is-a? Or maybe only in subclasses (and
 * delegate)? Same with interactive? That is, make two narrowed down interfaces.
 */
public interface IConstraint<T> {
	/**
	 * Return a constraint that honors both this constraint and {@code other}, if such a constraint
	 * would accept anything except {@code null}. Otherwise, return {@code null}.
	 *
	 * @return a constraint or {@code null}
	 */
	IConstraint<T> combine(IConstraint<?> other);

	/**
	 * Fundamentally, check that {@code value} satisfies this constraint and throw an exception
	 * otherwise. As long as the method returns normally, {@code value} is a valid value, regardless
	 * of the return value. However, when wrapping a persister in a constraint, it is possible that
	 * the persister treats some magic values differently. If the constraint isn't aware of these
	 * magical values it should typically not try to validate them. This is signaled by the
	 * persister by returning true from this method.
	 *
	 * @return true if this value is considered magical and further validation should be skipped,
	 *         false otherwise. Any return value mean that the {@code value} is valid.
	 * @throws NullPointerException
	 *             if {@code value} is null and this constraint doesn't allow it
	 * @throws IllegalArgumentException
	 *             if some type aspect of {@code value} prevents it from being valid
	 */
	boolean validate(T value)  ;

	/**
	 * A string representation independent of locale or internationalization, that when parsed using
	 * {@link #parsePersisted(String)} (on this instance) yields a result that is
	 * {@link Object#equals(Object) equal} to the given {@code value}. That is, the exact
	 * representation must be preserved.
	 *
	 * @return a string representation independent of locale or internationalization.
	 * @throws NullPointerException
	 *             if {@code value} is null and this constraint doesn't allow it
	 * @throws IllegalArgumentException
	 *             if some type aspect of {@code value} prevents it from being valid
	 */
	String persistableString(T value)  ;

	/**
	 * Parse a persisted string. Only guaranteed to be able to parse strings produced by
	 * {@link #persistableString(Object)} on this instance. Only use this on persisted strings,
	 * never for interactive input.
	 *
	 * @return a valid value for this instance
	 * @throws NullPointerException
	 *             if {@code persistedValue} is null
	 */
	T parsePersisted(String persistedValue)  ;

	/**
	 * An exact string representation taking locale and internationalization into account. When
	 * parsed using {@link #parseInteractive(String)} (on this instance) yields a result that is
	 * {@link Object#equals(Object) equal} to the given {@code value}. That is, the exact
	 * representation must be preserved.
	 *
	 * @return a string representation taking locale and internationalization into account.
	 * @throws NullPointerException
	 *             if {@code value} is null and this constraint doesn't allow it
	 * @throws IllegalArgumentException
	 *             if some type aspect of {@code value} prevents it from being valid
	 */
	String interactiveFormat(T value)  ;

	/**
	 * Parse an interactive string. Only guaranteed to be able to parse strings produced by
	 * {@link #interactiveFormat(Object)} on this instance and in the same locale. Only use this for
	 * interactive input, never for persisted strings.
	 *
	 * @return a valid value for this instance
	 * @throws NullPointerException
	 *             if {@code interactiveValue} is null
	 */
	T parseInteractive(String interactiveValue)  ;
}
