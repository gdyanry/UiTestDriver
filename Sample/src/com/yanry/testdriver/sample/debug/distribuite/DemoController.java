package com.yanry.testdriver.sample.debug.distribuite;

import com.yanry.testdriver.sample.debug.TestApp;
import com.yanry.testdriver.server.spring.CommunicatorController;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rongyu.yan on 4/14/2017.
 */
@RestController()
@RequestMapping("demo")
public class DemoController extends CommunicatorController {
    public DemoController() {
        super(600);
    }

    @Override
    protected void populateGraph(TestManager manager) {
        TestApp.defineGraph(manager);
    }
}
