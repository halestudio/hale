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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import org.apache.http.entity.ContentType;

/**
 * TODO Type description
 * 
 * @author stefano
 */
@SuppressWarnings("javadoc")
public class ResourceBuilder {

	private final Resource resource;

	public static ResourceBuilder namespace(String prefix) {
		return new ResourceBuilder(new Namespace(prefix));
	}

	public static ResourceBuilder workspace(String name) {
		return new ResourceBuilder(new Workspace(name));
	}

	public static ResourceBuilder dataStoreFile(InputStream stream) {
		return new ResourceBuilder(new DataStoreFile(stream));
	}

	public static ResourceBuilder dataStoreFile(InputStream stream, ContentType contentType) {
		return new ResourceBuilder(new DataStoreFile(stream, contentType));
	}

	public static <T extends DataStore> ResourceBuilder dataStore(String name,
			Class<T> dataStoreType) {
		if (dataStoreType == null) {
			throw new IllegalArgumentException("DataStore type not specified");
		}

		Constructor<T> constructor;
		try {
			// TODO: this code assumes a constructor taking a single String
			// parameter exists
			constructor = dataStoreType.getConstructor(String.class);

			return new ResourceBuilder(constructor.newInstance(name));
		} catch (Exception e) {
			throw new RuntimeException(
					"Cannot instantiate DataStore type: " + dataStoreType.getName());
		}
	}

	public static ResourceBuilder featureType(String name) {
		return new ResourceBuilder(new FeatureType(name));
	}

	public static ResourceBuilder layer(String name) {
		return new ResourceBuilder(new Layer(name));
	}

	private ResourceBuilder(Resource resource) {
		this.resource = resource;
	}

	public ResourceBuilder setAttribute(String name, Object value) {
		this.resource.setAttribute(name, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Resource> T build() {
		return (T) this.resource;
	}

	public void print(OutputStream out) throws IOException {
		this.resource.write(out);
	}

}
