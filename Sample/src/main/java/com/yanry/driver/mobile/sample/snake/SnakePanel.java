package com.yanry.driver.mobile.sample.snake;

import com.yanry.driver.mobile.sample.snake.graph.GameState;
import com.yanry.driver.mobile.sample.snake.graph.SnakeController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SnakePanel extends JPanel {

    private static final long serialVersionUID = 1197860599866744836L;

    private static final Font FONT = new Font("Tahoma", Font.BOLD, 25);

    private SnakeController controller;

    public SnakePanel(SnakeController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(GameConfigure.COL_COUNT * GameConfigure.CELL_SIZE,
                GameConfigure.ROW_COUNT * GameConfigure.CELL_SIZE));
        setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw snake body
        SnakeModel model = controller.getSnakeModel();
        List<Point> snakePoints = model.getSnakePoints();
        int size = snakePoints.size();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                g.setColor(Color.RED);
            } else {
                int delta = i * 0xff / (size - 1);
                g.setColor(new Color(0xff - delta, delta, 0));
            }
            Point snakePoint = snakePoints.get(i);
            g.fillRect(snakePoint.x * GameConfigure.CELL_SIZE,
                    snakePoint.y * GameConfigure.CELL_SIZE,
                    GameConfigure.CELL_SIZE,
                    GameConfigure.CELL_SIZE);
        }

        // draw fruit
        if (model.getFruitPos() != null) {
            g.setColor(Color.ORANGE);
            g.fillOval(model.getFruitPos().x * GameConfigure.CELL_SIZE + 2,
                    model.getFruitPos().y * GameConfigure.CELL_SIZE + 2,
                    GameConfigure.CELL_SIZE - 4, GameConfigure.CELL_SIZE - 4);
        }

        /*
         * Draw the grid on the board. This makes it easier to see exactly
         * where we in relation to the fruit.
         */
        g.setColor(Color.DARK_GRAY);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        for (int x = 0; x < GameConfigure.COL_COUNT; x++) {
            for (int y = 0; y < GameConfigure.ROW_COUNT; y++) {
                g.drawLine(x * GameConfigure.CELL_SIZE, 0, x * GameConfigure.CELL_SIZE, getHeight());
                g.drawLine(0, y * GameConfigure.CELL_SIZE, getWidth(), y * GameConfigure.CELL_SIZE);
            }
        }

        /*
         * Show a message on the screen based on the current game state.
         */
        if (controller.getCurrentState() == GameState.GAME_OVER ||
                controller.getCurrentState() == GameState.NEW ||
                controller.getCurrentState() == GameState.PAUSE) {
            g.setColor(Color.WHITE);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            String largeMessage = null;
            String smallMessage = null;
            if (controller.getCurrentState() == GameState.NEW) {
                largeMessage = "Snake Game!";
                smallMessage = "Press Enter to Start";
            } else if (controller.getCurrentState() == GameState.GAME_OVER) {
                largeMessage = "Game Over!";
                smallMessage = "Press Enter to Restart";
            } else if (controller.getCurrentState() == GameState.PAUSE) {
                largeMessage = "Paused";
                smallMessage = "Press P to Resume";
            }

            g.setFont(FONT);
            g.drawString(largeMessage, centerX - g.getFontMetrics().stringWidth(largeMessage) / 2, centerY - 50);
            g.drawString(smallMessage, centerX - g.getFontMetrics().stringWidth(smallMessage) / 2, centerY + 50);
        }
    }
}
