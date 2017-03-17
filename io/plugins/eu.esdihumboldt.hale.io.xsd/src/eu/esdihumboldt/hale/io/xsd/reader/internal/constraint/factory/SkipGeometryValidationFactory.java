/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.factory;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.SkipGeometryValidation;

/**
 * Converts {@link SkipGeometryValidation} constraints to {@link Value}s and
 * vice versa.
 * 
 * @author Simon Templer
 */
public class SkipGeometryValidationFactory
		implements ValueConstraintFactory<SkipGeometryValidation> {

	@Override
	public Value store(SkipGeometryValidation constraint, TypeReferenceBuilder typeIndex)
			throws Exception {
		// there is no configuration, either it is set or not
		return Value.of(true);
	}

	@Override
	public SkipGeometryValidation restore(Value value, Definition<?> definition,
			TypeResolver typeIndex, ClassResolver resolver) throws Exception {
		return SkipGeometryValidation.getInstance();
	}

}
