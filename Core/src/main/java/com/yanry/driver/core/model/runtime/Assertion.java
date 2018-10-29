/**
 *
 */
package com.yanry.driver.core.model.runtime;

import com.yanry.driver.core.model.base.Expectation;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.HandyObject;
import lib.common.util.object.Visible;

/**
 * @author yanry
 * <p>
 * Jan 9, 2017
 */
public class Assertion extends HandyObject {

    private Expectation expectation;
    private Boolean isPass;

    public Assertion(Expectation expectation, Boolean isPass) {
        this.expectation = expectation;
        this.isPass = isPass;
    }

    @Visible
    @EqualsPart
    public Expectation getExpectation() {
        return expectation;
    }

    @Visible
    @EqualsPart
    public Boolean isPass() {
        return isPass;
    }

}
