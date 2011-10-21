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

package eu.esdihumboldt.hale.ui.views.properties.typedefinition;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;
import eu.esdihumboldt.hale.ui.views.properties.DefaultFilter;

/**
 * Filter that lets only {@link TypeDefinition}s with enumeration values which
 * are not <code>null</code> pass.
 * 
 * @author Patrick Lieb
 */
public class TypeDefinitionEnumerationFilter extends DefaultFilter {

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.DefaultFilter#isFiltered(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public boolean isFiltered(EntityDefinition input) {
		if (input instanceof TypeEntityDefinition) {
			return ((TypeEntityDefinition) input).getDefinition()
					.getConstraint(Enumeration.class).getValues() == null;
		}
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.DefaultFilter#isFiltered(eu.esdihumboldt.hale.common.schema.model.Definition)
	 */
	@Override
	public boolean isFiltered(Definition<?> input) {
		if (input instanceof TypeDefinition) {
			return ((TypeDefinition) input).getConstraint(Enumeration.class)
					.getValues() == null;
		}
		return false;
	}
}
