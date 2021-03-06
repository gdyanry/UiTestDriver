package com.yanry.driver.core.distribute;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.runtime.communicator.SerializedCommunicator;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import com.yanry.driver.core.model.runtime.record.CommunicateRecord;
import yanry.lib.java.model.json.JSONArray;
import yanry.lib.java.model.json.JSONObject;
import yanry.lib.java.model.log.Logger;
import yanry.lib.java.util.object.ObjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by rongyu.yan on 3/14/2017.
 */
public class ServerReception extends SerializedCommunicator {
    private StateSpace stateSpace;
    private long timestamp;
    private JSONObject lastInstruction;
    private SynchronousQueue<JSONObject> instructionQueue;
    private SynchronousQueue<String> feedbackQueue;
    private boolean isAbort;

    /**
     * @param stateSpace
     * @return return an array containing optional paths.
     */
    public JSONArray prepare(StateSpace stateSpace) {
        stateSpace.setCommunicator(this);
        this.stateSpace = stateSpace;
        instructionQueue = new SynchronousQueue(true);
        feedbackQueue = new SynchronousQueue<>(true);
        List<Path> paths = stateSpace.getConcernedPaths();
        JSONArray jsonArray = new JSONArray();
        for (Path path : paths) {
            jsonArray.put(ObjectUtil.getPresentation(path));
        }
        return jsonArray;
    }

    /**
     * @param indexesToTraverse cannot be null. An empty array indicates traversing all paths.
     * @param executor
     * @return return a json object whose value is an array with the first element indicating repeat count
     * and the second element indicating an instruction.
     */
    public JSONObject traverse(JSONArray indexesToTraverse, Executor executor) {
        int[] indexes = null;
        int length = indexesToTraverse.length();
        if (length > 0) {
            indexes = new int[length];
            for (int i = 0; i < length; i++) {
                indexes[i] = indexesToTraverse.getInt(i);
            }
        }
        int[] finalIndexes = indexes;
        executor.execute(() -> {
            ArrayList<CommunicateRecord> result = stateSpace.traverse(finalIndexes);
            JSONArray ja = new JSONArray();
            for (Object o : result) {
                ja.put(ObjectUtil.getPresentation(o));
            }
            lastInstruction = new JSONObject().put(Const.RESPONSE_TYPE_RECORD, ja);
            try {
                instructionQueue.put(lastInstruction);
            } catch (InterruptedException e) {
                Logger.getDefault().catches(e);
            }
        });
        try {
            return instructionQueue.take();
        } catch (InterruptedException e) {
            Logger.getDefault().catches(e);
            return null;
        }
    }

    /**
     * @param feedback
     * @param timestamp
     * @return return a json object whose value is either an array with the first element indicating repeat count
     * and the second element indicating an instruction or an array of test records, differentiated by the key.
     */
    public JSONObject interact(String feedback, long timestamp) {
        if (timestamp == this.timestamp) {
            return lastInstruction;
        }
        this.timestamp = timestamp;
        try {
            feedbackQueue.put(feedback);
            return instructionQueue.take();
        } catch (InterruptedException e) {
            Logger.getDefault().catches(e);
            return null;
        }
    }

    /**
     * @return return a json object whose value is an array of test records.
     */
    public JSONObject abort() {
        isAbort = true;
        stateSpace.abort();
        try {
            feedbackQueue.put("1");
            return instructionQueue.take();
        } catch (InterruptedException e) {
            Logger.getDefault().catches(e);
            return null;
        }
    }

    private String carryOut(int repeat, Object presentable) {
        if (isAbort) {
            return "0";
        }
        try {
            lastInstruction = new JSONObject().put(Const.RESPONSE_TYPE_INSTRUCTION, new JSONArray().put(repeat).put(ObjectUtil.getPresentation(presentable)));
            instructionQueue.put(lastInstruction);
            return feedbackQueue.take();
        } catch (InterruptedException e) {
            Logger.getDefault().catches(e);
            return null;
        }
    }

    @Override
    protected <V> String checkState(int repeat, Obtainable<V> stateToCheck) {
        return carryOut(repeat, stateToCheck);
    }

    @Override
    protected String performAction(int repeat, ExternalEvent externalEvent) {
        return carryOut(repeat, externalEvent);
    }

    @Override
    protected String verifyExpectation(int repeat, Expectation expectation) {
        return carryOut(repeat, expectation);
    }
}
