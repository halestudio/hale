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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.extension.ProjectFileFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Factory for I/O action related project files
 * 
 * @author Simon Templer
 */
public class ActionFileFactory implements ProjectFileFactory {

	private final IConfigurationElement conf;

	private final ServiceProvider serviceProvider;

	/**
	 * Create a factory based on the given configuration
	 * 
	 * @param element the configuration element
	 * @param serviceProvider the service provider to use for I/O advisors
	 */
	public ActionFileFactory(IConfigurationElement element, ServiceProvider serviceProvider) {
		this.conf = element;
		this.serviceProvider = serviceProvider;
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
		Map<String, Value> loadParameters = new HashMap<String, Value>();
		IConfigurationElement[] load = conf.getChildren("load");
		if (load != null && load.length > 0) {
			loadActionId = load[0].getAttribute("action");
			loadProviderId = load[0].getAttribute("provider");
			addParameters(loadParameters, load[0]);
		}

		checkNotNull(loadActionId, "Action ID for loading the project file not specified");

		String saveActionId = null;
		String saveProviderId = null;
		Map<String, Value> saveParameters = new HashMap<String, Value>();
		IConfigurationElement[] save = conf.getChildren("save");
		if (save != null && save.length > 0) {
			saveActionId = save[0].getAttribute("action");
			saveProviderId = save[0].getAttribute("provider");
			addParameters(saveParameters, save[0]);
		}

		checkNotNull(saveActionId, "Action ID for saving the project file not specified");

		return new ActionProjectFile(loadActionId, loadProviderId, loadParameters, saveActionId,
				saveProviderId, saveParameters, serviceProvider);
	}

	private void addParameters(Map<String, Value> parameterMap, IConfigurationElement conf) {
		IConfigurationElement[] parameters = conf.getChildren("parameter");
		for (IConfigurationElement parameter : parameters) {
			// only supporting simple string parameters
			parameterMap.put(parameter.getAttribute("name"),
					Value.of(parameter.getAttribute("value")));
		}
	}

}
