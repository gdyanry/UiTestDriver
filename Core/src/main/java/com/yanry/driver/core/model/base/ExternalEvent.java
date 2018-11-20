/**
 *
 */
package com.yanry.driver.core.model.base;

import java.util.LinkedList;

/**
 * @author yanry
 * <p>
 * Jan 6, 2017
 */
public class ExternalEvent extends Event {
    LinkedList<State> preconditions;

    public void addPrecondition(State state) {
        if (preconditions == null) {
            preconditions = new LinkedList<>();
        }
        preconditions.add(state);
    }
}
