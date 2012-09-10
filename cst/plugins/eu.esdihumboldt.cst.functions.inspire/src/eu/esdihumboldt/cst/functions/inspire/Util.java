/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.functions.inspire;

import java.util.Collection;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Util class for geographical name and identifier function
 * 
 * @author Kevin Mais
 * 
 */
public class Util {

	/**
	 * Looks for a child in <code>definition</code> with the name
	 * <code>localpart</code>
	 * 
	 * @param localpart the name of the child to look for
	 * @param definition the type definition to search the children through
	 * @return the child with the name <code>localpart</code>, can be
	 *         <code>null</code>
	 */
	public static PropertyDefinition getChild(String localpart, TypeDefinition definition) {

		Collection<? extends ChildDefinition<?>> children = definition.getChildren();

		for (ChildDefinition<?> child : children) {
			if (child.asProperty() != null && child.getName().getLocalPart().equals(localpart)) {
				return child.asProperty();
			}
		}

		return null;

	}

}
