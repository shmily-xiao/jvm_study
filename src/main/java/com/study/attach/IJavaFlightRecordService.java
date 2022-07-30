package com.study.attach;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author wzj
 * @date 2022/06/10
 */
public interface IJavaFlightRecordService {
    /**
     * Returns the available JDK Flight Recorder recordings. The immutable descriptor represents the
     * state at the time the method was called. To get an updated representation the method must be
     * called again.
     *
     * @return the available Flight Recording recordings.
     * @throws FlightRecorderException
     */
    List<IRecordingDescriptor> getAvailableRecordings() throws FlightRecorderException;

    /**
     * Get a recording that best represents all the previously recorded data.
     *
     * @return a recording descriptor.
     * @throws FlightRecorderException
     */
    IRecordingDescriptor getSnapshotRecording() throws FlightRecorderException;

    /**
     * Returns the updated recording descriptor for the specified recording.
     *
     * @param descriptor
     *            the recording for which to get the recording descriptor.
     * @return the recording descriptor for the specified recording id.
     */
    IRecordingDescriptor getUpdatedRecordingDescription(IRecordingDescriptor descriptor) throws FlightRecorderException;

    /**
     * Starts a new JDK Flight Recorder recording.
     *
     * @param recordingOptions
     *            the recording options. Use {@link RecordingOptionsBuilder} to create.
     * @param eventOptions
     *            the event options.
     * @return the {@link IRecordingDescriptor} representing the started recording.
     * @throws FlightRecorderException
     *             if there was a problem starting the recording.
     */
    IRecordingDescriptor start(IRecordingDescriptor descriptor, Map<String, String> recordingOptions, Map<String, String> eventOptions)
            throws FlightRecorderException;

    /**
     * Stops the recording represented by the {@link IRecordingDescriptor}.
     *
     * @param descriptor
     *            the recording to stop.
     * @throws FlightRecorderException
     *             if there was a problem stopping the recording.
     */
    void stop(IRecordingDescriptor descriptor) throws FlightRecorderException;

    /**
     * Closes the recording represented by the {@link IRecordingDescriptor}. A closed recording will
     * no longer be listed among the available recordings. It's corresponding MBean will be removed.
     *
     * @param descriptor
     *            the recording to close.
     * @throws FlightRecorderException
     *             if there was a problem closing the recording.
     */
    void close(IRecordingDescriptor descriptor) throws FlightRecorderException;

    /**
     * Returns the recording options for the specified recording. Note that options can be changed
     * over time. The {@link IConstrainedMap} is immutable - call again to get the updated settings
     * for a particular recording.
     *
     * @param recording
     *            the recording for which to retrieve the recording options.
     * @return the {@link IConstrainedMap} for the specified recording.
     * @throws FlightRecorderException
     *             if there was a problem retrieving the options.
     */
    Map<String, String>  getRecordingOptions(IRecordingDescriptor recording) throws FlightRecorderException;

    /**
     * Returns the event settings for the specified recording.
     *
     * @param recording
     *            the recording for which to return the settings.
     * @return the event settings for the specified recording.
     * @throws FlightRecorderException
     */
    Map<String, String> getEventSettings(IRecordingDescriptor recording) throws FlightRecorderException;

    /**
     * Opens a stream from the specified recording. Will read all available data. The content of the
     * stream is gzipped. You would normally want to wrap it in a {@link GZIPInputStream}.
     *
     * @param descriptor
     *            the recording from which to retrieve the data.
     * @param removeOnClose
     *            whether the recording should be removed when the stream is closed or not
     * @return an input stream from which to read the recording data.
     * @throws FlightRecorderException
     *             if there was a problem reading the recording data.
     */
    byte[] openStream(IRecordingDescriptor descriptor, boolean removeOnClose) throws FlightRecorderException;


    /**
     * @return the server templates for event settings found on the server.
     */
    List<String> getServerTemplates() throws FlightRecorderException;

    /**
     * Updates the event options for the specified descriptor.
     *
     * @param descriptor
     *            the recording to update the event options for.
     * @param options
     *            the new, overriding, event options. If null, the current options will be used.
     */
    void updateEventOptions(IRecordingDescriptor descriptor,  Map<String, String>  options)
            throws FlightRecorderException;

    /**
     * Updates the recording options for the specified recording.
     *
     * @param descriptor
     *            the recording to update the event settings for.
     * @param options
     *            the new options to set.
     */
    void updateRecordingOptions(IRecordingDescriptor descriptor,   Map<String, String>  options)
            throws FlightRecorderException;
}
