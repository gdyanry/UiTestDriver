package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Practice;
import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.base.ValuePredicate;
import com.yanry.driver.core.model.event.GlobalExternalEvent;
import com.yanry.driver.core.model.event.NegationEvent;
import com.yanry.driver.core.model.event.StateChangeEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.predicate.Within;
import com.yanry.driver.core.model.property.CombinedProperty;
import com.yanry.driver.core.model.property.StateSnapShoot;
import com.yanry.driver.mobile.sample.snake.GameConfigure;
import com.yanry.driver.mobile.sample.snake.SnakeModel;
import lib.common.model.log.Logger;
import lib.common.util.object.ObjectUtil;

import java.awt.*;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Stream;

import static com.yanry.driver.mobile.sample.snake.graph.SnakeEvent.*;

public class SnakeController extends StateSpace {
    private static final int STATE_NONE = 0;
    private static final int STATE_COLLECTING = 1;
    private static final int STATE_CONSUMING = 2;
    private static final int STATE_DYING = 3;
    private GameState gameState;
    private SnakeHeadX snakeHeadX;
    private SnakeHeadY snakeHeadY;
    private CombinedProperty snakeHead;
    private SnakeModel snakeModel;
    private Direction direction;
    private int state;
    private LinkedList<Practice> practices;
    private PractiseDebug debug = new PractiseDebug();
    private HashSet<String> invalidStates;

