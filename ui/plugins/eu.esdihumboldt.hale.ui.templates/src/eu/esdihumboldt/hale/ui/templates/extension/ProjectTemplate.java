/*
 * Copyright (c) 2012 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.templates.extension;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Represents a project template defined through the extension point.
 * 
 * @author Simon Templer
 */
public interface ProjectTemplate extends Identifiable {

	/**
	 * Get the project display name.
	 * 
	 * @return the project name
	 */
	public abstract String getName();

	/**
	 * Get the project file location.
	 * 
	 * @return the input supplier
	 * @throws URISyntaxException if the location is invalid
	 */
	public abstract LocatableInputSupplier<? extends InputStream> getLocation() throws URISyntaxException;

	/**
	 * Get the icon URL.
	 * 
	 * @return the icon URL, may be <code>null</code>
	 */
	public abstract URL getIconURL();

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public abstract String getId();

}