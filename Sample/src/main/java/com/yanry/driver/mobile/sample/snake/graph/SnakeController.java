package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.NonPropertyExpectation;
import com.yanry.driver.core.model.base.ValuePredicate;
import com.yanry.driver.core.model.communicator.Communicator;
import com.yanry.driver.core.model.event.NegationEvent;
import com.yanry.driver.core.model.event.StateChangeEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.extension.CombinedProperty;
import com.yanry.driver.core.model.extension.StateSnapShoot;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.predicate.Within;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import com.yanry.driver.mobile.sample.snake.GameConfigure;
import com.yanry.driver.mobile.sample.snake.SnakeModel;
import lib.common.model.log.Logger;

import java.awt.*;
import java.util.stream.Stream;

public class SnakeController extends Graph implements Communicator {
    private GameState gameState;

    public SnakeController(SnakeModel snakeModel) {
        setCommunicator(this);
        gameState = new GameState(this);
        Direction direction = new Direction(this);
        SnakeHeadX snakeHeadX = new SnakeHeadX(this);
        SnakeHeadY snakeHeadY = new SnakeHeadY(this);
        CombinedProperty snakeHead = new CombinedProperty(this, "snakeHead", snakeHeadX, snakeHeadY);
        // start
        createPath(SnakeEvent.PressStart.get(), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameStateValue.Move)
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
                })).addContextPredicate(gameState, new Within<>(GameStateValue.New, GameStateValue.GameOver));
        // Move -> Pause
        createPath(SnakeEvent.PressPause.get(), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameStateValue.Pause))
                .addContextValue(gameState, GameStateValue.Move);
        // Pause -> Move
        createPath(SnakeEvent.PressPause.get(), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameStateValue.Move))
                .addContextValue(gameState, GameStateValue.Pause);
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
        }), gameState.getStaticExpectation(Timing.IMMEDIATELY, false, GameStateValue.GameOver))
                .addContextValue(gameState, GameStateValue.Move);
        // right
        createPath(SnakeEvent.MoveAhead.get(), snakeHeadX.getShiftExpectation(Timing.IMMEDIATELY, false, true, 1))
                .addContextValue(direction, DirectionValue.Right)
                .addContextValue(gameState, GameStateValue.Move);
        // left
        createPath(SnakeEvent.MoveAhead.get(), snakeHeadX.getShiftExpectation(Timing.IMMEDIATELY, false, false, 1))
                .addContextValue(direction, DirectionValue.Left)
                .addContextValue(gameState, GameStateValue.Move);
        // up
        createPath(SnakeEvent.MoveAhead.get(), snakeHeadY.getShiftExpectation(Timing.IMMEDIATELY, false, false, 1))
                .addContextValue(direction, DirectionValue.Up)
                .addContextValue(gameState, GameStateValue.Move);
        // down
        createPath(SnakeEvent.MoveAhead.get(), snakeHeadY.getShiftExpectation(Timing.IMMEDIATELY, false, true, 1))
                .addContextValue(direction, DirectionValue.Down)
                .addContextValue(gameState, GameStateValue.Move);
        // -> right
        createPath(SnakeEvent.TuneRight.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, DirectionValue.Right))
                .addContextValue(gameState, GameStateValue.Move)
                .addContextPredicate(direction, Equals.of(DirectionValue.Left).not());
        // -> left
        createPath(SnakeEvent.TuneLeft.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, DirectionValue.Left))
                .addContextValue(gameState, GameStateValue.Move)
                .addContextPredicate(direction, Equals.of(DirectionValue.Right).not());
        // -> up
        createPath(SnakeEvent.TurnUp.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, DirectionValue.Up))
                .addContextValue(gameState, GameStateValue.Move)
                .addContextPredicate(direction, Equals.of(DirectionValue.Down).not());
        // -> down
        createPath(SnakeEvent.TuneDown.get(), direction.getStaticExpectation(Timing.IMMEDIATELY, false, DirectionValue.Down))
                .addContextValue(gameState, GameStateValue.Move)
                .addContextPredicate(direction, Equals.of(DirectionValue.Up).not());
        // on move
        ActionExpectation expectation = new ActionExpectation() {
            @Override
            protected void run() {
                Point fruitPos = snakeModel.getFruitPos();
                if (fruitPos.x == snakeHeadX.getCurrentValue() && fruitPos.y == snakeHeadY.getCurrentValue()) {
                    snakeModel.spawnFruit();
                } else if (snakeModel.length() >= GameConfigure.MIN_SNAKE_LENGTH) {
                    snakeModel.removeLast();
                }
                snakeModel.push(new Point(snakeHeadX.getCurrentValue(), snakeHeadY.getCurrentValue()));
            }
        };
        createPath(new StateChangeEvent<>(snakeHeadX), expectation).addContextValue(gameState, GameStateValue.Move);
        createPath(new StateChangeEvent<>(snakeHeadY), expectation).addContextValue(gameState, GameStateValue.Move);
    }

    public GameStateValue getCurrentState() {
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
