package com.study.attach.impl;

import com.study.attach.IRecordingDescriptor;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.util.Map;

/**
 * @author wzj
 * @date 2022/06/10
 */
public class RecordingDescriptor implements IRecordingDescriptor {

    private static final String KEY_NAME = "name";
    private static final String KEY_ID = "id";
    private static final String KEY_STATE = "state";
    // This key does not seem to correspond with the one used to set recording options.
    private static final String KEY_TO_DISK = "toDisk";

    private final String serverId;
    private final long id;
    private final String name;
    private final RecordingState state;
    private final boolean toDisk;

    public RecordingDescriptor(String serverId, CompositeData data) {
        this.serverId = serverId;
        this.id = (Long) data.get(KEY_ID);
        this.name = (String) data.get(KEY_NAME);
        this.state = decideState((String) data.get(KEY_STATE));
        this.toDisk = (Boolean) data.get(KEY_TO_DISK);
    }
    private static RecordingState decideState(String state) {
        if ("NEW".equals(state)) {
            return RecordingState.CREATED;
        } else if ("RUNNING".equals(state) || "DELAYED".equals(state) || "STARTING".equals(state)) {
            return RecordingState.RUNNING;
        } else if ("STOPPED".equals(state)) {
            return RecordingState.STOPPED;
        } else if ("STOPPING".equals(state)) {
            return RecordingState.STOPPING;
        } else {
            return RecordingState.CLOSED;
        }
    }

    public String getServerId() {
        return serverId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RecordingState getState() {
        return state;
    }

    public boolean isToDisk() {
        return toDisk;
    }

    @Override
    public Map<String, ?> getOptions() {
        return null;
    }

    @Override
    public ObjectName getObjectName() {
        return null;
    }

    @Override
    public boolean getToDisk() {
        return toDisk;
    }
}
