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

package eu.esdihumboldt.hale.io.validation.ui;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.io.validation.ValidatorConfiguration;
import eu.esdihumboldt.hale.io.validation.service.ValidatorConfigurationService;
import eu.esdihumboldt.hale.ui.io.action.AbstractActionUIAdvisor;

/**
 * Validator configuration import action UI advisor.
 * 
 * @author Florian Esser
 */
public class ValidatorConfigurationImportUIAdvisor
		extends AbstractActionUIAdvisor<ValidatorConfiguration> {

	@Override
	public Class<ValidatorConfiguration> getRepresentationType() {
		return ValidatorConfiguration.class;
	}

	@Override
	public boolean supportsRemoval() {
		return true;
	}

	@Override
	public boolean removeResource(String resourceId) {
		ValidatorConfigurationService service = PlatformUI.getWorkbench()
				.getService(ValidatorConfigurationService.class);
		return service.removeConfiguration(resourceId);
	}

	@Override
	public boolean supportsRetrieval() {
		return true;
	}

	@Override
	public ValidatorConfiguration retrieveResource(String resourceId) {
		ValidatorConfigurationService service = PlatformUI.getWorkbench()
				.getService(ValidatorConfigurationService.class);
		return service.getConfiguration(resourceId);
	}

}
