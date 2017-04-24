/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.runtime;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;

/**
 * @author yanry
 *         <p>
 *         Jan 9, 2017
 */
@Presentable
public class Assertion {

	private Expectation expectation;
    private Boolean isPass;

    public Assertion(Expectation expectation, Boolean isPass) {
        this.expectation = expectation;
		this.isPass = isPass;
	}

	@Presentable
	public Expectation getExpectation() {
		return expectation;
	}

	@Presentable
    public Boolean isPass() {
        return isPass;
	}

}
