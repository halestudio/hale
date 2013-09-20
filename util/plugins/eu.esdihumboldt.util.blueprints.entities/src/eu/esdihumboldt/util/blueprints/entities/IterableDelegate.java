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

package eu.esdihumboldt.util.blueprints.entities;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * Wraps a vertex iterable and wraps vertices with entities.
 * 
 * @param <T> the entity type
 * @author Simon Templer
 */
public class IterableDelegate<T> implements Iterable<T> {

	/**
	 * The internal vertex iterable.
	 */
	protected final Iterable<Vertex> vertices;

	private final Graph graph;

	private Constructor<T> constructor;

	/**
	 * Create a delegating iterable that wraps vertices in entity objects.
	 * 
	 * @param vertices the vertices
	 * @param entityType the entity type
	 * @param graph the associated graph
	 */
	public IterableDelegate(Iterable<Vertex> vertices, Class<T> entityType, Graph graph) {
		super();
		this.vertices = vertices;
		this.graph = graph;
		try {
			this.constructor = entityType.getConstructor(Vertex.class, Graph.class);
		} catch (Exception e) {
			throw new IllegalStateException("Could not retrieve vertex entity constructor", e);
		}
	}

	/**
	 * Wrap the given vertex in an entity.
	 * 
	 * @param vertex the vertex to wrap
	 * @return the entity wrapping the vertex
	 */
	protected T wrap(Vertex vertex) {
		try {
			return constructor.newInstance(vertex, graph);
		} catch (Exception e) {
			throw new IllegalStateException("Could not create vertex entity", e);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private final Iterator<Vertex> it = vertices.iterator();

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public T next() {
				return wrap(it.next());
			}

			@Override
			public void remove() {
				it.remove();
			}
		};
	}

}
