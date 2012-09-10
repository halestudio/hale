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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;

/**
 * Factory for I/O action related project files
 * 
 * @author Simon Templer
 */
public class ActionFileFactory implements ProjectFileFactory {

	private final IConfigurationElement conf;

	/**
	 * Create a factory based on the given configuration
	 * 
	 * @param element the configuration element
	 */
	public ActionFileFactory(IConfigurationElement element) {
		this.conf = element;
	}

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public String getId() {
		return conf.getAttribute("name");
	}

	/**
	 * @see ProjectFileFactory#createProjectFile()
	 */
	@Override
	public ProjectFile createProjectFile() {
		String loadActionId = null;
		String loadProviderId = null;
		Map<String, String> loadParameters = new HashMap<String, String>();
		IConfigurationElement[] load = conf.getChildren("load");
		if (load != null && load.length > 0) {
			loadActionId = load[0].getAttribute("action");
			loadProviderId = load[0].getAttribute("provider");
			addParameters(loadParameters, load[0]);
		}

		checkNotNull(loadActionId, "Action ID for loading the project file not specified");

		String saveActionId = null;
		String saveProviderId = null;
		Map<String, String> saveParameters = new HashMap<String, String>();
		IConfigurationElement[] save = conf.getChildren("save");
		if (save != null && save.length > 0) {
			saveActionId = save[0].getAttribute("action");
			saveProviderId = save[0].getAttribute("provider");
			addParameters(saveParameters, save[0]);
		}

		checkNotNull(saveActionId, "Action ID for saving the project file not specified");

		return new ActionProjectFile(loadActionId, loadProviderId, loadParameters, saveActionId,
				saveProviderId, saveParameters);
	}

	private void addParameters(Map<String, String> parameterMap, IConfigurationElement conf) {
		IConfigurationElement[] parameters = conf.getChildren("parameter");
		for (IConfigurationElement parameter : parameters) {
			parameterMap.put(parameter.getAttribute("name"), parameter.getAttribute("value"));
		}
	}

}
