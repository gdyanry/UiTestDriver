package com.yanry.driver.mobile.model;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.expectation.AbstractStaticPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.TextValidity;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class LoginPathHandler {
    private CurrentUser currentUser;
    private TextValidity userValidity;
    private TextValidity pwdValidity;

    public LoginPathHandler(CurrentUser currentUser, TextValidity userValidity, TextValidity pwdValidity) {
        this.currentUser = currentUser;
        this.userValidity = userValidity;
        this.pwdValidity = pwdValidity;
        currentUser.getUserPasswordMap().entrySet().forEach(e -> {
            userValidity.addPositiveCases(e.getKey());
            pwdValidity.addPositiveCases(e.getValue());
        });
    }

    public void handleCurrentUserOnSuccessLogin(Timing timing, Function<AbstractStaticPropertyExpectation, Path> getSuccessLoginPath) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> getSuccessLoginPath.apply(currentUser
                .getStaticExpectation(timing, false, e.getKey()))
                .addInitState(userValidity.getText(), e.getKey())
                .addInitState(pwdValidity.getText(), e.getValue()));
    }

    public void initStateToSuccessLogin(Supplier<Path> pathToAddInitState) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> pathToAddInitState.get()
                .addInitState(userValidity.getText(), e.getKey())
                .addInitState(pwdValidity.getText(), e.getValue()));
    }

    public void initStateToInvalidUser(Supplier<Path> pathToAddInitState) {
        userValidity.getValidContents().stream().filter(c -> !currentUser.getUserPasswordMap().keySet().contains(c))
                .forEach(c -> pathToAddInitState.get()
                        .addInitState(userValidity.getText(), c)
                        .addInitState(pwdValidity, true));
    }

    public void initStateToInvalidPassword(Supplier<Path> pathToAddInitState) {
        currentUser.getUserPasswordMap().entrySet().forEach(e -> pwdValidity.getValidContents().stream().filter(c -> !c.equals(e.getValue()))
                .forEach(c -> pathToAddInitState.get()
                        .addInitState(userValidity.getText(), e.getKey())
                        .addInitState(pwdValidity.getText(), c)));
    }
}
