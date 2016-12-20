/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.xml.validator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXParseException;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
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
		Validator val = ValidatorFactory.getInstance()
				.createValidator(schemaLocations.toArray(new URI[schemaLocations.size()]));
		InputStream in = getSource().getInput();
		try {
			Report report = val.validate(in);

			// use the report information to populate reporter
			for (SAXParseException warning : report.getWarnings()) {
				reporter.warn(new IOMessageImpl(//
						warning.getLocalizedMessage(), //
						warning, //
						warning.getLineNumber(), //
						warning.getColumnNumber()));
			}
			for (SAXParseException error : report.getErrors()) {
				reporter.error(new IOMessageImpl(//
						error.getLocalizedMessage(), //
						error, //
						error.getLineNumber(), //
						error.getColumnNumber()));
			}

			reporter.setSuccess(report.isValid());
			return reporter;
		} finally {
			in.close();
			progress.end();
		}
	}

	@Override
	protected String getReportLabel() {
		return "XML validation";
	}

	@Override
	protected String getDefaultFailSummary() {
		return "Validating the XML file failed";
	}

	@Override
	protected String getDefaultSuccessSummary() {
		return "The XML file is valid";
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
