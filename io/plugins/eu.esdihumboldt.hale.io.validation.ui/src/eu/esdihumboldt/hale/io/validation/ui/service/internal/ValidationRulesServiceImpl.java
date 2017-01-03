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

package eu.esdihumboldt.hale.io.validation.ui.service.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import eu.esdihumboldt.hale.io.validation.ValidationRule;
import eu.esdihumboldt.hale.io.validation.ui.service.ValidationRulesService;

/**
 * Validation rules service
 * 
 * @author Florian Esser
 */
public class ValidationRulesServiceImpl implements ValidationRulesService {

	/**
	 * Maps resource identifiers to validation rules
	 */
	private final Map<String, ValidationRule> rules = new HashMap<>();

	/**
	 * @see eu.esdihumboldt.hale.io.schematron.ui.service.ValidationRulesService#getRules()
	 */
	@Override
	public List<ValidationRule> getRules() {
		return Lists.newArrayList(rules.values());
	}

	/**
	 * @see eu.esdihumboldt.hale.io.schematron.ui.service.ValidationRulesService#addRule(java.lang.String,
	 *      eu.esdihumboldt.hale.io.schematron.ValidationRule)
	 */
	@Override
	public void addRule(String resourceId, ValidationRule schema) {
		rules.put(resourceId, schema);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.schematron.ui.service.ValidationRulesService#removeRule(java.lang.String)
	 */
	@Override
	public boolean removeRule(String resourceId) {
		if (rules.containsKey(resourceId)) {
			rules.remove(resourceId);
			return true;
		}

		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.schematron.ui.service.ValidationRulesService#getRule(java.lang.String)
	 */
	@Override
	public ValidationRule getRule(String resourceId) {
		return rules.get(resourceId);
	}

}
