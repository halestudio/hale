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

package eu.esdihumboldt.hale.common.align.tgraph

import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.util.Gremlin

import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality


/**
 * {@link TGraph} helpers.
 * 
 * @author Simon Templer
 */
class TGraphHelpers implements TGraphConstants {

	private static boolean loaded

	/**
	 * Load Gremlin and the Groovy {@link TGraph} helper methods.
	 */
	static void load() {
		synchronized (TGraphHelpers) {
			if (!loaded) {
				// load gremlin
				Gremlin.load()

				Vertex.metaClass.cell = {
					->
					delegate.getProperty(P_CELL)
				}
				Vertex.metaClass.entity = {
					->
					delegate.getProperty(P_ENTITY)
				}
				Vertex.metaClass.definition = {
					->
					delegate.entity().definition
				}
				Vertex.metaClass.cardinality = {
					->
					def card = delegate.getProperty(P_CARDINALITY);
					if (card) {
						// a cardinality set as property overrides other cardinalities
						return card
					}

					// try to retrieve the cardinality from the entity definition
					EntityDefinition entity = delegate.entity()
					switch (entity.getDefinition()) {
						case ChildDefinition:
							return delegate.entity().getDefinition().getConstraint(Cardinality)
						default:
						// e.g. a type
						//TODO allow different type cardinalities for Join etc.
							return Cardinality.CC_EXACTLY_ONCE;
					}
				}
				Cardinality.metaClass.mayOccurMultipleTimes = {
					->
					delegate.maxOccurs == Cardinality.UNBOUNDED || delegate.maxOccurs > 1
				}
				Vertex.metaClass.context = {
					->
					def contexts = delegate.in(EDGE_CONTEXT).toList()
					assert contexts.size() <= 1
					contexts.empty ? null : contexts.first()
				}
				Vertex.metaClass.parentContext = {
					->
					def contexts = delegate.out(EDGE_PARENT).in(EDGE_CONTEXT).toList()
					assert contexts.size() <= 1
					contexts.empty ? null : contexts.first()
				}

				loaded = true;
			}
		}
	}
}
