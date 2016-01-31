/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.server.config;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringValueResolver;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.HalePlatform;

/**
 * Placeholder configurer backed by the {@link IConfigurationService} provided
 * as OSGi service.
 * 
 * @author Simon Templer
 */
public class ConfigurationServicePlaceholderConfigurer extends PlaceholderConfigurerSupport {

	private static final ALogger log = ALoggerFactory
			.getLogger(ConfigurationServicePlaceholderConfigurer.class);

	private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

		private final PropertyPlaceholderHelper helper;

		private final PlaceholderResolver resolver;

		public PlaceholderResolvingStringValueResolver() {
			this.helper = new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix,
					valueSeparator, ignoreUnresolvablePlaceholders);
			this.resolver = new PlaceholderResolver() {

				@Override
				public String resolvePlaceholder(String placeholderName) {
					IConfigurationService cs = HalePlatform.getService(IConfigurationService.class);
					if (cs != null) {
						return cs.get(placeholderName);
					}
					else {
						log.warn("Could not access configuration service to provide configuration value "
								+ placeholderName);
						return null;
					}
				}

			};
		}

		@Override
		public String resolveStringValue(String strVal) throws BeansException {
			String value = this.helper.replacePlaceholders(strVal, this.resolver);
			return (value.equals(nullValue) ? null : value);
		}
	}

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver();
		this.doProcessProperties(beanFactory, valueResolver);
	}

}
