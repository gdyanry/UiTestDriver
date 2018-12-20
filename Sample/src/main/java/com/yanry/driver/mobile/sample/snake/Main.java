package com.yanry.driver.mobile.sample.snake;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.mobile.sample.snake.graph.SnakeController;

import javax.swing.*;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        final SnakeModel gameModel = new SnakeModel();
        SnakeController controller = new SnakeController(gameModel);
        final SnakeGame game = new SnakeGame(controller, gameModel);
        controller.setWatcher(new GraphWatcher() {
            @Override
            public void onTransitionComplete(Map<Property, Object> propertyCache, Set<Property> nullCache, Set<Path> verifiedPaths) {
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
