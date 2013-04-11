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

package eu.esdihumboldt.hale.io.gml.writer;

import java.text.MessageFormat;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.CityGMLConstants;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.AbstractTypeMatcher;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.PathElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Instance writer for CityGML schemas, using CityModel as container, with
 * cityObjectMembers.
 * 
 * @author Simon Templer
 */
public class CityGMLInstanceWriter extends GmlInstanceWriter implements CityGMLConstants {

	/**
	 * @see StreamGmlWriter#findDefaultContainter(XmlIndex, IOReporter)
	 */
	@Override
	protected XmlElement findDefaultContainter(XmlIndex targetIndex, IOReporter reporter) {
		// find CityModel element as root

		for (XmlElement element : targetIndex.getElements().values()) {
			QName name = element.getName();

			if (CITY_MODEL_ELEMENT.equals(name.getLocalPart())
					&& name.getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)) {
				return element;
			}
		}

		throw new IllegalStateException(MessageFormat.format(
				"Element {0} not found in the schema.", CITY_MODEL_ELEMENT));
	}

	/**
	 * @see StreamGmlWriter#findMemberAttribute(TypeDefinition, QName,
	 *      TypeDefinition)
	 */
	@Override
	protected DefinitionPath findMemberAttribute(TypeDefinition container, QName containerName,
			final TypeDefinition memberType) {
		AbstractTypeMatcher<TypeDefinition> matcher = new AbstractTypeMatcher<TypeDefinition>() {

			@Override
			protected DefinitionPath matchPath(TypeDefinition type, TypeDefinition matchParam,
					DefinitionPath path) {
				PathElement firstProperty = null;
				for (PathElement step : path.getSteps()) {
					if (step.isProperty()) {
						firstProperty = step;
						break;
					}
				}

				if (firstProperty != null
						&& firstProperty.getName().getLocalPart()
								.equals(CITY_OBJECT_MEMBER_ELEMENT) && type.equals(memberType)) {
					return path;
				}

				return null;
			}
		};

		// candidate match
		List<DefinitionPath> candidates = matcher.findCandidates(container, containerName, true,
				memberType);
		if (candidates != null && !candidates.isEmpty()) {
			return candidates.get(0); // FIXME how to decide between candidates?
		}

		return null;
	}

}
