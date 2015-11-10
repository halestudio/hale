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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

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
	private final DefinitionGroup declaredChildren;

	/**
	 * The list of inherited children, names mapped to child definitions
	 */
	private LinkedHashMap<QName, ChildDefinition<?>> inheritedChildren;

	/**
	 * The map of children that are overridden for some reason.
	 */
	private Map<QName, ChildDefinition<?>> overriddenChildren;

	private final Function<ChildDefinition<?>, ChildDefinition<?>> overriddenChildrenTransformer = new Function<ChildDefinition<?>, ChildDefinition<?>>() {

		@Override
		public ChildDefinition<?> apply(ChildDefinition<?> input) {
			if (overriddenChildren != null) {
				ChildDefinition<?> overridden = overriddenChildren.get(input.getName());
				if (overridden != null) {
					return overridden;
				}
			}

			return input;
		}
	};

	/**
	 * Create a type definition with the given name
	 * 
	 * @param name the type name
	 */
	public DefaultTypeDefinition(QName name) {
		this(name, true);
	}

	/**
	 * Create a type definition with the given name
	 * 
	 * @param name the type name
	 * @param flattenAllowed if flattening of declared groups is allowed
	 */
	public DefaultTypeDefinition(QName name, boolean flattenAllowed) {
		super(name);

		declaredChildren = new DefaultGroup(getIdentifier() + "/declaredChildren", flattenAllowed);
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
		if (overriddenChildren == null || overriddenChildren.isEmpty()) {
			return declaredChildren.getDeclaredChildren();
		}
		else {
			return Collections2.transform(declaredChildren.getDeclaredChildren(),
					overriddenChildrenTransformer);
		}
	}

	/**
	 * @see DefinitionGroup#addChild(ChildDefinition)
	 */
	@Override
	public void addChild(ChildDefinition<?> child) {
		declaredChildren.addChild(child);
	}

	/**
	 * Get the unmodifiable map of inherited children.
	 * 
	 * @return the inherited children, names mapped to definitions
	 */
	protected Map<QName, ChildDefinition<?>> getInheritedChildren() {
		initInheritedChildren();

		if (overriddenChildren == null || overriddenChildren.isEmpty()) {
			return Collections.unmodifiableMap(inheritedChildren);
		}
		else {
			Map<QName, ChildDefinition<?>> result = new LinkedHashMap<>();
			for (Entry<QName, ChildDefinition<?>> entry : inheritedChildren.entrySet()) {
				QName name = entry.getKey();
				ChildDefinition<?> child = overriddenChildren.get(name);
				if (child == null) {
					child = entry.getValue();
				}
				result.put(name, child);
			}
			return result;
		}
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
		if (overriddenChildren == null || overriddenChildren.isEmpty()) {
			children.addAll(inheritedChildren.values());
		}
		else {
			children.addAll(Collections2.transform(inheritedChildren.values(),
					overriddenChildrenTransformer));
		}

		// add declared children afterwards - correct order for output
		children.addAll(getDeclaredChildren());

		return children;
	}

	/**
	 * Initialize the inherited children.<br>
	 * May not be called while creating the model.
	 */
	private void initInheritedChildren() {
		synchronized (this) {
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
						ChildDefinition<?> reparent = DefinitionUtil.reparentChild(parentChild,
								this);

						inheritedChildren.put(reparent.getName(), reparent);
					}
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
		ChildDefinition<?> result = null;

		if (overriddenChildren != null) {
			result = overriddenChildren.get(name);
		}

		if (result == null) {
			result = declaredChildren.getChild(name);
		}

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

	/**
	 * Override the child with the same name.
	 * 
	 * @param child the child to replace the original child
	 */
	public void overrideChild(ChildDefinition<?> child) {
		if (overriddenChildren == null) {
			overriddenChildren = new HashMap<>();
		}
		overriddenChildren.put(child.getName(), child);
	}

}
