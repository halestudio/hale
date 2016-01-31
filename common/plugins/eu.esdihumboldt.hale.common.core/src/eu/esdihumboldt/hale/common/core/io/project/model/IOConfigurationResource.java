/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.project.model;

import java.net.URI;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Immutable resource wrapper for an I/O configuration.
 * 
 * @author Simon Templer
 */
public class IOConfigurationResource implements Resource {

	private final IOConfiguration config;

	private final URI projectLocation;

	/**
	 * Create a new resource.
	 * 
	 * @param config the I/O configuration to wrap
	 * @param projectLocation the location of the project relative URIs should
	 *            be resolved against, should be <code>null</code> only if there
	 *            are no relative resources
	 */
	public IOConfigurationResource(IOConfiguration config, URI projectLocation) {
		super();
		this.config = config;
		this.projectLocation = projectLocation;
	}

	@Override
	public URI getSource() {
		Value sourceValue = config.getProviderConfiguration().get(ImportProvider.PARAM_SOURCE);
		if (sourceValue != null) {
			return URI.create(sourceValue.as(String.class));
		}
		return null;
	}

	@Override
	public URI getAbsoluteSource() {
		URI uri = getSource();
		if (uri != null) {
			if (uri.isAbsolute()) {
				return uri;
			}

			if (projectLocation != null) {
				// resolve against project
				return projectLocation.resolve(uri);
			}
		}
		return null;
	}

	@Override
	public String getResourceId() {
		Value idValue = config.getProviderConfiguration().get(ImportProvider.PARAM_RESOURCE_ID);
		if (idValue != null) {
			return idValue.as(String.class);
		}
		return null;
	}

	@Override
	public IContentType getContentType() {
		Value ctValue = config.getProviderConfiguration().get(ImportProvider.PARAM_CONTENT_TYPE);
		if (ctValue != null) {
			return HalePlatform.getContentTypeManager().getContentType(ctValue.as(String.class));
		}
		return null;
	}

	@Override
	public String getActionId() {
		return config.getActionId();
	}

	@Override
	public String getProviderId() {
		return config.getProviderId();
	}

	@Override
	public IOConfiguration copyConfiguration(boolean absolute) {
		IOConfiguration copy = config.clone();
		URI abs = getAbsoluteSource();
		if (abs != null) {
			copy.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE,
					Value.of(abs.toASCIIString()));
		}
		return copy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getResourceId() == null) ? 0 : getResourceId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IOConfigurationResource other = (IOConfigurationResource) obj;
		if (getResourceId() == null) {
			if (other.getResourceId() != null)
				return false;
		}
		else if (!getResourceId().equals(other.getResourceId()))
			return false;
		return true;
	}

}
