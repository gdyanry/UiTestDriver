package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ActionGuard;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.base.TransitionRehearsal;
import com.yanry.driver.core.model.event.GlobalExternalEvent;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.property.StateSnapShoot;
import com.yanry.driver.mobile.sample.snake.GameConfigure;
import com.yanry.driver.mobile.sample.snake.SnakeModel;
import yanry.lib.java.model.log.Logger;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.List;

import static com.yanry.driver.mobile.sample.snake.graph.SnakeEvent.*;

public class SnakeRehearsal extends TransitionRehearsal {
    private SnakeController controller;
    private GlobalExternalEvent[] actionOptions = {SnakeEvent.MoveAhead.get(), SnakeEvent.TurnDown.get(), SnakeEvent.TurnLeft.get(),
            SnakeEvent.TurnRight.get(), SnakeEvent.TurnUp.get()};

    public SnakeRehearsal(SnakeController controller) {
        super(GameConfigure.FALLBACK_LIMIT);
        this.controller = controller;

    }

    @Override
    protected OutputStream getLogOutputStream() throws IOException {
        return new FileOutputStream("g:/practise-debug.txt", false);
    }

    @Override
    protected String getLogHeader() {
        return controller.getSnakeModel().getFruitPos().toString();
    }

    @Override
    protected char getActionLogChar(ExternalEvent action) {
        GlobalExternalEvent globalExternalEvent = (GlobalExternalEvent) action;
        if (globalExternalEvent == SnakeEvent.MoveAhead.get()) {
            return 'M';
        } else if (globalExternalEvent == SnakeEvent.TurnDown.get()) {
            return 'D';
        } else if (globalExternalEvent == SnakeEvent.TurnLeft.get()) {
            return 'L';
        } else if (globalExternalEvent == SnakeEvent.TurnRight.get()) {
            return 'R';
        } else if (globalExternalEvent == SnakeEvent.TurnUp.get()) {
            return 'U';
        }
        System.err.println(action);
        return '?';
    }

