/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.xsd.anytype;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultGroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.PropertyTypeOverrideProperty;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Helper methods for custom type content.
 * 
 * @author Simon Templer
 */
public class CustomTypeContentHelper {

	private static final ALogger log = ALoggerFactory.getLogger(CustomTypeContentHelper.class);

	/**
	 * Apply custom type configurations to the given schema.
	 * 
	 * @param index the XML schema
	 * @param config the custom type configurations
	 */
	public static void applyConfigurations(XmlIndex index, CustomTypeContentConfiguration config) {
		for (CustomTypeContentAssociation assoc : config.getAssociations()) {
			applyConfiguration(index, assoc);
		}
	}

	/**
	 * Apply a custom type configuration to the given schema.
	 * 
	 * @param index the XML schema
	 * @param association the custom type configuration for an individual
	 *            property
	 */
	public static void applyConfiguration(XmlIndex index,
			CustomTypeContentAssociation association) {
		CustomTypeContent config = association.getConfig();

		// property identified by a list of qualified names
		List<QName> property = association.getProperty();
		PropertyDefinition propDef = null;
		DefinitionGroup propParent = null;
		if (property != null && property.size() > 1) {
			QName typeName = property.get(0);
			TypeDefinition type = index.getType(typeName);
			if (type != null) {
				LinkedList<QName> nameQueue = new LinkedList<>(
						property.subList(1, property.size()));
				Definition<?> parent = null;
				Definition<?> child = type;
				while (!nameQueue.isEmpty() && child != null) {
					parent = child;
					QName name = nameQueue.pollFirst();
					child = DefinitionUtil.getChild(parent, name);
				}
				if (nameQueue.isEmpty() && child instanceof PropertyDefinition) {
					propDef = (PropertyDefinition) child;
					if (parent instanceof TypeDefinition) {
						propParent = (DefinitionGroup) parent;
					}
					else if (parent instanceof ChildDefinition<?>) {
						ChildDefinition<?> pc = (ChildDefinition<?>) parent;
						if (pc.asProperty() != null) {
							propParent = pc.asProperty().getPropertyType();
						}
						else if (pc.asGroup() != null) {
							propParent = pc.asGroup();
						}
					}
					else {
						log.error("Illegal parent for custom type content property");
						return;
					}
				}
				else {
					log.warn(
							"Cannot apply custom type content configuration due to invalid property path");
					return;
				}
			}
			else {
				log.warn(
						"Cannot apply custom type content configuration due because the type {} starting the property path could not be found",
						typeName);
				return;
			}
		}
		else {
			log.warn("Cannot apply custom type content configuration due to missing property path");
			return;
		}

		switch (config.getMode()) {
		case simple:
			applySimpleMode(propDef, propParent, config);
			break;
		case elements:
			applyElementsMode(propDef, propParent, config, index);
			break;
		default:
			log.error("Unrecognized custom type content mode {}", config.getMode().name());
		}

	}

	private static void applyElementsMode(PropertyDefinition propDef, DefinitionGroup propParent,
			CustomTypeContent config, XmlIndex index) {
		// build new property type based on config
		DefaultTypeDefinition type = new DefaultTypeDefinition(
				new QName(propDef.getIdentifier(), "customElementsContentType"), false);
		type.setConstraint(MappableFlag.DISABLED);
		DefaultGroupPropertyDefinition choice = new DefaultGroupPropertyDefinition(
				new QName(propDef.getIdentifier(), "customElementsContentChoice"), type, false);
		choice.setConstraint(new DisplayName("elements"));
		choice.setConstraint(ChoiceFlag.ENABLED);
		choice.setConstraint(Cardinality.CC_ANY_NUMBER);

		for (QName elementName : config.getElements()) {
			XmlElement element = index.getElements().get(elementName);
			if (element != null) {
				DefaultPropertyDefinition elementProp = new DefaultPropertyDefinition(elementName,
						choice, element.getType());
				elementProp.setConstraint(Cardinality.CC_EXACTLY_ONCE);
				elementProp.setConstraint(NillableFlag.DISABLED);
			}
			else {
				log.error("Element {} could not be found when creating custom type content",
						elementName);
			}
		}

		replaceTypeForProperty(propDef, propParent, type);
	}

	@SuppressWarnings("unused")
	private static void applySimpleMode(PropertyDefinition propDef, DefinitionGroup propParent,
			CustomTypeContent config) {
		// XXX currently no modification needed

//		replaceTypeForProperty(propDef, propParent, type);
	}

	private static void replaceTypeForProperty(PropertyDefinition propDef,
			DefinitionGroup propParent, TypeDefinition newPropertyType) {
		PropertyDefinition newProperty;
		if (propDef instanceof PropertyTypeOverrideProperty) {
			newProperty = new PropertyTypeOverrideProperty(
					((PropertyTypeOverrideProperty) propDef).getDecoratedProperty(),
					newPropertyType);
		}
		else {
			newProperty = new PropertyTypeOverrideProperty(propDef, newPropertyType);
		}

		if (propParent instanceof DefaultTypeDefinition) {
			DefaultTypeDefinition type = (DefaultTypeDefinition) propParent;
			type.overrideChild(newProperty);
		}
//		else if (propParent instanceof DefaultGroupPropertyDefinition) {
//			// TODO
//		}
		else {
			log.error(
					"Could not update custom content property because of unsupported parent definition group");
		}
	}
}
