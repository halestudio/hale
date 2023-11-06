/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json.internal.schema;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * Context for building a schema for Json data. Responsible for managing shared
 * types, e.g. for primitive values.
 * 
 * @author Simon Templer
 */
public class SchemaBuilderContext {

	private final String defaultNamespace;
	private final TypeIndex sharedTypes;

	private final Map<QName, TypeDefinition> commonTypes = new HashMap<>();

	/**
	 * 
	 * @param defaultNamespace the namespace to use for common types
	 * @param sharedTypes the shared types (e.g. from other loaded schemas) for
	 *            type reuse
	 */
	public SchemaBuilderContext(String defaultNamespace, TypeIndex sharedTypes) {
		super();
		this.defaultNamespace = defaultNamespace;
		this.sharedTypes = sharedTypes;
	}

	/**
	 * Get a type representing a Json primitive type.
	 * 
	 * @param valueType the desired Json type
	 * @return the type definition for the Json type
	 */
	public TypeDefinition getType(JsonValueType valueType) {
		QName name = new QName(defaultNamespace, valueType.typeName);

		TypeDefinition result = sharedTypes != null ? sharedTypes.getType(name) : null;

		if (result == null) {
			result = commonTypes.computeIfAbsent(name, n -> {
				DefaultTypeDefinition type = new DefaultTypeDefinition(n);

				type.setConstraint(MappableFlag.DISABLED);

				type.setConstraint(HasValueFlag.ENABLED);
				type.setConstraint(Binding.get(valueType.binding));

				return type;
			});
		}

		return result;
	}

	/**
	 * Get a type representing a GeoJson geometry type.
	 * 
	 * @param geomType the desired geometry type
	 * @return the type definition for the geometry type
	 */
	public TypeDefinition getGeometryType(Class<? extends Geometry> geomType) {
		QName name = new QName(defaultNamespace, geomType.getSimpleName() + "GeometryType");

		TypeDefinition result = sharedTypes != null ? sharedTypes.getType(name) : null;

		if (result == null) {
			result = commonTypes.computeIfAbsent(name, n -> {
				DefaultTypeDefinition type = new DefaultTypeDefinition(n);

				type.setConstraint(MappableFlag.DISABLED);

				type.setConstraint(HasValueFlag.ENABLED);
				type.setConstraint(GeometryType.get(geomType));
				type.setConstraint(Binding.get(GeometryProperty.class));

				return type;
			});
		}

		return result;
	}

	/**
	 * 
	 * @return the common types that were created during schema building
	 */
	public Iterable<TypeDefinition> getCommonTypes() {
		return commonTypes.values();
	}

}
