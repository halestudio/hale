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
