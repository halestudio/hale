/**
 * 
 */
package eu.esdihumboldt.goml.omwg.comparator;

import org.apache.log4j.Logger;

import org.opengis.feature.Property;

import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * @author croc
 *
 */
public class OtherwiseComparator implements Comparator {
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(OtherwiseComparator.class);

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.goml.omwg.comparator.Comparator#evaluate(eu.esdihumboldt.goml.omwg.Restriction, org.opengis.feature.Property)
	 */
	public boolean evaluate(Restriction sourceRestriction, Property sourceProp) {		
		LOG.debug("Return value always = " + true);
		return true;
	}

}
