/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.convert;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.convert.extension.ConverterExtension;
import eu.esdihumboldt.hale.common.convert.extension.ConverterInfo;
import eu.esdihumboldt.hale.common.core.service.ServiceFactory;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Conversion service factory.
 * 
 * @author Simon Templer
 */
public class ConversionServiceFactory implements ServiceFactory {

	private static final ALogger log = ALoggerFactory.getLogger(ConversionServiceFactory.class);

	/**
	 * This service is a singleton, the same instance must be retrieved as
	 * {@link ConversionService} and {@link ConverterRegistry}.
	 */
	private final CachingConversionService serviceInstance;

	/**
	 * Default constructor.
	 */
	public ConversionServiceFactory() {
		serviceInstance = new CachingConversionService();

		// register converters from extension
		for (ConverterInfo factory : ConverterExtension.getInstance().getElements()) {
			try {
				serviceInstance.addConverter(factory.createConverter());
			} catch (Exception e) {
				log.error("Could not add converter to conversion service", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T createService(Class<T> serviceInterface, ServiceProvider serviceLocator) {
		if (serviceInterface.equals(ConversionService.class)) {
			return (T) serviceInstance;
		}

		if (serviceInterface.equals(ConverterRegistry.class)) {
			return (T) serviceInstance;
		}

		return null;
	}

}
