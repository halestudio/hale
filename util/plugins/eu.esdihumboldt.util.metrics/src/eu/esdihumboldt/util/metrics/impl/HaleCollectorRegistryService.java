/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.util.metrics.impl;

import java.util.function.Supplier;

import eu.esdihumboldt.util.metrics.CollectorRegistryService;
import eu.esdihumboldt.util.metrics.MetricsProvider;
import io.prometheus.client.CollectorRegistry;

/**
 * Default implementation of {@link CollectorRegistryService}.
 * 
 * @author Simon Templer
 */
public class HaleCollectorRegistryService implements CollectorRegistryService {

	private final CollectorRegistry registry;

	/**
	 * Create a service instance using the given registry.
	 * 
	 * @param registry the collector registry
	 */
	public HaleCollectorRegistryService(CollectorRegistry registry) {
		super();
		this.registry = registry;
	}

	/**
	 * @return if metric collection is enabled
	 */
	public boolean isMetricCollectionEnabled() {
		String value = System.getenv("HALE_METRICS_ENABLED");
		if (value == null) {
			// ENV variable used in hale connect services
			value = System.getenv("FEATURE_METRICS");
		}
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	@Override
	public void register(Supplier<MetricsProvider> metricsProvider) {
		if (registry != null && isMetricCollectionEnabled()) {
			metricsProvider.get().bindTo(registry);
		}
	}

}
