package com.yanry.driver.mobile.sample.snake;

import java.awt.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class SnakeModel {

    private Random random = new Random();

    private LinkedList<Point> snakePoints = new LinkedList<>();

    private Point fruitPoint;

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

    public LinkedList<Point> getSnakePoints() {
        return new LinkedList<>(snakePoints);
    }

    public Point getFruitPos() {
        return fruitPoint;
    }

    public boolean contains(int x, int y) {
        return snakePoints.contains(new Point(x, y));
    }

    public boolean isBody(Point point) {
        return !Objects.equals(point, snakePoints.peekFirst()) && snakePoints.contains(point);
    }

    public boolean containsX(int x) {
        for (Point point : snakePoints) {
            if (point.x == x) {
                return true;
            }
        }
        return false;
    }

    public boolean containsY(int y) {
        for (Point point : snakePoints) {
            if (point.y == y) {
                return true;
            }
        }
        return false;
    }

    public void spawnFruit() {
        int index = random.nextInt(GameConfigure.COL_COUNT * GameConfigure.ROW_COUNT - snakePoints.size());
        int freeFound = -1;
        for (int x = 0; x < GameConfigure.COL_COUNT; x++) {
            for (int y = 0; y < GameConfigure.ROW_COUNT; y++) {
                if (!contains(x, y) && ++freeFound == index) {
                    fruitPoint = new Point(x, y);
                    break;
                }
            }
        }
    }
}
