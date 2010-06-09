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
package eu.esdihumboldt.cst.transformer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.filter.identity.FeatureId;

/**
 * This class provides means of equipping features with repeatable Feature IDs.
 * 
 * @author Thorsten Reitz
 */
public class IdGenerator {
	
	private static long sequence = 0;
	
	/**
	 * Uses the {@link #hashCode()} of the source {@link Feature}'s ID to 
	 * return a new {@link FeatureId}.
	 * @param f the {@link Feature} from which the new ID should be derived from.
	 * @return a int {@link FeatureId} based on the source's {@link FeatureId}.
	 */
	public static FeatureId getNumericHashcodeId(Feature f) {
		return new FeatureIdImpl("" + f.getIdentifier().getID().hashCode());
	}
	
	/**
	 * Uses the {@link #hashCode()} of the source {@link Feature}'s ID and the 
	 * {@link #hashCode()} of the {@link Feature} itself to return a new 
	 * {@link FeatureId}.
	 * @param f the {@link Feature} from which the new ID should be derived from. 
	 * @return a {@link FeatureId} containing a UUID in String form.
	 */
	public static FeatureId getUuidHashcodeId(Feature f) {
		UUID fid = new UUID(f.getIdentifier().getID().hashCode(), f.hashCode());
		return new FeatureIdImpl(fid.toString());
	}
	
	/**
	 * @param f the {@link Feature} from which the new ID should be derived from.
	 * @param fieldnames a {@link List} with the local names of the fields that 
	 * should be used for the calculation of the new {@link FeatureId} content. 
	 * @return a {@link FeatureId} containing a UUID in String form.
	 */
	public static FeatureId getUuidHashcodeId(Feature f, List<String> fieldnames) {
		List<String> values = new ArrayList<String>();
		for (String fieldname : fieldnames) {
			Property p = f.getProperty(fieldname);
			if (p != null) {
				values.add(p.getValue().toString());
			}
		}
		UUID fid = new UUID(f.getIdentifier().getID().hashCode(), values.hashCode());
		return new FeatureIdImpl(fid.toString());
	}
	
	/**
	 * @return a {@link FeatureId} with the next sequential long value.
	 */
	public static synchronized FeatureId getSequentialId() {
		return new FeatureIdImpl(++sequence + "");
	}
	
	/**
	 * resets the Sequence counter.
	 */
	public static synchronized void resetSequence() {
		sequence = 0;
	}

}
