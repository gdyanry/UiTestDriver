package com.yanry.driver.mobile.sample.snake;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Watcher;
import com.yanry.driver.mobile.sample.snake.graph.SnakeController;
import lib.common.model.log.ConsoleHandler;
import lib.common.model.log.LogLevel;
import lib.common.model.log.Logger;
import lib.common.model.log.SimpleFormatter;

import javax.swing.*;

public class Main {
    private static final boolean AUTO = true;

    public static void main(String[] args) {
        Logger.getDefault().addHandler(new ConsoleHandler(new SimpleFormatter().sequenceNumber().method(), LogLevel.Verbose));
        final SnakeModel gameModel = new SnakeModel();
        SnakeController controller = new SnakeController(gameModel, AUTO);
        final SnakeGame game = new SnakeGame(controller, gameModel);
        controller.setWatcher(new Watcher() {
            @Override
            public void onTransitionComplete() {
                SwingUtilities.invokeLater(() -> {
                    game.repaint();
                    game.setTitle("Greedy Snake(" + gameModel.length() + ")");
                });
            }

            @Override
            public <V> void onStateChange(Property<V> property, V fromVal, V toVal) {
                System.out.println(String.format(">>>>%s - %s", property, property.getCurrentValue()));
            }
        });
        game.startGame();
    }
}
