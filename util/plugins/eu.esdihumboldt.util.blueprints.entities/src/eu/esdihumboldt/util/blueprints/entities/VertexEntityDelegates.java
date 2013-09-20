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

import java.util.NoSuchElementException;

import com.google.common.collect.Iterables;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * Utility methods that {@link VertexEntityTransformation} delegates to for some
 * calls.
 * 
 * @author Simon Templer
 */
public class VertexEntityDelegates {

	/**
	 * Name of findAll delegate method.
	 */
	public static final String METHOD_FIND_ALL = "findAllDelegate";

	/**
	 * Name of findBy delegate method.
	 */
	public static final String METHOD_FIND_BY = "findByDelegate";

	/**
	 * Name of findBy delegate method returning a single result.
	 */
	public static final String METHOD_GET_BY = "getByDelegate";

	/**
	 * Find all vertices of a specific class.
	 * 
	 * @param graph the graph to search
	 * @param className the entity name
	 * @param typeProperty the name of the property holding the entity class
	 *            information
	 * @return an iterable other the vertices
	 */
	public static Iterable<Vertex> findAllDelegate(Graph graph, String className,
			String typeProperty) {
		if (graph instanceof OrientGraph) {
			return ((OrientGraph) graph).getVerticesOfClass(className);
		}
		else {
			return graph.getVertices(typeProperty, className);
		}
	}

	/**
	 * Find vertices of a specific class with a specific value for a given
	 * attribute.
	 * 
	 * @param graph the graph to search
	 * @param className the entity name
	 * @param typeProperty the name of the property holding the entity class
	 *            information
	 * @param propertyName the name of the property to check for the given value
	 * @param value the value that vertices should have for the given property
	 * @return an iterable other the vertices
	 */
	public static Iterable<Vertex> findByDelegate(Graph graph, String className,
			String typeProperty, String propertyName, Object value) {
		if (graph instanceof OrientGraph) {
			OrientGraph orient = (OrientGraph) graph;

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT FROM ");
			sql.append(className);
			sql.append(" WHERE ");
			sql.append(propertyName);
			sql.append(" = ");
			sql.append("$testvalue");

			OCommandSQL command = new OCommandSQL(sql.toString());
			command.getContext().setVariable("testvalue", value);

			return orient.command(command).execute();
		}
		else {
			return graph.query().has(typeProperty, className).has(propertyName, value).vertices();
		}
	}

	/**
	 * Find a vertex of a specific class with a specific value for a given
	 * attribute.
	 * 
	 * @param graph the graph to search
	 * @param className the entity name
	 * @param typeProperty the name of the property holding the entity class
	 *            information
	 * @param propertyName the name of the property to check for the given value
	 * @param value the value that vertices should have for the given property
	 * @return the found vertex or <code>null</code> if it does not exist
	 * @throws NonUniqueResultException if there are multiple vertices matching
	 *             the criteria
	 */
	public static Vertex getByDelegate(Graph graph, String className, String typeProperty,
			String propertyName, Object value) throws NonUniqueResultException {
		Iterable<Vertex> vertices = findByDelegate(graph, className, typeProperty, propertyName,
				value);
		try {
			return Iterables.getOnlyElement(vertices);
		} catch (NoSuchElementException e) {
			return null;
		} catch (IllegalArgumentException e) {
			throw new NonUniqueResultException(e);
		}
	}

}
