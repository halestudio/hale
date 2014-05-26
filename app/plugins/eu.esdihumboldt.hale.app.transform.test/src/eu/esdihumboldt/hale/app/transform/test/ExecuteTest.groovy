/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.app.transform.test;

import eu.esdihumboldt.hale.app.transform.ExecApplication
import eu.esdihumboldt.hale.common.app.ApplicationUtil



/**
 * Test command line application execution.
 * 
 * @author Simon Templer
 */
class ExecuteTest extends GroovyTestCase {

	void testUsage() {
		transform { File output, int code ->
			// check exit code
			assert code != 0

			// check if usage was printed
			def lines = output.readLines()
			assert lines[0].contains('Usage') || lines[1].contains('Usage')
		}
	}

	// general stuff / utilities

	/**
	 * Run the transformation application and write the output to a file.
	 * @param exec a closure taking the {@link File} the output was written to as argument,
	 *   and additionally the exit code
	 * @return the exit code
	 */
	private int transform(List<String> args = [], Closure exec) {
		PrintStream console = System.out
		File output = File.createTempFile('app-test', '.log')
		output.createNewFile()
		output.deleteOnExit()
		System.setOut(new PrintStream(output))
		console.println ">> Writing output to ${output}..."
		int res
		try {
			console.println ">> Executing application with the following arguments: ${args.join(' ')}"

			/*
			 * XXX conflicts with PDE JUnit application and is thus not
			 * executable like this locally in Eclipse.
			 */
			//res = (int) ApplicationUtil.launchApplication('hale.transform', args)
			res = ApplicationUtil.launchSyncApplication(new ExecApplication(), args)
		} finally {
			System.setOut(console)
		}

		console.println ">> Application exited with code $res"

		output.eachLine { line, number ->
			console.println "$number\t: $line"
		}

		if (exec.maximumNumberOfParameters == 1) {
			exec(output)
		}
		else {
			exec(output, res)
		}

		return res
	}
}
