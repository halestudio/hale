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

package eu.esdihumboldt.hale.schema.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.schema.model.DefinitionUtil;

/**
 * Default {@link DefinitionGroup} implementation used internally in 
 * {@link DefaultTypeDefinition} and {@link DefaultGroupPropertyDefinition}.
 * It has a random UUID as identifier.
 * @author Simon Templer
 */
public class DefaultGroup implements DefinitionGroup {
	
	private final String identifier = UUID.randomUUID().toString();
	
	/**
	 * The list of declared children (qualified name mapped to child definition, LinkedHashMap because order must be maintained for writing)
	 */
	private final LinkedHashMap<QName, ChildDefinition<?>> declaredChildren = new LinkedHashMap<QName, ChildDefinition<?>>();
	
	private LinkedHashMap<QName, ChildDefinition<?>> flattenedChildren;
	
	private final boolean flatten;

	/**
	 * Create a group
	 * @param flatten if contained group properties may be replaced by 
	 *   their children if possible
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
		flattenedChildren = null;
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
			if (flattenedChildren == null) {
				Collection<? extends ChildDefinition<?>> flat = flattenIfPossible(declaredChildren.values());
				flattenedChildren = new LinkedHashMap<QName, ChildDefinition<?>>();
				for (ChildDefinition<?> child : flat) {
					flattenedChildren.put(child.getName(), child);
				}
			}
			return flattenedChildren;
		}
		else {
			return declaredChildren;
		}
	}
	
	/**
	 * Replace groups with their children where possible
	 * @param children the children
	 * @return the flattened children
	 */
	private Collection<? extends ChildDefinition<?>> flattenIfPossible(
			Collection<? extends ChildDefinition<?>> children) {
		Collection<ChildDefinition<?>> result = new ArrayList<ChildDefinition<?>>();
		
		for (ChildDefinition<?> child : children) {
			if (child.asGroup() != null && child.asGroup().allowFlatten()) {
				// replace group with children
				for (ChildDefinition<?> groupChild : child.asGroup().getDeclaredChildren()) {
					result.add(DefinitionUtil.redeclareChild(groupChild, child.asGroup()));
				}
			}
			else {
				result.add(child);
			}
		}
		
		return result;
	}

}
