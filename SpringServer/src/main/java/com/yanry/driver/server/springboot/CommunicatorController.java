package com.yanry.driver.server.springboot;

import com.yanry.driver.core.distribute.Const;
import com.yanry.driver.core.distribute.ServerReception;
import com.yanry.driver.core.model.base.StateSpace;
import lib.common.model.Singletons;
import lib.common.model.cache.TimedCache;
import lib.common.model.json.JSONArray;
import lib.common.model.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by rongyu.yan on 3/27/2017.
 * <p>
 * Subclasses should be annotated with {@link RestController}(required) and {@link RequestMapping}(optional)
 */
public abstract class CommunicatorController {
    private TimedCache<ServerReception> receptionMap;
    private Executor executor;

    public CommunicatorController(int sessionTimeoutSecond) {
        receptionMap = new TimedCache<>(sessionTimeoutSecond, Singletons.get(Timer.class));
        executor = Executors.newCachedThreadPool();
    }

    protected abstract void populateGraph(StateSpace stateSpace);

    @GetMapping(Const.HTTP_PATH_PREPARE)
    public String prepare(HttpServletResponse response) {
        String token = UUID.randomUUID().toString();
        ServerReception reception = new ServerReception();
        StateSpace stateSpace = new StateSpace();
        populateGraph(stateSpace);
        receptionMap.put(token, reception);
        response.setHeader(Const.HTTP_HEADER_TOKEN, token);
        return reception.prepare(stateSpace).toString();
    }

    @GetMapping(Const.HTTP_PATH_TRAVERSE)
    public String traverse(@RequestHeader String token, String p) {
        ServerReception reception = receptionMap.get(token);
        if (reception != null) {
            return reception.traverse(new JSONArray(p), executor).toString();
        }
        return Const.RESPONSE_BAD_TOKEN;
    }

    @GetMapping(Const.HTTP_PATH_INTERACT)
    public String interact(@RequestHeader String token, long t, String f) {
        ServerReception reception = receptionMap.get(token);
        if (reception != null) {
            JSONObject interact = reception.interact(f, t);
            if (interact.has(Const.RESPONSE_TYPE_RECORD)) {
                receptionMap.remove(token);
            }
            return interact.toString();
        }
        return Const.RESPONSE_BAD_TOKEN;
    }

    @GetMapping(Const.HTTP_PATH_ABORT)
    public String abort(@RequestHeader String token) {
        ServerReception reception = receptionMap.get(token);
        if (reception != null) {
            receptionMap.remove(token);
            return reception.abort().toString();
        }
        return Const.RESPONSE_BAD_TOKEN;
    }
}
