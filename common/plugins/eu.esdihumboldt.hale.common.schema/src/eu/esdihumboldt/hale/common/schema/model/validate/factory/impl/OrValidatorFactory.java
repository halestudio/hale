/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.model.validate.factory.impl;

import java.util.List;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.util.validator.CombinedValidator;
import eu.esdihumboldt.util.validator.OrValidator;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Converts {@link OrValidator}s to {@link Value}s and vice versa.
 * 
 * @author Simon Templer
 */
public class OrValidatorFactory extends AbstractCombinedValidatorFactory<OrValidator> {

	@Override
	protected CombinedValidator createValidator(List<Validator> children) {
		return new OrValidator(children);
	}

}
