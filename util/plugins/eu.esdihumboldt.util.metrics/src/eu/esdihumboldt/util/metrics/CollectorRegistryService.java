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

package eu.esdihumboldt.util.metrics;

import java.util.function.Supplier;

import eu.esdihumboldt.util.metrics.impl.HaleCollectorRegistryService;
import io.prometheus.client.CollectorRegistry;

/**
 * Service interface for providing access to the platform
 * {@link CollectorRegistry}.
 * 
 * @author Simon Templer
 */
public interface CollectorRegistryService {

	/**
	 * Default registry service
	 * 
	 * Note: Originally wanted to provide this via hale service provider
	 * mechanism, but this lead to not easily resolvable bundle dependency
	 * cycles.
	 */
	CollectorRegistryService DEFAULT = new HaleCollectorRegistryService(
			CollectorRegistry.defaultRegistry);

	/**
	 * Register a metric provider.
	 * 
	 * @param metricsProvider supplier for metrics provider that is called only
	 *            if metric collection is enabled
	 */
	void register(Supplier<MetricsProvider> metricsProvider);

}
