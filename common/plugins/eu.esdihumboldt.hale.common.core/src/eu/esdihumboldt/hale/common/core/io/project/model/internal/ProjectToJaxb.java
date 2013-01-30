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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ComplexPropertyType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.IOConfigurationType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ObjectFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ProjectFileType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ProjectType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.PropertyType;

/**
 * Convert a {@link Project} to a JAXB {@link ProjectType}.
 * 
 * @author Simon Templer
 */
public class ProjectToJaxb {

	private static ObjectFactory of = new ObjectFactory();

	private static DatatypeFactory df = null;
	static {
		try {
			df = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException dce) {
			throw new IllegalStateException("Exception while obtaining DatatypeFactory instance",
					dce);
		}
	}

	/**
	 * Convert the given project to the corresponding JAXB type.
	 * 
	 * @param project the project to convert
	 * @return the converted project
	 */
	public static ProjectType convert(Project project) {
		ProjectType result = new ProjectType();

		result.setAuthor(project.getAuthor());
		result.setCreated(toXMLCalendar(project.getCreated()));
		result.setDescription(project.getDescription());
		result.setModified(toXMLCalendar(project.getModified()));
		result.setName(project.getName());
		result.setSaveConfig(toIOConfigurationType(project.getSaveConfiguration()));
		result.setVersion(project.getHaleVersion().toString());

		// resources
		for (IOConfiguration resource : project.getResources()) {
			result.getResource().add(toIOConfigurationType(resource));
		}

		// project files
		for (ProjectFileInfo file : project.getProjectFiles()) {
			result.getFile().add(toProjectFileType(file));
		}

		// properties
		for (Entry<String, String> property : project.getProperties().entrySet()) {
			Object p = createProperty(property.getKey(), property.getValue());
			if (p instanceof PropertyType) {
				result.getAbstractProperty().add(of.createProperty((PropertyType) p));
			}
			else if (p instanceof ComplexPropertyType) {
				result.getAbstractProperty().add(of.createComplexProperty((ComplexPropertyType) p));
			}
		}

		return result;
	}

	private static ProjectFileType toProjectFileType(ProjectFileInfo file) {
		ProjectFileType result = new ProjectFileType();

		result.setName(file.getName());
		result.setLocation(file.getLocation().toString());

		return result;
	}

	private static IOConfigurationType toIOConfigurationType(IOConfiguration config) {
		IOConfigurationType result = new IOConfigurationType();

		result.setActionId(config.getActionId());
		result.setName(config.getName());
		result.setProviderId(config.getProviderId());

		for (Entry<String, String> setting : config.getProviderConfiguration().entrySet()) {
			Object property = createProperty(setting.getKey(), setting.getValue());
			if (property instanceof PropertyType) {
				result.getAbstractSetting().add(of.createSetting((PropertyType) property));
			}
			else if (property instanceof ComplexPropertyType) {
				result.getAbstractSetting().add(
						of.createComplexSetting((ComplexPropertyType) property));
			}
		}

		return result;
	}

	private static Object createProperty(String name, Object value) {
		if (value instanceof String || value == null) {
			// string property value or null
			PropertyType result = new PropertyType();
			result.setName(name);
			result.setValue((String) value);
			return result;
		}
		else if (value instanceof Element) {
			// parameter is the DOM
			ComplexPropertyType result = new ComplexPropertyType();

			result.setName(name);
			result.setAny((Element) value);

			return result;
		}
		else {
			// complex value
			ComplexValueDefinition cvd = ComplexValueExtension.getInstance().getDefinition(
					value.getClass());
			if (cvd != null) {
				ComplexPropertyType result = new ComplexPropertyType();
				Element element = cvd.toDOM(value);
				result.setName(name);
				result.setAny(element);
				return result;
			}
			else {
				throw new IllegalStateException("No definition for complex parameter value found");
			}
		}
	}

	private static XMLGregorianCalendar toXMLCalendar(Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(date.getTime());
		return df.newXMLGregorianCalendar(gc);
	}

}
