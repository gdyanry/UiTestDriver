package com.yanry.driver.mobile.sample.login.distribuite;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.mobile.sample.login.LoginTest;
import com.yanry.driver.server.springboot.CommunicatorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rongyu.yan on 4/14/2017.
 */
@RestController()
@RequestMapping("login")
public class DemoController extends CommunicatorController {
    public DemoController() {
        super(600);
    }

    @Override
    protected void populateGraph(StateSpace stateSpace) {
        new LoginTest(stateSpace);
    }
}
