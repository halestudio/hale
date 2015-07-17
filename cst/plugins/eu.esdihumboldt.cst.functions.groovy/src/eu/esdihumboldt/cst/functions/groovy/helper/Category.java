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

package eu.esdihumboldt.cst.functions.groovy.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Category for Groovy script helper functions. A category is defined by its
 * path.
 * 
 * @author Simon Templer
 */
public class Category implements HelperFunctionOrCategory {

	/**
	 * The root category.
	 */
	public static final Category ROOT = new Category();

	private final List<String> path;

	/**
	 * Create a category from the given path.
	 * 
	 * @param path the path defining the category
	 */
	public Category(Iterable<String> path) {
		super();
		this.path = ImmutableList.copyOf(path);
	}

	/**
	 * Create a category from the given path.
	 * 
	 * @param path the path defining the category
	 */
	public Category(String... path) {
		super();
		this.path = Collections.unmodifiableList(Arrays.asList(path));
	}

	@Override
	public String toString() {
		if (path == null || path.isEmpty()) {
			return "ROOT";
		}
		return Joiner.on('.').join(path);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		}
		else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String getName() {
		if (path == null || path.isEmpty()) {
			return "";
		}
		else {
			return path.get(path.size() - 1);
		}
	}

	@Override
	public Category asCategory() {
		return this;
	}

	@Override
	public HelperFunction<?> asFunction() {
		return null;
	}

	/**
	 * Get the parent category.
	 * 
	 * @return the parent category
	 */
	@Nullable
	public Category getParent() {
		if (path == null || path.isEmpty()) {
			// no parent
			return null;
		}
		else if (path.size() == 1) {
			return Category.ROOT;
		}
		else {
			return new Category(Iterables.limit(path, path.size() - 1));
		}
	}

}
