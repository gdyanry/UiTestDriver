package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.base.ValuePredicate;
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

import java.awt.*;
import java.util.stream.Stream;

import static com.yanry.driver.mobile.sample.snake.graph.SnakeEvent.*;

public class SnakeController extends StateSpace {
    private GameState gameState;
    private SnakeHeadX snakeHeadX;
    private SnakeHeadY snakeHeadY;
    private CombinedProperty snakeHead;
    private SnakeModel snakeModel;
    private Direction direction;
    private SnakeRehearsal rehearsal;

    public SnakeController() {
        rehearsal = new SnakeRehearsal(this);
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
                        direction.cleanCache();
                        snakeHeadX.cleanCache();
                        snakeHeadY.cleanCache();
                        snakeModel.clear();
                        snakeModel.push(new Point(snakeHeadX.getCurrentValue(), snakeHeadY.getCurrentValue()));
                        snakeModel.spawnFruit();
                        rehearsal.reset();
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

    public Direction getDirection() {
        return direction;
    }

    public SnakeHeadX getSnakeHeadX() {
        return snakeHeadX;
    }

    public SnakeHeadY getSnakeHeadY() {
        return snakeHeadY;
    }

    public String getCurrentState() {
        return gameState.getCurrentValue();
    }

    public void makeAction() {
        rehearsal.makeAction();
    }
}
