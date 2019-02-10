package com.yanry.driver.mobile.sample.snake;

import com.yanry.driver.core.model.libtemp.revert.RevertManager;
import com.yanry.driver.core.model.libtemp.revert.RevertibleLinkedList;
import com.yanry.driver.core.model.libtemp.revert.RevertibleObject;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SnakeModel {

    private Random random;
    private RevertibleLinkedList<Point> snakePoints;
    private RevertibleObject<Point> fruitPoint;

    public SnakeModel(RevertManager revertManager) {
        random = new Random();
        snakePoints = new RevertibleLinkedList<>(revertManager);
        fruitPoint = new RevertibleObject<>(revertManager);
    }

    public void push(Point newPoint) {
        snakePoints.push(newPoint);
    }

    public Point removeLast() {
        return snakePoints.removeLast();
    }

    public void clear() {
        snakePoints.clear();
    }

    public int length() {
        return snakePoints.size();
    }

    public List<Point> getSnakePoints() {
        return snakePoints.getList();
    }

    public Point getFruitPos() {
        return fruitPoint.get();
    }

    public boolean contains(int x, int y) {
        return snakePoints.contains(new Point(x, y));
    }

    public boolean isBody(Point point) {
        return !Objects.equals(point, snakePoints.peekFirst()) && snakePoints.contains(point);
    }

    public void spawnFruit() {
        int index = random.nextInt(GameConfigure.COL_COUNT * GameConfigure.ROW_COUNT - snakePoints.size());
        int freeFound = -1;
        for (int x = 0; x < GameConfigure.COL_COUNT; x++) {
            for (int y = 0; y < GameConfigure.ROW_COUNT; y++) {
                if (!contains(x, y) && ++freeFound == index) {
                    fruitPoint.set(new Point(x, y));
                    return;
                }
            }
        }
    }
}
