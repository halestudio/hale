/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.opengis.feature.type.FeatureType;

/**
 * A static helper service that collects target {@link FeatureType}s.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TargetSchemaProvider {
	
	private static TargetSchemaProvider instance = new TargetSchemaProvider();
	
	private Map<String, FeatureType> targetTypes;
	
	private TargetSchemaProvider() {
		this.targetTypes = new HashMap<String, FeatureType>();
	}
	
	public static TargetSchemaProvider getInstance() {
		return TargetSchemaProvider.instance;
	}
	
	/**
	 * Add any {@link Collection} of {@link FeatureType}s to this service.
	 * @param types
	 */
	public void addTypes(Collection<FeatureType> types) {
		for (FeatureType ft : types) {
			this.targetTypes.put(
					ft.getName().getNamespaceURI() + "/" + ft.getName().getLocalPart(), ft);
		}
	}
	
	/**
	 * retrieve a FeatureType by it's URL.
	 * @param key the URL to identify the {@link FeatureType}.
	 * @return the searched {@link FeatureType}, or null.
	 */
	public FeatureType getType(String key) {
		return this.targetTypes.get(key);
	}

}
