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

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceModelValidator;

/**
 * Factory for instance model based validators.
 * 
 * @author Simon Templer
 */
public class InstanceModelValidatorFactory
		extends AbstractConfigurationFactory<InstanceModelValidator> {

	/**
	 * Create an {@link InstanceModelValidator} factory based on the given
	 * configuration element.
	 * 
	 * @param conf the configuration element
	 */
	public InstanceModelValidatorFactory(IConfigurationElement conf) {
		super(conf, "class");
	}

	@Override
	public void dispose(InstanceModelValidator validator) {
		// do nothing
	}

	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}
}
