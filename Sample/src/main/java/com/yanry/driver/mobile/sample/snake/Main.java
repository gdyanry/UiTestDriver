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
        Logger.getDefault().addHandler(new ConsoleHandler(new SimpleFormatter().sequenceNumber().time().thread().method(5), LogLevel.Verbose));
        SnakeController controller = new SnakeController();
        final SnakeGame game = new SnakeGame(controller);
        controller.setWatcher(new Watcher() {
            @Override
            public void onTransitionComplete() {
                SwingUtilities.invokeLater(() -> {
                    game.repaint();
                    SnakeModel gameModel = controller.getSnakeModel();
                    Property[] properties = controller.getSnakeHead().getProperties();
                    game.setTitle(String.format("Greedy Snake(%s, %s-%s)", gameModel.length(), properties[0].getCurrentValue(), properties[1].getCurrentValue()));
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
