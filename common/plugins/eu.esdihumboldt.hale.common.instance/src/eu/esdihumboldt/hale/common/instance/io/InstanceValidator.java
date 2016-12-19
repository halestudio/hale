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

package eu.esdihumboldt.hale.common.instance.io;

import java.io.InputStream;
import java.util.Collection;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ValidatorInputProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Provides support for validating instances
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public interface InstanceValidator extends IOProvider {

	/**
	 * Set the source that is to be validated
	 * 
	 * @param source the source input supplier
	 */
	void setSource(LocatableInputSupplier<? extends InputStream> source);

	/**
	 * Set the schemas/rules to be used for validation.
	 * 
	 * @param validatorInput the validation schema locations
	 */
	void setValidatorInput(Collection<? extends Locatable> validatorInput);

	void configure(ValidatorInputProvider provider);

}
