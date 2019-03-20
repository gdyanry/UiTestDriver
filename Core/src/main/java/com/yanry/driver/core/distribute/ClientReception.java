package com.yanry.driver.core.distribute;

import yanry.lib.java.model.json.JSONArray;
import yanry.lib.java.model.json.JSONObject;
import yanry.lib.java.model.log.Logger;

/**
 * Created by rongyu.yan on 3/23/2017.
 */
public abstract class ClientReception {
    private long timestamp;
    private int lastFeedback;
    private boolean isRetry;
    private boolean isAbort;

    protected abstract String requestPrepare() throws Exception;

    protected abstract String requestTraverse(JSONArray pathsToTraverse) throws Exception;

    protected abstract String requestInteract(long timestamp, int feedback) throws Exception;

    protected abstract String requestAbort() throws Exception;

    /**
     * @param optionPaths
     * @return return null to requestTraverse all paths.
     */
    protected abstract JSONArray selectPathsToTraverse(JSONArray optionPaths);

    protected abstract int handleInstruction(int repeat, JSONObject instruction);

    protected abstract void handleTestRecords(JSONArray records);

    protected abstract void onBadToken();

    public void start() throws Exception {
        String string = requestPrepare();
        JSONArray jsonArray = new JSONArray(string);
        jsonArray = selectPathsToTraverse(jsonArray);
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        string = requestTraverse(jsonArray);
        if (Const.RESPONSE_BAD_TOKEN.equals(string)) {
            onBadToken();
            return;
        }
        JSONObject jsonObject = new JSONObject(string);
        while (jsonObject.has(Const.RESPONSE_TYPE_INSTRUCTION)) {
            if (!isRetry && !isAbort) {
                JSONArray instruction = jsonObject.getJSONArray(Const.RESPONSE_TYPE_INSTRUCTION);
                timestamp = System.currentTimeMillis();
                lastFeedback = handleInstruction(instruction.getInt(0), instruction.getJSONObject(1));
            }
            if (isAbort) {
                string = requestAbort();
                if (Const.RESPONSE_BAD_TOKEN.equals(string)) {
                    onBadToken();
                    return;
                }
                jsonObject = new JSONObject(string);
                break;
            }
            try {
                string = requestInteract(timestamp, lastFeedback);
                if (Const.RESPONSE_BAD_TOKEN.equals(string)) {
                    onBadToken();
                    return;
                }
                jsonObject = new JSONObject(string);
                isRetry = false;
            } catch (Exception e) {
                Logger.getDefault().catches(e);
                isRetry = true;
            }
        }
        handleTestRecords(jsonObject.getJSONArray(Const.RESPONSE_TYPE_RECORD));
    }

    public void abort() {
        isAbort = true;
    }
}
