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
 * Compares the Property values in the sourceRestriction with the
 * <b><i>corresponding</b></i> Property value. If the source Property being
 * compared contains <b><i>one</b></i> occurrence of any <b><i>value</b></i> in
 * the <b><i>corresponding</i></b> target Property the evaluation returns true.
 * 
 * @author Mark Doyle (Logica)
 * @see org.opengis.feature.Property
 * @see IValueExpression
 */
public class OneOfComparator implements IComparator {

	private static final Logger LOG = Logger.getLogger(OneOfComparator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.goml.omwg.comparator.IComparator#evaluate(java.util.List,
	 * java.util.List)
	 */
	public boolean evaluate(Restriction sourceRestriction, Property sourceProp) {
		List<IValueExpression> sourceValues = sourceRestriction.getValue();
		Object sourcePropValue = sourceProp.getValue();

		LOG.debug("There are " + sourceValues.size() + " source values in the "
				+ sourceRestriction);
		LOG.debug("The source property value is: " + sourcePropValue);

		for (IValueExpression value : sourceValues) {
			// TODO Should we be testing the equivalence of the source property
			// value against the literal?
			// Is literal always populated? I would have assumed the equal()
			// would have worked on the value object
			if (sourcePropValue.equals(value.getLiteral())) {
				// We have found one of the values so we can break and return
				// true;
				LOG.debug("Found a match between " + sourcePropValue + " and "
						+ value.getLiteral());
				return true;
			}
		}

		LOG.debug("No matches found for " + sourcePropValue);
		return false;
	}

}
