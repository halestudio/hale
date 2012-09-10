/**
 * 
 */
package eu.esdihumboldt.commons.goml.omwg.comparator;

import org.apache.log4j.Logger;
import org.opengis.feature.Property;

import eu.esdihumboldt.commons.goml.omwg.Restriction;

/**
 * @author croc
 * 
 */
public class OtherwiseComparator implements IComparator {
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger
			.getLogger(OtherwiseComparator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.goml.omwg.comparator.IComparator#evaluate(eu.esdihumboldt
	 * .goml.omwg.Restriction, org.opengis.feature.Property)
	 */
	public boolean evaluate(Restriction sourceRestriction, Property sourceProp) {
		LOG.debug("Return value always = " + true);
		return true;
	}

}
