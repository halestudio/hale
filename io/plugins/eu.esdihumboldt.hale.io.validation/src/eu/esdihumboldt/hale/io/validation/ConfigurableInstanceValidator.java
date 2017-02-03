/*
 * Copyright (c) 2017 wetransform GmbH
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

import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;

/**
 * Interface for instance validators that support configuration via a
 * {@link ValidatorConfiguration} object.
 * 
 * @author Florian Esser
 */
public interface ConfigurableInstanceValidator extends InstanceValidator {

	/**
	 * Configures the validator via the given {@link ValidatorConfiguration}.
	 * 
	 * @param configuration configuration object to apply
	 */
	void configure(ValidatorConfiguration configuration);
}
