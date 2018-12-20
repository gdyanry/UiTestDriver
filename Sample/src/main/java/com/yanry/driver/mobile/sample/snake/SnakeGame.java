package com.yanry.driver.mobile.sample.snake;

import com.yanry.driver.mobile.sample.snake.graph.SnakeController;
import com.yanry.driver.mobile.sample.snake.graph.SnakeEvent;
import lib.common.entity.DaemonTimer;
import lib.common.model.Singletons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.TimerTask;

public class SnakeGame extends JFrame {

    private static final long serialVersionUID = 5135000138657306646L;

    private final SnakePanel panel;

    private final SnakeController gameController;

    public SnakeGame(final SnakeController controller, final SnakeModel gameModel) {
        super("Greedy Snake");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        this.gameController = controller;
        this.panel = new SnakePanel(gameController, gameModel);
        add(panel, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {

                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        gameController.fire(SnakeEvent.TurnUp.get());
                        break;

                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        gameController.fire(SnakeEvent.TuneDown.get());
                        break;

                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        gameController.fire(SnakeEvent.TuneLeft.get());
                        break;

                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        gameController.fire(SnakeEvent.TuneRight.get());
                        break;

                    case KeyEvent.VK_P:
                        gameController.fire(SnakeEvent.PressPause.get());
                        break;

                    case KeyEvent.VK_ENTER:
                        gameController.fire(SnakeEvent.PressStart.get());
                        break;
                }
            }

        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Starts the game running.
     */
    public void startGame() {
        Singletons.get(DaemonTimer.class).schedule(new TimerTask() {
            @Override
            public void run() {
                gameController.fire(SnakeEvent.MoveAhead.get());
            }
        }, 0, GameConfigure.FRAME_TIME);
    }
}
