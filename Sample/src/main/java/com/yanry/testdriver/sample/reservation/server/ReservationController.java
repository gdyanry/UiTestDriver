package com.yanry.testdriver.sample.reservation.server;

import com.yanry.testdriver.sample.reservation.property.NetworkConnectivity;
import com.yanry.testdriver.server.spring.CommunicatorController;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.property.CurrentUser;
import com.yanry.testdriver.ui.mobile.extend.property.LoginState;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rongyu.yan on 4/18/2017.
 */
@RestController
@RequestMapping("reservation")
public class ReservationController extends CommunicatorController {
    public ReservationController() {
        super(600);
    }

    @Override
    protected void populateGraph(WindowManager manager) {
        CurrentUser currentUser = new CurrentUser(manager);
        currentUser.addUserPassword("xiaoming.wang", "aaa111").addUserPassword("daming.wang", "aaa111");
        LoginState loginState = new LoginState(manager, currentUser);
        NetworkConnectivity connectivity = new NetworkConnectivity(manager);
        manager.registerProperties(currentUser, loginState, connectivity);
    }
}
