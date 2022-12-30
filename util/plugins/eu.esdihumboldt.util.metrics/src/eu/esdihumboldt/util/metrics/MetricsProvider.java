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

import io.prometheus.client.CollectorRegistry;

/**
 * Common interface for metric providers.
 * 
 * @author Simon Templer
 */
@FunctionalInterface
public interface MetricsProvider {

	/**
	 * Register with the give collector registry.
	 * 
	 * @param registry the collector registry
	 */
	void bindTo(CollectorRegistry registry);

}
