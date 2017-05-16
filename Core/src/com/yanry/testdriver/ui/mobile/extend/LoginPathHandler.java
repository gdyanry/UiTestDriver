package com.yanry.testdriver.ui.mobile.extend;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.SearchableSwitchableProperty;
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
            etUser.addPositiveCase(e.getKey());
            etPwd.addPositiveCase(e.getValue());
        });
    }

    public void handleCurrentUserOnSuccessLogin(Timing timing, Function<SearchableSwitchableProperty<String>
            .SwitchablePropertyExpectation, Path> getSuccessLoginPath) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> getSuccessLoginPath.apply(currentUser
                .getStaticExpectation(timing, e.getKey())).addInitState(etUser.getInputContent(), e.getKey())
                .addInitState(etPwd.getInputContent(), e.getValue()));
    }

    public void initStateToSuccessLogin(Supplier<Path> pathToAddInitState) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> pathToAddInitState.get().addInitState(etUser
                .getInputContent(), e.getKey()).addInitState(etPwd.getInputContent(), e.getValue()));
    }

    public void initStateToInvalidUser(Supplier<Path> pathToAddInitState) {
        etUser.getValidContents().stream().filter(c -> !currentUser.getUserPasswordMap().keySet().contains(c))
                .forEach(c -> pathToAddInitState.get().addInitState(etUser.getInputContent(), c).addInitState(etPwd
                        .getValidity(), true));
    }

    public void initStateToInvalidPassword(Supplier<Path> pathToAddInitState) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> etPwd.getValidContents().stream().filter(c -> !c
                .equals(e.getValue())).forEach(c -> pathToAddInitState.get().addInitState(etUser.getInputContent(),
                e.getKey()).addInitState(etPwd.getInputContent(), c)));
    }
}
