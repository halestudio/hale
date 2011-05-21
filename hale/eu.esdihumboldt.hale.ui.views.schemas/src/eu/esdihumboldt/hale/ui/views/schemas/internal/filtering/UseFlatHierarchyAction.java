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

package eu.esdihumboldt.hale.ui.views.schemas.internal.filtering;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import eu.esdihumboldt.hale.ui.views.schemas.SchemasViewPlugin;
import eu.esdihumboldt.hale.ui.views.schemas.internal.ConfigurableModelContentProvider;
import eu.esdihumboldt.hale.ui.views.schemas.internal.Messages;

/**
 * TODO Explain the purpose of this type here.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class UseFlatHierarchyAction 
	extends AbstractContentProviderAction {
	
	public UseFlatHierarchyAction() {
		super.setIdentifier("UseFlatHierarchyAction"); //$NON-NLS-1$
	}
	
	/**
	 * @see Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return SchemasViewPlugin.getImageDescriptor("/icons/flat_hierarchy.png"); //$NON-NLS-1$
	}
	
	/**
	 * @see Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return Messages.UseFlatHierarchyAction_OrganizeTooltipText;
	}

	/**
	 * @see AbstractContentProviderAction#updateContentProvider(ConfigurableModelContentProvider)
	 */
	@Override
	protected void updateContentProvider(
			ConfigurableModelContentProvider contentProvider) {
		contentProvider.setFlatten(isChecked());
	}
}
