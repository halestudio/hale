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
