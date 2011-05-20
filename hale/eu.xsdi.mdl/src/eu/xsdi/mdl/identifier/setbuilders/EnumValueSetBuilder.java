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

package eu.xsdi.mdl.identifier.setbuilders;

import java.util.SortedSet;
import java.util.TreeSet;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.xsdi.mdl.identifier.CharacteristicSetBuilder;

/**
 * {@link CharacteristicSetBuilder} returning a Set(enum1 value, enum2value, ...) of an 
 * {@link AttributeDefinition}.
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 * @since 0.1.0
 */
public class EnumValueSetBuilder implements CharacteristicSetBuilder<AttributeDefinition> {
	
	@Override
	public SortedSet<?> getCharacteristic(AttributeDefinition ad) {
		SortedSet<String> result = new TreeSet<String>();
		return result;
	}

}
