package com.yanry.driver.core.model.base;

import lib.common.model.log.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;

public abstract class TransitionRehearsal {
    private static final int STATUS_NONE = 0;
    private static final int STATUS_COLLECTING = 1;
    private static final int STATUS_READY = 2;
    private static final int STATUS_DYING = 3;

    private int lastIndex;

    private int status;
    private LinkedList<ActionGuard> actionGuards;
    private HashSet<Object> invalidSpots;

    public TransitionRehearsal() {
        actionGuards = new LinkedList<>();
        invalidSpots = new HashSet<>();
    }

    public void reset() {
        status = STATUS_NONE;
    }

    public <V> boolean collectActions(StateSpace stateSpace, State<V> toState) {
        return stateSpace.getExecutor().sync(() -> {
            status = STATUS_COLLECTING;
            actionGuards.clear();

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

            Object initTag = new Object();
            stateSpace.tag(initTag);
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
                            stateSpace.revert(initTag);
                            invalidSpots.clear();
                            status = STATUS_DYING;
                            return false;
                        } else {
                            // 回退
                            guard = actionGuards.pop();
                            guard.invalidate(guard.getSelectedAction());
                            stateSpace.revert(guard);
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

            stateSpace.revert(initTag);
            invalidSpots.clear();
            status = STATUS_READY;
            return true;
        });
    }

    private void addLogLine(DataOutputStream outStream, String content) throws IOException {
        outStream.writeUTF(new StringBuilder().append('\n').append(content).append('\n').toString());
    }

    private <V> boolean nextMove(State<V> toState, ActionGuard guard, StateSpace stateSpace) {
        Object currentSpot = getCurrentSnapshot();
        if (currentSpot != null && invalidSpots.contains(currentSpot)) {
            return false;
        }
        stateSpace.tag(guard);
        ExternalEvent action = tryAction(stateSpace, guard, toState.trySatisfy(guard));
        if (action != null) {
            guard.setSelectedAction(action);
            return true;
        } else {
            if (currentSpot != null) {
                invalidSpots.add(currentSpot);
            }
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
