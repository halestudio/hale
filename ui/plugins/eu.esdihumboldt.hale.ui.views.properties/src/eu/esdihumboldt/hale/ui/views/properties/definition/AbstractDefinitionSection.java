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

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.views.properties.AbstractTextSection;

/**
 * Abstract section for definition properties
 * 
 * @author Patrick Lieb
 * @param <T> the definition type
 */
public abstract class AbstractDefinitionSection<T extends Definition<?>> extends
		AbstractTextSection {

	/**
	 * the general Definition for this package
	 */
	private T definition;

	/**
	 * @param def the Definition
	 */
	protected void setDefinition(T def) {
		definition = def;
	}

	/**
	 * @return the definition
	 */
	public T getDefinition() {
		return definition;
	}

}
