package com.yanry.testdriver.ui.mobile.model.extend.view;

import com.yanry.testdriver.ui.mobile.model.base.*;
import com.yanry.testdriver.ui.mobile.model.base.view.ViewContainer;
import com.yanry.testdriver.ui.mobile.model.extend.action.EnterText;
import com.yanry.testdriver.ui.mobile.model.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.model.extend.property.NoCacheProperty;
import com.yanry.testdriver.ui.mobile.model.extend.value.EditTextValidity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public abstract class EditText extends DependantValidationView {
    private ObjectProperty<String> inputContent;
    private Graph graph;
    private ObjectProperty<EditTextValidity> serverValidity;
    private Set<String> clientValidContents;
    private Set<String> serverValidContents;

    public EditText(ViewContainer parent, String name, DependantValidationView dependantValidationView, boolean defaultValidity, Graph graph) {
        super(parent, name, dependantValidationView);
        this.graph = graph;
        inputContent = new ObjectProperty<String>(false) {
            @Override
            public String checkValue(Timing timing) {
                return getInitContent();
            }

            @Override
            public String toString() {
                return EditText.this.toString() + "内容";
            }
        };
        serverValidity = new ServerValidity();
        // reset input content when window is created.
        Expectation initContent = new FollowingAction() {
            @Override
            public void run() {
                inputContent.setCacheValue(getInitContent());
            }

            @Override
            public String toString() {
                return String.format("初始化%s：%s", EditText.this, getInitContent());
            }
        };
        new Path(graph, null, getWindow().getCreateEvent(), initContent);
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
        present(new Path(graph, getWindow(), action, new PermanentExpectation<>(inputContent, content, Timing.IMMEDIATELY)));
        clientValidContents.add(content);
        if (isServerValid) {
            serverValidContents.add(content);
        }
    }

    public Path addNegativeTestCase(String content, String errMsg, Event commitEvent, int toastDuration) {
        EnterText action = new EnterText(this, content);
        present(new Path(graph, getWindow(), action, new PermanentExpectation<>(inputContent, content, Timing.IMMEDIATELY)));
        Path path = new Path(graph, getWindow(), commitEvent, new Toast(Timing.IMMEDIATELY, toastDuration, errMsg));
        path.put(inputContent, content);
        allClientDependantValidationPass(false, path);
        return path;
    }

    public ObjectProperty<EditTextValidity> getServerValidity() {
        return serverValidity;
    }

    protected abstract String getInitContent();

    @Override
    protected boolean checkClientValidity() {
        return clientValidContents.contains(inputContent.getCurrentValue());
    }

    @Override
    protected boolean transitToValidity(Graph graph, boolean validity, boolean isTransitEvent) {
        return graph.transitToState(inputContent, c -> validity && clientValidContents.contains(c),
                true);
    }

    public class ServerValidity extends NoCacheProperty<EditTextValidity> {
        @Override
        public EditTextValidity checkValue(Timing timing) {
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
        public boolean transitTo(Graph graph, EditTextValidity toValue, boolean isTransitEvent) {
            switch (toValue) {
                case ClientFail:
                    return graph.transitToState(inputContent, c -> !clientValidContents.contains(c), true);
                case ClientPass_ServerFail:
                    return graph.transitToState(inputContent, c -> clientValidContents.contains(c) && !serverValidContents.contains(c),
                            true);
                default:
                    return graph.transitToState(inputContent, c -> serverValidContents.contains(c), true);
            }
        }

        @Presentable
        public EditText getView() {
            return EditText.this;
        }
    }
}
