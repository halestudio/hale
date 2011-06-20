// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2010 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.hale.instance.geometry;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.schema.geometry.CRSDefinition;

/**
 * Provides support for converting {@link CRSDefinition} to string and vice 
 * versa based on the {@link CRSDefinitionFactory}ies available as OSGi
 * services.
 *    
 * @author Simon Templer
 */
public class CRSDefinitionManager {
	
	/**
	 * Get the CRS definition manager instance
	 * 
	 * @return the CRS definition manager instance
	 */
	public static CRSDefinitionManager getInstance() {
		if (instance == null) {
			instance = new CRSDefinitionManager();
		}
		
		return instance;
	}
	
	private static CRSDefinitionManager instance;
	
	/**
	 * Represent the given CRS definition as a definition string, so that it 
	 * can be used to again create a CRS definition instance using 
	 * {@link #parse(String)}.
	 * @param <T> the CRS definition type
	 *   
	 * @param crsDef the CRS definition to create a string representation for
	 * @return the string representation of the CRS definition
	 */
	@SuppressWarnings("unchecked")
	public <T extends CRSDefinition> String asString(T crsDef) {
		for (CRSDefinitionFactory<?> factory : OsgiUtils.getServices(CRSDefinitionFactory.class)) {
			if (factory.getDefinitionClass().equals(crsDef.getClass())) {
				return factory.getIdentifier() + ":" + ((CRSDefinitionFactory<T>) factory).asString(crsDef); //$NON-NLS-1$
			}
		}
		
		return null;
	}
	
	/**
	 * Parse the given definition string and create a CRS definition instance.
	 * 
	 * @param value the definition string to parse
	 * @return the CRS definition instance or <code>null</code>
	 */
	public CRSDefinition parse(String value) {
		for (CRSDefinitionFactory<?> factory : OsgiUtils.getServices(CRSDefinitionFactory.class)) {
			String prefix = factory.getIdentifier() + ":"; //$NON-NLS-1$
			if (value.startsWith(prefix)) {
				String main = value.substring(prefix.length());
				return factory.parse(main);
			}
		}
		
		return null;
	}
	
}
