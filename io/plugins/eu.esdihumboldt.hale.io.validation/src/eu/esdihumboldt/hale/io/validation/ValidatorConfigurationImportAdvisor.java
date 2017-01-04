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

import java.text.MessageFormat;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.io.validation.service.ValidatorConfigurationService;

/**
 * Import advisor for validator configurations
 * 
 * @author Florian Esser
 */
public class ValidatorConfigurationImportAdvisor
		extends DefaultIOAdvisor<ValidatorConfigurationReader> {

	private static final ALogger log = ALoggerFactory
			.getLogger(ValidatorConfigurationImportAdvisor.class);

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(ValidatorConfigurationReader provider) {
		ValidatorConfiguration configuration = provider.getConfiguration();

		ValidatorConfigurationService service = getService(ValidatorConfigurationService.class);
		if (service != null) {
			service.addConfiguration(provider.getResourceIdentifier(), configuration);
		}
		else {
			log.warn(MessageFormat.format(
					"Implementation for service interface {0} could not be found!",
					ValidatorConfigurationService.class.getCanonicalName()));
		}

		super.handleResults(provider);
	}

}
