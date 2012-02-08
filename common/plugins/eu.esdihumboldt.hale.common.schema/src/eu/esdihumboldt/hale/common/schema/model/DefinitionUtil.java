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
	 * @return the child with the given name of the given definition
	 */
	public static ChildDefinition<?> getChild(ChildDefinition<?> definition, QName name) {
		if (definition.asGroup() != null)
			return definition.asGroup().getChild(name);
		if (definition.asProperty() != null)
			return definition.asProperty().getPropertyType().getChild(name);

		throw new IllegalStateException("Illegal child definition.");
	}
}
