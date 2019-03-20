package com.yanry.driver.mobile.sample.login.distribuite;

import com.yanry.driver.core.distribute.HttpClientReception;
import yanry.lib.java.model.json.JSONArray;
import yanry.lib.java.model.json.JSONObject;

import java.util.Scanner;

/**
 * Created by rongyu.yan on 3/27/2017.
 */
public class ClientDemo extends HttpClientReception {
    private static final String BASE_URL = "http://localhost:8080/login/";
    private static final String CHARSET = "utf-8";
    private Scanner scanner;

    public ClientDemo() {
        super(BASE_URL, CHARSET);
        scanner = new Scanner(System.in);
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
        System.out.println(String.format("%s:%s%n", repeat, instruction));
        String readLine = scanner.nextLine();
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
