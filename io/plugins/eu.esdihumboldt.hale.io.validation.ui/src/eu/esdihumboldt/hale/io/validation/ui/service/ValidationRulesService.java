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

package eu.esdihumboldt.hale.io.validation.ui.service;

import java.util.List;

import eu.esdihumboldt.hale.io.validation.ValidationRule;

/**
 * Service for managing validation rules
 * 
 * @author Florian Esser
 */
public interface ValidationRulesService {

	/**
	 * @return the validation rules
	 */
	List<ValidationRule> getRules();

	/**
	 * Adds a validation rule
	 * 
	 * @param resourceId the resource identifier of the schema
	 * @param schema schema to add
	 */
	void addRule(String resourceId, ValidationRule schema);

	/**
	 * @param resourceId
	 * @return
	 */
	boolean removeRule(String resourceId);

	/**
	 * @param resourceId
	 * @return
	 */
	ValidationRule getRule(String resourceId);
}
