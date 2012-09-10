/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.specification.util;

/**
 * This interface declares methods to access property information used by JAXB
 * NamespacePrefixMapper.
 * 
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public interface NamespacePrefixMapper {
	/**
	 * Returns a property name as String
	 * 
	 * @return
	 */
	public String getPropertyName();

}
