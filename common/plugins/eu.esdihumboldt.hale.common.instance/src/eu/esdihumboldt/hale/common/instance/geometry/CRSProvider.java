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

package eu.esdihumboldt.hale.common.instance.geometry;

import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Interface for classes that provide CRS definitions based on
 * {@link PropertyDefinition}s.
 * 
 * @author Simon Templer
 */
public interface CRSProvider {

	/**
	 * Get the CRS definition for values of the given property definition.
	 * 
	 * @param parentType the definition of the type of the parent instance
	 * @param propertyPath the property path in the instance
	 * 
	 * @return the CRS definition or <code>null</code> if it can't be determined
	 */
	CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath);

	/**
	 * Get the CRS definition for values of the given property definition. If no
	 * CRS definition can be determined, use the one passed in
	 * <code>defaultCrs</code>.
	 * 
	 * @param parentType the definition of the type of the parent instance
	 * @param propertyPath the property path in the instance
	 * @param defaultCrs Default CRS definition to use if none can be determined
	 *            (may be null)
	 * @return the CRS definition or <code>null</code> if it can't be determined
	 */
	default CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath,
			CRSDefinition defaultCrs) {
		return getCRS(parentType, propertyPath);
	}
}
