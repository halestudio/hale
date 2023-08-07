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

package eu.esdihumboldt.hale.io.gml.reader.internal.wfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.InstanceResolver;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.IndexInstanceReference;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceCollection;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceCollection.GmlInstanceIterator;
import eu.esdihumboldt.hale.io.gml.reader.internal.instance.StreamGmlInstance;

/**
 * Instance collection based on a GML input stream from WFS requests.<br>
 * <br>
 * The {@link InstanceIterator} created by {@link #iterator()} will
 * transparently issue multiple requests to the WFS. When all features that
 * match the request have been retrieved from the WFS (possibly via multiple
 * requests), the iterator will be closed and subsequent calls to hasNext() will
 * return false.<br>
 * <br>
 * The number of features to retrieve per request must be provided when creating
 * the instance collection.
 * 
 * For WFS 2.0.0/2.0.2 a starting offset can be provided by adding a
 * <code>STARTINDEX</code> parameter to the source location. The maximum overall
 * number of features to retrive can be set for by adding a
 * <code>MAXFEATURES</code> (WFS 1.1.0) or <code>COUNT</code> (WFS 2.0.0/2.0.2)
 * parameter to the source location or programmatically via
 * {@link #setMaxNumberOfFeatures(int)}.
 * 
 * @author Florian Esser
 */
public class WfsBackedGmlInstanceCollection implements InstanceCollection {

	/**
	 * Constant indicating unlimited feature retrieval.
	 */
	public static final int UNLIMITED = -1;

	private final ALogger log = ALoggerFactory.getLogger(WfsBackedGmlInstanceCollection.class);

	// Original source location
	private final URI primordialUri;
	// Key/value pairs of the query part of the primordial URI. Keys are stored
	// in uppercase by convention.
	private final Map<String, String> primordialQueryParams = new HashMap<>();

	// URI used by iterator to create follow-up requests
	private final URI baseUri;

	// XXX Use WFSVersion instead. To resolve dependency cycle, create
	// e.e.h.common.wfs project?
	private final String wfsVersion;

	// Absolute limit for the amount of features to retrieve (default:
	// unlimited)
	private int maxNumberOfFeatures = UNLIMITED;

	// Number of features to retrieve at most with one WFS GetFeature request
	private final int featuresPerRequest;

	private final int size;

	// Parameters needed for instantiation of GmlInstanceCollection
	private final TypeIndex sourceSchema;
	private final boolean restrictToFeatures;
	private final boolean ignoreRoot;
	private final boolean strict;
	private final CRSProvider crsProvider;
	private final boolean ignoreNamespaces;
	private final IOProvider ioProvider;
	private final String tmpdir;

	/**
	 * Create a GML instance collection based on the given WFS source.
	 * 
	 * @param source the source
	 * @param sourceSchema the source schema
	 * @param restrictToFeatures if only instances that are GML features shall
	 *            be loaded
	 * @param ignoreRoot if the root element should be ignored for creating
	 *            instances even if it is recognized as an allowed instance type
	 * @param strict if associating elements with properties should be done
	 *            strictly according to the schema, otherwise a fall-back is
	 *            used trying to populate values also on invalid property paths
	 * @param ignoreNamespaces if parsing of the XML instances should allow
	 *            types and properties with namespaces that differ from those
	 *            defined in the schema
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 * @param provider the I/O provider to get values
	 * @param featuresPerRequest Number of features to retrieve at most with one
	 *            WFS GetFeature request, or {@value #UNLIMITED} to disable
	 *            pagination
	 * @param ignoreNumberMatched If featuresPerRequest is set, ignore the
	 *            number of matches reported by the WFS
	 * @throws URISyntaxException thrown if the WFS request URL cannot be
	 *             generated from the source location URI
	 */
	public WfsBackedGmlInstanceCollection(LocatableInputSupplier<? extends InputStream> source,
			TypeIndex sourceSchema, boolean restrictToFeatures, boolean ignoreRoot, boolean strict,
			boolean ignoreNamespaces, CRSProvider crsProvider, IOProvider provider,
			int featuresPerRequest, boolean ignoreNumberMatched) throws URISyntaxException {

		this(source, sourceSchema, restrictToFeatures, ignoreRoot, strict, ignoreNamespaces,
				crsProvider, provider, featuresPerRequest, ignoreNumberMatched, null);
	}

