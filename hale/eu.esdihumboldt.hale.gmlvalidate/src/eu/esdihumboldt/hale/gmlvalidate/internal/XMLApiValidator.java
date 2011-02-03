/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlvalidate.internal;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import eu.esdihumboldt.hale.gmlvalidate.Report;
import eu.esdihumboldt.hale.gmlvalidate.Validator;
import eu.esdihumboldt.hale.schemaprovider.Schema;

/**
 * Validate using the XML API.
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class XMLApiValidator implements Validator {

	private final Schema schema;
	
	/**
	 * Constructor
	 * 
	 * @param schema the schema
	 */
	public XMLApiValidator(Schema schema) {
		super();
		this.schema = schema;
	}

	/**
	 * @see Validator#validate(java.io.InputStream)
	 */
	@Override
	public Report validate(InputStream xml) {
		javax.xml.validation.Schema schema;
		try {
			// create a SchemaFactory capable of understanding WXS schemas
		    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		    factory.setResourceResolver(new SchemaResolver(this.schema.getLocation().toURI()));		    
	
		    // load a WXS schema, represented by a Schema instance
		    Source schemaFile = new StreamSource(this.schema.getLocation().openStream());
		    schema = factory.newSchema(schemaFile);
		} catch (Exception e) {
			throw new IllegalStateException("Error parsing schema for XML validation", e);
		}

	    // create a Validator instance, which can be used to validate an instance document
	    javax.xml.validation.Validator validator = schema.newValidator();
	    ReportImpl report = new ReportImpl();
		validator.setErrorHandler(new ReportErrorHandler(report));
	    
	    // validate the XML document
		try {
			validator.validate(new StreamSource(xml));
			return report;
		} catch (Exception e) {
			throw new IllegalStateException("Error validating XML file", e);
		}
	}

}
