/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.geometric.join;

import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Document
import org.w3c.dom.Element

import eu.esdihumboldt.cst.functions.geometric.join.SpatialJoinParameter.SpatialJoinCondition
import eu.esdihumboldt.hale.common.align.helper.EntityDefinitionComparator
import eu.esdihumboldt.hale.common.align.io.LoadAlignmentContext
import eu.esdihumboldt.hale.common.align.io.impl.DOMEntityDefinitionHelper
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.xml.XmlUtil
import groovy.xml.dom.DOMCategory

/**
 * Descriptor for conversion between DOM and SpatialJoinParameter.
 * 
 * @author Kai Schwierczek
 */
public class SpatialJoinParameterType implements ComplexValueType<SpatialJoinParameter, LoadAlignmentContext> {

	/**
	 * Spatial Join function namespace
	 */
	public static final String NAMESPACE = 'http://www.esdi-humboldt.eu/hale/join/spatial'

	private final EntityDefinitionComparator comparator = new EntityDefinitionComparator()

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#fromDOM(org.w3c.dom.Element)
	 */
	@Override
	public SpatialJoinParameter fromDOM(Element fragment, LoadAlignmentContext context) {
		assert context

		TypeIndex sIndex = context.sourceTypes
		SchemaSpaceID ssid = SchemaSpaceID.SOURCE

		use (DOMCategory) {
			// get type list
			// Cannot use fragment.'class' because that results in the class object of fragment.
			// getElementsByTagName is okay, because there are only direct 'class' children...
			def domTypes = fragment.getElementsByTagName('class')
			List<TypeEntityDefinition> types = new ArrayList<>(domTypes.size())
			domTypes.each { type ->
				types.add(DOMEntityDefinitionHelper.typeFromDOM(type, sIndex, ssid))
			}

			// get conditions
			def domConditions = fragment.'jp:condition'
			Set<SpatialJoinCondition> conditions = new HashSet<>(domConditions.size())
			domConditions.each { condition ->
				def domProperties = condition.'property'
				def relation = condition.'jp:relation'.text()
				conditions.add(new SpatialJoinCondition(
						DOMEntityDefinitionHelper.propertyFromDOM(domProperties[0], sIndex, ssid),
						DOMEntityDefinitionHelper.propertyFromDOM(domProperties[1], sIndex, ssid),
						relation))
			}

			return new SpatialJoinParameter(types, conditions)
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#toDOM(java.lang.Object)
	 */
	@Override
	public Element toDOM(SpatialJoinParameter value) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().newDocument()
		def result = doc.createElementNS(NAMESPACE, "jp:spatialjoin-parameter")
		value.types.each{ type ->
			def typeDom = DOMEntityDefinitionHelper.typeToDOM(type)
			result.appendChild(
					result.ownerDocument.adoptNode(typeDom)?:result.ownerDocument.importNode(typeDom, true))
		}
		value.conditions.sort(false) { SpatialJoinCondition c1, SpatialJoinCondition c2 ->
			int compared = comparator.compare(c1.baseProperty, c2.baseProperty)
			if (!compared) {
				compared = comparator.compare(c1.joinProperty, c2.joinProperty)
			}
			compared
		}.each { condition ->
			def conditionNode = doc.createElementNS(NAMESPACE, "jp:condition")
			result.appendChild(conditionNode)

			def baseProp = DOMEntityDefinitionHelper.propertyToDOM(condition.baseProperty)
			conditionNode.appendChild(
					conditionNode.ownerDocument.adoptNode(baseProp)?:conditionNode.ownerDocument.importNode(baseProp, true))

			def joinProp = DOMEntityDefinitionHelper.propertyToDOM(condition.joinProperty)
			conditionNode.appendChild(
					conditionNode.ownerDocument.adoptNode(joinProp)?:conditionNode.ownerDocument.importNode(joinProp, true))

			def b = NSDOMBuilder.newBuilder(jp: NAMESPACE)
			Element n = b 'jp:relation', condition.relation
			XmlUtil.append(conditionNode, n)
		}
		return result
	}

	@Override
	public Class<LoadAlignmentContext> getContextType() {
		LoadAlignmentContext.class
	}
}
