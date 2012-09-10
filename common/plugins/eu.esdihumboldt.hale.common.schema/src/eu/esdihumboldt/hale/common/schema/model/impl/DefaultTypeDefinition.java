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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default {@link TypeDefinition} implementation.
 * 
 * @author Simon Templer
 */
public class DefaultTypeDefinition extends AbstractDefinition<TypeConstraint> implements
		TypeDefinition {

	/**
	 * The definition of the super type
	 */
	private DefaultTypeDefinition superType;

	/**
	 * The sub-types
	 */
	private SortedSet<DefaultTypeDefinition> subTypes;

	/**
	 * The declared children
	 */
	private final DefinitionGroup declaredChildren = new DefaultGroup(true);

	/**
	 * The list of inherited children, names mapped to child definitions
	 */
	private LinkedHashMap<QName, ChildDefinition<?>> inheritedChildren;

	/**
	 * Create a type definition with the given name
	 * 
	 * @param name the type name
	 */
	public DefaultTypeDefinition(QName name) {
		super(name);
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return name.getNamespaceURI() + "/" + name.getLocalPart();
	}

	/**
	 * @see TypeDefinition#getDeclaredChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren() {
		return declaredChildren.getDeclaredChildren();
	}

	/**
	 * @see DefinitionGroup#addChild(ChildDefinition)
	 */
	@Override
	public void addChild(ChildDefinition<?> child) {
		declaredChildren.addChild(child);
	}

	/**
	 * {@inheritDoc}<br>
	 * May not be called while creating the model.
	 * 
	 * @see TypeDefinition#getChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getChildren() {
		Collection<ChildDefinition<?>> children = new ArrayList<ChildDefinition<?>>();

		initInheritedChildren();

		// add inherited children
		children.addAll(inheritedChildren.values());

		// add declared children afterwards - correct order for output
		children.addAll(getDeclaredChildren());

		return children;
	}

	/**
	 * Initialize the inherited children.<br>
	 * May not be called while creating the model.
	 */
	private void initInheritedChildren() {
		if (inheritedChildren == null) {
			inheritedChildren = new LinkedHashMap<QName, ChildDefinition<?>>();

			// populate inherited attributes
			DefaultTypeDefinition parent = getSuperType();
			LinkedList<DefaultTypeDefinition> parents = new LinkedList<DefaultTypeDefinition>();
			while (parent != null) {
				parents.add(parent);

				parent = parent.getSuperType();
			}

			// add children starting from the topmost supertype
			Iterator<DefaultTypeDefinition> it = parents.descendingIterator();
			while (it.hasNext()) {
				parent = it.next();

				for (ChildDefinition<?> parentChild : parent.getDeclaredChildren()) {
					// create reparented copy
					ChildDefinition<?> reparent = DefinitionUtil.reparentChild(parentChild, this);

					inheritedChildren.put(reparent.getName(), reparent);
				}
			}
		}
	}

	/**
	 * @see AbstractDefinition#getInheritedConstraint(Class)
	 */
	@Override
	protected <T extends TypeConstraint> T getInheritedConstraint(Class<T> constraintType) {
		TypeDefinition superType = getSuperType();

		if (superType != null) {
			// get the super type constraint (this also may be an inherited
			// constraint from its super types)
			T superConstraint = superType.getConstraint(constraintType);

			if (superConstraint.isInheritable()) {
				// if inheritance is allowed for the constraint, return it
				return superConstraint;
			}
		}

		return null;
	}

	/**
	 * @see TypeDefinition#getSuperType()
	 */
	@Override
	public DefaultTypeDefinition getSuperType() {
		return superType;
	}

	/**
	 * Set the type's super type. This will add this type to the super type's
	 * sub-type list and remove it from the previous super type (if any).
	 * 
	 * @param superType the super-type to set
	 */
	public void setSuperType(DefaultTypeDefinition superType) {
		if (this.superType != null) {
			this.superType.removeSubType(this);
		}

		this.superType = superType;

		superType.addSubType(this);
	}

	/**
	 * Remove a sub-type
	 * 
	 * @param subtype the sub-type to remove
	 * @see #setSuperType(DefaultTypeDefinition)
	 */
	protected void removeSubType(DefaultTypeDefinition subtype) {
		if (subTypes != null) {
			subTypes.remove(subtype);
		}
	}

	/**
	 * Add a sub-type
	 * 
	 * @param subtype the sub-type to add
	 * @see #setSuperType(DefaultTypeDefinition)
	 */
	protected void addSubType(DefaultTypeDefinition subtype) {
		if (subTypes == null) {
			subTypes = new TreeSet<DefaultTypeDefinition>();
		}

		subTypes.add(subtype);
	}

	/**
	 * @see TypeDefinition#getSubTypes()
	 */
	@Override
	public Collection<? extends DefaultTypeDefinition> getSubTypes() {
		if (subTypes == null) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableCollection(subTypes);
		}
	}

	/**
	 * {@inheritDoc}<br>
	 * May not be called while creating the model.
	 * 
	 * @see TypeDefinition#getChild(QName)
	 */
	@Override
	public ChildDefinition<?> getChild(QName name) {
		ChildDefinition<?> result = declaredChildren.getChild(name);

		if (result == null) {
			initInheritedChildren();

			result = inheritedChildren.get(name);
		}

		return result;
	}

	/**
	 * @see AbstractDefinition#toString()
	 */
	@Override
	public String toString() {
		return "[type] " + super.toString();
	}

}
