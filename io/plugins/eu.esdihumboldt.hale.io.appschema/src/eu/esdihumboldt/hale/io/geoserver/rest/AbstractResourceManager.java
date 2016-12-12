/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.geoserver.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.io.geoserver.Resource;

/**
 * Base class for classes representing GeoServer resource managers.
 * 
 * <p>
 * The basic idea is that a resource manager can retrieve the list of resourcees
 * of type <code>T</code> and can execute the standard CRUD operations on a
 * specific resource instance, called the "managed resource", which must be
 * explicitly set by calling the {@link #setResource(Resource)} method.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 * @param <T> the type of the managed resource
 */
public abstract class AbstractResourceManager<T extends Resource> implements ResourceManager<T> {

	/**
	 * Base path for all REST services.
	 */
	public static final String REST_BASE = "rest";
	/**
	 * Default request / response body format.
	 */
	public static final String DEF_FORMAT = "xml";

	/**
	 * Base GeoServer URL (e.g. http://localhost:8080/geoserver)
	 */
	protected URL geoserverUrl;
	/**
	 * The resource to manage.
	 */
	protected T resource;

	private Executor executor;

	/**
	 * Constructor.
	 * 
	 * @param geoserverUrl the base GeoServer URL
	 * @throws MalformedURLException if the provided URL is invalid
	 */
	public AbstractResourceManager(String geoserverUrl) throws MalformedURLException {
		this(new URL(geoserverUrl));
	}

