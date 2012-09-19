/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
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
