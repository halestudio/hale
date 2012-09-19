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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.project.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.common.core.io.project.extension.internal.ActionFileFactory;
import eu.esdihumboldt.hale.common.core.io.project.extension.internal.CustomFileFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Extension for {@link ProjectFile}s
 * 
 * @author Simon Templer
 */
public class ProjectFileExtension extends IdentifiableExtension<ProjectFileFactory> {

	/**
	 * Get the extension instance
	 * 
	 * @return the project file extension
	 */
	public static ProjectFileExtension getInstance() {
		if (instance == null) {
			instance = new ProjectFileExtension();
		}
		return instance;
	}

	/**
	 * The project extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.project";

	private static ProjectFileExtension instance;

	/**
	 * Default constructor
	 */
	private ProjectFileExtension() {
		super(ID);
	}

	/**
	 * @see IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "name";
	}

	/**
	 * @see IdentifiableExtension#create(String, IConfigurationElement)
	 */
	@Override
	protected ProjectFileFactory create(String elementId, IConfigurationElement element) {
		if (element.getName().equals("action-file")) {
			return new ActionFileFactory(element);
		}
		else if (element.getName().equals("custom-file")) {
			return new CustomFileFactory(element);
		}
		return null;
	}

}
