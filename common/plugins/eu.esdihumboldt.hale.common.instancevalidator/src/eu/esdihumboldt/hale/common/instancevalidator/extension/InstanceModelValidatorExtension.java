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

package eu.esdihumboldt.hale.common.instancevalidator.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceModelValidator;

/**
 * Extension for instance model based validators.
 * 
 * @author Simon Templer
 */
public class InstanceModelValidatorExtension
		extends AbstractExtension<InstanceModelValidator, InstanceModelValidatorFactory> {

	/**
	 * The extension point ID.
	 */
	public static final String ID = "eu.esdihumboldt.hale.instancevalidator";

	private static InstanceModelValidatorExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the instance model validator extension
	 */
	public static InstanceModelValidatorExtension getInstance() {
		if (instance == null) {
			instance = new InstanceModelValidatorExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	private InstanceModelValidatorExtension() {
		super(ID);
	}

	@Override
	protected InstanceModelValidatorFactory createFactory(IConfigurationElement conf)
			throws Exception {
		return new InstanceModelValidatorFactory(conf);
	}

}
