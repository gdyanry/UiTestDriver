package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.LoginPathHandler;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.extend.property.CurrentUser;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.ValidateEditText;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class Login extends TestManager.Window {
    public static String ET_USER;
    public static String ET_PWD;
    public static String USER_VALIDATION;
    public static String PWD_VALIDATION;

    public Login(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        ValidateEditText etUser = new ValidateEditText(this, new ByDesc(ET_USER), null);
        ValidateEditText etPwd = new ValidateEditText(this, new ByDesc(ET_PWD), etUser.getValidity());
        LoginPathHandler loginPathHandler = new LoginPathHandler(getProperty(CurrentUser.class), etUser, etPwd);
        Click clickLogin = new Click(new View(this, new ByText("登录")));
        TextView userValidationView = new TextView(this, new ByDesc(USER_VALIDATION));
        TextView pwdVAlidationView = new TextView(this, new ByDesc(PWD_VALIDATION));

        createPath(getCreateEvent(), etUser.getInputContent().getActionExpectation(""));
        createPath(getCreateEvent(), etPwd.getInputContent().getActionExpectation(""));
        etUser.setEmptyValidationCase(clickLogin, userValidationView.getVisibility().getStaticExpectation(Timing
                .IMMEDIATELY, true)).addFollowingAction(userValidationView.getText().);
    }
}
