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

package eu.esdihumboldt.hale.app.transform;

import static eu.esdihumboldt.hale.app.transform.ExecUtil.*

import javax.xml.parsers.DocumentBuilderFactory

import org.eclipse.equinox.app.IApplicationContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.SAXException

import eu.esdihumboldt.hale.common.app.AbstractApplication
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.impl.ElementValue
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * Application that executes a transformation based on a project file.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ExecApplication extends AbstractApplication<ExecContext> {

	public static final String SETTING_PREFIX = '-S'
	public static final String XML_SETTING_PREFIX = '-X'

	enum Configurable {
		source,
		target,
		validate
	}

	private Configurable lastConfigurable

	@CompileStatic(TypeCheckingMode.SKIP)
	@Override
	protected Object run(ExecContext executionContext, IApplicationContext appContext) {
		// set system err to system out, otherwise system err messages seem to get lost
		System.setErr(System.out);

		def args = appContext.arguments[IApplicationContext.APPLICATION_ARGS]

		if (args == null || args.toList().isEmpty()) {
			// no application parameters provided
			usage()
			0 // don't exit with error
		}
		else if (validate(executionContext)) {
			try {
				applyProxySettings()
				new ExecTransformation().run(executionContext)
			} catch (Exception | AssertionError e) {
				error "Transformation execution failed: $e.message"
				if (executionContext.logException) {
					e.printStackTrace()
				}
				1 // exit with error
			}
		}
		else {
			usage()
		}
	}

	protected void applyProxySettings() {
		// unable to access ProxySettings class here (UI dependency)
		// --> just use system properties (they have to be provided)

		// in addition, set default Authenticator based on system properties
		String proxyUser = System.getProperty('http.proxyUser')
		String proxyPassword = System.getProperty('http.proxyPassword')

		if (proxyUser && proxyPassword) {
			Authenticator.setDefault(new eu.esdihumboldt.util.http.HttpAuth(proxyUser, proxyPassword))
		}
	}

	protected boolean validate(ExecContext ec) {
		// project is required
		if (!ec.project) {
			error('You need to provide a reference to a HALE project that defines the mapping for the transformation')
			return false
		}

		// source is required
		if (!ec.sources) {
			error('You need to provide a reference to source data to transform')
			return false
		}

		// target is required
		if (!ec.target) {
			error('You need to provide a target to save the transformation result to')
			return false
		}

		true
	}

	protected String getBaseCommand() {
		'HALE -nosplash -application hale.transform'
	}

	protected int usage() {
		println """
Usage:
$baseCommand
     [-argsFile <file-with-arguments>]
     -project <file-or-URI-to-HALE-project>
     -source <file-or-URI-to-source-data>
         [-include <file-pattern>]
         [-exclude <file-pattern>]
         [-providerId <ID-of-source-reader>]
         [<setting>...]
     [-filter <filter-expression>]
     [-filterOn <type> <filter-expression>]
     [-excludeType <type>]
     [-exclude <filter-expression>]
     -target <target-file-or-URI>
         [-preset <name-of-export-preset>]
         [-providerId <ID-of-target-writer>]
         [<setting>...]
     [-validate <ID-of-target-validator> [<setting>...]]
     [options...]

  where setting is
     ${SETTING_PREFIX}<setting-name> <value>
     ${XML_SETTING_PREFIX}<setting-name> <path-to-XML-file>

  and options are
     -reportsOut <reports-file>
     -stacktrace
     -trustGroovy
     -overallFilterContext

  Sources
    You can provide multiple sources for the transformation. If the source is a
    directory, you can specify multiple -include and -exclude parameters to
    control which files to load.
    If you do not specify -include, it defaults to "**", i.e. all files being
    included, even if they are in sub-directories.
    Patterns use the glob pattern syntax as defined in Java and should be
    quoted to not be interpreted by the shell, see
    http://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-

  Filtering sources
    The options -filter, -filterOn, -excludeType and -exclude serve to filter
    the source data, before the transformation is performed.
    If you specify multiple filters with -filter or -filterOn, only one of
    them must match for an instance to be included. If only -filterOn
    statements are provided, objects not matching the respective types are
    included. Exclusion prevents any object from being transformed.

    <filter-expressions> by default are interpreted as CQL. You can specify
    a specific filter language supported by hale by including a corresponding
    prefix, followed by a colon and the filter expression itself, e.g.:
    CQL:name <> ''

    The option -overallFilterContext ensures that the context available for
    filters is shared for all sources. The filter context can for instance be
    used in groovy: filters.

  Providing arguments as file
    You can also specify the arguments in a file using the -argsFile
    parameter. Each line in the file is interpreted as a separate argument.
    For example:
    -param1
    value
    -param2
    value
    ...
""".trim()

		// general error code
		new Integer(1)
	}

	@Override
	protected void processCommandLineArguments(String[] args, ExecContext executionContext) throws Exception {
		if (args == null)
			return;
		for (int i = 0; i < args.length; i++) {
			// check for args without parameters (i.e., a flag arg)
			processFlag(args[i], executionContext)
			// check for args with parameters. If we are at the last argument or
			// if the next one
			// has a '-' as the first character, then we can't have an arg with
			// a param so continue.
			if (i == args.length - 1 || args[i + 1].startsWith("-")) //$NON-NLS-1$
				continue

			//check for args file, if supplied then handle it and continue
			if(args[i] == '-args-file' || args[i] == '-argsFile'){
				processArgumentsFile(args[++i], executionContext)
				continue;
			}


			int noofValues = getNumberOfValues(args[i])
			if(noofValues == 1){
				processParameter(args[i], args[++i] , executionContext)
			}
			else{
				List<String> values = [];
				//loop through each values.
				(1..noofValues).each{
					if(!args[i+it].startsWith("-"))
						values << args[i+it]
				}
				def param = args[i];
				//check values supplied in arguments match with expected number of values??
				if(values.size() == noofValues){
					//call special parameter handling function
					processSpecialParameter(param, values, executionContext)
					i += noofValues;
				}else {
					// treat this as an error and continue without this parameter
					error("Illegal parameter $param, only allowed with $noofValues values")
					i += values.size();
				}
			}
		}
	}

	private void processArgumentsFile(String value, ExecContext executionContext){

		URI argsFile = fileOrUri(value);

		if (argsFile == null){
			warn("file path supplied in -argsFile is not valid file url: $value");
			return;
		}

		File file = new File(argsFile);
		if (!file.exists()) {
			warn('file path supplied in -argsFile does not exist.');
			return;
		}

		// read file line by line.
		// here consideration: each line is considered as argument
		List<String> args = []
		file.eachLine { line -> args << line }
		//recall processCommandLineArguments function with args file parameter.
		processCommandLineArguments(args as String[] ,executionContext )

	}


	private int getNumberOfValues(String param){
		switch (param) {
			case '-filter-on':
			case '-filterOn':
				return 2
			default:
				return 1
		}
	}

	private void processSpecialParameter(String param, List<String> values,
			ExecContext executionContext) throws Exception {
		switch (param) {
			case '-filter-on':
			case '-filterOn':
				if(values.size()==2){
					executionContext.filters.addTypeFilter(values[0], values[1])
				}
				else {
					warn('Illegal parameter -filter-on, only allowed with type and filter expression')
				}
				break
			default:
				break
		}
	}

	@Override
	protected void processParameter(String param, String value,
			ExecContext executionContext) throws Exception {
		switch (param) {
			case '-project':
			// project file or URI to project
				executionContext.project = fileOrUri(value)
				lastConfigurable = null
				break
			case '-source':
			// source file or URI to source data
				executionContext.sources << fileOrUri(value)
				executionContext.sourceProviderIds << null
				executionContext.sourceIncludes << []
				executionContext.sourceExcludes << []
				executionContext.sourcesSettings << [:]
				lastConfigurable = Configurable.source
				break
			case '-validate':
				executionContext.validateProviderIds.add(value)
				lastConfigurable = Configurable.validate
				break
			case '-out':
			case '-target':
			// target file or URI
				executionContext.target = fileOrUri(value)
				lastConfigurable = Configurable.target
				break
			case '-reportsOut':
			case '-reportsFile':
			// reports file
				executionContext.reportsOut = new File(value)
				lastConfigurable = null
				break

			case '-preset':
			// the target preset
				executionContext.preset = value
				break

			case '-providerId':
			// specify/override provider ID
				if (lastConfigurable) {
					switch (lastConfigurable) {
						case Configurable.source:
							executionContext.sourceProviderIds[executionContext.sources.size() - 1] = value
							break
						case Configurable.target:
							executionContext.targetProviderId = value
							break
					}
				}
				else {
					warn('Unexpected parameter -providerId')
				}
				break

			case '-filter':
				executionContext.filters.addUnconditionalFilter(value);
				break

			case '-exclude':
				executionContext.filters.addExcludeFilter(value);
				break

			case '-filter-on':
			case '-filterOn':
				warn('Illegal parameter -filter-on, only allowed with type and filter expression')
				break
			case '-exclude-type':
			case '-excludeType':
				executionContext.filters.addExcludedType(value)
				break
			case '-exclude': // fall through
			case '-include':
				if (lastConfigurable == Configurable.source) {
					List<List<String>> cludes = (param == '-include') ? (executionContext.sourceIncludes) : (executionContext.sourceExcludes)
					int sourceIndex = executionContext.sources.size() - 1
					List<String> cludeList = cludes[sourceIndex]
					if (!cludeList) {
						cludeList = []
						cludes[sourceIndex] = cludeList
					}
					cludeList << value
				}
				else {
					warn("Unexpected parameter $param, only allowed for configuring a source")
				}

			default:
				if (param.startsWith(SETTING_PREFIX) && param.length() > SETTING_PREFIX.length()) {
					// setting
					String key = param[SETTING_PREFIX.length()..-1]
					storeSetting(key, value, false)
				}
				if(param.startsWith(XML_SETTING_PREFIX) && param.length() > XML_SETTING_PREFIX.length()){
					// XML setting
					String key = param[XML_SETTING_PREFIX.length()..-1]
					storeSetting(key, value, true)
				}
				break
		}
	}

	/**
	 * Create an URI from a String that is a file or URI.
	 * 
	 * @param value the string value
	 * @return the URI
	 */
	protected URI fileOrUri(String value) {
		try {
			URI uri = URI.create(value)
			if (uri.scheme && uri.scheme.length() > 1) {
				// only accept as URI if a schema is present
				// and the scheme is more than just one character
				// which is likely a Windows drive letter
				return uri
			}
			else {
				return new File(value).toURI()
			}
		} catch (e) {
			return new File(value).toURI()
		}
	}

	/**
	 * Store a setting for a {@link Configurable}
	 * 
	 * @param key the setting key/name
	 * @param value the setting's value
	 */
	protected void storeSetting(String key, String value, boolean xml) {
		if (lastConfigurable) {
			// static Groovy can't deal with generics properly...
			ExecContext ec = (ExecContext) this.executionContext

			Value val
			if (xml) {
				val = new ElementValue(elementFromPath(value), null)
			}
			else {
				val = Value.of(value)
			}

			switch (lastConfigurable) {
				case Configurable.source:
					ec.sourcesSettings[ec.sources.size() - 1][key] = val
					break
				case Configurable.target:
					ec.targetSettings[key] = val
					break
				case Configurable.validate:
					ec.validateSettings[key] = val
					break
			}
		}
		else {
			fail('Setting must be specified in context of either source or target')
		}
	}

	/**
	 * Create Element Object from String value
	 * 
	 * @param value The file/path to the XML file as String
	 * @return The (XML) Element
	 */
	protected Element elementFromPath(String value){
		// parse value to URI
		URI uri = fileOrUri(value)

		// build the document out of the String value
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
		dbf.setNamespaceAware(true)
		Document doc

		try{
			InputStream input = new DefaultInputSupplier(uri).getInput()
			input.withStream { InputStream it ->
				doc = dbf.newDocumentBuilder().parse(it)
			}
			return doc.getDocumentElement()
		}
		catch(IOException e){
			fail('An error occurred reading the XML file.')
		}
		catch(SAXException e){
			fail('XML file must be a valid XML.'+e)
		}
		return null
	}

	@Override
	protected void processFlag(String arg, ExecContext executionContext) {
		switch (arg) {
			case '-stacktrace':
				executionContext.logException = true
				break
			case '-trustGroovy':
				executionContext.restrictGroovy = false
				break
			case '-overallFilterContext':
				executionContext.filters.globalContext = true
				break
		}
	}

	@Override
	protected ExecContext createExecutionContext() {
		new ExecContext()
	}
}
