package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.*;
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
import java.util.stream.Stream;

public class SnakeController extends StateSpace implements Communicator {
    private GameState gameState;
    private SnakeHeadX snakeHeadX;
    private SnakeHeadY snakeHeadY;
    private CombinedProperty snakeHead;

    public SnakeController(SnakeModel snakeModel) {
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
        createPath(SnakeEvent.TuneRight.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, Direction.RIGHT))
                .addContextValue(gameState, GameState.MOVE)
                .addContextPredicate(direction, Equals.of(Direction.LEFT).not());
        // -> left
        createPath(SnakeEvent.TuneLeft.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, Direction.LEFT))
                .addContextValue(gameState, GameState.MOVE)
                .addContextPredicate(direction, Equals.of(Direction.RIGHT).not());
        // -> up
        createPath(SnakeEvent.TurnUp.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, Direction.UP))
                .addContextValue(gameState, GameState.MOVE)
                .addContextPredicate(direction, Equals.of(Direction.DOWN).not());
        // -> down
        createPath(SnakeEvent.TuneDown.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, Direction.DOWN))
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

    public void makeAction(Point fruitPos) {
        if (gameState.getCurrentValue() == GameState.MOVE) {
            ActionCollector actionCollector = new ActionCollector(1);
//            actionCollector.addPromise(gameState, Equals.of(GameState.GAME_OVER).not());
            snakeHead.switchTo(Equals.of(StateSnapShoot.builder().append(snakeHeadX, fruitPos.x).append(snakeHeadY, fruitPos.y).build()), actionCollector);
            ExternalEvent event = actionCollector.pop();
            if (event != null && !SnakeEvent.MoveAhead.get().equals(event)) {
                fireLater(event);
            }
        }
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
