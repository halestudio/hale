/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.validation;

import java.net.URI;

/**
 * Representation of a validation rules file
 * 
 * @author Florian Esser
 */
public class ValidationRule {

	private final String rule;

	private final URI location;

	/**
	 * Creates a validation rule
	 * 
	 * @param rule Validation rule
	 * @param location the schema's location
	 */
	public ValidationRule(String rule, URI location) {
		this.rule = rule;
		this.location = location;
	}

	/**
	 * Get the rule
	 * 
	 * @return the validation rule
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * Get the source location of the validation rule
	 * 
	 * @return the location
	 */
	public URI getLocation() {
		return location;
	}
}
