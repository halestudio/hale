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

import eu.esdihumboldt.hale.common.core.io.ImportProvider;

/**
 * Interface for validation rules readers
 * 
 * @author Florian Esser
 */
public interface ValidationRuleReader extends ImportProvider {

	/**
	 * The action identifier.
	 */
	public static final String ACTION_ID = "eu.esdihumboldt.hale.io.validation.read";

	/**
	 * @return the imported rule
	 */
	public ValidationRule getRule();
}
