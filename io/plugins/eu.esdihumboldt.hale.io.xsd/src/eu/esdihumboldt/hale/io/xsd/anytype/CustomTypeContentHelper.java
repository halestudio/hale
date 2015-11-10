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
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
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
	public static void applyConfiguration(XmlIndex index, CustomTypeContentAssociation association) {
		CustomTypeContent config = association.getConfig();

		// property identified by a list of qualified names
		List<QName> property = association.getProperty();
		PropertyDefinition propDef = null;
		if (property != null && property.size() > 1) {
			QName typeName = property.get(0);
			TypeDefinition type = index.getType(typeName);
			if (type != null) {
				LinkedList<QName> nameQueue = new LinkedList<>(property.subList(1, property.size()));
				Definition<?> parent = null;
				Definition<?> child = type;
				while (!nameQueue.isEmpty() && child != null) {
					parent = child;
					QName name = nameQueue.pollFirst();
					child = DefinitionUtil.getChild(parent, name);
				}
				if (nameQueue.isEmpty() && child instanceof PropertyDefinition) {
					propDef = (PropertyDefinition) child;
				}
				else {
					log.warn("Cannot apply custom type content configuration due to invalid property path");
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
			applySimpleMode(propDef, config);
		case elements:
			applyElementsMode(propDef, config);
		default:
			log.error("Unrecognized custom type content mode {}", config.getMode().name());
		}

	}

	private static void applyElementsMode(PropertyDefinition propDef, CustomTypeContent config) {
		// TODO Auto-generated method stub

	}

	private static void applySimpleMode(PropertyDefinition propDef, CustomTypeContent config) {
		// XXX currently no modification needed
	}
}