    @Override
    protected void initActionGuard(ActionGuard guard) {
        String currentDirection = controller.getDirection().getCurrentValue();
        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
            guard.invalidate(SnakeEvent.TurnUp.get());
            guard.invalidate(SnakeEvent.TurnDown.get());
        } else {
            guard.invalidate(SnakeEvent.TurnLeft.get());
            guard.invalidate(SnakeEvent.TurnRight.get());
        }
        int headX = controller.getSnakeHeadX().getCurrentValue();
        int headY = controller.getSnakeHeadY().getCurrentValue();
        SnakeModel snakeModel = controller.getSnakeModel();
        Point neckPos = snakeModel.getNeckPos();
        if (neckPos == null) {
            return;
        }
        int jointX = headX;
        int jointY = headY;
        // test点选在蛇头的左或上
        int testX = headX;
        int testY = headY;
        if (neckPos.x == headX) {
            jointY = neckPos.y > headY ? --headY : ++headY;
            testX--;
        } else if (neckPos.y == headY) {
            jointX = neckPos.x > headX ? --headX : ++headX;
            testY--;
        }
        List<Point> snakePoints = snakeModel.getSnakePoints();
        if (jointX >= 0 && jointY >= 0 && snakePoints.contains(new Point(jointX, jointY))) {
            // 打圈时应避免往圈里面走
            int jointCount = 0;
            // 连续的交点按一个点算
            boolean isJoint = false;
            for (Point point : snakePoints) {
                if (point.y == testY && point.x > testX) {
                    if (!isJoint) {
                        jointCount++;
                    }
                    isJoint = true;
                } else {
                    isJoint = false;
                }
                if (point.x == jointX && point.y == jointY) {
                    break;
                }
            }
            boolean isOutside = jointCount % 2 == 0;
            if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                guard.invalidate(isOutside ? SnakeEvent.TurnRight.get() : SnakeEvent.TurnLeft.get());
            } else if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                guard.invalidate(isOutside ? SnakeEvent.TurnDown.get() : SnakeEvent.TurnUp.get());
            }
        }
    }

    @Override
    protected ExternalEvent tryAction(StateSpace stateSpace, ActionGuard tag, ExternalEvent action) {
        if (action != null) {
            if (tryFire(tag, action)) {
                return action;
            }
        }
//        return Stream.of(actionOptions).filter(a -> tag.isValid(a)).sorted(Comparator.comparingInt(a -> {
//            // 按曼哈顿距离排序
//            ExternalEvent actionToGetPos = a;
//            if (actionToGetPos == SnakeEvent.MoveAhead.get()) {
//                String direction = controller.getDirection().getCurrentValue();
//                if (direction == Direction.DOWN) {
//                    actionToGetPos = SnakeEvent.TurnDown.get();
//                } else if (direction == Direction.LEFT) {
//                    actionToGetPos = SnakeEvent.TurnLeft.get();
//                } else if (direction == Direction.RIGHT) {
//                    actionToGetPos = SnakeEvent.TurnRight.get();
//                } else if (direction == Direction.UP) {
//                    actionToGetPos = SnakeEvent.TurnUp.get();
//                }
//            }
//            int headX = controller.getSnakeHeadX().getCurrentValue();
//            int headY = controller.getSnakeHeadY().getCurrentValue();
//            Point fruitPos = controller.getSnakeModel().getFruitPos();
//            if (actionToGetPos == SnakeEvent.TurnDown.get()) {
//                return Math.abs(headX - fruitPos.x) + Math.abs(headY + 1 - fruitPos.y);
//            } else if (actionToGetPos == SnakeEvent.TurnLeft.get()) {
//                return Math.abs(headX - 1 - fruitPos.x) + Math.abs(headY - fruitPos.y);
//            } else if (actionToGetPos == SnakeEvent.TurnRight.get()) {
//                return Math.abs(headX + 1 - fruitPos.x) + Math.abs(headY - fruitPos.y);
//            } else {
//                return Math.abs(headX - fruitPos.x) + Math.abs(headY - 1 - fruitPos.y);
//            }
//        })).filter(a -> tryFire(tag, a)).findFirst().orElse(null);

        Logger.getDefault().dd("no valid action, try the rests.");
        if (tryFire(tag, SnakeEvent.MoveAhead.get())) {
            return SnakeEvent.MoveAhead.get();
        }
        // 优先选择远离中点的方向
        Point midPos = controller.getSnakeModel().getMidPos();
        Integer snakeHeadX = controller.getSnakeHeadX().getCurrentValue();
        if (midPos.x != snakeHeadX) {
            GlobalExternalEvent recommendedAction = midPos.x > snakeHeadX ? TurnLeft.get() : TurnRight.get();
            Logger.getDefault().d("middle point is %s, recommend action: %s", midPos, recommendedAction);
            if (tryFire(tag, recommendedAction)) {
                return recommendedAction;
            }
        }
        GlobalExternalEvent recommendedAction = midPos.y > controller.getSnakeHeadY().getCurrentValue() ? TurnUp.get() : TurnDown.get();
        Logger.getDefault().dd("recommend action: ", recommendedAction);
        if (tryFire(tag, recommendedAction)) {
            return recommendedAction;
        }
        for (SnakeEvent snakeEvent : EnumSet.of(TurnUp, TurnDown, TurnLeft, TurnRight)) {
            if (tryFire(tag, snakeEvent.get())) {
                return snakeEvent.get();
            }
        }
        return null;
    }

    private boolean tryFire(ActionGuard actionGuard, ExternalEvent event) {
        if (!actionGuard.isValid(event)) {
            return false;
        }
        controller.syncFire(event, null);
        if (event != SnakeEvent.MoveAhead.get()) {
            controller.syncFire(SnakeEvent.MoveAhead.get(), null);
        }
        if (controller.getCurrentState() != GameState.MOVE) {
            actionGuard.invalidate(event);
            controller.revert(actionGuard);
            Logger.getDefault().dd("invalid action: ", event);
            controller.tag(actionGuard);
            return false;
        }
        return true;
    }

    public void makeAction() {
        if (controller.getCurrentState() == GameState.MOVE) {
            if (isDeadEnd()) {
                controller.asyncFire(SnakeEvent.MoveAhead.get());
            } else if (!isCollecting()) {
                ExternalEvent action = nextAction();
                if (action == null) {
                    Point fruitPos = controller.getSnakeModel().getFruitPos();
                    Equals<StateSnapShoot> toState = Equals.of(StateSnapShoot.builder()
                            .append(controller.getSnakeHeadX(), fruitPos.x)
                            .append(controller.getSnakeHeadY(), fruitPos.y)
                            .build());
                    if (!collectActions(controller, controller.getSnakeHead().getState(toState))) {
                        Logger.getDefault().ee("game over!");
                        controller.printCache(s -> Logger.getDefault().ii(s));
                    }
                    makeAction();
                } else {
                    controller.asyncFire(action);
                    if (action != SnakeEvent.MoveAhead.get()) {
                        controller.asyncFire(SnakeEvent.MoveAhead.get());
                    }
                }
            }
        }
    }
}
