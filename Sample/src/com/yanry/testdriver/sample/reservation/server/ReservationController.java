package com.yanry.testdriver.sample.reservation.server;

import com.yanry.testdriver.sample.reservation.property.NetworkConnectivity;
import com.yanry.testdriver.sample.reservation.window.*;
import com.yanry.testdriver.server.spring.CommunicatorController;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
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
    protected void populateGraph(TestManager manager) {
        CurrentUser currentUser = new CurrentUser(manager);
        currentUser.addUserPassword("xiaoming.wang", "aaa111").addUserPassword("daming.wang", "aaa111");
        LoginState loginState = new LoginState(currentUser);
        NetworkConnectivity connectivity = new NetworkConnectivity(manager);
        manager.registerProperties(currentUser, loginState, connectivity);
        manager.registerWindows(new Main(manager), new MenuOnMain(manager), new Login(manager), new FilterOnMain
                (manager), new SelectDateOnMain(manager), new SpecificationOnMain(manager), new Reserve(manager), new
                PeriodicReserve(manager), new SelectRoom(manager), new MyReservation(manager));
    }
}
