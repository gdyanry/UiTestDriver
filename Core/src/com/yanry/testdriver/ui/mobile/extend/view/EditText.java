package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.DynamicExpectation;
import com.yanry.testdriver.ui.mobile.base.StateProperty;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.action.EnterText;
import com.yanry.testdriver.ui.mobile.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.extend.property.NoCacheProperty;
import com.yanry.testdriver.ui.mobile.extend.value.EditTextValidity;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public abstract class EditText extends DependantValidationView {
    private StateProperty<String> inputContent;
    private StateProperty<EditTextValidity> serverValidity;
    private Set<String> clientValidContents;
    private Set<String> serverValidContents;
    private Graph graph;

    public EditText(Graph graph, ViewContainer parent, ViewSelector selector, DependantValidationView
            dependantValidationView, boolean defaultValidity) {
        super(parent, selector, dependantValidationView);
        this.graph = graph;
        inputContent = new StateProperty<String>() {
            @Override
            public boolean transitTo(Predicate<String> to, List<Path> superPathContainer) {
                return graph.transitToState(this, to, superPathContainer);
            }

            @Override
            protected String checkValue() {
                return getInitContent();
            }

            @Override
            protected Graph getGraph() {
                return graph;
            }

            @Override
            public boolean ifNeedVerification() {
                return false;
            }
        };
        serverValidity = new ServerValidity();
        // reset input content when window is created.
        Util.createPath(graph, null, getWindow().getCreateEvent(), new DynamicExpectation() {
            @Override
            public void run() {
                inputContent.setCacheValue(getInitContent());
            }
        });
        // add validity for empty string
        clientValidContents = new HashSet<>();
        serverValidContents = new HashSet<>();
        if (defaultValidity) {
            clientValidContents.add("");
            serverValidContents.add("");
        }
    }

    public void addPositiveTestCase(String content, boolean isServerValid) {
        EnterText action = new EnterText(this, content);
        present(Util.createPath(graph, getWindow(), action, inputContent.getExpectation(Timing.IMMEDIATELY, content)));
        clientValidContents.add(content);
        if (isServerValid) {
            serverValidContents.add(content);
        }
    }

    public Path addNegativeTestCase(String content, String errMsg, Event commitEvent, int toastDuration) {
        EnterText action = new EnterText(this, content);
        present(Util.createPath(graph, getWindow(), action, inputContent.getExpectation(Timing.IMMEDIATELY, content)));
        Path path = Util.createPath(graph, getWindow(), commitEvent, new Toast(graph, errMsg, Timing.IMMEDIATELY,
                toastDuration));
        path.put(inputContent, content);
        allClientDependantValidationPass(false, path);
        return path;
    }

    public StateProperty<EditTextValidity> getServerValidity() {
        return serverValidity;
    }

    protected abstract String getInitContent();

    @Override
    protected Graph getGraph() {
        return graph;
    }

    @Override
    protected boolean checkClientValidity() {
        return clientValidContents.contains(inputContent.getCurrentValue());
    }

    @Override
    protected boolean transitToValidity(boolean validity, List<Path> superPathContainer) {
        return graph.transitToState(inputContent, c -> validity && clientValidContents.contains(c), superPathContainer);
    }

    public class ServerValidity extends NoCacheProperty<EditTextValidity> {
        @Override
        protected EditTextValidity checkValue() {
            String currentInput = inputContent.getCurrentValue();
            if (serverValidContents.contains(currentInput)) {
                return EditTextValidity.ServerPass;
            } else if (clientValidContents.contains(currentInput)) {
                return EditTextValidity.ClientPass_ServerFail;
            } else {
                return EditTextValidity.ClientFail;
            }
        }

        @Override
        protected Graph getGraph() {
            return graph;
        }

        @Override
        public boolean transitTo(Predicate<EditTextValidity> to, List<Path> superPathContainer) {
            if (to.test(EditTextValidity.ClientFail)) {
                return graph.transitToState(inputContent, c -> !clientValidContents.contains(c), superPathContainer);
            } else if (to.test(EditTextValidity.ClientPass_ServerFail)) {
                return graph.transitToState(inputContent, c -> clientValidContents.contains(c) &&
                        !serverValidContents.contains(c), superPathContainer);
            } else {
                return graph.transitToState(inputContent, c -> serverValidContents.contains(c), superPathContainer);
            }
        }

        @Presentable
        public EditText getView() {
            return EditText.this;
        }
    }
}
