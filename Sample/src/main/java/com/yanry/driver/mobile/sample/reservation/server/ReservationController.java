package com.yanry.driver.mobile.sample.reservation.server;

import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.LoginState;
import com.yanry.driver.mobile.sample.reservation.property.NetworkConnectivity;
import com.yanry.driver.server.springboot.CommunicatorController;
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
