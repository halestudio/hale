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

package eu.esdihumboldt.hale.common.instance.extension.validation;

import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReporter;

/**
 * Base interface for validators.
 * 
 * @author Simon Templer
 */
public interface ValidatorBase {

	/**
	 * Check the validation context after all instances have been validated.
	 * 
	 * @param context the validation context
	 * @param reporter the instance validation reporter
	 * @throws ValidationException if the instances are invalid and the reporter
	 *             is not used to report the problems
	 */
	@SuppressWarnings("unused")
	default void validateContext(InstanceValidationContext context,
			InstanceValidationReporter reporter) throws ValidationException {
		// override me
	}

}
