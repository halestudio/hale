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

package eu.esdihumboldt.hale.common.instance.extension.validation;

import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReporter;

/**
 * Marker interface for constraint validators.
 * 
 * @author Kai Schwierczek
 */
public interface ConstraintValidator {

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