	/**
	 * Constructor.
	 * 
	 * @param geoserverUrl the base GeoServer URL
	 */
	public AbstractResourceManager(URL geoserverUrl) {
		this.geoserverUrl = geoserverUrl;

	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#setCredentials(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void setCredentials(String user, String password) {
		if (executor == null) {
			executor = Executor.newInstance();
		}
		HttpHost geoserverHost = new HttpHost(geoserverUrl.getHost(), geoserverUrl.getPort(),
				geoserverUrl.getProtocol());
		executor.auth(geoserverHost, user, password);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#list()
	 */
	@Override
	public Document list() {
		try {
			return executor.execute(Request.Get(getResourceListURL())).handleResponse(
					new XmlResponseHandler());
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#setResource(eu.esdihumboldt.hale.io.geoserver.Resource)
	 */
	@Override
	public void setResource(T resource) {
		this.resource = resource;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#exists()
	 */
	@Override
	public boolean exists() {
		checkResourceSet();

		try {
			return executor.execute(Request.Get(getResourceURL())).handleResponse(
					new ResponseHandler<Boolean>() {

						/**
						 * @see org.apache.http.client.ResponseHandler#handleResponse(org.apache.http.HttpResponse)
						 */
						@Override
						public Boolean handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							int statusCode = response.getStatusLine().getStatusCode();
							String reason = response.getStatusLine().getReasonPhrase();

							switch (statusCode) {
							case 200:
								return true;
							case 404:
								return false;
							default:
								throw new HttpResponseException(statusCode, reason);
							}
						}

					});
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#read()
	 */
	@Override
	public Document read() {
		return read(null);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#read(java.util.Map)
	 */
	@Override
	public Document read(Map<String, String> parameters) {
		checkResourceSet();

		try {
			String url = getResourceURL();
			String queryString = buildQueryString(parameters);
			return executor.execute(Request.Get(url + queryString)).handleResponse(
					new XmlResponseHandler());
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#create()
	 */
	@Override
	public URL create() {
		return create(null);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#create(java.util.Map)
	 */
	@Override
	public URL create(Map<String, String> parameters) {
		checkResourceSet();

		try {
			String url = getResourceListURL();
			String queryString = buildQueryString(parameters);
			ByteArrayEntity entity = new ByteArrayEntity(resource.asByteArray());
			entity.setContentType(resource.contentType().getMimeType());

			return executor.execute(Request.Post(url + queryString).body(entity)).handleResponse(
					new ResponseHandler<URL>() {

						/**
						 * @see org.apache.http.client.ResponseHandler#handleResponse(org.apache.http.HttpResponse)
						 */
						@Override
						public URL handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							StatusLine statusLine = response.getStatusLine();
							if (statusLine.getStatusCode() >= 300) {
								throw new HttpResponseException(statusLine.getStatusCode(),
										statusLine.getReasonPhrase());
							}
							if (statusLine.getStatusCode() == 201) {
								Header locationHeader = response.getFirstHeader("Location");
								if (locationHeader != null) {
									return new URL(locationHeader.getValue());
								}
							}
							return null;
						}
					});
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#update()
	 */
	@Override
	public void update() {
		update(null);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#update(java.util.Map)
	 */
	@Override
	public void update(Map<String, String> parameters) {
		checkResourceSet();

		try {
			String url = getResourceURL();
			String queryString = buildQueryString(parameters);
			ByteArrayEntity entity = new ByteArrayEntity(resource.asByteArray());
			entity.setContentType(resource.contentType().getMimeType());

			executor.execute(Request.Put(url + queryString).body(entity)).handleResponse(
					new EmptyResponseHandler());
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#delete()
	 */
	@Override
	public void delete() {
		delete(null);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.ResourceManager#delete(java.util.Map)
	 */
	@Override
	public void delete(Map<String, String> parameters) {
		checkResourceSet();

		try {
			String url = getResourceURL();
			String queryString = buildQueryString(parameters);
			executor.execute(Request.Delete(url + queryString)).handleResponse(
					new EmptyResponseHandler());
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	private void checkResourceSet() {
		if (this.resource == null) {
			throw new IllegalStateException("Resource not set");
		}
	}

	/**
	 * Construct URL of the list of resources of type <code>T</code>.
	 * 
	 * @return the resource list URL
	 */
	protected String getResourceListURL() {
		return getRestServiceUrl(getResourceListPath());
	}

	/**
	 * Construct URL of the managed resource.
	 * 
	 * @return the resource URL
	 */
	protected String getResourceURL() {
		return getRestServiceUrl(getResourcePath());
	}

	private String getRestServiceUrl(String resourcePath) {
		List<String> urlParts = new ArrayList<String>();
		urlParts.add(geoserverUrl.toString());
		urlParts.add(REST_BASE);
		urlParts.add(resourcePath);

		urlParts.replaceAll(urlPart -> normalizeUrlPart(urlPart));
		final String resourceUrl = Joiner.on('/').skipNulls().join(urlParts);
		return Joiner.on(".").skipNulls().join(Arrays.asList(resourceUrl, getFormat()));
	}

	private String buildQueryString(Map<String, String> queryParameters) {
		if (queryParameters == null || queryParameters.size() == 0) {
			return "";
		}

		StringBuilder queryBuilder = new StringBuilder("?");
		try {
			for (Entry<String, String> param : queryParameters.entrySet()) {
				queryBuilder.append(param.getKey()).append("=")
						.append(URLEncoder.encode(param.getValue(), "UTF-8"));

			}
		} catch (UnsupportedEncodingException e) {
			// should never happen
			// TODO: log exception?
		}

		return queryBuilder.toString();
	}

	private String normalizeUrlPart(String urlPart) {
		// remove slashes at the beginning and end of the URL part
		// and return null if it empty or contains only whitespace
		urlPart = StringUtils.stripStart(urlPart, "/");
		urlPart = StringUtils.stripEnd(urlPart, "/");
		return StringUtils.defaultIfBlank(urlPart, null);
	}

	/**
	 * Template method, to be implemented by subclasses.
	 * 
	 * <p>
	 * Should return the path to the list of resources of type <code>T</code>
	 * (relative to GeoServer's REST base path, e.g.
	 * http://localhost:8080/geoserver/rest).
	 * 
	 * @return the resource list path
	 */
	protected abstract String getResourceListPath();

	/**
	 * Template method, to be implemented by subclasses.
	 * 
	 * <p>
	 * Should return the path to the managed resource (relative to GeoServer's
	 * REST base path, e.g. http://localhost:8080/geoserver/rest).
	 * </p>
	 * 
	 * @return the resource path
	 */
	protected abstract String getResourcePath();

	/**
	 * Retrieves the default request / response body format.
	 * 
	 * @return the format
	 */
	protected String getFormat() {
		return DEF_FORMAT;
	}

	/**
	 * Response handler that parses the response body into an XML
	 * {@link Document}.
	 * 
	 * @author Stefano Costa, GeoSolutions
	 */
	private class XmlResponseHandler implements ResponseHandler<Document> {

		/**
		 * @see org.apache.http.client.ResponseHandler#handleResponse(org.apache.http.HttpResponse)
		 */
		@Override
		public Document handleResponse(HttpResponse response) throws ClientProtocolException,
				IOException {
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}
			if (entity == null) {
				throw new ClientProtocolException("Response contains no content");
			}
			DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder docBuilder = dbFac.newDocumentBuilder();
				ContentType contentType = ContentType.getOrDefault(entity);
				if (!isXml(contentType)) {
					throw new ClientProtocolException("Unexpected content type: " + contentType);
				}
				Charset charset = contentType.getCharset();
				if (charset == null) {
					charset = Charset.forName("UTF-8");
				}
				return docBuilder.parse(entity.getContent());
			} catch (ParserConfigurationException ex) {
				throw new IllegalStateException(ex);
			} catch (SAXException ex) {
				throw new ClientProtocolException("Malformed XML document", ex);
			}
		}

	}

	private boolean isXml(ContentType contentType) {
		if (contentType == null) {
			return false;
		}

		String mimeType = contentType.getMimeType();
		return mimeType.equals("text/xml") || mimeType.equals("application/xml");
	}

	/**
	 * Response handler that does nothing with the response body, but just
	 * checks the response status and throws an exception if the status code is
	 * greater than or equal to 300.
	 * 
	 * @author Stefano Costa, GeoSolutions
	 */
	private class EmptyResponseHandler implements ResponseHandler<Void> {

		/**
		 * @see org.apache.http.client.ResponseHandler#handleResponse(org.apache.http.HttpResponse)
		 */
		@Override
		public Void handleResponse(HttpResponse response) throws ClientProtocolException,
				IOException {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}
			return null;
		}

	}
}
