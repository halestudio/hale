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

package eu.esdihumboldt.hale.ui.templates.extension.internal;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.templates.extension.ProjectTemplate;

/**
 * Project template descriptor based on a configuration element.
 * 
 * @author Simon Templer
 */
public class ProjectTemplateImpl implements ProjectTemplate {

	private final IConfigurationElement element;
	private final String elementId;

	/**
	 * Create a project template from a configuration element.
	 * 
	 * @param element the configuration element
	 * @param elementId the element ID
	 */
	public ProjectTemplateImpl(IConfigurationElement element, String elementId) {
		this.element = element;
		this.elementId = elementId;
	}

	@Override
	public String getName() {
		return element.getAttribute("name");
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getLocation() throws URISyntaxException {
		URI location = new URI(element.getAttribute("location"));
		return new DefaultInputSupplier(location);
	}

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

	@Override
	public String getId() {
		return elementId;
	}

}
