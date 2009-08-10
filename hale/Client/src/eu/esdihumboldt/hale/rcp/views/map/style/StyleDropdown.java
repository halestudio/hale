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
package eu.esdihumboldt.hale.rcp.views.map.style;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.HALEActivator;

/**
 * Drop-down action for style editing
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StyleDropdown extends DropdownAction {
	
	/**
	 * Creates a style drop-down
	 */
	public StyleDropdown() {
		super("Styles");
		
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				HALEActivator.PLUGIN_ID, "/icons/styles.gif"));
		
		addItem(new ActionContributionItem(new DatasetStyleDropdown(DatasetType.reference)));
		addItem(new ActionContributionItem(new DatasetStyleDropdown(DatasetType.transformed)));
	}
	
}
