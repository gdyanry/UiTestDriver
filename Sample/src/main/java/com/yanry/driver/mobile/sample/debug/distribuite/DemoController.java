package com.yanry.driver.mobile.sample.debug.distribuite;

import com.yanry.driver.core.extend.WindowManager;
import com.yanry.driver.mobile.sample.debug.TestApp;
import com.yanry.driver.server.springboot.CommunicatorController;
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
    protected void populateGraph(WindowManager manager) {
        TestApp.defineGraph(manager);
    }
}
