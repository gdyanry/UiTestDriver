package com.yanry.testdriver.server.spring.demo;

import com.yanry.testdriver.server.spring.CommunicatorController;
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
}
