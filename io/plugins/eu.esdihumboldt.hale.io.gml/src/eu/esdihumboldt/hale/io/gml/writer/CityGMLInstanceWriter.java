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

package eu.esdihumboldt.hale.io.gml.writer;

import java.text.MessageFormat;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.AbstractTypeMatcher;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.PathElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Instance writer for CityGML schemas, using CityModel as container, with
 * cityObjectMembers.
 * @author Simon Templer
 */
public class CityGMLInstanceWriter extends GmlInstanceWriter {
	
	/**
	 * 
	 */
	private static final String CITY_OBJECT_MEMBER_ELEMENT = "cityObjectMember";

	private static final String CITY_MODEL_ELEMENT = "CityModel";
	
	private static final String CITYGML_NAMESPACE_CORE = "http://www.opengis.net/citygml";

	/**
	 * @see StreamGmlWriter#findDefaultContainter(XmlIndex, IOReporter)
	 */
	@Override
	protected XmlElement findDefaultContainter(XmlIndex targetIndex,
			IOReporter reporter) {
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
	 * @see StreamGmlWriter#findMemberAttribute(TypeDefinition, QName, TypeDefinition)
	 */
	@Override
	protected DefinitionPath findMemberAttribute(TypeDefinition container,
			QName containerName, final TypeDefinition memberType) {
		AbstractTypeMatcher<TypeDefinition> matcher = new AbstractTypeMatcher<TypeDefinition>() {
			
			@Override
			protected DefinitionPath matchPath(TypeDefinition type,
					TypeDefinition matchParam, DefinitionPath path) {
				PathElement firstProperty = null; 
				for (PathElement step : path.getSteps()) {
					if (step.isProperty()) {
						firstProperty = step;
						break;
					}
				}
				
				if (firstProperty != null 
						&& firstProperty.getName().getLocalPart().equals(CITY_OBJECT_MEMBER_ELEMENT) 
						&& type.equals(memberType)) {
					return path;
				}
				
				return null;
			}
		};
		
		// candidate match
		List<DefinitionPath> candidates = matcher.findCandidates(container, 
				containerName, true, memberType);
		if (candidates != null && !candidates.isEmpty()) {
			return candidates.get(0); //FIXME how to decide between candidates?
		}
		
		return null;
	}

}
