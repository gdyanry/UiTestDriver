package com.yanry.driver.core.model.base;

import lib.common.model.Singletons;
import lib.common.model.log.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Random;

public abstract class TransitionRehearsal {
    private static final int STATUS_NONE = 0;
    private static final int STATUS_COLLECTING = 1;
    private static final int STATUS_READY = 2;
    private static final int STATUS_DYING = 3;

    private int lastIndex;

    private int status;
    private LinkedList<ActionGuard> actionGuards;
    private Object startTag;
    private int fallbackLimit;

    public TransitionRehearsal(int fallbackLimit) {
        this.fallbackLimit = fallbackLimit;
        actionGuards = new LinkedList<>();
        startTag = new Object();
    }

    public void reset() {
        status = STATUS_NONE;
    }

    public <V> boolean collectActions(StateSpace stateSpace, State<V> toState) {
        return stateSpace.getExecutor().sync(() -> {
            status = STATUS_COLLECTING;
            actionGuards.clear();
            int fallbackCounter = 0;

            DataOutputStream outStream = null;
            try {
                OutputStream outputStream = getLogOutputStream();
                if (outputStream != null) {
                    outStream = new DataOutputStream(new BufferedOutputStream(outputStream));
                    String logHeader = getLogHeader();
                    if (logHeader != null) {
                        addLogLine(outStream, logHeader);
                    }
                }
            } catch (IOException e) {
                Logger.getDefault().catches(e);
            }

            stateSpace.tag(startTag);
            while (!toState.isSatisfied()) {
                ActionGuard guard = new ActionGuard();
                initActionGuard(guard);
                while (true) {
                    if (nextMove(toState, guard, stateSpace)) {
                        if (outStream != null) {
                            int index = actionGuards.size();
                            try {
                                if (index > lastIndex) {
                                    outStream.write(getActionLogChar(guard.getSelectedAction()));
                                } else {
                                    outStream.write('\n');
                                    for (int i = 0; i < index; i++) {
                                        outStream.write(' ');
                                    }
                                    outStream.write(getActionLogChar(guard.getSelectedAction()));
                                }
                            } catch (IOException e) {
                                Logger.getDefault().catches(e);
                            }
                            lastIndex = index;
                        }

                        actionGuards.push(guard);
                        break;
                    } else {
                        if (actionGuards.size() == 0) {
                            stateSpace.revert(startTag);
                            status = STATUS_DYING;
                            return false;
                        } else {
                            if (fallbackCounter++ < fallbackLimit) {
                                // 回退到上一步
                                guard = fallback(stateSpace);
                            } else {
                                fallbackCounter = 0;
                                // 随机回退
                                int len = actionGuards.size();
                                int n = Singletons.get(Random.class).nextInt(len);
                                Logger.getDefault().i("fallback: %s(len=%s)", n, len);
                                for (int i = 0; i < n; i++) {
                                    actionGuards.pop();
                                }
                                guard = fallback(stateSpace);
                            }
                        }
                    }
                }
            }

            if (outStream != null) {
                try {
                    addLogLine(outStream, "=====END=====");
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            stateSpace.revert(startTag);
            status = STATUS_READY;
            return true;
        });
    }

    private ActionGuard fallback(StateSpace stateSpace) {
        ActionGuard guard = actionGuards.pop();
        guard.invalidate(guard.getSelectedAction());
        stateSpace.revert(guard);
        return guard;
    }

    private void addLogLine(DataOutputStream outStream, String content) throws IOException {
        outStream.writeUTF(new StringBuilder().append('\n').append(content).append('\n').toString());
    }

    private <V> boolean nextMove(State<V> toState, ActionGuard guard, StateSpace stateSpace) {
        stateSpace.tag(guard);
        ExternalEvent action = tryAction(stateSpace, guard, toState.trySatisfy(guard));
        if (action != null) {
            guard.setSelectedAction(action);
            return true;
        } else {
            return false;
        }
    }

    public ExternalEvent nextAction() {
        if (status == STATUS_READY && actionGuards.size() > 0) {
            return actionGuards.removeLast().getSelectedAction();
        }
        return null;
    }

    public boolean isCollecting() {
        return status == STATUS_COLLECTING;
    }

    public boolean isReady() {
        return status == STATUS_READY;
    }

    public boolean isDeadEnd() {
        return status == STATUS_DYING;
    }

    protected abstract OutputStream getLogOutputStream() throws IOException;

    protected abstract String getLogHeader();

    protected abstract char getActionLogChar(ExternalEvent action);

    protected abstract void initActionGuard(ActionGuard guard);

    protected abstract Object getCurrentSnapshot();

    protected abstract ExternalEvent tryAction(StateSpace stateSpace, ActionGuard tag, ExternalEvent action);
}
