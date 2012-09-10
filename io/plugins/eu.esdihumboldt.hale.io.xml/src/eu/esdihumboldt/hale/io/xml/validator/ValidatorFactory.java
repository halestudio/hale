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

package eu.esdihumboldt.hale.io.xml.validator;

import java.net.URI;

import eu.esdihumboldt.hale.io.xml.validator.internal.XMLApiValidator;
import eu.esdihumboldt.hale.io.xml.validator.internal.XercesValidator;

/**
 * Factory for {@link Validator}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ValidatorFactory {

	private static ValidatorFactory instance;

	/**
	 * Get the factory instance
	 * 
	 * @return the factory instance
	 */
	public static ValidatorFactory getInstance() {
		if (instance == null) {
			instance = new ValidatorFactory();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private ValidatorFactory() {
		super();
	}

	/**
	 * Create a validator that relies on the schema locations specified in the
	 * file.
	 * 
	 * @return the validator
	 */
	public Validator createValidator() {
		return new XercesValidator();
	}

	/**
	 * Create a validator that relies on the given schema for validation.
	 * 
	 * @param schemaLocations the schemas
	 * @return the validator
	 */
	public Validator createValidator(URI... schemaLocations) {
		return new XMLApiValidator(schemaLocations);
	}

}
