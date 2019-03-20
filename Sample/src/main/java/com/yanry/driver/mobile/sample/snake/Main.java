package com.yanry.driver.mobile.sample.snake;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Watcher;
import com.yanry.driver.mobile.sample.snake.graph.SnakeController;
import yanry.lib.java.model.log.ConsoleHandler;
import yanry.lib.java.model.log.LogLevel;
import yanry.lib.java.model.log.Logger;
import yanry.lib.java.model.log.SimpleFormatter;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Logger.getDefault().addHandler(new ConsoleHandler(new SimpleFormatter().sequenceNumber().time().thread().method(5), LogLevel.Info));
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
                Logger.getDefault().v(">>>>%s - %s", property, property.getCurrentValue());
            }
        });
        game.startGame();
    }
}
