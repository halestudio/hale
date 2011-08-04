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

package eu.esdihumboldt.hale.ui.views.properties.grouppropertydefinition;

import org.eclipse.core.runtime.Assert;

import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.ui.views.properties.AbstractSection;

/**
 * TODO Type description
 * @author Patrick Lieb
 */
public abstract class AbstractGroupPropertyDefinitionSection extends AbstractSection{

	/**
	 * the general GroupPropertyDefintion for this package
	 */
	protected static GroupPropertyDefinition GROUPPROPERTYDEFINITION;
	
	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.AbstractSection#setInput(java.lang.Object)
	 */
	@Override
	protected void setInput(Object input) {
		Assert.isTrue(input instanceof GroupPropertyDefinition);
		GROUPPROPERTYDEFINITION = (GroupPropertyDefinition) input;
	}
}
