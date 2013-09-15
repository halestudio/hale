/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.util.groovy.paths;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Base class for path accessors.
 * 
 * It mutates, so an instance is only usable once.
 * 
 * @param <C> the path element type
 * @author Simon Templer
 */
public abstract class AbstractAccessor<C> extends GroovyObjectSupport {

	/**
	 * The paths
	 */
	private List<? extends Path<C>> accessorPaths;

	/**
	 * Creates a new accessor.
	 * 
	 * @param initialPaths the initial paths, usually containing only one path
	 *            with the parent element
	 */
	public AbstractAccessor(List<? extends Path<C>> initialPaths) {
		this.accessorPaths = initialPaths;
	}

	@Override
	public Object getProperty(String property) {
		return findChildren(property, Collections.EMPTY_LIST);
	}

	@Override
	public Object invokeMethod(String name, Object args) {
		try {
			return super.invokeMethod(name, args);
		} catch (MissingMethodException e) {
			// missing method
			return findChildren(name, InvokerHelper.asList(args));
		}
	}

	/**
	 * Find children with the given name.
	 * 
	 * @param name the property name
	 * @return this accessor
	 */
	public AbstractAccessor<C> findChildren(String name) {
		return findChildren(name, Collections.EMPTY_LIST);
	}

	/**
	 * Find children with the given name.
	 * 
	 * @param name the property name
	 * @param args the list of additional arguments apart from the name
	 * @return this accessor
	 */
	public AbstractAccessor<C> findChildren(String name, List<?> args) {
		accessorPaths = findChildPaths(accessorPaths, name, args);
		return this;
	}

	/**
	 * Find child paths for the given name.
	 * 
	 * @param parentPaths the parent paths
	 * @param name the property name
	 * @param args the list of additional arguments apart from the name
	 * @return the list of sub paths replacing the parent paths
	 */
	protected abstract List<? extends Path<C>> findChildPaths(List<? extends Path<C>> parentPaths,
			String name, List<?> args);

	/**
	 * Get all found paths.
	 * 
	 * @return the list of paths
	 */
	public List<? extends Path<C>> all() {
		return accessorPaths;
	}

	/**
	 * Get a unique found child path.
	 * 
	 * @return a child path or <code>null</code> if none was found
	 * @throws IllegalStateException if there are multiple paths
	 */
	public Path<C> eval() {
		return eval(true);
	}

	/**
	 * Get a single found child path.
	 * 
	 * @param unique if the path must be unique
	 * @return a child path or <code>null</code> if none was found
	 * @throws IllegalStateException if there are multiple paths but a unique
	 *             path was requested
	 */
	public Path<C> eval(boolean unique) {
		if (accessorPaths == null || accessorPaths.isEmpty()) {
			return null;
		}
		else if (!unique || accessorPaths.size() == 1) {
			// return a single property
			return accessorPaths.get(0);
		}
		else {
			throw new IllegalStateException("Multiple possible child paths found");
		}
	}

}
