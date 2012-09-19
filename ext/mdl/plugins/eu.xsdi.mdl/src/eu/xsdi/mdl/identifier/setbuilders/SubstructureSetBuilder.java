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

package eu.xsdi.mdl.identifier.setbuilders;

import java.util.SortedSet;
import java.util.TreeSet;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.xsdi.mdl.identifier.CharacteristicSetBuilder;

/**
 * {@link CharacteristicSetBuilder} returning a Set describing the substructure of an 
 * {@link AttributeDefinition}.
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 * @since 0.1.0
 */
public class SubstructureSetBuilder implements CharacteristicSetBuilder<AttributeDefinition> {

	@Override
	public SortedSet<?> getCharacteristic(AttributeDefinition ad) {
		SortedSet<Long> result = new TreeSet<Long>();
		result.add(new Long(ad.getMinOccurs()));
		result.add(new Long(ad.getMaxOccurs()));
		return result;
	}
}
