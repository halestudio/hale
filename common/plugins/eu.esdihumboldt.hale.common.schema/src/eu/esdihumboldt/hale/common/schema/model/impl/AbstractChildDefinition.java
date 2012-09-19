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

package eu.esdihumboldt.hale.common.schema.model.impl;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Abstract child definition implementation
 * 
 * @param <C> the supported constraint type
 * 
 * @author Simon Templer
 */
public abstract class AbstractChildDefinition<C> extends AbstractDefinition<C> implements
		ChildDefinition<C> {

	/**
	 * The parent group
	 */
	private final DefinitionGroup parentGroup;

	/**
	 * Creates a new child and adds it to the parent group
	 * 
	 * @param name the child qualified name
	 * @param parentGroup the parent group
	 */
	public AbstractChildDefinition(QName name, DefinitionGroup parentGroup) {
		super(name);
		this.parentGroup = parentGroup;

		parentGroup.addChild(this);
	}

	/**
	 * @see ChildDefinition#getDeclaringGroup()
	 */
	@Override
	public DefinitionGroup getDeclaringGroup() {
		return parentGroup;
	}

	/**
	 * @see ChildDefinition#getParentType()
	 */
	@Override
	public TypeDefinition getParentType() {
		DefinitionGroup parent = getDeclaringGroup();

		if (parent instanceof TypeDefinition) {
			return (TypeDefinition) parent;
		}
		else if (parent instanceof ChildDefinition<?>) {
			return ((ChildDefinition<?>) parent).getParentType();
		}

//		throw new IllegalStateException("No parent type defined.");
		return null;
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		if (getParentType() == null) {
			return name.getNamespaceURI() + "/" + name.getLocalPart();
		}
		else {
			return getParentType().getIdentifier() + "/" + name.getLocalPart(); //$NON-NLS-1$
		}
	}

}
