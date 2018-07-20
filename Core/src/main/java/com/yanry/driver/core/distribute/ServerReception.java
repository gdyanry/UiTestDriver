package com.yanry.driver.core.distribute;

import com.yanry.driver.core.extend.communicator.SerializedCommunicator;
import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.expectation.Expectation;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.Util;
import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.runtime.StateToCheck;
import lib.common.model.json.JSONArray;
import lib.common.model.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by rongyu.yan on 3/14/2017.
 */
public class ServerReception extends SerializedCommunicator {
    private Graph graph;
    private long timestamp;
    private JSONObject lastInstruction;
    private SynchronousQueue<JSONObject> instructionQueue;
    private SynchronousQueue<String> feedbackQueue;
    private boolean isAbort;

    /**
     * @param graph
     * @return return an array containing optional paths.
     */
    public JSONArray prepare(Graph graph) {
        graph.registerCommunicator(this);
        this.graph = graph;
        instructionQueue = new SynchronousQueue(true);
        feedbackQueue = new SynchronousQueue<>(true);
        List<Path> paths = graph.prepare();
        JSONArray jsonArray = new JSONArray();
        for (Path path : paths) {
            jsonArray.put(Util.getPresentation(path));
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
            List<Object> result = graph.traverse(finalIndexes);
            JSONArray ja = new JSONArray();
            for (Object o : result) {
                ja.put(Util.getPresentation(o));
            }
            lastInstruction = new JSONObject().put(Const.RESPONSE_TYPE_RECORD, ja);
            try {
                instructionQueue.put(lastInstruction);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        try {
            return instructionQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return return a json object whose value is an array of test records.
     */
    public JSONObject abort() {
        isAbort = true;
        graph.abort();
        try {
            feedbackQueue.put("1");
            return instructionQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String carryOut(int repeat, Object presentable) {
        if (isAbort) {
            return "0";
        }
        try {
            lastInstruction = new JSONObject().put(Const.RESPONSE_TYPE_INSTRUCTION, new JSONArray().put(repeat).put(Util.getPresentation(presentable)));
            instructionQueue.put(lastInstruction);
            return feedbackQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected <V> String checkState(int repeat, StateToCheck<V> stateToCheck) {
        return carryOut(repeat, stateToCheck);
    }

    @Override
    protected String performAction(int repeat, ActionEvent actionEvent) {
        return carryOut(repeat, actionEvent);
    }

    @Override
    protected String verifyExpectation(int repeat, Expectation expectation) {
        return carryOut(repeat, expectation);
    }

    @Override
    public String fetchValue(Property<String> property) {
        return carryOut(0, property);
    }
}