	/**
	 * Create a GML instance collection based on the given WFS source.
	 * 
	 * @param source the source
	 * @param sourceSchema the source schema
	 * @param restrictToFeatures if only instances that are GML features shall
	 *            be loaded
	 * @param ignoreRoot if the root element should be ignored for creating
	 *            instances even if it is recognized as an allowed instance type
	 * @param strict if associating elements with properties should be done
	 *            strictly according to the schema, otherwise a fall-back is
	 *            used trying to populate values also on invalid property paths
	 * @param ignoreNamespaces if parsing of the XML instances should allow
	 *            types and properties with namespaces that differ from those
	 *            defined in the schema
	 * @param crsProvider CRS provider in case no CRS is specified, may be
	 *            <code>null</code>
	 * @param provider the I/O provider to get values
	 * @param featuresPerRequest Number of features to retrieve at most with one
	 *            WFS GetFeature request, or {@value #UNLIMITED} to disable
	 *            pagination
	 * @param ignoreNumberMatched If featuresPerRequest is set, ignore the
	 *            number of matches reported by the WFS
	 * @param tmpDirPath tmp dir path to download all wfs files
	 * @throws URISyntaxException thrown if the WFS request URL cannot be
	 *             generated from the source location URI
	 */
	public WfsBackedGmlInstanceCollection(LocatableInputSupplier<? extends InputStream> source,
			TypeIndex sourceSchema, boolean restrictToFeatures, boolean ignoreRoot, boolean strict,
			boolean ignoreNamespaces, CRSProvider crsProvider, IOProvider provider,
			int featuresPerRequest, boolean ignoreNumberMatched, String tmpDirPath)
			throws URISyntaxException {
		this.sourceSchema = sourceSchema;
		this.restrictToFeatures = restrictToFeatures;
		this.ignoreRoot = ignoreRoot;
		this.strict = strict;
		this.crsProvider = crsProvider;
		this.ignoreNamespaces = ignoreNamespaces;
		this.ioProvider = provider;

		this.primordialUri = source.getLocation();

		this.tmpdir = tmpDirPath;
		// Build base URI from original location by removing STARTINDEX and
		// MAXFEATURES/COUNT parameters if present
		URIBuilder builder = new URIBuilder(primordialUri);
		builder.getQueryParams().forEach(
				qp -> primordialQueryParams.put(qp.getName().toUpperCase(), qp.getValue()));

		wfsVersion = primordialQueryParams.get("VERSION");
		if (wfsVersion == null || wfsVersion.isEmpty()) {
			throw new IllegalArgumentException("WFS URL must contain VERSION parameter");
		}

		List<NameValuePair> params = builder.getQueryParams();
		params.removeIf(nvp -> nvp.getName().equalsIgnoreCase("STARTINDEX"));
		params.removeIf(
				nvp -> nvp.getName().equalsIgnoreCase(getMaxFeaturesParameterName(wfsVersion)));
		builder.clearParameters();
		builder.addParameters(params);
		this.baseUri = builder.build();

		// If a MAXFEATURES/COUNT parameter is present in the primordial URI,
		// set maxNumberOfFeatures accordingly
		if (primordialQueryParams.containsKey(getMaxFeaturesParameterName(wfsVersion))) {
			// Allow possible NumberFormatException to be thrown up to prevent
			// unintended retrieval of too many features
			maxNumberOfFeatures = Integer
					.parseInt(primordialQueryParams.get(getMaxFeaturesParameterName(wfsVersion)));

			if (maxNumberOfFeatures < 0) {
				throw new IllegalArgumentException(
						MessageFormat.format("Parameter \"{0}\" must be a non-negative integer.",
								getMaxFeaturesParameterName(wfsVersion)));
			}
		}

		// Use primordial URI and issue "hits" request to check if the WFS will
		// return anything at all
		int hits;
		if (ignoreNumberMatched) {
			hits = UNKNOWN_SIZE;
		}
		else {
			try {
				hits = requestHits(primordialUri);
			} catch (WFSException e) {
				log.debug(MessageFormat.format(
						"Failed to perform hits query (REQUESTTYPE=hits): {0}", e.getMessage()), e);
				hits = UNKNOWN_SIZE;
			}
		}

		switch (wfsVersion) {
		case "1.1.0":
			// The "numberOfFeatures" reported by a 1.1.0 WFS may be smaller
			// than the actual number of features matches by the query if the
			// number of features returned per query is limited on the server
			// side. Therefore do not rely on it as a size information here.
			this.size = UNKNOWN_SIZE;
			break;
		case "2.0.0":
		case "2.0.2":
			// The "numberMatched" reported by a 2.0.0/2.0.2 WFS should be
			// number of features matched by the query. If hits equals
			// UNKNOWN_SIZE then size is also set to that value
			this.size = isLimited() ? Math.min(maxNumberOfFeatures, hits) : hits;
			break;
		default:
			this.size = UNKNOWN_SIZE;
		}

		if (featuresPerRequest != UNLIMITED && featuresPerRequest <= 0) {
			throw new IllegalArgumentException(MessageFormat.format(
					"featuresPerRequest must be a positive integer or {0} to disable pagination",
					UNLIMITED));
		}
		this.featuresPerRequest = featuresPerRequest;
	}

