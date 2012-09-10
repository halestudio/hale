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
package eu.esdihumboldt.hale.ui.style;

import org.eclipse.jface.action.ActionContributionItem;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;
import eu.esdihumboldt.hale.ui.style.internal.Messages;
import eu.esdihumboldt.hale.ui.util.action.DropdownAction;

/**
 * Drop-down action for style editing
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@Deprecated
public class StyleDropdown extends DropdownAction {

	/**
	 * Creates a style drop-down
	 */
	public StyleDropdown() {
		super(Messages.StyleDropdown_SuperTitle);

		setImageDescriptor(InstanceStylePlugin.getImageDescriptor("/icons/styles.gif")); //$NON-NLS-1$

		addItem(new ActionContributionItem(new DatasetStyleDropdown(DataSet.SOURCE)));
		addItem(new ActionContributionItem(new DatasetStyleDropdown(DataSet.TRANSFORMED)));
	}

}
