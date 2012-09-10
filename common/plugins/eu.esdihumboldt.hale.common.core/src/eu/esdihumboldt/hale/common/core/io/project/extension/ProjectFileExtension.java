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
