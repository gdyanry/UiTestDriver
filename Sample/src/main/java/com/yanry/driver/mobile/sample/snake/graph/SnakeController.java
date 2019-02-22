package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.*;
import com.yanry.driver.core.model.event.GlobalExternalEvent;
import com.yanry.driver.core.model.event.NegationEvent;
import com.yanry.driver.core.model.event.StateChangeEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.predicate.Within;
import com.yanry.driver.core.model.property.CombinedProperty;
import com.yanry.driver.core.model.property.StateSnapShoot;
import com.yanry.driver.core.model.runtime.communicator.Communicator;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import com.yanry.driver.mobile.sample.snake.GameConfigure;
import com.yanry.driver.mobile.sample.snake.SnakeModel;
import lib.common.model.log.Logger;

import java.awt.*;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.stream.Stream;

import static com.yanry.driver.mobile.sample.snake.graph.SnakeEvent.*;

public class SnakeController extends StateSpace implements Communicator {
    private GameState gameState;
    private SnakeHeadX snakeHeadX;
    private SnakeHeadY snakeHeadY;
    private CombinedProperty snakeHead;
    private SnakeModel snakeModel;

    public SnakeController() {
        snakeModel = new SnakeModel(this);
        setCommunicator(this);
        gameState = new GameState(this);
        Direction direction = new Direction(this);
        snakeHeadX = new SnakeHeadX(this);
        snakeHeadY = new SnakeHeadY(this);
        snakeHead = new CombinedProperty(this, "snakeHead", snakeHeadX, snakeHeadY);
        // start
        createPath(SnakeEvent.PressStart.get(), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameState.MOVE)
                .addFollowingExpectation(new ActionExpectation() {
                    @Override
                    protected void run() {
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

    public void makeAction() {
        Point fruitPos = snakeModel.getFruitPos();
        if (gameState.getCurrentValue() == GameState.MOVE) {
            HashSet<ExternalEvent> invalidEvents = new HashSet<>();
            ActionFilter actionFilter = event -> !invalidEvents.contains(event);
            while (true) {
                ExternalEvent event = snakeHead.switchTo(Equals.of(StateSnapShoot.builder().append(snakeHeadX, fruitPos.x).append(snakeHeadY, fruitPos.y).build()), actionFilter);
                if (event != null) {
                    if (!tryFire(invalidEvents, event)) continue;
                } else {
                    Logger.getDefault().ww("no valid action, try the rests.");
                    // 优先选择远离中点的方向
                    Point midPos = snakeModel.getMidPos();
                    GlobalExternalEvent recommendedAction = midPos.x > snakeHeadX.getCurrentValue() ? TurnLeft.get() : TurnRight.get();
                    Logger.getDefault().i("middle point is %s, recommend action: %s", midPos, recommendedAction);
                    if (tryFire(invalidEvents, recommendedAction)) {
                        return;
                    }
                    recommendedAction = midPos.y > snakeHeadY.getCurrentValue() ? TurnUp.get() : TurnDown.get();
                    Logger.getDefault().ii("recommend action: ", recommendedAction);
                    if (tryFire(invalidEvents, recommendedAction)) {
                        return;
                    }
                    for (SnakeEvent snakeEvent : EnumSet.of(TurnUp, TurnDown, TurnLeft, TurnRight)) {
                        if (tryFire(invalidEvents, snakeEvent.get())) {
                            return;
                        }
                    }
                    Logger.getDefault().ee("game over!");
                    printCache(s -> Logger.getDefault().dd(s));
                    asyncFire(SnakeEvent.MoveAhead.get());
                }
                break;
            }
        }
    }

    private boolean tryFire(HashSet<ExternalEvent> invalidEvents, ExternalEvent event) {
        if (invalidEvents.contains(event)) {
            return false;
        }
        Object tag = new Object();
        tag(tag);
        if (event != SnakeEvent.MoveAhead.get()) {
            syncFire(event, null);
        }
        syncFire(SnakeEvent.MoveAhead.get(), null);
        String newValue = gameState.getCurrentValue();
        revertTo(tag);
        if (newValue != GameState.MOVE) {
            invalidEvents.add(event);
            Logger.getDefault().ii("invalid action: ", event);
            return false;
        }
        asyncFire(event);
        if (event != SnakeEvent.MoveAhead.get()) {
            asyncFire(SnakeEvent.MoveAhead.get());
        }
        return true;
    }

    public String getCurrentState() {
        return gameState.getCurrentValue();
    }

    @Override
    public <V> V fetchState(Obtainable<V> stateToCheck) {
        Logger.getDefault().ww("we are not supposed to get here!");
        return null;
    }

    @Override
    public boolean performAction(ExternalEvent externalEvent) {
        return true;
    }

    public Boolean verifyExpectation(NonPropertyExpectation expectation) {
        return true;
    }
}
