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

package eu.esdihumboldt.hale.ui.views.properties.definition;

import org.eclipse.core.runtime.Assert;

import eu.esdihumboldt.hale.schema.model.Definition;
import eu.esdihumboldt.hale.ui.views.properties.AbstractSection;

/**
 * Abstract section for definition properties
 * @author Patrick Lieb
 */
public class AbstractDefinitionSection extends AbstractSection{
	
	/**
	 * the general Definition for this package
	 */
	protected static Definition<?> DEFINITION;
	
	/**
	 * @param def the Definition
	 */
	protected static void setDefinition(Definition<?> def){
		DEFINITION = def;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.AbstractSection#setInput(java.lang.Object)
	 */
	@Override
	protected void setInput(Object input) {
		Assert.isTrue(input instanceof Definition<?>);
		DEFINITION = (Definition<?>) input;
	}

}
