package com.yanry.testdriver.ui.mobile.model.distribute;

import lib.common.model.http.HttpGet;
import lib.common.model.json.JSONArray;

/**
 * Created by rongyu.yan on 4/13/2017.
 */
public abstract class HttpClientReception extends ClientReception {
    private String charset;
    private String baseUrl;
    private String token;

    public HttpClientReception(String baseUrl, String charset) {
        if (baseUrl.endsWith("/")) {
            this.baseUrl = baseUrl;
        } else {
            this.baseUrl = baseUrl + "/";
        }
        this.charset = charset;
    }

    @Override
    protected String requestPrepare() throws Exception {
        HttpGet httpGet = new HttpGet(baseUrl + Const.HTTP_PATH_PREPARE);
        httpGet.send();
        token = httpGet.getConnection().getHeaderField(Const.HTTP_HEADER_TOKEN);
        return httpGet.getString(charset);
    }

    @Override
    protected String requestTraverse(JSONArray pathsToTraverse) throws Exception {
        HttpGet httpGet = new HttpGet(String.format("%s%s?p=%s", baseUrl, Const.HTTP_PATH_TRAVERSE, pathsToTraverse));
        httpGet.getConnection().setRequestProperty(Const.HTTP_HEADER_TOKEN, token);
        return httpGet.getString(charset);
    }

    @Override
    protected String requestInteract(long timestamp, int feedback) throws Exception {
        HttpGet httpGet = new HttpGet(String.format("%s%s?f=%s&t=%s", baseUrl, Const.HTTP_PATH_INTERACT, feedback,
                timestamp));
        httpGet.getConnection().setRequestProperty(Const.HTTP_HEADER_TOKEN, token);
        return httpGet.getString(charset);
    }

    @Override
    protected String requestAbort() throws Exception {
        HttpGet httpGet = new HttpGet(baseUrl + Const.HTTP_PATH_ABORT);
        httpGet.getConnection().setRequestProperty(Const.HTTP_HEADER_TOKEN, token);
        return httpGet.getString(charset);
    }
}
