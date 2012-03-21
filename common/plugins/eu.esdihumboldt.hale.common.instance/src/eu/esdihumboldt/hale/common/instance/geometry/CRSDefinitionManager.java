// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2010 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.hale.common.instance.geometry;

import java.util.ArrayList;
import java.util.List;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.util.definition.AbstractObjectFactory;

/**
 * Provides support for converting {@link CRSDefinition} to string and vice 
 * versa based on the {@link CRSDefinitionFactory}ies available as OSGi
 * services.
 *    
 * @author Simon Templer
 */
public class CRSDefinitionManager extends AbstractObjectFactory<CRSDefinition,
		CRSDefinitionFactory<?>> {
	
	private CRSDefinitionManager() {
		super();
	}
	
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
	 * @see AbstractObjectFactory#getDefinitions()
	 */
	@Override
	protected List<CRSDefinitionFactory<?>> getDefinitions() {
		List<CRSDefinitionFactory<?>> result = new ArrayList<CRSDefinitionFactory<?>>();
		for (CRSDefinitionFactory<?> def : OsgiUtils.getServices(CRSDefinitionFactory.class)) {
			result.add(def);
		}
		return result;
	}
	
}
