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

	private static final String ARGS_FILE_PATH = "./projects/arguments/arguments_file.txt"

	private static final String FILTER_EXPRESSION1 = "CQL:name='River Till'";
	private static final String FILTER_EXPRESSION2 = "CQL:width>'15.0'";
	private static final String FILTER_EXPRESSION3Typ = "{eu:esdihumboldt:hale:example}RiverType";
	private static final String FILTER_EXPRESSION3Exp = "width='10.0'";
	private static final String FILTER_EXPRESSION4 = "name='River Rede'";
	private static final String EXCLUDED_TYPE = "River";
	private static final int TRANSFORMED_DATA_SIZE_TYPEDFILTER = 60;
	private static final int TRANSFORMED_DATA_SIZE_ARGSFILE = 60;
	private static final int TRANSFORMED_DATA_SIZE_UNCONDITIONAL_FILTER = 34;
	private static final int EXCLUDE_TYPE_DATA_SIZE = 0;



	/**
	 * Copies the args file to a temporary file.
	 * 
	 */
	private void createArgumentsTempFile(File tempArgsFile) throws IOException {

		InputStream is = ExecuteTest.class.getClassLoader().getResourceAsStream(ARGS_FILE_PATH)
		FileOutputStream os = new FileOutputStream(tempArgsFile)
		//os << is; // line is getting failed in JIRA build
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		is.close()
		os.close()
	}

	/**
	 *
	 * Test Args file of an example project.	 
	 * 
	 */
	void testArgsFileTransformXml() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()

		File tempArgsFile = File.createTempFile('arguments', '.txt')
		try{
			createArgumentsTempFile(tempArgsFile);

			println ">> Arguments will be read from ${tempArgsFile}"
			println ">> Transformed data will be written to ${targetFile}..."
			transform(['-args-file', //
				tempArgsFile.absolutePath, //
				'-target', //
				targetFile.absolutePath, //
				'-providerId', //
				'eu.esdihumboldt.hale.io.inspiregml.writer' //
			]) { //
				File output, int code ->
				// check exit code
				assert code == 0
			}
			validateArgsFileHydroXml(targetFile)

			tempArgsFile.delete();

		}catch(Exception ex){

		}
	}
	/**
	 *
	 * Test typed filter of an example project.	 *
	 */
	void testExcludeTypeTransformXml() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform(['-project', HYDRO_PROJECT, '-source', HYDRO_DATA, '-exclude-type', EXCLUDED_TYPE, '-target', targetFile.absolutePath, // select target provider
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', // override a setting
			'-Sinspire.sds.localId', //
			'1234', //
			'-Sinspire.sds.metadata', //
			METADATA_PATH //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateExcludeTypeHydroXml(targetFile)
	}


	/**
	 *
	 * Test typed filter of an example project.	 *
	 */
	void testTypedFilteredTransformXml() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([
			'-project',
			HYDRO_PROJECT,
			'-source',
			HYDRO_DATA,
			'-filter',
			FILTER_EXPRESSION4,
			'-filter-on',
			FILTER_EXPRESSION3Typ,
			FILTER_EXPRESSION3Exp,
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

		validateTypedFilteredHydroXml(targetFile)
	}



	/**
	 *
	 * Test unconditional filtered transformation of an example project.	 *
	 */
	void testUnconditionalFilteredTransformXml() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform(['-project', HYDRO_PROJECT, '-source', HYDRO_DATA, '-filter', FILTER_EXPRESSION4, '-target', targetFile.absolutePath, // select target provider
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', // override a setting
			'-Sinspire.sds.localId', '1234', '-Sinspire.sds.metadata', METADATA_PATH]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateUnconditionalFilteredHydroXml(targetFile)
	}

	/**
	 * XXX Disabled because for some reason it breaks the test execution
	 * part of the build process, even though the test itself is executed w/o problems.
	 * The problem seems to be the framework shutdown, maybe related to the use of OrientDB
	 * within the transformation in this test.
	 * 
	 * Test transformation of an example project.	 * 
	 */
	void testTransformXml() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform(['-project', HYDRO_PROJECT, '-source', HYDRO_DATA, '-target', targetFile.absolutePath, // select target provider
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', // override a setting
			'-Sinspire.sds.localId', '1234', '-Sinspire.sds.metadata', METADATA_PATH]) { //
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

		transform(['-project', HYDRO_PROJECT, '-source', HYDRO_DATA, '-target', targetFile.absolutePath, // select target provider for export
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', // override a setting
			'-Xinspire.sds.metadata.inline', METADATA_PATH]) { //
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

		transform(['-project', HYDRO_PROJECT, '-source', HYDRO_DATA, '-target', targetFile.absolutePath, // select preset for export
			'-preset', 'INSPIRE SpatialDataSet', // override a setting
			'-Sinspire.sds.localId', '1234']) { //
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
	private void validateExcludeTypeHydroXml(File targetFile) {
		// check written file
		def root = new XmlSlurper().parse(targetFile)
		// check container
		assert root.name() == 'SpatialDataSet'
		// check transformed feature count
		assert root.member.Watercourse.size() == EXCLUDE_TYPE_DATA_SIZE
		// check metadata language tag
		//assert root.metadata.MD_Metadata.language.CharacterString.text() == 'DE'
		// check metadata date tag
		//assert root.metadata.MD_Metadata.dateStamp.Date.text() == '2014-06-10'
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateUnconditionalFilteredHydroXml(File targetFile) {
		// check written file
		def root = new XmlSlurper().parse(targetFile)
		// check container
		assert root.name() == 'SpatialDataSet'
		// check transformed feature count
		assert root.member.Watercourse.size() == TRANSFORMED_DATA_SIZE_UNCONDITIONAL_FILTER
		// check metadata language tag
		//assert root.metadata.MD_Metadata.language.CharacterString.text() == 'DE'
		// check metadata date tag
		//assert root.metadata.MD_Metadata.dateStamp.Date.text() == '2014-06-10'
	}


	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateTypedFilteredHydroXml(File targetFile) {
		// check written file
		def root = new XmlSlurper().parse(targetFile)
		// check container
		assert root.name() == 'SpatialDataSet'
		// check transformed feature count
		assert root.member.Watercourse.size() == TRANSFORMED_DATA_SIZE_TYPEDFILTER
		// check metadata language tag
		//assert root.metadata.MD_Metadata.language.CharacterString.text() == 'DE'
		// check metadata date tag
		//assert root.metadata.MD_Metadata.dateStamp.Date.text() == '2014-06-10'
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateArgsFileHydroXml(File targetFile) {
		// check written file
		def root = new XmlSlurper().parse(targetFile)
		// check container
		assert root.name() == 'SpatialDataSet'
		// check transformed feature count
		assert root.member.Watercourse.size() == TRANSFORMED_DATA_SIZE_ARGSFILE
		// check metadata language tag
		//assert root.metadata.MD_Metadata.language.CharacterString.text() == 'DE'
		// check metadata date tag
		//assert root.metadata.MD_Metadata.dateStamp.Date.text() == '2014-06-10'
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
		//assert root.metadata.MD_Metadata.language.CharacterString.text() == 'DE'
		// check metadata date tag
		//assert root.metadata.MD_Metadata.dateStamp.Date.text() == '2014-06-10'
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
