/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.model;

import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.impl.AbstractChildDefinition;

/**
 * A group of children, which may be properties or property groups
 * 
 * @author Simon Templer
 */
public interface DefinitionGroup {

	/**
	 * Get the properties and property groups declared by the type
	 * 
	 * @return the definitions of the declared properties and groups
	 */
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren();

	/**
	 * Get the child with the given name
	 * 
	 * @param name the child name
	 * 
	 * @return the child definition or <code>null</code> if no child with the
	 *         given name is available
	 */
	public ChildDefinition<?> getChild(QName name);

	/**
	 * Add a declared child, this is called by the
	 * {@link AbstractChildDefinition} constructor.
	 * 
	 * @param child the child definition
	 */
	public void addChild(ChildDefinition<?> child);

	/**
	 * Get the group identifier
	 * 
	 * @return the unique name of the group
	 */
	public String getIdentifier();

}
