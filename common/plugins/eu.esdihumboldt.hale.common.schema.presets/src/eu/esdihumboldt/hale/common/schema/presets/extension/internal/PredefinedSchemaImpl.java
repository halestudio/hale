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

package eu.esdihumboldt.hale.common.schema.presets.extension.internal;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaCategory;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaCategoryExtension;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset;

/**
 * Predefined schema based on a configuration element.
 * 
 * @author Simon Templer
 */
public class PredefinedSchemaImpl implements SchemaPreset {

	private final IConfigurationElement element;
	private final String elementId;

	/**
	 * Create a predefined schema from a configuration element.
	 * 
	 * @param element the configuration element
	 * @param elementId the element ID
	 */
	public PredefinedSchemaImpl(IConfigurationElement element, String elementId) {
		this.element = element;
		this.elementId = elementId;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset#getName()
	 */
	@Override
	public String getName() {
		return element.getAttribute("name");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset#getDescription()
	 */
	@Override
	public String getDescription() {
		return element.getAttribute("description");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset#getLocation()
	 */
	@Override
	public LocatableInputSupplier<? extends InputStream> getLocation() {
		IConfigurationElement[] children = element.getChildren("uri");

		if (children != null && children.length > 0) {
			IConfigurationElement uri = children[0];
			URI location = URI.create(uri.getAttribute("value"));
			return new DefaultInputSupplier(location);
		}

		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset#getIconURL()
	 */
	@Override
	public URL getIconURL() {
		String icon = element.getAttribute("icon");
		return getURL(icon);
	}

	private URL getURL(String resource) {
		if (resource != null && !resource.isEmpty()) {
			String contributor = element.getDeclaringExtension().getContributor().getName();
			Bundle bundle = Platform.getBundle(contributor);

			if (bundle != null) {
				return bundle.getResource(resource);
			}
		}

		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset#getId()
	 */
	@Override
	public String getId() {
		return elementId;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset#getTag()
	 */
	@Override
	public String getTag() {
		return element.getAttribute("tag");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset#getVersion()
	 */
	@Override
	public String getVersion() {
		return element.getAttribute("version");
	}

	@Override
	public String getCategoryId() {
		String catId = element.getAttribute("category");
		// check if category actually exists
		SchemaCategory cat = SchemaCategoryExtension.getInstance().get(catId);
		if (cat != null) {
			return catId;
		}
		else {
			return null;
		}
	}

}
