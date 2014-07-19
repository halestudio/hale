

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
		target
	}

	private Configurable lastConfigurable

	@Override
	protected Object run(ExecContext executionContext, IApplicationContext appContext) {
		// set system err to system out, otherwise system err messages seem to get lost
		System.setErr(System.out);

		if (validate(executionContext)) {
			try {
				new ExecTransformation().run(executionContext)
			} catch (Exception | AssertionError e) {
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

	protected boolean validate(ExecContext ec) {
		// project is required
		if (!ec.project) {
			error('You need to provide a reference to a HALE project that defines the mapping for the transformation')
			return false
		}

		// source is required
		if (!ec.source) {
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

	protected int usage() {
		println """
Usage:
HALE -nosplash -application hale.transform
     -project <file-or-URI-to-HALE-project>
     -source <file-or-URI-to-source-data>
         [-providerId <ID-of-source-reader>]
         [<setting>...]
     -target <target-file>
         [-preset <name-of-export-preset>]
         [-providerId <ID-of-target-writer>]
         [<setting>...]
     [options...]

  where setting is
     ${SETTING_PREFIX}<setting-name> <value>

  and options are
     -reportsOut <reports-file>
     -stacktrace
		""".trim()

		// general error code
		new Integer(1)
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
				executionContext.source = fileOrUri(value)
				lastConfigurable = Configurable.source
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
							executionContext.sourceProviderId = value
							break
						case Configurable.target:
							executionContext.targetProviderId = value
							break
					}
				}
				else {
					//TODO message?
				}
				break
			default:
				if (param.startsWith(SETTING_PREFIX) && param.length() > SETTING_PREFIX.length()) {
					// setting
					String key = param[SETTING_PREFIX.length()..-1]
					storeSetting(key, value)
				}
				if(param.startsWith(XML_SETTING_PREFIX) && param.length() > XML_SETTING_PREFIX.length()){
					// XML setting
					String key = param[XML_SETTING_PREFIX.length()..-1]
					storeXMLSetting(key, value)
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
			if (uri.scheme) {
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
	protected void storeSetting(String key, String value) {
		if (lastConfigurable) {
			// static Groovy can't deal with generics properly...
			ExecContext ec = (ExecContext) this.executionContext
			switch (lastConfigurable) {
				case Configurable.source:
					ec.sourceSettings[key] = valueFromString(value)
					break
				case Configurable.target:
					ec.targetSettings[key] = valueFromString(value)
					break
			}
		}
		else {
			fail('Setting must be specified in context of either source or target')
		}
	}

	/**
	 * Creates a {@link Value} object from a string given as parameter.
	 * @param value the string value
	 * @return the Value representation
	 */
	protected Value valueFromString(String value) {
		//TODO support detection of complex values
		Value.of(value)
	}

	/**
	 * Store a Setting of a {@link Configurable} if it is an XML 
	 * file Parameter
	 * 
	 * @param key the XML setting key/name
	 * @param value XML path/file
	 */
	protected void storeXMLSetting(String key, String value){
		if (lastConfigurable) {
			// static Groovy can't deal with generics properly...
			ExecContext ec = (ExecContext) this.executionContext
			// try to parse XML file to ElementValue
			ElementValue ev = new ElementValue(elementValueFromString(value), null)

			if (ev)
				switch (lastConfigurable) {
					case Configurable.source:
						ec.sourceSettings[key] = ev
						break
					case Configurable.target:
						ec.targetSettings[key] = ev
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
	protected Element elementValueFromString(String value){
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
		}
	}

	@Override
	protected ExecContext createExecutionContext() {
		new ExecContext()
	}
}
