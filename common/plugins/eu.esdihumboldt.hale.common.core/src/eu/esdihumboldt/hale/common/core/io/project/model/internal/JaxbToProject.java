/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.project.model.internal;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.osgi.framework.Version;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.ElementValue;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ComplexPropertyType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ExportConfigurationType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.IOConfigurationType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ProjectFileType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ProjectType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ValueType;
import jakarta.xml.bind.JAXBElement;

/**
 * Converts a JAXB {@link ProjectType} to a {@link Project}.
 * 
 * @author Simon Templer
 */
public class JaxbToProject {

	/**
	 * Convert the given project.
	 * 
	 * @param project the project to convert
	 * @return the project model object
	 */
	public static Project convert(ProjectType project) {
		Project result = new Project();

		result.setAuthor(project.getAuthor());
		result.setCreated(toDate(project.getCreated()));
		result.setDescription(project.getDescription());
		result.setHaleVersion(toVersion(project.getVersion()));
		result.setModified(toDate(project.getModified()));
		result.setName(project.getName());

		result.setSaveConfiguration(toIOConfiguration(project.getSaveConfig()));

		for (IOConfigurationType resource : project.getResource()) {
			result.getResources().add(toIOConfiguration(resource));
		}

		for (ExportConfigurationType exportConfig : project.getExportConfig()) {
			String name = exportConfig.getName();
			if (name != null && !name.isEmpty()) {
				result.getExportConfigurations().put(name,
						toIOConfiguration(exportConfig.getConfiguration()));
			}
		}

		for (ProjectFileType file : project.getFile()) {
			result.getProjectFiles()
					.add(new ProjectFileInfo(file.getName(), URI.create(file.getLocation())));
		}

		for (JAXBElement<?> property : project.getAbstractProperty()) {
			Object value = property.getValue();
			if (value instanceof PropertyType) {
				addProperty(result.getProperties(), (PropertyType) value);
			}
			else if (value instanceof ComplexPropertyType) {
				addProperty(result.getProperties(), (ComplexPropertyType) value);
			}
		}

		return result;
	}

	/**
	 * Convert a JAXB representation to an {@link IOConfiguration}.
	 * 
	 * @param config the JAXB representation
	 * @return the I/O configuration
	 */
	public static IOConfiguration toIOConfiguration(IOConfigurationType config) {
		if (config == null) {
			return null;
		}

		IOConfiguration result = new IOConfiguration();

		result.setActionId(config.getActionId());
		result.setProviderId(config.getProviderId());

		for (JAXBElement<?> setting : config.getAbstractSetting()) {
			Object value = setting.getValue();
			if (value instanceof PropertyType) {
				addProperty(result.getProviderConfiguration(), (PropertyType) value);
			}
			else if (value instanceof ComplexPropertyType) {
				addProperty(result.getProviderConfiguration(), (ComplexPropertyType) value);
			}
		}

		// cache
		ValueType cache = config.getCache();
		if (cache != null) {
			Value value = Value.NULL;
			if (cache.getAny() != null) {
				value = new ElementValue(cache.getAny(), null);
			}
			else if (cache.getValue() != null) {
				value = Value.of(cache.getValue());
			}
			result.setCache(value);
		}

		return result;
	}

	private static void addProperty(Map<String, Value> properties, PropertyType value) {
		properties.put(value.getName(), Value.of(value.getValue()));
	}

	private static void addProperty(Map<String, Value> properties, ComplexPropertyType value) {
		properties.put(value.getName(), new ElementValue(value.getAny(), null));
	}

	private static Version toVersion(String version) {
		if (version == null) {
			return null;
		}

		return Version.parseVersion(version);
	}

	private static Date toDate(XMLGregorianCalendar date) {
		if (date == null) {
			return null;
		}

		return date.toGregorianCalendar().getTime();
	}

}