	/**
	 * Set an absolute limit for the amount of features to be retrieved.
	 * 
	 * @param maxNumberOfFeatures valid values are -1 for unlimited retrieval or
	 *            a positive integer (or zero) to impose an absolute feature
	 *            limit.
	 */
	public void setMaxNumberOfFeatures(int maxNumberOfFeatures) {
		if (maxNumberOfFeatures < -1) {
			throw new IllegalArgumentException(
					"Invalid maximum: must be either -1 (unlimited) or a non-negative integer.");
		}
		this.maxNumberOfFeatures = maxNumberOfFeatures;
	}

	/**
	 * @see InstanceCollection#hasSize()
	 */
	@Override
	public boolean hasSize() {
		return size != UNKNOWN_SIZE;
	}

	/**
	 * @see InstanceCollection#size()
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * @see Iterable#iterator()
	 */
	@Override
	public WfsBackedGmlInstanceIterator iterator() {
		return new WfsBackedGmlInstanceIterator();
	}

	/**
	 * @see InstanceCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * @return true if pagination is enabled
	 */
	public boolean isPaged() {
		return featuresPerRequest != UNLIMITED;
	}

	/**
	 * @return true if an absolute limit of features to be retrieved is set
	 */
	public boolean isLimited() {
		return maxNumberOfFeatures != UNLIMITED;
	}

	/**
	 * @see InstanceCollection#select(Filter)
	 */
	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	/**
	 * @see InstanceResolver#getReference(Instance)
	 */
	@Override
	public InstanceReference getReference(Instance instance) {
		if (instance instanceof StreamGmlInstance) {
			// XXX Possible improvement: return reference based on feature ID if
			// source WFS supports a GetFeatureById query
			return new IndexInstanceReference(instance.getDataSet(),
					((StreamGmlInstance) instance).getIndexInStream());
		}

		throw new IllegalArgumentException(
				"Reference can only be determined based on a StreamGmlInstance");
	}

	/**
	 * @see InstanceResolver#getInstance(InstanceReference)
	 */
	@Override
	public Instance getInstance(InstanceReference reference) {
		IndexInstanceReference ref = (IndexInstanceReference) reference;

		WfsBackedGmlInstanceIterator it = iterator();
		try {
			for (int i = 0; i < ref.getIndex(); i++) {
				// skip all instances before the referenced instance
				it.skip();
			}
			return it.next(); // return the referenced instance
		} finally {
			it.close();
		}
	}

	private int requestHits(URI requestUri) throws WFSException {
		URIBuilder builder = new URIBuilder(requestUri);
		builder.addParameter("RESULTTYPE", "hits");

		InputStream in;
		try {
			in = builder.build().toURL().openStream();
		} catch (IOException | URISyntaxException e) {
			throw new WFSException(
					MessageFormat.format("Unable to execute WFS request: {0}", e.getMessage()), e);
		}

		return FeatureCollectionHelper.getNumberOfFeatures(in);
	}

	private String getMaxFeaturesParameterName(String version) {
		// XXX Use WFSVersion
		switch (version) {
		case "1.1.0":
			return "MAXFEATURES";
		case "2.0.0":
		case "2.0.2":
			return "COUNT";
		default:
			throw new IllegalArgumentException("Unsupported WFS version");
		}
	}

	/**
	 * Iterates over {@link Instance}s in the GML stream retrieved from the WFS.
	 * 
	 * The iterator will not load more features per WFS GetFeature request than
	 * specified in the {@link WfsBackedGmlInstanceCollection}. If the WFS query
	 * yields more results, multiple GetFeature request will be issued
	 * transparently to the WFS until all results have been retrieved or the
	 * maximum number of features as specified in the
	 * {@link WfsBackedGmlInstanceCollection} was reached.
	 * 
	 * @author Florian Esser
	 */
	public class WfsBackedGmlInstanceIterator implements InstanceIterator {

		private GmlInstanceCollection currentCollection;
		private GmlInstanceIterator iterator;
		private int totalFeaturesProcessed;

		/**
		 * Create the iterator
		 */
		public WfsBackedGmlInstanceIterator() {
			createNextIterator();
		}

