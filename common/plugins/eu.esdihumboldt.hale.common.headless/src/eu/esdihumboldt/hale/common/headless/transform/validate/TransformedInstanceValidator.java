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

package eu.esdihumboldt.hale.common.headless.transform.validate;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Validator that performs validation on transformed {@link Instance} objects. A
 * validator object is only used once, thus it may hold state regarding the
 * validation.
 * 
 * @author Simon Templer
 */
public interface TransformedInstanceValidator {

	/**
	 * Validate an instance.
	 * 
	 * @param instance the instance to validate
	 */
	void validateInstance(Instance instance);

	/**
	 * Perform validation of state collected during instance validation.
	 */
	void validateCompleted();

}
