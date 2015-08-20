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

package eu.esdihumboldt.hale.io.geoserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.entity.ContentType;

import eu.esdihumboldt.hale.io.geoserver.template.Templates;

/**
 * Base class for classes representing GeoServer resources.
 * 
 * <p>
 * The basic idea is that a resource has a name, a set of attributes (the actual
 * set of attributes depends on the resource type) and a content, which is
 * either read from a template file, or directly from an input stream.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public abstract class AbstractResource implements Resource {

	/**
	 * Default resource content type.
	 */
	public static final ContentType DEF_CONTENT_TYPE = ContentType.APPLICATION_XML
			.withCharset("UTF-8");
	/**
	 * Zipped archive content type.
	 */
	public static final ContentType ZIP_CONTENT_TYPE = ContentType.create("application/zip");

	/**
	 * Resource attributes.
	 */
	protected Map<String, Object> attributes;
	/**
	 * The set of allowed attribute names for this particular resource type.
	 */
	protected Set<String> allowedAttributes;

	/**
	 * Default constructor.
	 * 
	 * <p>
	 * Should be invoked by subclasses.
	 * </p>
	 */
	public AbstractResource() {
		this.attributes = new HashMap<String, Object>();
		this.allowedAttributes = new HashSet<String>();
		this.allowedAttributes.addAll(allowedAttributes());
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#contentType()
	 */
	@Override
	public ContentType contentType() {
		return DEF_CONTENT_TYPE;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("name must be set");
		}

		return this.attributes.get(name);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object value) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("name must be set");
		}
		if (!this.allowedAttributes.contains(name)) {
			throw new IllegalArgumentException(String.format(
					"Variable \"%s\" not allowed in this resource", name));
		}

		this.attributes.put(name, value);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#write(java.io.OutputStream)
	 */
	@Override
	public void write(OutputStream out) throws IOException {

		// unset unspecified variables by setting their value to null
		for (String var : this.allowedAttributes) {
			if (!this.attributes.containsKey(var)) {
				this.attributes.put(var, null);
			}
		}

		InputStream resourceStream = locateResource();
		if (resourceStream != null) {
			BufferedInputStream input = new BufferedInputStream(resourceStream);
			BufferedOutputStream output = new BufferedOutputStream(out);
			try {

				for (int b = input.read(); b >= 0; b = input.read()) {
					output.write(b);
				}
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					// ignore exception on close
				}
				try {
					output.close();
				} catch (IOException e) {
					// ignore exception on close
				}
			}
		}

	}

	/**
	 * Grabs an input stream from which resource content can be read.
	 * 
	 * <p>
	 * If a non-null template location is returned by the
	 * {@link #templateLocation()} method, the returned input stream points to
	 * the result of the merging of the template with the resource; otherwise,
	 * the result of {@link #resourceStream()} is returned.
	 * </p>
	 * 
	 * @return the input stream from which resource content is read
	 * @throws IOException if an I/O error occurs
	 */
	protected InputStream locateResource() throws IOException {

		if (templateLocation() != null && !templateLocation().isEmpty()) {
			// resource is loaded from a template and attributes map is used to
			// interpolate variables in it
			return Templates.getInstance().loadTemplate(templateLocation(), this.attributes);
		}
		else {
			// load resource straight from the classpath
			return resourceStream();
		}

	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#asStream()
	 */
	@Override
	public InputStream asStream() throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(asByteArray());

		return bis;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#asByteArray()
	 */
	@Override
	public byte[] asByteArray() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			write(bos);

			return bos.toByteArray();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				// ignore exception on close
			}
		}
	}

	/**
	 * Should be overridden by sublcasses loading resource content from
	 * template.
	 * 
	 * @return the template location (relative to classpath)
	 */
	protected String templateLocation() {
		return null;
	}

	/**
	 * Should be overridden by subclasses loading resource content from an
	 * {@link InputStream}, e.g. file resources.
	 * 
	 * @return the {@link InputStream} from which resource content should be
	 *         read
	 */
	protected InputStream resourceStream() {
		return null;
	}

	/**
	 * Return the set of allowed attributes.
	 * 
	 * <p>
	 * The method must be implemented by subclasses and is invoked by the
	 * default constructor to populate the {@link #allowedAttributes} set.
	 * </p>
	 * 
	 * @return the set of allowed attributes
	 */
	protected abstract Set<String> allowedAttributes();
}
