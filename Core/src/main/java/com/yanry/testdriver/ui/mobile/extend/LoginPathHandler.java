package com.yanry.testdriver.ui.mobile.extend;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.expectation.PropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.property.CurrentUser;
import com.yanry.testdriver.ui.mobile.extend.view.ValidateEditText;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class LoginPathHandler {
    private CurrentUser currentUser;
    private ValidateEditText etUser;
    private ValidateEditText etPwd;

    public LoginPathHandler(CurrentUser currentUser, ValidateEditText etUser, ValidateEditText etPwd) {
        this.currentUser = currentUser;
        this.etUser = etUser;
        this.etPwd = etPwd;
        currentUser.getUserPasswordMap().entrySet().forEach(e -> {
            etUser.addPositiveCases(e.getKey());
            etPwd.addPositiveCases(e.getValue());
        });
    }

    public void handleCurrentUserOnSuccessLogin(Timing timing, Function<PropertyExpectation, Path> getSuccessLoginPath) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> getSuccessLoginPath.apply(currentUser
                .getExpectation(timing, e.getKey())).addInitState(etUser.getContent(), e.getKey())
                .addInitState(etPwd.getContent(), e.getValue()));
    }

    public void initStateToSuccessLogin(Supplier<Path> pathToAddInitState) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> pathToAddInitState.get().addInitState(etUser
                .getContent(), e.getKey()).addInitState(etPwd.getContent(), e.getValue()));
    }

    public void initStateToInvalidUser(Supplier<Path> pathToAddInitState) {
        etUser.getValidContents().stream().filter(c -> !currentUser.getUserPasswordMap().keySet().contains(c))
                .forEach(c -> pathToAddInitState.get().addInitState(etUser.getContent(), c).addInitState(etPwd
                        .getValidity(), true));
    }

    public void initStateToInvalidPassword(Supplier<Path> pathToAddInitState) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> etPwd.getValidContents().stream().filter(c -> !c
                .equals(e.getValue())).forEach(c -> pathToAddInitState.get().addInitState(etUser.getContent(),
                e.getKey()).addInitState(etPwd.getContent(), c)));
    }
}
