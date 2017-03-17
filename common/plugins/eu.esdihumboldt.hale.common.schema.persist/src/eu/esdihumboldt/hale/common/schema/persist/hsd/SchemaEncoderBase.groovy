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

package eu.esdihumboldt.hale.common.schema.persist.hsd;

import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintExtension
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintFactoryDescriptor
import eu.esdihumboldt.util.Pair
import groovy.transform.CompileStatic


/**
 * Base class for schema encoders providing some helpers.
 * 
 * @author Simon Templer
 */
@CompileStatic
abstract class SchemaEncoderBase {

	static final Comparator<String> nullStringComparator = {
		String s1, String s2 ->
		if (s1 == s2) {
			0
		}
		else if (s1 == null) {
			-1
		}
		else if (s2 == null) {
			1
		}
		else {
			s1 <=> s2
		}
	} as Comparator

	/**
	 * Sort schemas.
	 * @param schemas the schemas to sort
	 * @return the sorted schemas
	 */
	Iterable<Schema> sortSchemas(Iterable<Schema> schemas) {
		schemas.sort(false) { Schema s1, Schema s2 ->
			int compared = nullStringComparator.compare(s1.namespace, s2.namespace)
			if (!compared) {
				compared = nullStringComparator.compare(s1.location?.toString(), s2.location?.toString())
			}
			compared
		}
	}

	/**
	 * Get all types from the schema.
	 * @param schema the schema
	 * @return the list of types (sorted)
	 */
	List<TypeDefinition> getSchemaTypes(Schema schema) {
		def types = []
		types.addAll(schema.types)
		// sort to have a reproducible order (e.g. for versioning)
		types.sort(true)
	}

	/**
	 * Get constraints for a definition in their value representation.
	 * @param d the definition
	 * @param typeIndex the type index
	 * @return the list of constraints, each a pair of the constraint ID and the corresponding value
	 */
	List<Pair<String, Value>> getConstraints(Definition<?> d, TypeReferenceBuilder refBuilder) {
		// constraints
		Collection<Pair<String, Value>> constraints = d.explicitConstraints.findResults { def constraint ->
			// get value constraint factory, if possible
			ValueConstraintFactoryDescriptor desc = ValueConstraintExtension.INSTANCE.getForConstraint(constraint)
			if (desc != null && desc.factory != null) {
				// determine value representation of constraint
				Value value = desc.factory.store(constraint, refBuilder)
				String id = desc.id
				new Pair<String, Value>(id, value)
			}
			else {
				(Pair<String, Value>)null
			}
		}

		constraints.sort(false) { Pair<String, Value> p1, Pair<String, Value> p2 ->
			nullStringComparator.compare(p1.first, p2.first)
		}
	}

}
