package com.yanry.driver.mobile.sample.snake;

import yanry.lib.java.model.revert.RevertManager;
import yanry.lib.java.model.revert.RevertibleLinkedList;
import yanry.lib.java.model.revert.RevertibleObject;

import java.awt.*;
import java.util.Iterator;
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

    public Point getMidPos() {
        int i = 0;
        Iterator<Point> iterator = snakePoints.iterator();
        int midPos = snakePoints.size() / 2;
        while (iterator.hasNext()) {
            if (i++ == midPos) {
                return iterator.next();
            }
            iterator.next();
        }
        return null;
    }

    public Point getNeckPos() {
        if (snakePoints.size() > 1) {
            return snakePoints.get(1);
        }
        return null;
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
