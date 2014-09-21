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

package eu.esdihumboldt.hale.common.align.extension.function.validator;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * Validator that checks whether the value is one of the given values.<br>
 * 
 * Expects parameters "value".
 * 
 * @author Kai Schwierczek
 */
public class EnumerationValidator implements Validator {

	private List<String> values;

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.Validator#validate(java.lang.String)
	 */
	@Override
	public String validate(String value) {
		if (values.contains(value))
			return null;
		else
			return "Input must be one of " + Joiner.on(", ").join(values);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.Validator#setParameters(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, String> parameters) {
		values = parameters.get("value");
	}

}
