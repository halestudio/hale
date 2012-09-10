/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
