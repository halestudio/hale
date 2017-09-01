/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.xsd.constraint.factory;

import java.util.Collection;

import javax.xml.namespace.QName;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Value constraint factory for {@link XmlElements}.
 * 
 * @author Simon Templer
 */
public class XmlElementsFactory implements ValueConstraintFactory<XmlElements> {

	private static final ALogger log = ALoggerFactory.getLogger(XmlElementsFactory.class);

	@Override
	public Value store(XmlElements constraint, TypeReferenceBuilder refBuilder) throws Exception {
		Collection<? extends XmlElement> elements = constraint.getElements();
		if (elements == null) {
			return null;
		}

		ValueList result = new ValueList();

		for (XmlElement element : elements) {
			result.add(elementToValue(element, refBuilder));
		}

		return result.toValue();
	}

	private Value elementToValue(XmlElement element,
			@SuppressWarnings("unused") TypeReferenceBuilder refBuilder) {
		ValueProperties result = new ValueProperties();

		result.put("name", Value.complex(element.getName()));
		if (element.getSubstitutionGroup() != null) {
			result.put("substitutionGroup", Value.complex(element.getSubstitutionGroup()));
		}
		// Not needed: type is always the same as the associated definition
//		if (element.getType() != null) {
//			Optional<Value> ref = refBuilder.createReference(element.getType());
//			if (ref.isPresent()) {
//				result.put("type", ref.get());
//			}
//			else {
//				// XXX how to deal with it?
//				log.error("Could not determine reference for type " + element.getType());
//			}
//		}

		return result.toValue();
	}

	@Override
	public XmlElements restore(Value value, Definition<?> definition, TypeResolver typeResolver,
			ClassResolver classResolver) throws Exception {
		XmlElements result = new XmlElements();

		ValueList list = value.as(ValueList.class);
		if (list != null) {
			for (Value val : list) {
				XmlElement element = valueToElement(val, definition, typeResolver);
				if (element != null) {
					result.addElement(element);
				}
			}
		}

		return result;
	}

	private XmlElement valueToElement(Value val, Definition<?> definition,
			@SuppressWarnings("unused") TypeResolver typeResolver) {
		ValueProperties props = val.as(ValueProperties.class);
		if (props != null) {
			QName name = props.getSafe("name").as(QName.class);
			if (name != null) {
				QName substitutionGroup = props.getSafe("substitutionGroup").as(QName.class);

				TypeDefinition type = null;
				if (definition instanceof TypeDefinition) {
					type = (TypeDefinition) definition;
				}
				else {
					log.error("Wrong definition for XmlElements constraint: " + definition);
				}

				// Not needed: type is always the same as the associated
				// definition
//				Value typeRef = props.get("type");
//				if (typeRef != null) {
//					Optional<TypeDefinition> maybeType = typeResolver.resolve(typeRef);
//					if (maybeType.isPresent()) {
//						type = maybeType.get();
//					}
//				}

				return new XmlElement(name, type, substitutionGroup);
			}
		}

		return null;
	}

}
