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

package eu.esdihumboldt.hale.common.schema.presets.extension;

import java.io.InputStream;
import java.net.URL;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Represents a predefined schema.
 * 
 * @author Simon Templer
 */
public interface SchemaPreset extends Named, Identifiable {

	/**
	 * @return the category identifier
	 */
	String getCategoryId();

	/**
	 * Get a tag to discern it from other versions of the same schema, e.g.
	 * specifying a repository name.
	 * 
	 * @return the schema tag, may be <code>null</code>
	 */
	public abstract String getTag();

	/**
	 * Get the schema version.
	 * 
	 * @return the schema version, may be <code>null</code>
	 */
	public abstract String getVersion();

	/**
	 * Get the schema description.
	 * 
	 * @return the description, may be <code>null</code>
	 */
	public abstract String getDescription();

	/**
	 * Get the schema location.
	 * 
	 * @return the input supplier
	 */
	public abstract LocatableInputSupplier<? extends InputStream> getLocation();

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