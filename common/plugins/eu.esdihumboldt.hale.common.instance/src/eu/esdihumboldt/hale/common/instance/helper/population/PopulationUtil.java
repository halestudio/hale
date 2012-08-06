/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.instance.helper.population;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.Population;

/**
 * Utilities regarding the {@link Population} constraint.
 * @author Simon Templer
 */
public abstract class PopulationUtil {
	
	/**
	 * Get the population of a given definition.
	 * @param def the definition, either a type, group or property definition 
	 * @return the population constraint of the definition
	 */
	public static Population getPopulation(Definition<?> def) {
		if (def instanceof TypeDefinition) {
			return ((TypeDefinition) def).getConstraint(Population.class);
		}
		if (def instanceof PropertyDefinition) {
			return ((PropertyDefinition) def).getConstraint(Population.class);
		}
		if (def instanceof GroupPropertyDefinition) {
			return ((GroupPropertyDefinition) def).getConstraint(Population.class);
		}
		
		throw new IllegalArgumentException("Invalid definition type");
	}

}
