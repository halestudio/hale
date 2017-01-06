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

package eu.esdihumboldt.util.svg.test;

/**
 * Test utilities.
 * 
 * @author Simon Templer
 */
public class TestUtil {

	/**
	 * Determines if the test is running from within Eclipse.
	 * 
	 * @return if we are currently running in the context of an Eclipse test run
	 */
	public static boolean isRunningEclipseTest() {
		String command = System.getProperty("sun.java.command");
		return command != null
				&& command.contains("org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader");
	}

}
