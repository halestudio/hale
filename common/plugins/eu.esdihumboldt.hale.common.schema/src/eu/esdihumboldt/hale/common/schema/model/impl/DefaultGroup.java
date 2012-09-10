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
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.impl.internal.ConstraintOverrideGroupProperty;
import eu.esdihumboldt.hale.common.schema.model.impl.internal.ConstraintOverrideProperty;

/**
 * Default {@link DefinitionGroup} implementation used internally in
 * {@link DefaultTypeDefinition} and {@link DefaultGroupPropertyDefinition}. It
 * has a random UUID as identifier.
 * 
 * @author Simon Templer
 */
public class DefaultGroup implements DefinitionGroup {

	private final String identifier = UUID.randomUUID().toString();

	/**
	 * The list of declared children (qualified name mapped to child definition,
	 * LinkedHashMap because order must be maintained for writing)
	 */
	private final LinkedHashMap<QName, ChildDefinition<?>> declaredChildren = new LinkedHashMap<QName, ChildDefinition<?>>();

	private LinkedHashMap<QName, ChildDefinition<?>> flattenedChildren;

	private final boolean flatten;

	/**
	 * Create a group
	 * 
	 * @param flatten if contained group properties may be replaced by their
	 *            children if possible
	 */
	public DefaultGroup(boolean flatten) {
		super();
		this.flatten = flatten;
	}

	/**
	 * @see DefinitionGroup#getDeclaredChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren() {
		return Collections.unmodifiableCollection(flattenChildren().values());
	}

	/**
	 * @see DefinitionGroup#getChild(QName)
	 */
	@Override
	public ChildDefinition<?> getChild(QName name) {
		return flattenChildren().get(name);
	}

	/**
	 * @see DefinitionGroup#addChild(ChildDefinition)
	 */
	@Override
	public void addChild(ChildDefinition<?> child) {
		if (flatten) {
			synchronized (this) {
				flattenedChildren = null;
			}
		}
		declaredChildren.put(child.getName(), child);
	}

	/**
	 * @see DefinitionGroup#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

	private LinkedHashMap<QName, ChildDefinition<?>> flattenChildren() {
		if (flatten) {
			synchronized (this) {
				if (flattenedChildren == null) {
					Collection<? extends ChildDefinition<?>> flat = flattenIfPossible(declaredChildren
							.values());
					flattenedChildren = new LinkedHashMap<QName, ChildDefinition<?>>();
					for (ChildDefinition<?> child : flat) {
						flattenedChildren.put(child.getName(), child);
					}
				}
				return flattenedChildren;
			}
		}
		else {
			return declaredChildren;
		}
	}

	/**
	 * Replace groups with their children where possible
	 * 
	 * @param children the children
	 * @return the flattened children
	 */
	private Collection<? extends ChildDefinition<?>> flattenIfPossible(
			Collection<? extends ChildDefinition<?>> children) {
		Collection<ChildDefinition<?>> result = new ArrayList<ChildDefinition<?>>();

		for (ChildDefinition<?> child : children) {
			boolean skipAdd = false;
			if (child.asGroup() != null) {
				if (child.asGroup().allowFlatten()) {
					// prevent the group from being added
					skipAdd = true;

					// replace group with children
					for (ChildDefinition<?> groupChild : child.asGroup().getDeclaredChildren()) {
						result.add(DefinitionUtil.redeclareChild(groupChild, child.asGroup()
								.getDeclaringGroup()));
					}
				}
				else if (child.asGroup().getDeclaredChildren().size() == 1) { // special
																				// case:
																				// group
																				// has
																				// exactly
																				// one
																				// child
					// check the cardinality of the group child
					ChildDefinition<?> groupChild = child.asGroup().getDeclaredChildren()
							.iterator().next();
					Cardinality gcc = null;
					if (groupChild.asProperty() != null) {
						gcc = groupChild.asProperty().getConstraint(Cardinality.class);
					}
					else if (groupChild.asGroup() != null) {
						gcc = groupChild.asGroup().getConstraint(Cardinality.class);
					}

					if (gcc != null && gcc.getMinOccurs() == 1 && gcc.getMaxOccurs() == 1) {
						// the cardinality of the group child is exactly one
						// it can take on the group cardinality and replace the
						// group

						// get group cardinality
						Cardinality groupCardinality = child.asGroup().getConstraint(
								Cardinality.class);

						// redeclare group child
						ChildDefinition<?> redeclaredChild = DefinitionUtil.redeclareChild(
								groupChild, child.asGroup().getDeclaringGroup());

						// set group cardinality on child
						if (redeclaredChild.asGroup() != null) {
							redeclaredChild = new ConstraintOverrideGroupProperty(
									redeclaredChild.asGroup(), groupCardinality);
						}
						else if (redeclaredChild.asProperty() != null) {
							redeclaredChild = new ConstraintOverrideProperty(
									redeclaredChild.asProperty(), groupCardinality);
						}

						// prevent the group from being added
						skipAdd = true;

						// add child
						result.add(redeclaredChild);
					}
				}
			}

			if (!skipAdd) {
				// add child as is
				result.add(child);
			}
		}

		return result;
	}

}
