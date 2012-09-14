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
public class LessThanComparator implements IComparator {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(LessThanComparator.class);

	// TODO finish this method
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

		// TODO Will there be a collection of source values for not equal?.
//		for (IValueExpression value : sourceValues) {
//		}
		LOG.debug("No matches found for " + sourcePropValue);
		return false;
	}

}
