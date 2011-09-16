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

package eu.xsdi.mdl.identifier;

import java.util.SortedSet;

/**
 * TODO Add Type comment
 * 
 * @author thorsten
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public interface CharacteristicSetBuilder<T> {
	
	/**
	 * @param t the object from which to extract the characteristic.
	 * @return the Set of characteristics that have been extracted.
	 */
	public SortedSet<?> getCharacteristic(T t);

}
