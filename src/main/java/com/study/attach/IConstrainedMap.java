package com.study.attach;

import java.util.Set;

/**
 * Read only interface for a map where the values are constrained by an {@link IConstraint} per key.
 * Note that as this is read only, its methods shouldn't throw any exceptions. This means that all
 * implementations must ensure upon creation and insertion that their contents are valid.
 *
 * @param <K>
 *            the type of the keys in the map
 */
public interface IConstrainedMap<K> {

	/**
	 * @return A {@link Set set} of keys which are known to be valid. That is, those that currently
	 *         are known to have a {@link IConstraint constraint}. This includes all keys which
	 *         currently have a value, but additional keys may be included. In other words,
	 *         {@link #get(Object)} may return {@code null} for some keys included in this set.
	 */
	Set<K> keySet();

	/**
	 * Get the mapped value for {@code key}, or null if no value is currently mapped. If this method
	 * ever returns a non-null value, {@link #getConstraint(Object)} for the same {@code key} will
	 * from that point forward return the same matching non-null constraint.
	 *
	 * @return the mapped value or {@code null}
	 */
	Object get(K key);

	/**
	 * Get a {@link IConstraint constraint} for mapped values of {@code key}, if a constraint has
	 * been imposed for {@code key}.
	 *
	 * @return a constraint or {@code null}
	 */
	IConstraint<?> getConstraint(K key);

	/**
	 * Get the persistable string of the mapped value for {@code key}, or null if no value is
	 * currently mapped. If this method ever returns a non-null value,
	 * {@link #getConstraint(Object)} for the same {@code key} will from that point forward return
	 * the same matching non-null constraint.
	 *
	 * @return a persistable string or {@code null}
	 */
	String getPersistableString(K key);

	/**
	 * Create an empty {@link IMutableConstrainedMap mutable} map, with the same initial constraints
	 * as this {@link IConstrainedMap map}. It might be possible to add {@link IConstraint
	 * constraints} to the created map, depending on the restrictions built into this map.
	 */
	IMutableConstrainedMap<K> emptyWithSameConstraints();

	/**
	 * Create a {@link IMutableConstrainedMap mutable} copy of this {@link IConstrainedMap map},
	 * containing the same initial values as this map. It might be possible to add
	 * {@link IConstraint constraints} to the copy, depending on the restrictions built into this
	 * map.
	 */
	IMutableConstrainedMap<K> mutableCopy();
}
