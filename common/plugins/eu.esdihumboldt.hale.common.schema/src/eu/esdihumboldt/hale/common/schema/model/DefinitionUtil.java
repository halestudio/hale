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

package eu.esdihumboldt.hale.common.schema.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.impl.internal.RedeclareGroupProperty;
import eu.esdihumboldt.hale.common.schema.model.impl.internal.RedeclareProperty;
import eu.esdihumboldt.hale.common.schema.model.impl.internal.ReparentGroupProperty;
import eu.esdihumboldt.hale.common.schema.model.impl.internal.ReparentProperty;

/**
 * Definition utility methods
 * @author Simon Templer
 */
public abstract class DefinitionUtil {

	/**
	 * Create a proxy for the given child with another parent
	 * 
	 * @param child the child
	 * @param newParent the new parent type
	 * @return the reparented child definition
	 */
	public static ChildDefinition<?> reparentChild(
			ChildDefinition<?> child,
			TypeDefinition newParent) {
		if (child.asProperty() != null) {
			return new ReparentProperty(child.asProperty(), newParent);
		}
		else if (child.asGroup() != null) {
			return new ReparentGroupProperty(child.asGroup(), newParent);
		}
		else {
			throw new IllegalStateException("Illegal child type.");
		}
	}
	
	/**
	 * Create a proxy for the given child with another declaring group
	 * 
	 * @param child the child
	 * @param newParent the new declaring group
	 * @return the redeclared child definition
	 */
	public static ChildDefinition<?> redeclareChild(
			ChildDefinition<?> child,
			DefinitionGroup newParent) {
		if (child.asProperty() != null) {
			return new RedeclareProperty(child.asProperty(), newParent);
		}
		else if (child.asGroup() != null) {
			return new RedeclareGroupProperty(child.asGroup(), newParent);
		}
		else {
			throw new IllegalStateException("Illegal child type.");
		}
	}

	/**
	 * Get all children of a definition group. For {@link TypeDefinition}
	 * also the inherited children will be returned.
	 * @param group the definition group
	 * @return the children
	 */
	public static Collection<? extends ChildDefinition<?>> getAllChildren(
			DefinitionGroup group) {
		if (group instanceof TypeDefinition) {
			return ((TypeDefinition) group).getChildren();
		}
		else {
			return group.getDeclaredChildren();
		}
	}
	
	/**
	 * Get all properties of a definition group. For {@link TypeDefinition}
	 * also the inherited children will be returned. If there are children 
	 * that are groups, their properties are also added. 
	 * @param group the definition group
	 * @return the children
	 */
	public static Collection<? extends PropertyDefinition> getAllProperties(
			DefinitionGroup group) {
		Collection<PropertyDefinition> result = new ArrayList<PropertyDefinition>();
		for (ChildDefinition<?> child : getAllChildren(group)) {
			if (child.asProperty() != null) {
				result.add(child.asProperty());
			}
			else {
				result.addAll(getAllProperties(child.asGroup()));
			}
		}
		return result;
	}

	/**
	 * Get the cardinality of a child definition.
	 * @param child the child definition
	 * @return the cardinality
	 */
	public static Cardinality getCardinality(ChildDefinition<?> child) {
		if (child.asProperty() != null) {
			return child.asProperty().getConstraint(Cardinality.class);
		}
		if (child.asGroup() != null) {
			return child.asGroup().getConstraint(Cardinality.class);
		}
		
		throw new IllegalStateException("Illegal child type.");
	}
	
	/**
	 * Returns the child definition of definition with the given name.
	 * 
	 * @param definition the definition 
	 * @param name the name of the child
	 * @return the child with the given name of the given definition, or
	 *   <code>null</code> if it doesn't exist
	 * @throws IllegalStateException if the given definition isn't group nor
	 *   property definition
	 */
	public static ChildDefinition<?> getChild(Definition<?> definition, QName name) {
		if (definition instanceof DefinitionGroup) {
			return ((DefinitionGroup) definition).getChild(name);
		}
		if (definition instanceof ChildDefinition<?>) {
			return getChild((ChildDefinition<?>) definition, name);
		}

		throw new IllegalStateException("Illegal definition.");
	}

	/**
	 * Returns the child definition of definition with the given name.
	 * 
	 * @param definition the definition 
	 * @param name the name of the child
	 * @return the child with the given name of the given definition, or
	 *   <code>null</code> if it doesn't exist
	 * @throws IllegalStateException if the given definition isn't group nor
	 *   property definition
	 */
	public static ChildDefinition<?> getChild(ChildDefinition<?> definition, QName name) {
		if (definition.asGroup() != null)
			return definition.asGroup().getChild(name);
		if (definition.asProperty() != null)
			return definition.asProperty().getPropertyType().getChild(name);

		throw new IllegalStateException("Illegal child definition.");
	}

	/**
	 * Returns the child definition of definition with the given name.
	 * @param definition the definition
	 * @param name the name of the child
	 * @param allowIgnoreNamespace specifies if when the child with the exact
	 *   name is not present, a child with a similar local name should be
	 *   returned
	 * @return the child with the given name if it exists, a child with a
	 *   similar local name if it exists or <code>null</code>
	 * @throws IllegalStateException if the given definition isn't group nor
	 *   property definition
	 */
	public static ChildDefinition<?> getChild(
			ChildDefinition<?> definition, QName name,
			boolean allowIgnoreNamespace) {
		ChildDefinition<?> result = getChild(definition, name);
		if (result != null || !allowIgnoreNamespace) {
			return result;
		}
		
		// get all children
		Collection<? extends ChildDefinition<?>> children = DefinitionUtil
				.getAllChildren((definition.asGroup() != null) ? (definition
						.asGroup()) : (definition.asProperty()
						.getPropertyType()));
		
		int rating = 0;
		for (ChildDefinition<?> child : children) {
			if (name.getLocalPart().equals(child.getName().getLocalPart())) {
				// same local name
				int childRating = namespaceEqualityRating(child.getName()
						.getNamespaceURI(), name.getNamespaceURI()); 
				if (result == null || childRating > rating) {
					result = child;
					rating = childRating;
				}
			}
		}
		
		return result;
	}

	/**
	 * Determine a rating on namespace equality.
	 * @param ns1 the first namespace to compare
	 * @param ns2 the second namespace to compare
	 * @return the equality rating, the higher the more 
	 *   equal/compatible are the namespaces
	 */
	private static int namespaceEqualityRating(String ns1,
			String ns2) {
		// we define the rating as how many characters are equal from the start
		int count;
		int commonLength = Math.min(ns1.length(), ns2.length());
		for (count = 0; count < commonLength && ns1.charAt(count) == ns2.charAt(count); count++) {
			// counts up while the chars are equal
		}
		return count;
	}
	
}
