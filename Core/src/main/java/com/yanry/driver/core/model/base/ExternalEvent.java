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
    private Context precondition;
    private LinkedList<Runnable> preActions;

    public void addPrecondition(Property property, ValuePredicate predicate) {
        if (precondition == null) {
            precondition = new Context();
        }
        precondition.add(property, predicate);
    }

    public void addPreAction(Runnable action) {
        if (preActions == null) {
            preActions = new LinkedList<>();
        }
        preActions.add(action);
    }

    Context getPrecondition() {
        return precondition;
    }

    public LinkedList<Runnable> getPreActions() {
        return preActions;
    }
}
