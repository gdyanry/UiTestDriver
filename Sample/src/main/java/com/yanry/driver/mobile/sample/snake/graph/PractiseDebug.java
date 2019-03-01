package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.event.GlobalExternalEvent;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PractiseDebug {
    private int lastIndex;
    private DataOutputStream stream;

    public PractiseDebug() {
        lastIndex = -1;
    }

    public void begin() {
        try {
            stream = new DataOutputStream(new FileOutputStream("g:/practise-debug.txt", false));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void end() {
        try {
            printLine("=====");
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printLine(String line) {
        try {
            stream.writeUTF(new StringBuilder().append('\n').append(line).append('\n').toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void debug(int index, ExternalEvent event) {
        try {
            if (index > lastIndex) {
                stream.write(getEventChar(event));
            } else {
                stream.write('\n');
                for (int i = 0; i < index; i++) {
                    stream.write(' ');
                }
                stream.write(getEventChar(event));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastIndex = index;
    }

    private char getEventChar(ExternalEvent event) {
        GlobalExternalEvent globalExternalEvent = (GlobalExternalEvent) event;
        if (globalExternalEvent == SnakeEvent.MoveAhead.get()) {
            return 'M';
        } else if (globalExternalEvent == SnakeEvent.TurnDown.get()) {
            return 'D';
        } else if (globalExternalEvent == SnakeEvent.TurnLeft.get()) {
            return 'L';
        } else if (globalExternalEvent == SnakeEvent.TurnRight.get()) {
            return 'R';
        } else if (globalExternalEvent == SnakeEvent.TurnUp.get()) {
            return 'U';
        }
        System.err.println(event);
        return '?';
    }
}
