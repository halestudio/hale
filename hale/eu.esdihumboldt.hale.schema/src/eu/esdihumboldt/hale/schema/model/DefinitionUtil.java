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

package eu.esdihumboldt.hale.schema.model;

import eu.esdihumboldt.hale.schema.model.impl.internal.RedeclareGroupProperty;
import eu.esdihumboldt.hale.schema.model.impl.internal.RedeclareProperty;
import eu.esdihumboldt.hale.schema.model.impl.internal.ReparentGroupProperty;
import eu.esdihumboldt.hale.schema.model.impl.internal.ReparentProperty;

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

}
