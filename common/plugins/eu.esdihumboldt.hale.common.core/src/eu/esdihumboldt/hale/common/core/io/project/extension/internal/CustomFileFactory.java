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

package eu.esdihumboldt.hale.common.core.io.project.extension.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Factory for a custom {@link ProjectFile}
 * 
 * @author Simon Templer
 */
public class CustomFileFactory implements ProjectFileFactory {

	private final String name;

	private final Class<? extends ProjectFile> fileClass;

	/**
	 * Create a factory based on the given configuration
	 * 
	 * @param element the configuration element
	 */
	@SuppressWarnings("unchecked")
	public CustomFileFactory(IConfigurationElement element) {
		this.name = element.getAttribute("name");
		this.fileClass = (Class<? extends ProjectFile>) ExtensionUtil.loadClass(element, "class");
	}

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public String getId() {
		return name;
	}

	/**
	 * @see ProjectFileFactory#createProjectFile()
	 */
	@Override
	public ProjectFile createProjectFile() {
		try {
			return fileClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed creating project file instance", e);
		}
	}

}
