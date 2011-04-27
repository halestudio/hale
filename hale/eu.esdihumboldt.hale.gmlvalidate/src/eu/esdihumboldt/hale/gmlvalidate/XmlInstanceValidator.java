/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.gmlvalidate;

import java.io.IOException;
import java.io.InputStream;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.instance.io.impl.AbstractInstanceValidator;

/**
 * Validates XML
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XmlInstanceValidator extends AbstractInstanceValidator {

	/**
	 * @see IOProvider#execute(ProgressIndicator)
	 */
	@Override
	public IOReport execute(ProgressIndicator progress)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Validating XML", true);
		DefaultIOReporter result = new DefaultIOReporter(getSource(), false) {

			@Override
			protected String getFailSummary() {
				return "Validating the XML file failed";
			}

			@Override
			protected String getSuccessSummary() {
				return "The XML file is valid";
			}
			
		};
		Validator val = ValidatorFactory.getInstance().createValidator(getSchemas());
		InputStream in = getSource().getInput();
		try {
			Report report = val.validate(in);
			//TODO use the report information/replace old report definition
			result.setSuccess(report.isValid());
			return result;
		} finally {
			in.close();
			progress.end();
		}
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

}
