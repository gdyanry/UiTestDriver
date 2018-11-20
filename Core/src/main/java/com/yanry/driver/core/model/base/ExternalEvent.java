/**
 *
 */
package com.yanry.driver.core.model.base;

/**
 * @author yanry
 * <p>
 * Jan 6, 2017
 */
public class ExternalEvent extends Event {
    private Context precondition;

    public void addPrecondition(Property property, ValuePredicate predicate) {
        if (precondition == null) {
            precondition = new Context();
        }
        precondition.add(property, predicate);
    }

    Context getPrecondition() {
        return precondition;
    }
}
