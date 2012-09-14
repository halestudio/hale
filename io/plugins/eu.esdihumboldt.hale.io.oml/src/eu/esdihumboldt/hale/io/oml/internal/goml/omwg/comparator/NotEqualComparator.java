/**
 * 
 */
package eu.esdihumboldt.hale.io.oml.internal.goml.omwg.comparator;

import java.util.List;

import org.apache.log4j.Logger;
import org.opengis.feature.Property;

import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Restriction;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueExpression;

/**
 * @author doylemr
 * 
 */
public class NotEqualComparator implements IComparator {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(NotEqualComparator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.goml.omwg.comparator.IComparator#evaluate(eu.esdihumboldt
	 * .goml.omwg.Restriction, org.opengis.feature.Property)
	 */
	@Override
	public boolean evaluate(Restriction sourceRestriction, Property sourceProp) {
		List<IValueExpression> sourceValues = sourceRestriction.getValue();
		Object sourcePropValue = sourceProp.getValue();

		LOG.debug("There are " + sourceValues.size() + " source values in the " + sourceRestriction);
		LOG.debug("The source property value is: " + sourcePropValue);

		// TODO Will there be a collection of source values for not equals?.
		for (IValueExpression value : sourceValues) {
			if (sourcePropValue.equals(value.getLiteral())) {
				LOG.debug("Found a match between " + sourcePropValue + " and " + value.getLiteral());
				// The value is equal so we must return false.
				return false;
			}
		}
		LOG.debug("No matches found for " + sourcePropValue);
		return true;
	}

}
