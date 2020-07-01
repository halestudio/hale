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

package eu.esdihumboldt.cst.functions.geometric.test.join

import javax.xml.namespace.QName

import org.w3c.dom.Element

import org.locationtech.jts.geom.Geometry

import eu.esdihumboldt.cst.functions.geometric.join.SpatialJoinParameter
import eu.esdihumboldt.cst.functions.geometric.join.SpatialJoinParameterType
import eu.esdihumboldt.cst.functions.geometric.join.SpatialJoinParameter.SpatialJoinCondition
import eu.esdihumboldt.cst.functions.geometric.join.SpatialRelationEvaluator.StandardRelation
import eu.esdihumboldt.hale.common.align.io.LoadAlignmentContext
import eu.esdihumboldt.hale.common.align.io.impl.internal.LoadAlignmentContextImpl
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition

/**
 * Tests for {@link SpatialJoinParameter}
 * 
 * @author Florian Esser
 */
@SuppressWarnings("restriction")
class SpatialJoinParameterTest extends GroovyTestCase {

	void testToFrom() {
		// create the schema
		Schema schema = new SchemaBuilder().schema {
			BaseType {
				id(Long)
				geom1(Geometry)
			}
			JoinType {
				id(Long)
				geom2(Geometry)
			}
		}

		List<TypeEntityDefinition> types = []
		for (TypeDefinition entDef : schema.getTypes()) {
			types.add(new TypeEntityDefinition(entDef, SchemaSpaceID.SOURCE, null));
		}

		SpatialJoinParameterType paramType = new SpatialJoinParameterType()
		TypeDefinition baseType = schema.getType(new QName("BaseType"));
		TypeEntityDefinition baseTypeEntity = new TypeEntityDefinition(baseType, SchemaSpaceID.SOURCE, null);
		TypeDefinition joinType = schema.getType(new QName("JoinType"));
		TypeEntityDefinition joinTypeEntity = new TypeEntityDefinition(joinType, SchemaSpaceID.SOURCE, null);

		PropertyEntityDefinition baseGeom = AlignmentUtil.getChild(baseTypeEntity, new QName("geom1"));
		PropertyEntityDefinition joinGeom = AlignmentUtil.getChild(joinTypeEntity, new QName("geom2"));
		SpatialJoinCondition condition = new SpatialJoinCondition(baseGeom, joinGeom, StandardRelation.CONTAINS.name());
		SpatialJoinParameter param = new SpatialJoinParameter(types, Collections.singleton(condition))

		LoadAlignmentContext context = new LoadAlignmentContextImpl(sourceTypes: schema);

		Element dom = paramType.toDOM(param)
		SpatialJoinParameter deserialized = paramType.fromDOM(dom, context)
		assertNotNull deserialized
		assertEquals param.types, deserialized.types
		assertEquals param.conditions, deserialized.conditions
	}
}
