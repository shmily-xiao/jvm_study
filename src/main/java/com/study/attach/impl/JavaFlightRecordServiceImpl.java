package com.study.attach.impl;

import com.study.attach.*;
import com.study.attach.integration.FlightRecorderExecutor;

import javax.management.openmbean.*;
import java.io.InputStream;
import java.util.*;

/**
 * @author wzj
 * @date 2022/06/10
 */
public class JavaFlightRecordServiceImpl implements IJavaFlightRecordService {

    @Override
    public List<IRecordingDescriptor> getAvailableRecordings() throws FlightRecorderException {
        FlightRecorderExecutor instance = FlightRecorderExecutor.INSTANCE;
        try {
            CompositeData[] recordings = (CompositeData[]) instance.getJfrAttribute("Recordings");
            List<IRecordingDescriptor> list = new ArrayList<>(recordings.length);
            for (CompositeData data : recordings) {
                list.add(new RecordingDescriptor(UUID.randomUUID().toString(), data));
            }
            return Collections.unmodifiableList(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public IRecordingDescriptor getSnapshotRecording() throws FlightRecorderException {
        FlightRecorderExecutor instance = FlightRecorderExecutor.INSTANCE;
        try {
            Long id = (Long) instance.invokeJFROperation("takeSnapshot", null, null);
            return getUpdatedRecordingDescriptor(id);
        } catch (Exception e) {
            throw new FlightRecorderException("Could not take a snapshot of the flight recorder", e);
        }
    }

    @Override
    public IRecordingDescriptor getUpdatedRecordingDescription(IRecordingDescriptor descriptor) throws FlightRecorderException {
        return getUpdatedRecordingDescriptor(descriptor.getId());
    }


    private IRecordingDescriptor getUpdatedRecordingDescriptor(Long id) throws FlightRecorderException {
        // getRecordingOptions doesn't quite contain all we need, so retrieve
        // everything and filter out what we need...
        for (IRecordingDescriptor recording : getAvailableRecordings()) {
            if (id.equals(recording.getId())) {
                return recording;
            }
        }
        return null;
    }


    @Override
    public IRecordingDescriptor start(IRecordingDescriptor descriptor, Map<String, String> recordingOptions, Map<String, String> eventOptions) throws FlightRecorderException {
        updateEventOptions(descriptor, eventOptions);
        updateRecordingOptions(descriptor, recordingOptions);

        Object[] params = new Object[]{descriptor.getId()};
        String[] signature = new String[]{long.class.getName()};

        instance.invokeJFROperation("startRecording", params, signature);
        return getUpdatedRecordingDescriptor(descriptor.getId());
    }

    @Override
    public void stop(IRecordingDescriptor descriptor) throws FlightRecorderException {
        Object[] params = new Object[]{descriptor.getId()};
        String[] signature = new String[]{long.class.getName()};
        instance.invokeJFROperation("stopRecording", params, signature);
    }

    @Override
    public void close(IRecordingDescriptor descriptor) throws FlightRecorderException {
        Object[] params = new Object[]{descriptor.getId()};
        String[] signature = new String[]{long.class.getName()};
        instance.invokeJFROperation("closeRecording", params, signature);
    }

    @Override
    public Map<String, String>  getRecordingOptions(IRecordingDescriptor recording) throws FlightRecorderException {
        Map<String, String> options =  new LinkedHashMap<>();
        TabularData tabularData = (TabularData)instance.invokeJFROperation("getRecordingOptions", new Object[]{recording.getId()}, new String[]{long.class.getName()});

        for (Object o : tabularData.values()) {
            CompositeData row = (CompositeData) o;
            String key = (String) row.get("key");
            String value = (String) row.get("value");

            if (null != key && null != value){
                options.put(key, value);
            }
        }
        return options;
    }

    @Override
    public Map<String, String> getEventSettings(IRecordingDescriptor recording) throws FlightRecorderException {
        try {
            TabularData tabularData = (TabularData)instance.invokeJFROperation("getRecordingSettings", new Object[]{recording.getId()}, new String[]{long.class.getName()});
            Map<String, String> settings = new LinkedHashMap<>();
            for (Object row : tabularData.values()) {
                CompositeData data = (CompositeData) row;
                String key = (String) data.get("key"); //$NON-NLS-1$
                String value = (String) data.get("value"); //$NON-NLS-1$
                settings.put(key,value);
            }
            return settings;
        } catch (Exception e) {
            FlightRecorderException flr = new FlightRecorderException("Could not retrieve recording options for recording " + recording.getName() + '.');
            flr.initCause(e);
            throw flr;
        }
    }


    @Override
    public byte[] openStream(IRecordingDescriptor descriptor, boolean removeOnClose) throws FlightRecorderException {

        Map<String, String> options = new HashMap<>(3);
        // 毫秒
//        options.put("startTime", "0");
//        options.put("endTime", "2");
        options.put("blockSize", "5000000");
        long streamId = 0;
        try {
            Object[] paramsClone = new Object[]{descriptor.getId(), true};
            String[] signatureClone = new String[]{long.class.getName(), boolean.class.getName()};
            long recordCloneId = (long) instance.invokeJFROperation("cloneRecording", paramsClone, signatureClone);

            Object[] params = new Object[]{recordCloneId, toTabularData(options)};
            String[] signature = new String[]{long.class.getName(), TabularData.class.getName()};
            streamId = (long) instance.invokeJFROperation("openStream", params, signature);

            Object[] paramsReadStream = new Object[]{streamId};
            String[] signatureReadStream = new String[]{long.class.getName()};
            byte[] streams = (byte[]) instance.invokeJFROperation("readStream", paramsReadStream, signatureReadStream);

            return streams;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (streamId != 0){
                Object[] paramsCloseStream = new Object[]{streamId};
                String[] signatureCloseStream = new String[]{long.class.getName()};
                instance.invokeJFROperation("closeStream", paramsCloseStream, signatureCloseStream);
            }
        }
        return null;
    }

    @Override
    public List<String> getServerTemplates() throws FlightRecorderException {
        return null;
    }

    @Override
    public void updateEventOptions(IRecordingDescriptor descriptor,  Map<String, String>  options) throws FlightRecorderException {
        Map<String, String>  current = getEventSettings(descriptor);
        Map<String, String> mergeData = extractDelta(options, current);
        try {
            Object[] params = new Object[]{descriptor.getId(), toTabularData(mergeData)};
            String[] signature = new String[]{long.class.getName(), TabularData.class.getName()};
            instance.invokeJFROperation("setRecordingSettings", params, signature);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    final static TabularType OPTIONS_TYPE;
    final static CompositeType OPTIONS_ROW_TYPE;
    static FlightRecorderExecutor instance;

    static {
        instance = FlightRecorderExecutor.INSTANCE;
        OPTIONS_ROW_TYPE = createOptionsRowType();
        OPTIONS_TYPE = createOptionsType(OPTIONS_ROW_TYPE);
    }

    private static CompositeType createOptionsRowType() {
        String typeName = "java.util.Map<java.lang.String, java.lang.String>";
        String[] keyValue = new String[] {"key", "value"};
        OpenType<?>[] openTypes = new OpenType[] {SimpleType.STRING, SimpleType.STRING};
        try {
            return new CompositeType(typeName, typeName, keyValue, keyValue, openTypes);
        } catch (OpenDataException e) {
            // Will never happen
            return null;
        }
    }

    private static TabularType createOptionsType(CompositeType rowType) {
        try {
            return new TabularType(rowType.getTypeName(), rowType.getTypeName(), rowType, new String[] {"key"});
        } catch (OpenDataException e) {
            // Will never happen
            return null;
        }
    }

    @Override
    public void updateRecordingOptions(IRecordingDescriptor descriptor, Map<String,String> options) throws FlightRecorderException {
        Map<String, String>  current = getRecordingOptions(descriptor);
        Map<String, String> stringIConstrainedMap = extractDelta(options, current);

        try {
            Object[] params = new Object[]{descriptor.getId(), toTabularData(stringIConstrainedMap)};
            String[] signature = new String[]{long.class.getName(), TabularData.class.getName()};
            instance.invokeJFROperation("setRecordingOptions", params, signature);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static TabularData toTabularData(Map<String, String> settings) throws OpenDataException {
        TabularDataSupport tdata = new TabularDataSupport(OPTIONS_TYPE);
        for (String key : settings.keySet()) {
            String value = settings.get(key);
            if (value != null) {
                tdata.put(new CompositeDataSupport(OPTIONS_ROW_TYPE, new String[] {"key", "value"}, new String[] {key, value}));
            }
        }
        return tdata;
    }

    public static Map<String, String> extractDelta(Map<String, String> options, Map<String, String> baseline) {
        Map<String, String> deltas = new LinkedHashMap<>();
        for (String key : options.keySet()) {
            String value = options.get(key);
            if ((value != null) && !value.equals(baseline.get(key))) {
                try {
                    deltas.put(key, value);
                } catch (Exception e) {
                }
            }
        }
        return deltas;
    }
}
