/**
 * 
 */
package eu.esdihumboldt.goml.omwg.comparator;

import java.util.List;

import org.opengis.feature.Property;

import eu.esdihumboldt.cst.align.ext.IValueExpression;
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
		List<IValueExpression> sourceValues = sourceRestriction.getValue();
		
		Object sourcePropValue = sourceProp.getValue();
		
		// TODO Will there be a collection of source values for equals?.
		for(IValueExpression value : sourceValues) {
			if(sourcePropValue.equals(value.getLiteral())) {
				// The value is equal so we can return true.
				return true;
			}
		}
		return false;
	}

}