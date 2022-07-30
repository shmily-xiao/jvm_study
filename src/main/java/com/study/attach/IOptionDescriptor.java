
package com.study.attach;

// FIXME: Move to flightrecorder.configuration unless used in relation to IDescribedMap.
public interface IOptionDescriptor<T> extends IDescribable {
	IConstraint<T> getConstraint();

	T getDefault();
}
