/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.io.wfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.PartitioningInputSupplier;
import eu.esdihumboldt.util.io.InputSupplier;

/**
 * {@link InputSupplier} for WFS requests
 * 
 * @author Florian Esser
 */
public class PartitioningWFSInputSupplier extends DefaultInputSupplier
		implements PartitioningInputSupplier<InputStream> {

	private final Map<String, String> queryParameters = new HashMap<>();
	private final WFSVersion wfsVersion;
	private int partitionFeatureLimit = 500;

	/**
	 * Create a partitioning input supplier for WFS requests with a default
	 * {@link #partitionFeatureLimit} of 500.
	 * 
	 * @param location WFS request URL
	 */
	public PartitioningWFSInputSupplier(URI location) {

		super(location);

		URIBuilder builder = new URIBuilder(location);
		builder.getQueryParams()
				.forEach(qp -> queryParameters.put(qp.getName().toUpperCase(), qp.getValue()));

		wfsVersion = WFSVersion.fromString(queryParameters.get("VERSION"), WFSVersion.V1_1_0);
	}

	/**
	 * Set the maximum number of features per partition.
	 * 
	 * @param limit feature limit
	 */
	public void setFeatureLimit(int limit) {
		this.partitionFeatureLimit = limit;
	}

	@Override
	public Collection<LocatableInputSupplier<InputStream>> getPartitions() {
		int featureCount;
		try {
			featureCount = determineFeatureCount(getLocation(), wfsVersion);
		} catch (WFSException | IOException | URISyntaxException e) {
			// If the feature count cannot be determined (e.g. because the WFS
			// does not support the "RESULTTYPE=hits" parameter),
			// no partitioning can be performed.
			//
			// Instead of failing here, fall back to performing an unpartitioned
			// request.
			return Arrays.asList(this);
		}

		if (!canPartition(getLocation())) {
			return Arrays.asList(this);
		}
		else {
			try {
				final List<LocatableInputSupplier<InputStream>> partitions = new ArrayList<>();
				for (URI partitionUri : partitionRequest(featureCount)) {
					partitions.add(new DefaultInputSupplier(partitionUri));
				}

				return partitions;
			} catch (URISyntaxException e) {
				// fall back to unpartitioned input
				return Arrays.asList(this);
			}
		}
	}

	private boolean canPartition(URI location) {
		if (!location.getScheme().equals("http") && !location.getScheme().equals("https")) {
			return false;
		}

		// check request type
		String request = queryParameters.get("REQUEST");
		if (request == null || !request.equalsIgnoreCase("GetFeature")) {
			return false;
		}

		String resultType = queryParameters.get("RESULTTYPE");
		if (resultType != null && !resultType.equalsIgnoreCase("results")) {
			// only "results" requests can be partitioned
			return false;
		}

		String maxFeaturesParameter = getMaxFeaturesParameterName(wfsVersion);
		if (maxFeaturesParameter == null) {
			// unsupported WFS version
			return false;
		}

		// check if request is already limited
		if (queryParameters.containsKey(maxFeaturesParameter)) {
			return false;
		}

		return true;
	}

	private static String getMaxFeaturesParameterName(WFSVersion version) {
		switch (version) {
		case V1_1_0:
			return "MAXFEATURES";
		case V2_0_0:
		case V2_0_2:
			return "COUNT";
		default:
			return null;
		}
	}

	/**
	 * Partition the WFS query
	 * 
	 * @param featureCount Total features expected
	 * @return list of URIs extended by KVP parameters to partition the requests
	 * @throws URISyntaxException thrown if a URI cannot be built
	 */
	private List<URI> partitionRequest(int featureCount) throws URISyntaxException {
		List<URI> result = new ArrayList<>();

		// If a STARTINDEX parameter is already present in the URL, use it as
		// an offset during the generation of the partitioned request
		int startIndexOffset = 0;
		String existingStartIndex = queryParameters.get("STARTINDEX");
		if (existingStartIndex != null && !existingStartIndex.isEmpty()) {
			startIndexOffset = Integer.parseInt(existingStartIndex);
		}

		int totalParts = (int) Math.ceil((double) featureCount / (double) partitionFeatureLimit);
		for (int i = 0; i < totalParts; i++) {
			URIBuilder builder = new URIBuilder(getLocation());

			// Remove existing STARTINDEX and MAXFEATURES/COUNT parameters if
			// present
			List<NameValuePair> params = builder.getQueryParams();
			params.removeIf(nvp -> nvp.getName().equalsIgnoreCase("STARTINDEX"));
			params.removeIf(
					nvp -> nvp.getName().equalsIgnoreCase(getMaxFeaturesParameterName(wfsVersion)));
			builder.clearParameters();
			builder.addParameters(params);

			// STARTINDEX is 0-based
			builder.addParameter("STARTINDEX",
					Integer.toString(startIndexOffset + partitionFeatureLimit * i));
			builder.addParameter(getMaxFeaturesParameterName(wfsVersion),
					Integer.toString(partitionFeatureLimit));
			result.add(builder.build());
		}

		return result;
	}

	private int determineFeatureCount(URI requestUri, WFSVersion version)
			throws MalformedURLException, IOException, URISyntaxException, WFSException {
		URIBuilder builder = new URIBuilder(requestUri);
		builder.addParameter("RESULTTYPE", "hits");

		InputStream in = builder.build().toURL().openStream();

		return FeatureCollectionHelper.getNumberOfFeatures(in, version);
	}
}