		/**
		 * Closes the iterator of the currently active GmlInstanceCollection and
		 * creates a new one for the next WFS request. If the new request yields
		 * no result, this {@link WfsBackedGmlInstanceIterator} is closed.
		 */
		private void proceedOrClose() {
			iterator.close();

			if (!isPaged() || isFeatureLimitReached()) {
				close();
			}
			else {
				createNextIterator();

				if (!iterator.hasNext()) {
					close();
				}
			}
		}

		private void createNextIterator() {
			URI nextUri;
			try {
				nextUri = calculateNextUri();
			} catch (URISyntaxException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}

			// Download all the files into a folder for Bucket service
			if (tmpdir != null) {
				try {
					URL url = nextUri.toURL();
					StringBuilder fileName = new StringBuilder(tmpdir);

					String urlPath = url.getPath();
					if (urlPath.startsWith("/")) {
						urlPath = urlPath.replaceFirst("/", "");
					}
					fileName.append("/").append(urlPath.replaceAll("/", "_")).append("_");

					// TODO: may be download files in a thread
					File downloadFile = new File(fileName.toString());
					nextUri = downloadFile.toURI();
					FileUtils.copyURLToFile(url, downloadFile);
				} catch (IOException e) {
					log.error(MessageFormat.format("Unable to download WFS request: {0}",
							e.getMessage()), e);
				}

			}
			log.debug(MessageFormat.format("Creating new iterator for URL \"{0}\"",
					nextUri.toString()));

			currentCollection = new GmlInstanceCollection(new DefaultInputSupplier(nextUri),
					sourceSchema, restrictToFeatures, ignoreRoot, strict, ignoreNamespaces,
					crsProvider, ioProvider);
			iterator = currentCollection.iterator();

			// Make sure root element is processed by the iterator
			iterator.hasNext();
		}

		private URI calculateNextUri() throws URISyntaxException {
			URIBuilder builder = new URIBuilder(baseUri);

			// Use STARTINDEX value in primordial URI as offset
			int offset = 0;
			if (primordialQueryParams.containsKey("STARTINDEX")) {
				try {
					offset = Math.max(0, Integer.parseInt(primordialQueryParams.get("STARTINDEX")));
				} catch (NumberFormatException e) {
					// Ignore if invalid
				}
			}

			if (isPaged() || offset > 0) {
				// Add STARTINDEX; is 0-based
				builder.addParameter("STARTINDEX",
						Integer.toString(offset + totalFeaturesProcessed));
			}

			final int maxFeatures;
			if (isPaged()) {
				maxFeatures = WfsBackedGmlInstanceCollection.this.featuresPerRequest;
			}
			else {
				maxFeatures = WfsBackedGmlInstanceCollection.this.maxNumberOfFeatures;
			}
			if (maxFeatures >= 0) {
				builder.addParameter(getMaxFeaturesParameterName(wfsVersion),
						Integer.toString(maxFeatures));
			}

			return builder.build();
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			if (isClosed()) {
				return false;
			}

			if (isFeatureLimitReached()) {
				close();
				return false;
			}

			if (!iterator.hasNext()) {
				// If all features from the current request have been read, try
				// to retrieve a new batch from the WFS and continue. If the new
				// request does not yield new results this iterator will be
				// closed.
				proceedOrClose();
			}

			if (!isClosed() && iterator.hasNext()) {
				return true;
			}

			return false;
		}

		/**
		 * @return true if the number of features processed is equal to (or
		 *         exceeds) the maximum number of features to processed or the
		 *         number of results reported by the WFS.
		 */
		protected boolean isFeatureLimitReached() {
			return (maxNumberOfFeatures != UNLIMITED
					&& totalFeaturesProcessed >= maxNumberOfFeatures)
					|| (size != UNKNOWN_SIZE && totalFeaturesProcessed >= size);
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Instance next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			Instance instance = iterator.next();
			return new StreamGmlInstance(instance, totalFeaturesProcessed++);
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.ResourceIterator#close()
		 */
		@Override
		public void close() {
			if (iterator != null) {
				iterator.close();
				iterator = null;
				currentCollection = null;
			}
		}

		/**
		 * @return true if all results from the original WFS request have been
		 *         retrieved
		 */
		public boolean isClosed() {
			return iterator == null;
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator#typePeek()
		 */
		@Override
		public TypeDefinition typePeek() {
			if (hasNext()) {
				return iterator.typePeek();
			}

			return null;
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator#supportsTypePeek()
		 */
		@Override
		public boolean supportsTypePeek() {
			return true;
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator#skip()
		 */
		@Override
		public void skip() {
			if (iterator.hasNext()) {
				iterator.skip();
			}
			else {
				proceedOrClose();
				if (hasNext()) {
					skip();
				}
			}
		}

	}

}
