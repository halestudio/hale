// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2010 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.hale.instance.geometry;

import eu.esdihumboldt.hale.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.util.definition.ObjectDefinition;

/**
 * Provides support for creating a {@link CRSDefinition} from a definition
 * string and vice versa.
 * @param <T> the CRS definition type
 * 
 * @author Simon Templer
 */
public interface CRSDefinitionFactory<T extends CRSDefinition> extends 
		ObjectDefinition<T> {
	
	// concrete typed interface

}
