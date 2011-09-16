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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;
import org.opengis.filter.identity.FeatureId;

import eu.esdihumboldt.commons.tools.FeatureInspector;

/**
 * This class provides means of equipping features with repeatable Feature IDs.
 * 
 * @author Thorsten Reitz
 */
public class IdGenerator {
	
	private static long sequence = 0;
	
	/**
	 * Uses the {@link #hashCode()} of the source {@link Feature}'s ID to 
	 * return a new {@link FeatureId}. The {@link FeatureId} of the source 
	 * {@link Feature} must not be null.
	 * @param f the {@link Feature} from which the new ID should be derived from.
	 * @return a int {@link FeatureId} based on the source's {@link FeatureId}.
	 */
	public static FeatureId getNumericHashcodeId(Feature f) {
		return new FeatureIdImpl("" + f.getIdentifier().getID().hashCode()); //$NON-NLS-1$
	}
	
	/**
	 * Uses the {@link #hashCode()} of the source {@link Feature}'s ID and the 
	 * {@link #hashCode()} of the {@link Feature} itself to return a new 
	 * {@link FeatureId}. The {@link FeatureId} of the source {@link Feature} 
	 * must not be null.
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
	 * A field's value may be null, but not all are allowed to be null.
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
		String mostSignificant = ""; //$NON-NLS-1$
		String leastSignificant = ""; //$NON-NLS-1$
		if (values.size() < 1) {
			throw new InvalidParameterException("The Feature passed in did " + //$NON-NLS-1$
					"not have a non-null field to use in ID calculation."); //$NON-NLS-1$
		}
		else if (values.size() == 1) {
			String input = values.get(0);
			mostSignificant = input.substring(0, input.length() / 2);
			leastSignificant = input.substring(input.length() / 2 + 1, input.length() - 1);
		} 
		else {
			for (int i = 0; i < values.size(); i++) {
				if (i % 2 == 0) {
					mostSignificant += values.get(i);
				}
				else {
					leastSignificant += values.get(i);
				}
			}
		}
		UUID fid = new UUID(mostSignificant.hashCode(), 
				leastSignificant.hashCode());
		return new FeatureIdImpl(fid.toString());
	}
	
	/**
	 * Create a feature ID for a transformed feature of the given target type
	 * originating from the given source feature.
	 * 
	 * @param source the source feature
	 * @param targetType the target type
	 * 
	 * @return a {@link FeatureId} containing a UUID in String form.
	 */
	public static FeatureId getTransformationId(Feature source, ComplexType targetType) {
		List<String> values = new ArrayList<String>();
		Collection<? extends Property> properties = FeatureInspector.getProperties(source);
		
		// get string property values
		for (Property p : properties) {
			//XXX do a selection on the properties? e.g. not using geometry properties
			Object value = p.getValue();
			if (value != null) {
				if (value instanceof ComplexAttribute) {
					//XXX use inner values?
				}
				else {
					values.add(value.toString());
				}
			}
		}
		
		//  target type as most significant part of the UUID
		String mostSignificant = targetType.getName().getURI();
		
		StringBuffer leastSignificant = new StringBuffer();
		
		if (values.size() < 1) {
			// no value - use feature ID
			leastSignificant.append(source.getIdentifier().toString());
		}
		else {
			for (int i = 0; i < values.size(); i++) {
				leastSignificant.append(values.get(i));
			}
		}
		
		UUID fid = new UUID(mostSignificant.hashCode(), 
				leastSignificant.toString().hashCode());
		
		return new FeatureIdImpl("_" + fid.toString()); //$NON-NLS-1$
	}
	
	/**
	 * @return a {@link FeatureId} with the next sequential long value.
	 */
	public static synchronized FeatureId getSequentialId() {
		return new FeatureIdImpl(++sequence + ""); //$NON-NLS-1$
	}
	
	/**
	 * resets the Sequence counter.
	 */
	public static synchronized void resetSequence() {
		sequence = 0;
	}

}
