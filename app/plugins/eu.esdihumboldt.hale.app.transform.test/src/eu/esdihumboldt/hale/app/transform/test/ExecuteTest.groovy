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
import eu.esdihumboldt.hale.common.test.TestUtil
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode



/**
 * Test command line application execution.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ExecuteTest extends GroovyTestCase {

	private static final String PLUGIN_NAME = 'eu.esdihumboldt.hale.app.transform.test'

	private static final String HYDRO_PROJECT = "platform:/plugin/$PLUGIN_NAME/projects/hydro/hydro-basic.halez"
	private static final String HYDRO_DATA = "platform:/plugin/$PLUGIN_NAME/projects/hydro/hydro-source.gml.gz"

	// XXX Doesn't work -> private static final String METADATA_PATH = "platform:/plugin/$PLUGIN_NAME/projects/gmdMD_Metadata.xml"
	// works, same as absolute path does
	private static final String METADATA_PATH = "./projects/gmdMD_Metadata.xml"

	/**
	 * XXX Disabled because for some reason it breaks the test execution
	 * part of the build process, even though the test itself is executed w/o problems.
	 * The problem seems to be the framework shutdown, maybe related to the use of OrientDB
	 * within the transformation in this test.
	 * 
	 * Test transformation of an example project.	 * 
	 */
	void ignore_testTransformXml() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([
			'-project',
			HYDRO_PROJECT,
			'-source',
			HYDRO_DATA,
			'-target',
			targetFile.absolutePath,
			// select target provider
			'-providerId',
			'eu.esdihumboldt.hale.io.inspiregml.writer',
			// override a setting
			'-Sinspire.sds.localId',
			'1234',
			'-Sinspire.sds.metadata',
			METADATA_PATH
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateHydroXml(targetFile)
	}

	/**
	 * XXX Disabled because for some reason it breaks the test execution
	 * part of the build process, even though the test itself is executed w/o problems.
	 * The problem seems to be the framework shutdown, maybe related to the use of OrientDB
	 * within the transformation in this test.
	 * 
	 * Test transformation of an example project.
	 * Try, if the metadata.inline parameter can be set with 
	 * content read from XML file.
	 */
	void ignore_testTransformXmlInline() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([
			'-project',
			HYDRO_PROJECT,
			'-source',
			HYDRO_DATA,
			'-target',
			targetFile.absolutePath,
			// select target provider for export
			'-providerId',
			'eu.esdihumboldt.hale.io.inspiregml.writer',
			// override a setting
			'-Xinspire.sds.metadata.inline',
			METADATA_PATH
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateHydroXml(targetFile)
	}

	/**
	 * XXX Disabled because for some reason it breaks the test execution
	 * part of the build process, even though the test itself is executed w/o problems.
	 * The problem seems to be the framework shutdown, maybe related to the use of OrientDB
	 * within the transformation in this test.
	 * 
	 * Test transformation of an example project.
	 *
	 */
	void ignore_testTransform() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([
			'-project',
			HYDRO_PROJECT,
			'-source',
			HYDRO_DATA,
			'-target',
			targetFile.absolutePath,
			// select preset for export
			'-preset',
			'INSPIRE SpatialDataSet',
			// override a setting
			'-Sinspire.sds.localId',
			'1234'
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateHydro(targetFile, '1234')
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateHydro(File targetFile, String dataSetId) {
		// check written file
		def root = new XmlSlurper().parse(targetFile)
		// check container
		assert root.name() == 'SpatialDataSet'
		// check transformed feature count
		assert root.member.Watercourse.size() == 982
		// check local ID
		assert root.identifier.Identifier.localId[0].text() == dataSetId
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateHydroXml(File targetFile) {
		// check written file
		def root = new XmlSlurper().parse(targetFile)
		// check container
		assert root.name() == 'SpatialDataSet'
		// check transformed feature count
		assert root.member.Watercourse.size() == 982
		// check metadata language tag
		assert root.metadata.MD_Metadata.language.CharacterString.text() == 'DE'
		// check metadata date tag
		assert root.metadata.MD_Metadata.dateStamp.Date.text() == '2014-06-10'
	}

	/**
	 * Run w/o parameters. Usage should be printed.
	 */
	void testUsage() {
		transform { File output, int code ->
			// check exit code
			assert code == 0

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
		/*
		 * Exclude scala-ide - it leads to NoClassDefFoundErrors because it somehow
		 * hooks into output and error streams used by Eclipse
		 */
		TestUtil.uninstallBundle('org.scala-ide.sdt.core')

		// set up necessary bundles for transformation
		TestUtil.startConversionService()
		TestUtil.startInstanceFactory();
		TestUtil.startTransformationService()


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
			ExecApplication app = new ExecApplication()
			res = (int) ApplicationUtil.launchSyncApplication(app, args)
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
