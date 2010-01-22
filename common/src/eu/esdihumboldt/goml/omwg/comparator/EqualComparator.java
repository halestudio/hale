/**
 * 
 */
package eu.esdihumboldt.goml.omwg.comparator;

import org.opengis.feature.Property;

import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * 
 * @author Mark Doyle (Logica)
 *
 */
public class EqualComparator implements Comparator {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.goml.omwg.comparator.Comparator#evaluate(java.util.List, java.util.List)
	 */
	public boolean evaluate(Restriction sourceRestriction, Property sourceProp) {
		throw new RuntimeException(this.getClass().getName() + " not yet implemented");
	}

}
