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

package eu.esdihumboldt.cst.transformer.service.rename;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureImpl;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;

import eu.esdihumboldt.cst.transformer.service.impl.IdGenerator;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * A helper class for building {@link FeatureImpl}s.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public class FeatureBuilder {
	
	/**
	 * The key for the feature user data where its source feature's
	 * {@link FeatureId} is put.
	 */
	public static final Object SOURCE_ID = "source_feature_id"; //$NON-NLS-1$
	
	/**
	 * Get the source feature ID for a transformed feature
	 * 
	 * @param transformed the transformed feature
	 * 
	 * @return the source feature ID (if any)
	 */
	public static FeatureId getSourceID(Feature transformed) {
		return (FeatureId) transformed.getUserData().get(SOURCE_ID);
	}

	/**
	 * @param ft the {@link FeatureType} for which to build a {@link Feature}.
	 * @param source a source {@link Feature} from which to use the ID.
	 * @param createNestedFeatures set to true if you want the feature builder 
	 * to already create one instance for each attribute that is itself a feature.
	 * @return the created feature
	 */
	@SuppressWarnings("unchecked")
	public static Feature buildFeature(FeatureType ft, Feature source, boolean createNestedFeatures) {
		SimpleFeatureType targetType = (SimpleFeatureType) ft;
		Feature target = null;
		
		Collection properties = new HashSet<Property>();
		for (AttributeDescriptor ad : targetType.getAttributeDescriptors()) {
			Identifier id = new FeatureIdImpl(ad.getLocalName());
			// create normal AttributeImpls
			if (ad instanceof GeometryDescriptorImpl) {
				properties.add(new GeometryAttributeImpl(
						null, (GeometryDescriptor)ad, id));
			}
			else if (ad instanceof AttributeDescriptorImpl) {
				if (ad.getType().getBinding().equals(Collection.class) && createNestedFeatures) {
					if (ad.getType() instanceof FeatureType) {
						properties.add(
								new AttributeImpl(Collections.singleton(
										buildFeature((FeatureType) ad.getType(), null, createNestedFeatures)), 
										ad, id));
					}
				}
				else {
					properties.add(new AttributeImpl(null, ad, id));
				}
			}
		}
		if (source == null) {
			target = new FeatureImpl(properties, targetType, 
					new FeatureIdImpl("_" + UUID.randomUUID().toString())); // XML ID may only start with _ or letter (no digit) //$NON-NLS-1$
		}
		else {
			// determine the feature ID for the target feature 
			FeatureId id = IdGenerator.getTransformationId(source, ft); //source.getIdentifier();
			target = new FeatureImpl(properties, targetType, id);
			// store the source feature ID in the user data
			target.getUserData().put(SOURCE_ID, source.getIdentifier());
		}

		return target;
	}

}
