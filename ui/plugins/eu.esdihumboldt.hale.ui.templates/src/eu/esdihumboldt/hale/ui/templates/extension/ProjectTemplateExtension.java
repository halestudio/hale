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

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.ui.templates.extension.internal.ProjectTemplateImpl;

/**
 * Extension point for predefined schemas.
 * 
 * @author Simon Templer
 */
public class ProjectTemplateExtension extends IdentifiableExtension<ProjectTemplate> {

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.templates";

	private static ProjectTemplateExtension instance;

	/**
	 * Get the extension singleton instance.
	 * 
	 * @return the extension instance
	 */
	public static final ProjectTemplateExtension getInstance() {
		synchronized (ProjectTemplateExtension.class) {
			if (instance == null) {
				instance = new ProjectTemplateExtension();
			}
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private ProjectTemplateExtension() {
		super(ID);
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	@Override
	protected ProjectTemplate create(String elementId, IConfigurationElement element) {
		if ("project".equals(element.getName())) {
			return new ProjectTemplateImpl(element, elementId);
		}

		return null;
	}

}
