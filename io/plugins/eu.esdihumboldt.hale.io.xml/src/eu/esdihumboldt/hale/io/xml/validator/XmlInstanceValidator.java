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

package eu.esdihumboldt.hale.io.xml.validator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceValidator;

/**
 * Validates XML
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XmlInstanceValidator extends AbstractInstanceValidator {

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Validating XML", ProgressIndicator.UNKNOWN);
		List<URI> schemaLocations = new ArrayList<URI>();
		for (Locatable schema : getSchemas()) {
			URI loc = schema.getLocation();
			if (loc != null) {
				schemaLocations.add(loc);
			}
			else {
				reporter.warn(new IOMessageImpl(
						"No location for schema, may cause validation to fail.", null));
			}
		}
		Validator val = ValidatorFactory.getInstance().createValidator(
				schemaLocations.toArray(new URI[schemaLocations.size()]));
		InputStream in = getSource().getInput();
		try {
			Report report = val.validate(in);
			// TODO use the report information/replace old report definition
			reporter.setSuccess(report.isValid());
			return reporter;
		} finally {
			in.close();
			progress.end();
		}
	}

	/**
	 * @see IOProvider#createReporter()
	 */
	@Override
	public IOReporter createReporter() {
		return new DefaultIOReporter(getSource(), "XML validation", false) {

			@Override
			protected String getFailSummary() {
				return "Validating the XML file failed";
			}

			@Override
			protected String getSuccessSummary() {
				return "The XML file is valid";
			}

		};
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XML file";
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

}
