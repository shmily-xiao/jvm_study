package com.study.attach;

import javax.management.ObjectName;
import java.util.Map;

/**
 * Interface that describe a JDK Flight Recorder recording.
 */
public interface IRecordingDescriptor {
	/**
	 * Defines the possible states a recording can be in. {@link RecordingState#CREATED}
	 * {@link RecordingState#RUNNING} {@link RecordingState#STOPPED}
	 */
	public enum RecordingState {
	/**
	 * The Recording has been created but not yet started.
	 */
		CREATED,
		/**
		 * The recording is running, i.e. it has been started, but not yet stopped.
		 */
		RUNNING,
		/**
		 * The recording has been started, and is stopping, but has not fully completed.
		 */
		STOPPING,
		/**
		 * The recording has been started, and then stopped. Either because the recording duration timed
		 * out, or because it was forced to stop.
		 */
		STOPPED,

		CLOSED;
	}

	/**
	 * Returns the id value of the recording.
	 *
	 * @return the id value of the recording.
	 */
	Long getId();

	/**
	 * Returns the symbolic name of the recording.
	 *
	 * @return the symbolic name of the recording.
	 */
	String getName();

	/**
	 * Returns the state of the recording when this {@link IRecordingDescriptor} was created.
	 *
	 * @return the state of the recording when this {@link IRecordingDescriptor} was created.
	 */
	RecordingState getState();

	/**
	 * Returns a Map&lt;String, Object&gt; with values that describes the various options in the
	 * recording. Options can, for instance, be duration and destFile.
	 *
	 * @return a Map&lt;String, Object&gt; with values that describes the various options in the
	 *         recording.
	 */
	Map<String, ?> getOptions();

	/**
	 * Returns the object name used to locate the MBean that is used to manage this recording.
	 *
	 * @return the object name used to locate the MBean that is used to manage this recording.
	 */
	ObjectName getObjectName();

	/**
	 * Returns whether the recording is stored to disk.
	 *
	 * @return <tt>true</tt> if the recording is stored to disk, <tt>false</tt> otherwise
	 */
	public boolean getToDisk();

}
