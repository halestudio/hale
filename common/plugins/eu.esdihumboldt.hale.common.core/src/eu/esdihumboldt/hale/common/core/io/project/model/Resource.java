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

/**
 * Represents a loaded resource.
 * 
 * @author Simon Templer
 */
public interface Resource {

	/**
	 * Get the resource source.
	 * 
	 * @return the source location or <code>null</code>
	 */
	public abstract URI getSource();

	/**
	 * Get the resource identifier.
	 * 
	 * @return the resource ID or <code>null</code>
	 */
	public abstract String getResourceId();

	/**
	 * Get the resource content type.
	 * 
	 * @return the content type or <code>null</code>
	 */
	public abstract IContentType getContentType();

	/**
	 * @return the identifier of the action the resource was loaded for
	 * @see eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration#getActionId()
	 */
	public abstract String getActionId();

	/**
	 * @return the identifier of the I/O provider the resource was loaded with
	 * @see eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration#getProviderId()
	 */
	public abstract String getProviderId();

	/**
	 * Copy the resource I/O configuration.
	 * 
	 * @return the configuration copy
	 */
	public IOConfiguration copyConfiguration();

}