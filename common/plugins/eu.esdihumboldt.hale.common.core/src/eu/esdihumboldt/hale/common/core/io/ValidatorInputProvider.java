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

package eu.esdihumboldt.hale.common.core.io;

import java.util.List;

import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;

/**
 * Interface to classes that provide input for validators
 * 
 * @author Florian Esser
 */
public interface ValidatorInputProvider {

	/**
	 * @return List of {@link Locatable}s that point to rules/schemas used as
	 *         validator input
	 */
	List<? extends Locatable> getValidatorInput();
}
