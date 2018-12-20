package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.event.GlobalExternalEvent;

public enum SnakeEvent {
    PressStart, PressPause, MoveAhead, TurnUp, TuneDown, TuneLeft, TuneRight;

    public GlobalExternalEvent get() {
        return GlobalExternalEvent.get(this);
    }
}