    public SnakeController() {
        invalidStates = new HashSet<>();
        practices = new LinkedList<>();
        snakeModel = new SnakeModel(this);
        gameState = new GameState(this);
        direction = new Direction(this);
        snakeHeadX = new SnakeHeadX(this);
        snakeHeadY = new SnakeHeadY(this);
        snakeHead = new CombinedProperty(this, "snakeHead", snakeHeadX, snakeHeadY);
        // start
        createPath(SnakeEvent.PressStart.get(), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameState.MOVE)
                .addFollowingExpectation(new ActionExpectation() {
                    @Override
                    protected void run() {
                        state = STATE_NONE;
                        direction.cleanCache();
                        snakeHeadX.cleanCache();
                        snakeHeadY.cleanCache();
                        snakeModel.clear();
                        snakeModel.push(new Point(snakeHeadX.getCurrentValue(), snakeHeadY.getCurrentValue()));
                        snakeModel.spawnFruit();
                    }
                })).addContextPredicate(gameState, new Within<>(GameState.NEW, GameState.GAME_OVER));
        // Move -> Pause
        createPath(SnakeEvent.PressPause.get(), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameState.PAUSE))
                .addContextValue(gameState, GameState.MOVE);
        // Pause -> Move
        createPath(SnakeEvent.PressPause.get(), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameState.MOVE))
                .addContextValue(gameState, GameState.PAUSE);
        // Move -> GameOver
        createPath(new NegationEvent<>(snakeHead, new ValuePredicate<>() {
            @Override
            public Stream<StateSnapShoot> getConcreteValues() {
                return null;
            }

            @Override
            public boolean test(StateSnapShoot value) {
                int x = value.getValue(snakeHeadX);
                int y = value.getValue(snakeHeadY);
                Point headPoint = new Point(x, y);
                boolean insideBorder = headPoint.x >= 0 && headPoint.x < GameConfigure.COL_COUNT &&
                        headPoint.y >= 0 && headPoint.y < GameConfigure.ROW_COUNT;
                return insideBorder && !snakeModel.isBody(headPoint);
            }
        }), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameState.GAME_OVER))
                .addContextValue(gameState, GameState.MOVE);
        // right
        createPath(SnakeEvent.MoveAhead.get(), snakeHeadX.getShiftExpectation(Timing.IMMEDIATELY, false, true, 1))
                .addContextValue(direction, Direction.RIGHT)
                .addContextValue(gameState, GameState.MOVE);
        // left
        createPath(SnakeEvent.MoveAhead.get(), snakeHeadX.getShiftExpectation(Timing.IMMEDIATELY, false, false, 1))
                .addContextValue(direction, Direction.LEFT)
                .addContextValue(gameState, GameState.MOVE);
        // up
        createPath(SnakeEvent.MoveAhead.get(), snakeHeadY.getShiftExpectation(Timing.IMMEDIATELY, false, false, 1))
                .addContextValue(direction, Direction.UP)
                .addContextValue(gameState, GameState.MOVE);
        // down
        createPath(SnakeEvent.MoveAhead.get(), snakeHeadY.getShiftExpectation(Timing.IMMEDIATELY, false, true, 1))
                .addContextValue(direction, Direction.DOWN)
                .addContextValue(gameState, GameState.MOVE);
        // -> right
        createPath(TurnRight.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, Direction.RIGHT))
                .addContextValue(gameState, GameState.MOVE)
                .addContextPredicate(direction, Equals.of(Direction.LEFT).not());
        // -> left
        createPath(TurnLeft.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, Direction.LEFT))
                .addContextValue(gameState, GameState.MOVE)
                .addContextPredicate(direction, Equals.of(Direction.RIGHT).not());
        // -> up
        createPath(TurnUp.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, Direction.UP))
                .addContextValue(gameState, GameState.MOVE)
                .addContextPredicate(direction, Equals.of(Direction.DOWN).not());
        // -> down
        createPath(TurnDown.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, Direction.DOWN))
                .addContextValue(gameState, GameState.MOVE)
                .addContextPredicate(direction, Equals.of(Direction.UP).not());
        // on move
        ActionExpectation expectation = new ActionExpectation() {
            @Override
            protected void run() {
                // 更新snake model
                Point fruitPos = snakeModel.getFruitPos();
                if (fruitPos.x == snakeHeadX.getCurrentValue() && fruitPos.y == snakeHeadY.getCurrentValue()) {
                    snakeModel.spawnFruit();
                } else if (snakeModel.length() >= GameConfigure.MIN_SNAKE_LENGTH) {
                    snakeModel.removeLast();
                }
                snakeModel.push(new Point(snakeHeadX.getCurrentValue(), snakeHeadY.getCurrentValue()));
            }
        };
        createPath(new StateChangeEvent<>(snakeHead), expectation).addContextValue(gameState, GameState.MOVE);
    }

    public SnakeModel getSnakeModel() {
        return snakeModel;
    }

    public CombinedProperty getSnakeHead() {
        return snakeHead;
    }

    private void collectAction() {
        state = STATE_COLLECTING;
        Point fruitPos = snakeModel.getFruitPos();
        Equals<StateSnapShoot> toState = Equals.of(StateSnapShoot.builder().append(snakeHeadX, fruitPos.x).append(snakeHeadY, fruitPos.y).build());

        debug.begin();
        debug.printLine(fruitPos.toString());

        while (!toState.test(snakeHead.getCurrentValue())) {
            Practice practice = new Practice();
            String currentDirection = direction.getCurrentValue();
            if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                practice.invalidate(SnakeEvent.TurnUp.get());
                practice.invalidate(SnakeEvent.TurnDown.get());
            } else {
                practice.invalidate(SnakeEvent.TurnLeft.get());
                practice.invalidate(SnakeEvent.TurnRight.get());
            }
            if (!nextMove(toState, practice)) {
                state = STATE_DYING;
                return;
            }
        }

        debug.end();

        invalidStates.clear();
        revertAll();
        state = STATE_CONSUMING;
    }

    private boolean nextMove(Equals<StateSnapShoot> toState, Practice practice) {
        String snapShootMd5;
        try {
            snapShootMd5 = ObjectUtil.getSnapShootMd5(snakeModel.getSnakePoints());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (invalidStates.contains(snapShootMd5)) {
            return nextMove(toState, invalidateCurrent());
        } else {
            while (true) {
                ExternalEvent event = snakeHead.switchTo(toState, practice);
                if (event != null) {
                    if (tryFire(practice, event)) {
                        return true;
                    }
                    continue;
                } else {
                    Logger.getDefault().ww("no valid action, try the rests.");
                    if (tryFire(practice, SnakeEvent.MoveAhead.get())) {
                        return true;
                    }
                    // 优先选择远离中点的方向
                    Point midPos = snakeModel.getMidPos();
                    if (midPos.x != snakeHeadX.getCurrentValue()) {
                        GlobalExternalEvent recommendedAction = midPos.x > snakeHeadX.getCurrentValue() ? TurnLeft.get() : TurnRight.get();
                        Logger.getDefault().i("middle point is %s, recommend action: %s", midPos, recommendedAction);
                        if (tryFire(practice, recommendedAction)) {
                            return true;
                        }
                    }
                    GlobalExternalEvent recommendedAction = midPos.y > snakeHeadY.getCurrentValue() ? TurnUp.get() : TurnDown.get();
                    Logger.getDefault().ii("recommend action: ", recommendedAction);
                    if (tryFire(practice, recommendedAction)) {
                        return true;
                    }
                    for (SnakeEvent snakeEvent : EnumSet.of(TurnUp, TurnDown, TurnLeft, TurnRight)) {
                        if (tryFire(practice, snakeEvent.get())) {
                            return true;
                        }
                    }
                    if (practices.isEmpty()) {
                        Logger.getDefault().ee("game over!");
                        printCache(s -> Logger.getDefault().ii(s));
                        return false;
                    }
                    // 走不下去了，回退到上一步
                    invalidStates.add(snapShootMd5);
                    return nextMove(toState, invalidateCurrent());
                }
            }
        }
    }

    private Practice invalidateCurrent() {
        Practice pop = practices.pop();
        pop.invalidate(pop.getSelectedEvent());
        revert(pop);
        return pop;
    }

    private boolean tryFire(Practice practice, ExternalEvent event) {
        if (!practice.isValid(event)) {
            return false;
        }
        tag(practice);
        syncFire(event, null);
        if (event != SnakeEvent.MoveAhead.get()) {
            syncFire(SnakeEvent.MoveAhead.get(), null);
        }
        if (gameState.getCurrentValue() != GameState.MOVE) {
            Integer jointX = snakeHeadX.getCurrentValue();
            Integer jointY = snakeHeadY.getCurrentValue();
            boolean isCircle = jointX >= 0 && jointY >= 0;
            String directionValue = direction.getCurrentValue();
            revert(practice);
            String currentDirection = direction.getCurrentValue();
            // 打圈时应避免往圈里面走
            if (isCircle && directionValue.equals(currentDirection)) {
                // test点选在蛇头的左或上
                int testX = snakeHeadX.getCurrentValue();
                int testY = snakeHeadY.getCurrentValue();
                if (currentDirection == Direction.DOWN || currentDirection == Direction.UP) {
                    testX--;
                } else if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                    testY--;
                }
                int jointCount = 0;
                // 连续的交点按一个点算
                boolean isJoint = false;
                for (Point point : snakeModel.getSnakePoints()) {
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
                    practice.invalidate(isOutside ? SnakeEvent.TurnRight.get() : SnakeEvent.TurnLeft.get());
                } else if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                    practice.invalidate(isOutside ? SnakeEvent.TurnDown.get() : SnakeEvent.TurnUp.get());
                }
            }
            practice.invalidate(event);
            Logger.getDefault().ii("invalid action: ", event);
            return false;
        }
        practice.setSelectedEvent(event);

        debug.debug(practices.size(), event);

        practices.push(practice);
        return true;
    }

    public String getCurrentState() {
        return gameState.getCurrentValue();
    }

    public void makeAction() {
        if (gameState.getCurrentValue() == GameState.MOVE) {
            switch (state) {
                case STATE_DYING:
                    asyncFire(SnakeEvent.MoveAhead.get());
                    return;
                case STATE_CONSUMING:
                case STATE_NONE:
                    if (practices.isEmpty()) {
                        collectAction();
                        makeAction();
                    } else {
                        ExternalEvent selectedEvent = practices.removeLast().getSelectedEvent();
                        asyncFire(selectedEvent);
                        if (selectedEvent != SnakeEvent.MoveAhead.get()) {
                            asyncFire(SnakeEvent.MoveAhead.get());
                        }
                    }
                    return;
            }
        }
    }
}
