/**
 * 
 */
package eu.esdihumboldt.goml.omwg.comparator;

import org.opengis.feature.Property;

import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * @author croc
 *
 */
public class OtherwiseComparator implements Comparator {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.goml.omwg.comparator.Comparator#evaluate(eu.esdihumboldt.goml.omwg.Restriction, org.opengis.feature.Property)
	 */
	public boolean evaluate(Restriction sourceRestriction, Property sourceProp) {
		return true;
	}

}
