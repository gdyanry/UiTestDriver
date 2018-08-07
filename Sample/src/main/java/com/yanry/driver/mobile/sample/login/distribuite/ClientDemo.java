package com.yanry.driver.mobile.sample.login.distribuite;

import com.yanry.driver.core.distribute.HttpClientReception;
import lib.common.model.json.JSONArray;
import lib.common.model.json.JSONObject;
import lib.common.util.ConsoleUtil;

/**
 * Created by rongyu.yan on 3/27/2017.
 */
public class ClientDemo extends HttpClientReception {
    private static final String BASE_URL = "http://localhost:8080/login/";
    private static final String CHARSET = "utf-8";

    public ClientDemo() {
        super(BASE_URL, CHARSET);
    }

    @Override
    protected JSONArray selectPathsToTraverse(JSONArray optionPaths) {
        System.out.println("============================== optional paths ============================");
        System.out.println(optionPaths);
        System.out.println("==========================================================================");
        return null;
    }

    @Override
    protected int handleInstruction(int repeat, JSONObject instruction) {
        String readLine = ConsoleUtil.readLine(String.format("%s:%s%n", repeat, instruction));
        if ("abort".equals(readLine)) {
            abort();
            return -1;
        }
        return Integer.parseInt(readLine);
    }

    @Override
    protected void handleTestRecords(JSONArray records) {
        System.out.println("============================== test record ==============================");
        for (int i = 0; i < records.length(); i++) {
            System.out.println(records.get(i));
        }
        System.out.println("=========================================================================");
    }

    @Override
    protected void onBadToken() {
        System.out.println("bad token!");
    }

    public static void main(String[] args) {
        ClientDemo clientDemo = new ClientDemo();
        try {
            clientDemo.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
