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

	private static final String HYDRO_PROJECT = "/testproject/hydro/project.halex"
	private static final String HYDRO_DATA = "/testproject/hydro/hydro-source.gml.gz"

	private static final String MULTI_TYPE_PROJECT = "/testproject/multitype/project.halex"
	private static final String MULTI_TYPE_DATA = "/testproject/multitype/multi-type-source.xml"


	// XXX Doesn't work -> private static final String METADATA_PATH = "platform:/plugin/$PLUGIN_NAME/projects/gmdMD_Metadata.xml"
	// works, same as absolute path does
	private static final String METADATA_PATH = "projects/gmdMD_Metadata.xml"

	private static final String HYDRO_ARGS_FILE_PATH = "projects/arguments/hydro_arguments_file.txt"
	private static final String HYDRO_EX1Typ = "{eu:esdihumboldt:hale:example}RiverType"
	private static final String HYDRO_EXP1Exp = "width='10.0'"
	private static final String HYDRO_EXP2UNCONDITIONAL = "name='River Rede'"
	private static final String HYDRO_EXCLUDED_TYPE = "River1"
	private static final int HYDRO_TDATA_SIZE_TYPEDFILTER = 60
	private static final int HYDRO_TDATA_SIZE_ARGSFILE = 60
	private static final int HYDRO_TDATA_SIZE_UNCONDITIONAL_FILTER = 34
	private static final int HYDRO_TDATA_SIZE_EXCLUDE_TYPE = 982


	private static final String MULTITYPE_EX1UNCONDITIONAL = "price>'15'" // shirt = 11 and hat= 12 : total = 23
	private static final String MULTITYPE_EX1Typ = "shirt"
	private static final String MULTITYPE_EX1Exp = "name='Polo shirt green'" // shirt = 6 in which (price < 15) = 2
	private static final String MULTITYPE_EX1EXCLUDETYPE = "hat" // hat = 0 .
	private static final int MULTITYPE_TDATA_SIZE = 13

	private static final String MULTITYPE_EX2UNCONDITIONAL = "price>'11'" //shirt = 11 and hat = 12 : total = 23
	private static final String MULTITYPE_EX2Typ = "shirt"
	private static final String MULTITYPE_EX2Exp = "name='Polo shirt green'" // shirt = 6 in which (price < 11) = 2
	private static final String MULTITYPE_EX2EXCLUDETYPE = "shirt" // shirt = 0
	private static final String MULTITYPE_EX2_2Typ = "{http://www.example.org/t1/}HatType"
	private static final String MULTITYPE_EX2_2Exp = "name='Big hat' or name='HarryPotter hat' or name='Round hat'" // Big hat=1 & HaryPotter hat=5 in which (price<11)=1 & Round hat=3 in which (price<11):2
	private static final int MULTITYPE_TDATA_SIZE2 = 15


	private static final String MULTITYPE_EX3Typ = "{http://www.example.org/t1/}ShirtType"
	private static final String MULTITYPE_EX3Exp = "price>'15.50' and price<'19.99'" // shirt = 3
	private static final String MULTITYPE_EX3_2Typ = "{http://www.example.org/t1/}HatType"
	private static final String MULTITYPE_EX3_2Exp = "name='HarryPotter hat' or (name='Round hat' and price<'10')" // HaryPotter hat=5 & Round hat=2
	private static final int MULTITYPE_TDATA_SIZE3 = 10

	private static final String MULTITYPE_ARGS_FILE_PATH = "projects/arguments/multitypes_arguments_file.txt"

	private static final String MULTITYPE_ARGS_2_FILE_PATH = "projects/arguments/multitypes_arguments_file2.txt"
	private static final int MULTITYPE_TDATA_SIZE4 = 30

	/**
	 * Copies the source file to a temporary file.
	 * 
	 */
	private void createTempFile(String sourceFilePath, File targetFile) throws IOException {
		InputStream is = ExecuteTest.class.getClassLoader().getResourceAsStream(sourceFilePath)
		FileOutputStream os = new FileOutputStream(targetFile)
		os << is;
		is.close()
		os.close()
	}

	/**
	 * getting URI of project file
	 */
	private URI getProjectURI(String path){
		URL url = ExecuteTest.class.getClassLoader().getResource(path)
		if(!url)
			throw new IllegalStateException("Could not find " + path)

		return url.toURI()
	}


	/**
	 * Test Args file without filter for multi types source project
	 */
	void testArgsFileWOFilterForMultiTypesSource() {
		File targetFile =  File.createTempFile('transform-multitype', '.xml')
		targetFile.deleteOnExit()

		File tempArgsFile = File.createTempFile('arguments', '.txt')
		tempArgsFile.deleteOnExit()
		createTempFile(MULTITYPE_ARGS_2_FILE_PATH, tempArgsFile);

		println ">> Arguments will be read from ${tempArgsFile}"
		println ">> Transformed data will be written to ${targetFile}..."
		transform([//
			'-args-file', tempArgsFile.absolutePath, //
			'-target', targetFile.absolutePath, //
			'-providerId', 'eu.esdihumboldt.hale.io.xml.writer', //
			'-Sxml.rootElement.name', 'collection' //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}
		validateMultipleTypeTransformedDataSize(targetFile, MULTITYPE_TDATA_SIZE4)
	}



	/**
	 * Test Args file of an project in which source contains more types
	 */
	void testArgsFileForMultiTypesSource() {
		File targetFile =  File.createTempFile('transform-multitype', '.xml')
		targetFile.deleteOnExit()

		File tempArgsFile = File.createTempFile('arguments', '.txt')
		tempArgsFile.deleteOnExit()
		createTempFile(MULTITYPE_ARGS_FILE_PATH, tempArgsFile);

		println ">> Arguments will be read from ${tempArgsFile}"
		println ">> Transformed data will be written to ${targetFile}..."
		transform([//
			'-args-file', tempArgsFile.absolutePath, //
			'-exclude-type', MULTITYPE_EX2EXCLUDETYPE, //
			'-filter-on', MULTITYPE_EX2_2Typ, MULTITYPE_EX2_2Exp, //
			'-target', targetFile.absolutePath, //
			'-providerId', 'eu.esdihumboldt.hale.io.xml.writer', //
			'-Sxml.rootElement.name', 'collection' //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}
		validateMultipleTypeTransformedDataSize(targetFile, MULTITYPE_TDATA_SIZE2)
	}

	/**
	 * Test filters on source consisted Multiple Types, complicated 2
	 */
	void testFiltersForMultiType3() {
		File targetFile =  File.createTempFile('transform-multitype', '.xml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(MULTI_TYPE_PROJECT).toString() , //
			'-source', getProjectURI(MULTI_TYPE_DATA).toString(), //
			'-filter-on', MULTITYPE_EX3Typ, MULTITYPE_EX3Exp, //
			'-filter-on', MULTITYPE_EX3_2Typ, MULTITYPE_EX3_2Exp, //
			'-target', targetFile.absolutePath, //
			'-providerId', 'eu.esdihumboldt.hale.io.xml.writer', //
			'-Sxml.rootElement.name', 'collection' //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateMultipleTypeTransformedDataSize(targetFile,MULTITYPE_TDATA_SIZE3)
	}


	/**
	 * Test filters on source consisted Multiple Types, complicated 1
	 */
	void testFiltersForMultiType2() {
		File targetFile =  File.createTempFile('transform-multitype', '.xml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(MULTI_TYPE_PROJECT).toString(), //
			'-source', getProjectURI(MULTI_TYPE_DATA).toString(), //
			'-filter', MULTITYPE_EX2UNCONDITIONAL, //
			'-filter-on', MULTITYPE_EX2Typ, MULTITYPE_EX2Exp, //
			'-exclude-type', MULTITYPE_EX2EXCLUDETYPE, //
			'-filter-on', MULTITYPE_EX2_2Typ, MULTITYPE_EX2_2Exp, //
			'-target', targetFile.absolutePath, //
			'-providerId', 'eu.esdihumboldt.hale.io.xml.writer', //
			'-Sxml.rootElement.name', 'collection' //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateMultipleTypeTransformedDataSize(targetFile,MULTITYPE_TDATA_SIZE2)
	}


	/**
	 * Test filters on source consisted Multiple Types*
	 */
	void testFiltersForMultiType() {
		File targetFile =  File.createTempFile('transform-multitype', '.xml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(MULTI_TYPE_PROJECT).toString() , //
			'-source', getProjectURI(MULTI_TYPE_DATA).toString(), //
			'-filter', MULTITYPE_EX1UNCONDITIONAL, //
			'-filter-on', MULTITYPE_EX1Typ, MULTITYPE_EX1Exp, //
			'-exclude-type', MULTITYPE_EX1EXCLUDETYPE, //
			'-target', targetFile.absolutePath, //
			'-providerId', 'eu.esdihumboldt.hale.io.xml.writer', //
			'-Sxml.rootElement.name', 'collection' //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateMultipleTypeTransformedDataSize(targetFile,MULTITYPE_TDATA_SIZE)
	}


	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateMultipleTypeTransformedDataSize(File targetFile, int dataSize) {
		// check written file
		def root = new XmlSlurper().parse(targetFile)
		// check container
		assert root.name() == 'collection'
		// check transformed feature count
		assert root.item.size() == dataSize
	}

	/**
	 * Test Args file of an hydro project.	 
	 */
	void testArgsFileForHydro() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()

		File tempArgsFile = File.createTempFile('arguments', '.txt')
		tempArgsFile.deleteOnExit()
		createTempFile(HYDRO_ARGS_FILE_PATH, tempArgsFile);

		println ">> Arguments will be read from ${tempArgsFile}"
		println ">> Transformed data will be written to ${targetFile}..."
		transform([//
			'-args-file', tempArgsFile.absolutePath, //
			'-target', targetFile.absolutePath, //
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer' //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}
		validateTransformedDataSize(targetFile, HYDRO_TDATA_SIZE_ARGSFILE)
	}

	/**
	 * Test exclude-type filter of an hydro project.	 *
	 */
	void testExcludeTypeForHydro() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(HYDRO_PROJECT).toString(), //
			'-source', getProjectURI(HYDRO_DATA).toString(), //
			'-exclude-type', HYDRO_EXCLUDED_TYPE, //
			'-target', targetFile.absolutePath, //
			//select target provider
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', //
			//override a setting
			'-Sinspire.sds.localId', '1234' //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateTransformedDataSize(targetFile,HYDRO_TDATA_SIZE_EXCLUDE_TYPE)
	}


	/**
	 * Test typed filter of an hydro project.	 *
	 */
	void testTypedFilterForHydro() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(HYDRO_PROJECT).toString(), //
			'-source', getProjectURI(HYDRO_DATA).toString(), //
			'-filter', HYDRO_EXP2UNCONDITIONAL, //
			'-filter-on', HYDRO_EX1Typ, HYDRO_EXP1Exp, //
			'-target', targetFile.absolutePath, //
			// select target provider
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', //
			// override a setting
			'-Sinspire.sds.localId', '1234'//
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateTransformedDataSize(targetFile,HYDRO_TDATA_SIZE_TYPEDFILTER)
	}

	/**
	 * Test unconditional filtered transformation of an hydro project.	 *
	 */
	void testUnconditionalFilterForHydro() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(HYDRO_PROJECT).toString(), //
			'-source', getProjectURI(HYDRO_DATA).toString(), //
			'-filter', HYDRO_EXP2UNCONDITIONAL, //
			'-target', targetFile.absolutePath, //
			// select target provider
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', //
			// override a setting
			'-Sinspire.sds.localId', '1234' //
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateTransformedDataSize(targetFile,HYDRO_TDATA_SIZE_UNCONDITIONAL_FILTER)
	}

	/**
	 * Test transformation of an hydro project.	 * 
	 */
	void testTransformXml() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()

		File tempMetadataFile = File.createTempFile('gmdMD_Metadata', '.xml')
		tempMetadataFile.deleteOnExit()
		createTempFile(METADATA_PATH, tempMetadataFile);

		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(HYDRO_PROJECT).toString(), //
			'-source', getProjectURI(HYDRO_DATA).toString(), //
			'-target', targetFile.absolutePath, //
			// select target provider
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', //
			// override a setting
			'-Sinspire.sds.localId', '1234', //
			'-Sinspire.sds.metadata', tempMetadataFile.absolutePath//
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateHydroXml(targetFile)
	}

	/**
	 * Test transformation of an hydro project.
	 * Try, if the metadata.inline parameter can be set with 
	 * content read from XML file.
	 */
	void testTransformXmlInline() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()

		File tempMetadataFile = File.createTempFile('gmdMD_Metadata', '.xml')
		tempMetadataFile.deleteOnExit()
		createTempFile(METADATA_PATH, tempMetadataFile);

		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(HYDRO_PROJECT).toString(), //
			'-source', getProjectURI(HYDRO_DATA).toString(), //
			'-target', targetFile.absolutePath, //
			//select target provider
			'-providerId', 'eu.esdihumboldt.hale.io.inspiregml.writer', //
			// override a setting
			'-Xinspire.sds.metadata.inline', tempMetadataFile.absolutePath]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateHydroXml(targetFile)
	}

	/**
	 * Test transformation of an hydro project.
	 */
	void testTransform() {
		File targetFile =  File.createTempFile('transform-hydro', '.gml')
		targetFile.deleteOnExit()
		println ">> Transformed data will be written to ${targetFile}..."

		transform([//
			'-project', getProjectURI(HYDRO_PROJECT).toString(), //
			'-source', getProjectURI(HYDRO_DATA).toString(), //
			'-target', targetFile.absolutePath, //
			// select preset for export
			'-preset', 'INSPIRE SpatialDataSet', //
			// override a setting
			'-Sinspire.sds.localId', '1234'//
		]) { //
			File output, int code ->
			// check exit code
			assert code == 0
		}

		validateHydro(targetFile, '1234')
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateTransformedDataSize(File targetFile, int dataSize) {
		// check written file
		def root = new XmlSlurper().parse(targetFile)
		// check container
		assert root.name() == 'SpatialDataSet'
		// check transformed feature count
		assert root.member.Watercourse.size() == dataSize
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
		//check metadata language tag
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
