/*
 * Copyright (c) 2017 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.test.allure;

import java.io.File;

import ru.yandex.qatools.allure.junit.AllureRunListener;

/**
 * Run listener for Allure.
 * 
 * @author Simon Templer
 */
public class AllureTestListener extends AllureRunListener {

	/**
	 * Default constructor.
	 */
	public AllureTestListener() {
		super();

		// print working directory to easier find allure reports
		// which are probably put to working directory/target/allure-results
		System.out.println("Current working directory is " + new File(".").getAbsolutePath());
	}

}
