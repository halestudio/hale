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

package eu.esdihumboldt.hale.common.schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Basic classification for definitions
 * 
 * @author Simon Templer
 * @since 2.2
 */
public enum Classification {
	/** Abstract feature type */
	ABSTRACT_FT("abstractFeatureType"),
	/** Concrete feature type */
	CONCRETE_FT("concreteFeatureType"),
	/** Abstract complex type */
	ABSTRACT_TYPE("abstractType"),
	/** Complex type */
	CONCRETE_TYPE("concreteType"),
	/** Numeric property */
	NUMERIC_PROPERTY("numericProperty"),
	/** String property */
	STRING_PROPERTY("stringProperty"),
	/** Complex property */
	COMPLEX_PROPERTY("complexProperty"),
	/** Geometric property */
	GEOMETRIC_PROPERTY("geometricProperty"),
	/** Group */
	GROUP("group"),
	/** Choice */
	CHOICE("choice"),
	/** Unknown */
	UNKNOWN("unknown");

	private final String code;

	Classification(String code) {
		this.code = code;
	}

	/**
	 * @return the associated code string
	 */
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code;
	}

	/**
	 * Determine the classification for a definition
	 * 
	 * @param def the definition
	 * @return the classification for the definition
	 */
	public static Classification getClassification(Definition<?> def) {
		if (def instanceof GroupPropertyDefinition) {
			if (((GroupPropertyDefinition) def).getConstraint(ChoiceFlag.class).isEnabled()) {
				return CHOICE;
			}

			return GROUP;
		}
		else if (def instanceof PropertyDefinition) {
			// use binding/constraints to determine type
			PropertyDefinition property = (PropertyDefinition) def;

			Class<?> binding = property.getPropertyType().getConstraint(Binding.class).getBinding();

			// geometry binding allowed also for types where HasValue is not
			// enabled (e.g. XML types where geometries are aggregated)
			GeometryType geometryType = property.getPropertyType()
					.getConstraint(GeometryType.class);
			if (geometryType.isGeometry()) {
				return GEOMETRIC_PROPERTY;
			}

			if (property.getPropertyType().getConstraint(HasValueFlag.class).isEnabled()) {
				// simple type
				if (Number.class.isAssignableFrom(binding) || Date.class.isAssignableFrom(binding)
						|| LocalDate.class.isAssignableFrom(binding)
						|| Instant.class.isAssignableFrom(binding)) {
					return NUMERIC_PROPERTY;
				}

				// default to string for simple types
				return STRING_PROPERTY;
			}
			else {
				// complex type
				return COMPLEX_PROPERTY;
			}
		}
		else if (def instanceof TypeDefinition) {
			TypeDefinition type = (TypeDefinition) def;
			if (isAbstractFeatureType(type)) {
				return ABSTRACT_FT;
			}
			else {
				TypeDefinition superType = type.getSuperType();
				while (superType != null) {
					if (isAbstractFeatureType(superType)) {
						return (type.getConstraint(AbstractFlag.class).isEnabled()) ? (ABSTRACT_FT)
								: (CONCRETE_FT);
					}

					superType = superType.getSuperType();
				}

				return (type.getConstraint(AbstractFlag.class).isEnabled()) ? (ABSTRACT_TYPE)
						: (CONCRETE_TYPE);
			}
		}

		return UNKNOWN;
	}

	/**
	 * Determines if a type is the AbstractFeatureType. This is related to GML.
	 * 
	 * @param type the type definition
	 * @return if the type is the AbstractFeatureType
	 */
	private static boolean isAbstractFeatureType(TypeDefinition type) {
		// XXX not really nice
		return type.getName().getLocalPart().equals("AbstractFeatureType")
				&& type.getName().getNamespaceURI().startsWith("http://www.opengis.net/")
				&& type.getName().getNamespaceURI().contains("gml");
	}
}
