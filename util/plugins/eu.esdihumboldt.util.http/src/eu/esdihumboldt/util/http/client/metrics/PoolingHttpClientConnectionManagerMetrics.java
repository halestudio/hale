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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;

import com.google.common.collect.ImmutableList;

import eu.esdihumboldt.util.Pair;
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
	 * @param enablePerHostMetrics if metrics that include information on routes
	 *            per individual host should be collected
	 * @param service the service for registering with the
	 *            {@link CollectorRegistry}
	 */
	public static void install(PoolingHttpClientConnectionManager connectionManager,
			String clientName, boolean enablePerHostMetrics, CollectorRegistryService service) {
		if (service != null) {
			service.register(() -> new PoolingHttpClientConnectionManagerMetrics(connectionManager,
					clientName, enablePerHostMetrics));
		}
	}

	private static final String METER_TOTAL_MAX_DESC = "The configured maximum number of allowed persistent connections for all routes.";
	private static final String METER_TOTAL_MAX = "httpcomponents_httpclient_pool_total_max";

	private static final String METER_TOTAL_CONNECTIONS_DESC = "The number of persistent and leased connections for all routes.";
	private static final String METER_TOTAL_CONNECTIONS = "httpcomponents_httpclient_pool_total_connections";

	private static final String METER_TOTAL_PENDING_DESC = "The number of connection requests being blocked awaiting a free connection for all routes.";
	private static final String METER_TOTAL_PENDING = "httpcomponents_httpclient_pool_total_pending";

	private static final String METER_DEFAULT_MAX_PER_ROUTE_DESC = "The configured default maximum number of allowed persistent connections per route.";
	private static final String METER_DEFAULT_MAX_PER_ROUTE = "httpcomponents_httpclient_pool_route_max_default";

	private static final String METER_HOSTS_CONNECTIONS_ACTIVE = "httpcomponents_httpclient_pool_hosts_active";
	private static final String METER_HOSTS_CONNECTIONS_ACTIVE_DESC = "The number of different target hosts for which connections are active.";

	private static final String METER_HOST_MAX_DESC = "The configured maximum number of allowed persistent connections for all routes for a specific target host.";
	private static final String METER_HOST_MAX = "httpcomponents_httpclient_pool_host_max";

	private static final String METER_HOST_CONNECTIONS_DESC = "The number of persistent and leased connections for all routes for a specific target host.";
	private static final String METER_HOST_CONNECTIONS = "httpcomponents_httpclient_pool_host_connections";

	private static final String METER_HOST_PENDING_DESC = "The number of connection requests being blocked awaiting a free connection for all routes for a specific target host.";
	private static final String METER_HOST_PENDING = "httpcomponents_httpclient_pool_host_pending";

	private static final String LABEL_CLIENT_NAME = "httpclient";
	private static final String LABEL_CONNECTIONS_STATE = "state";
	private static final String LABEL_HOST = "host";

	private static final String STATE_LEASED = "leased";
	private static final String STATE_AVAILABLE = "available";

	private static final ImmutableList<String> LABEL_CLIENT_NAME_AND_STATE = ImmutableList
			.of(LABEL_CLIENT_NAME, LABEL_CONNECTIONS_STATE);
	private static final ImmutableList<String> LABEL_CLIENT_NAME_AND_HOST_AND_STATE = ImmutableList
			.of(LABEL_CLIENT_NAME, LABEL_HOST, LABEL_CONNECTIONS_STATE);
	private static final ImmutableList<String> LABEL_CLIENT_NAME_AND_HOST = ImmutableList
			.of(LABEL_CLIENT_NAME, LABEL_HOST);
	private static final ImmutableList<String> LABEL_CLIENT_NAME_ONLY = ImmutableList
			.of(LABEL_CLIENT_NAME);

	private final PoolingHttpClientConnectionManager connectionManager;
	private final String clientName;
	private final boolean enablePerHostMetrics;

	private final List<String> clientNameOnly;
	private final List<String> clientNameAndAvailable;
	private final List<String> clientNameAndLeased;

	/**
	 * Create a metric collector for a
	 * {@link PoolingHttpClientConnectionManager}.
	 * 
	 * @param connectionManager the connection manager
	 * @param clientName the client name the metrics should be labeled with
	 * @param enablePerHostMetrics if metrics that include information on routes
	 *            per individual host should be collected
	 */
	public PoolingHttpClientConnectionManagerMetrics(
			PoolingHttpClientConnectionManager connectionManager, String clientName,
			boolean enablePerHostMetrics) {
		super();
		this.connectionManager = connectionManager;
		this.clientName = clientName;
		this.enablePerHostMetrics = enablePerHostMetrics;

		this.clientNameOnly = ImmutableList.of(clientName);
		this.clientNameAndAvailable = ImmutableList.of(clientName, STATE_AVAILABLE);
		this.clientNameAndLeased = ImmutableList.of(clientName, STATE_LEASED);
	}

	@Override
	public List<MetricFamilySamples> collect() {
		PoolStats stats = connectionManager.getTotalStats();

		List<MetricFamilySamples> result = new ArrayList<>();

		GaugeMetricFamily pending = new GaugeMetricFamily(METER_TOTAL_PENDING,
				METER_TOTAL_PENDING_DESC, LABEL_CLIENT_NAME_ONLY);
		pending.addMetric(clientNameOnly, stats.getPending());
		result.add(pending);

		GaugeMetricFamily max = new GaugeMetricFamily(METER_TOTAL_MAX, METER_TOTAL_MAX_DESC,
				LABEL_CLIENT_NAME_ONLY);
		max.addMetric(clientNameOnly, stats.getMax());
		result.add(max);

		GaugeMetricFamily total = new GaugeMetricFamily(METER_TOTAL_CONNECTIONS,
				METER_TOTAL_CONNECTIONS_DESC, LABEL_CLIENT_NAME_AND_STATE);
		total.addMetric(clientNameAndAvailable, stats.getAvailable());
		total.addMetric(clientNameAndLeased, stats.getLeased());
		result.add(total);

		GaugeMetricFamily maxRouteDefault = new GaugeMetricFamily(METER_DEFAULT_MAX_PER_ROUTE,
				METER_DEFAULT_MAX_PER_ROUTE_DESC, LABEL_CLIENT_NAME_ONLY);
		maxRouteDefault.addMetric(clientNameOnly, connectionManager.getDefaultMaxPerRoute());
		result.add(maxRouteDefault);

		// aggregate pool stats per host name
		java.util.stream.Collector<Pair<HttpRoute, PoolStats>, ?, PoolStats> aggregation = Collectors
				.mapping(e -> e.getSecond(),
						Collectors.reducing(new PoolStats(0, 0, 0, 0), (a, b) -> {
							return new PoolStats(a.getLeased() + b.getLeased(),
									a.getPending() + b.getPending(),
									a.getAvailable() + b.getAvailable(), a.getMax() + b.getMax());
						}));
		Map<String, PoolStats> hostStats = connectionManager.getRoutes().stream()
				.map(route -> new Pair<>(route, connectionManager.getStats(route)))
				.collect(Collectors.groupingBy(e -> e.getFirst().getTargetHost().getHostName(),
						aggregation));

		long activeHostRoutes = hostStats.entrySet().stream()
				.filter(e -> e.getValue().getLeased() > 0).count();

		GaugeMetricFamily activeRoutes = new GaugeMetricFamily(METER_HOSTS_CONNECTIONS_ACTIVE,
				METER_HOSTS_CONNECTIONS_ACTIVE_DESC, LABEL_CLIENT_NAME_ONLY);
		activeRoutes.addMetric(clientNameOnly, activeHostRoutes);

		if (enablePerHostMetrics) {
			// only add per host metrics if specifically enabled (because the
			// route/host label is unbounded)

			GaugeMetricFamily pendingRoute = new GaugeMetricFamily(METER_HOST_PENDING,
					METER_HOST_PENDING_DESC, LABEL_CLIENT_NAME_AND_HOST);
			result.add(pendingRoute);

			GaugeMetricFamily maxRoute = new GaugeMetricFamily(METER_HOST_MAX, METER_HOST_MAX_DESC,
					LABEL_CLIENT_NAME_AND_HOST);
			result.add(maxRoute);

			GaugeMetricFamily connectionsRoute = new GaugeMetricFamily(METER_HOST_CONNECTIONS,
					METER_HOST_CONNECTIONS_DESC, LABEL_CLIENT_NAME_AND_HOST_AND_STATE);
			result.add(connectionsRoute);

			hostStats.forEach((host, routeStats) -> {
				List<String> nameAndHost = ImmutableList.of(clientName, host);
				pendingRoute.addMetric(nameAndHost, routeStats.getPending());
				maxRoute.addMetric(nameAndHost, routeStats.getMax());

				connectionsRoute.addMetric(ImmutableList.of(clientName, host, STATE_AVAILABLE),
						routeStats.getAvailable());
				connectionsRoute.addMetric(ImmutableList.of(clientName, host, STATE_LEASED),
						routeStats.getAvailable());
			});
		}

		return result;
	}

	@Override
	public void bindTo(CollectorRegistry registry) {
		register(registry);
	}

}
