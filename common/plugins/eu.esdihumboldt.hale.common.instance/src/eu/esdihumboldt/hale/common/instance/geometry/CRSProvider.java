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
	public CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath);

}
