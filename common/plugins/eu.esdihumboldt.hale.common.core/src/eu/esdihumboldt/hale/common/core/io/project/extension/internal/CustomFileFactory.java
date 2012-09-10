/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.io.project.extension.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
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
