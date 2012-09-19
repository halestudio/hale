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

package eu.esdihumboldt.hale.io.xml.validator.internal;

import java.io.InputStream;
import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.io.xml.validator.Report;
import eu.esdihumboldt.hale.io.xml.validator.Validator;

/**
 * Validate using the XML API.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XMLApiValidator implements Validator {

	private final URI[] schemaLocations;

	/**
	 * Constructor
	 * 
	 * @param schemaLocations the schema locations
	 */
	public XMLApiValidator(URI[] schemaLocations) {
		super();
		this.schemaLocations = schemaLocations;
	}

	/**
	 * @see Validator#validate(InputStream)
	 */
	@Override
	public Report validate(InputStream xml) {
		javax.xml.validation.Schema validateSchema;
		try {
			URI mainUri = null;
			Source[] sources = new Source[schemaLocations.length];
			for (int i = 0; i < this.schemaLocations.length; i++) {
				URI schemaLocation = this.schemaLocations[i];

				if (mainUri == null) { // use first schema location for main URI
					mainUri = schemaLocation;
				}

				// load a WXS schema, represented by a Schema instance
				DefaultInputSupplier dis = new DefaultInputSupplier(schemaLocation);
				sources[i] = new StreamSource(dis.getInput());
			}
			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setResourceResolver(new SchemaResolver(mainUri));
			validateSchema = factory.newSchema(sources);
		} catch (Exception e) {
			throw new IllegalStateException("Error parsing schema for XML validation", e); //$NON-NLS-1$
		}

		// create a Validator instance, which can be used to validate an
		// instance document
		javax.xml.validation.Validator validator = validateSchema.newValidator();
		ReportImpl report = new ReportImpl();
		validator.setErrorHandler(new ReportErrorHandler(report));

		// validate the XML document
		try {
			validator.validate(new StreamSource(xml));
			return report;
		} catch (Exception e) {
			throw new IllegalStateException("Error validating XML file", e); //$NON-NLS-1$
		}
	}

}
