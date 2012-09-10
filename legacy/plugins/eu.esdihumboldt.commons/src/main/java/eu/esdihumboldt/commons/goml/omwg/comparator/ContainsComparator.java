/**
 * 
 */
package eu.esdihumboldt.commons.goml.omwg.comparator;

import java.util.List;

import org.apache.log4j.Logger;
import org.opengis.feature.Property;

import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.specification.cst.align.ext.IValueExpression;

/**
 * @author doylemr
 * 
 */
public class ContainsComparator implements IComparator {
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger
			.getLogger(ContainsComparator.class);

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

		LOG.debug("There are " + sourceValues.size() + " source values in the "
				+ sourceRestriction);
		LOG.debug("The source property value is: " + sourcePropValue);

		// TODO Will there be a collection of source values for equals?.
		for (IValueExpression value : sourceValues) {
			if (sourcePropValue.equals(value.getLiteral())) {
				LOG.debug("Found a match between " + sourcePropValue + " and "
						+ value.getLiteral());
				// Found value in the sourceProp so we can return true.
				return true;
			}
		}
		LOG.debug("No matches found for " + sourcePropValue);
		return false;
	}

}
