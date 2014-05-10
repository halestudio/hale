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

package eu.esdihumboldt.hale.common.codelist.config;

import java.util.List;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Immutable key representing an entity definition only by its type and child
 * names.
 * 
 * @author Simon Templer
 */
public class DummyEntityKey {

	private final List<QName> names;

	/**
	 * Create an entity key from a list of names.
	 * 
	 * @param names list of names, the first name is the type name, the
	 *            following are child names
	 */
	public DummyEntityKey(Iterable<QName> names) {
		super();
		this.names = ImmutableList.copyOf(names);
	}

	/**
	 * Create an entity key from an entity definition.
	 * 
	 * @param entityDef the entity definition
	 * @param skipGroups if group names should be skipped
	 */
	public DummyEntityKey(EntityDefinition entityDef, boolean skipGroups) {
		super();

		Builder<QName> builder = ImmutableList.builder();

		builder.add(entityDef.getType().getName());

		for (ChildContext element : entityDef.getPropertyPath()) {
			if (!skipGroups || element.getChild().asGroup() == null) {
				builder.add(element.getChild().getName());
			}
		}

		this.names = builder.build();
	}

	/**
	 * @return the list of names defining a default entity definition, first the
	 *         type name, then the child names
	 */
	public List<QName> getNames() {
		return names;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((names == null) ? 0 : names.hashCode());
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
		DummyEntityKey other = (DummyEntityKey) obj;
		if (names == null) {
			if (other.names != null)
				return false;
		}
		else if (!names.equals(other.names))
			return false;
		return true;
	}

}
