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

package eu.esdihumboldt.util.http.client.metrics;

import java.util.List;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;

import com.google.common.collect.ImmutableList;

import eu.esdihumboldt.util.metrics.CollectorRegistryService;
import eu.esdihumboldt.util.metrics.MetricsProvider;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.GaugeMetricFamily;

/**
 * Metrics collector for {@link PoolingHttpClientConnectionManager}.
 * 
 * Inspired by https://github.com/micrometer-metrics/micrometer/pull/1223/files
 * and
 * https://github.com/jborgers/http-client-monitor/blob/main/http-client-monitor-lib/src/main/java/com/jpinpoint/monitor/PoolingHttpClientConnectionManagerStats.java
 * 
 * @author Simon Templer
 */
public class PoolingHttpClientConnectionManagerMetrics extends Collector
		implements MetricsProvider {

	/*
	 * Note: Request execution times could be tracked with a custom
	 * HttpRequestExecutor
	 * 
	 * Example: https://github.com/micrometer-metrics/micrometer/pull/1286/files
	 */

	/**
	 * Install a metric collector for a
	 * {@link PoolingHttpClientConnectionManager}.
	 * 
	 * @param connectionManager the connection manager
	 * @param clientName the client name the metrics should be labeled with
	 * @param service the service for registering with the
	 *            {@link CollectorRegistry}
	 */
	public static void install(PoolingHttpClientConnectionManager connectionManager,
			String clientName, CollectorRegistryService service) {
		if (service != null) {
			service.register(() -> new PoolingHttpClientConnectionManagerMetrics(connectionManager,
					clientName));
		}
	}

	private static final String METER_TOTAL_MAX_DESC = "The configured maximum number of allowed persistent connections for all routes.";
	private static final String METER_TOTAL_MAX = "httpcomponents.httpclient.pool.total.max";
	private static final String METER_TOTAL_CONNECTIONS_DESC = "The number of persistent and leased connections for all routes.";
	private static final String METER_TOTAL_CONNECTIONS = "httpcomponents.httpclient.pool.total.connections";
	private static final String METER_TOTAL_PENDING_DESC = "The number of connection requests being blocked awaiting a free connection for all routes.";
	private static final String METER_TOTAL_PENDING = "httpcomponents.httpclient.pool.total.pending";
	private static final String METER_DEFAULT_MAX_PER_ROUTE_DESC = "The configured default maximum number of allowed persistent connections per route.";
	private static final String METER_DEFAULT_MAX_PER_ROUTE = "httpcomponents.httpclient.pool.route.max.default";

	private static final String TAG_CLIENT_NAME = "httpclient";
	private static final String TAG_CONNECTIONS_STATE = "state";

	private static final ImmutableList<String> LABEL_CLIENT_NAME_AND_STATE = ImmutableList
			.of(TAG_CLIENT_NAME, TAG_CONNECTIONS_STATE);
	private static final ImmutableList<String> LABEL_CLIENT_NAME_ONLY = ImmutableList
			.of(TAG_CLIENT_NAME);

	private final PoolingHttpClientConnectionManager connectionManager;

	private final List<String> clientNameOnly;
	private final List<String> clientNameAndAvailable;
	private final List<String> clientNameAndLeased;

	/**
	 * Create a metric collector for a
	 * {@link PoolingHttpClientConnectionManager}.
	 * 
	 * @param connectionManager the connection manager
	 * @param clientName the client name the metrics should be labeled with
	 */
	public PoolingHttpClientConnectionManagerMetrics(
			PoolingHttpClientConnectionManager connectionManager, String clientName) {
		super();
		this.connectionManager = connectionManager;

		this.clientNameOnly = ImmutableList.of(clientName);
		this.clientNameAndAvailable = ImmutableList.of(clientName, "available");
		this.clientNameAndLeased = ImmutableList.of(clientName, "leased");
	}

	@Override
	public List<MetricFamilySamples> collect() {
		PoolStats stats = connectionManager.getTotalStats();

		GaugeMetricFamily pending = new GaugeMetricFamily(METER_TOTAL_PENDING,
				METER_TOTAL_PENDING_DESC, LABEL_CLIENT_NAME_ONLY);
		pending.addMetric(clientNameOnly, stats.getPending());

		GaugeMetricFamily max = new GaugeMetricFamily(METER_TOTAL_MAX, METER_TOTAL_MAX_DESC,
				LABEL_CLIENT_NAME_ONLY);
		max.addMetric(clientNameOnly, stats.getMax());

		GaugeMetricFamily available = new GaugeMetricFamily(METER_TOTAL_CONNECTIONS,
				METER_TOTAL_CONNECTIONS_DESC, LABEL_CLIENT_NAME_AND_STATE);
		available.addMetric(clientNameAndAvailable, stats.getAvailable());

		GaugeMetricFamily leased = new GaugeMetricFamily(METER_TOTAL_CONNECTIONS,
				METER_TOTAL_CONNECTIONS_DESC, LABEL_CLIENT_NAME_AND_STATE);
		leased.addMetric(clientNameAndLeased, stats.getLeased());

		GaugeMetricFamily maxRoute = new GaugeMetricFamily(METER_DEFAULT_MAX_PER_ROUTE,
				METER_DEFAULT_MAX_PER_ROUTE_DESC, LABEL_CLIENT_NAME_ONLY);
		maxRoute.addMetric(clientNameOnly, connectionManager.getDefaultMaxPerRoute());

		return ImmutableList.of(pending, max, available, leased, maxRoute);
	}

	@Override
	public void bindTo(CollectorRegistry registry) {
		register(registry);
	}

}
