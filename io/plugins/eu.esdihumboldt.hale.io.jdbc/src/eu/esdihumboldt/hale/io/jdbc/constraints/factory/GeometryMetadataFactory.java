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

package eu.esdihumboldt.hale.io.jdbc.constraints.factory;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.io.jdbc.constraints.GeometryMetadata;

/**
 * Value conversion for geometry metadata constraint.
 * 
 * @author Simon Templer
 */
public class GeometryMetadataFactory implements ValueConstraintFactory<GeometryMetadata> {

	private static final String NAME_SRS = "srs";
	private static final String NAME_DIMENSION = "dimension";
	private static final String NAME_SRS_TEXT = "srsText";
	private static final String NAME_AUTH_NAME = "authName";

	@Override
	public Value store(GeometryMetadata constraint, Map<TypeDefinition, String> typeIndex)
			throws Exception {
		ValueProperties props = new ValueProperties();

		String srs = constraint.getSrs();
		if (srs != null) {
			props.put(NAME_SRS, Value.of(srs));
		}

		props.put(NAME_DIMENSION, Value.of(constraint.getDimension()));

		String srsText = constraint.getSrsText();
		if (srsText != null) {
			props.put(NAME_SRS_TEXT, Value.of(srsText));
		}

		String auth_name = constraint.getAuthName();
		if (auth_name != null) {
			props.put(NAME_AUTH_NAME, Value.of(auth_name));
		}

		return props.toValue();
	}

	@Override
	public GeometryMetadata restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		ValueProperties props = value.as(ValueProperties.class);
		if (props != null) {
			String srs = props.getSafe(NAME_SRS).as(String.class);
			int dimension = props.getSafe(NAME_DIMENSION).as(Integer.class, 0);
			String srsText = props.getSafe(NAME_SRS_TEXT).as(String.class);
			String auth_name = props.getSafe(NAME_AUTH_NAME).as(String.class);

			return new GeometryMetadata(srs, dimension, srsText, auth_name);
		}
		else {
			return new GeometryMetadata();
		}
	}
}
