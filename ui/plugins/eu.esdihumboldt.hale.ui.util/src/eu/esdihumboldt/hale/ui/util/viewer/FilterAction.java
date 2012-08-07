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

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Action that enables/disables a filter on a viewer.
 * @author Simon Templer
 */
public class FilterAction extends Action {
	
	private final StructuredViewer viewer;
	
	private final ViewerFilter filter;

	private final String activateMessage;

	private final String deactivateMessage;

	private final boolean inverse;

	/**
	 * Create a filter action. Initially, the filter is disabled.
	 * @param activateMessage the message to show for activating the filer
	 * @param deactivateMessage the message to show for deactivating the filer
	 * @param imageDesc the image descriptor
	 * @param viewer the viewer the filter is to be applied to
	 * @param filter the filter
	 * @param initiallyChecked if the action should be initially checked
	 * @param inverse if the mode is inverse - meaning if the action is checked,
	 *   the filter is disabled
	 */
	public FilterAction(String activateMessage, String deactivateMessage, 
			ImageDescriptor imageDesc, StructuredViewer viewer, ViewerFilter filter,
			boolean initiallyChecked, boolean inverse) {
		super(activateMessage, AS_CHECK_BOX);
		this.viewer = viewer;
		this.filter = filter;
		this.inverse = inverse;
		
		this.activateMessage = activateMessage;
		this.deactivateMessage = deactivateMessage;
		
		setImageDescriptor(imageDesc);
		setChecked(initiallyChecked);
		run();
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		boolean active = isChecked();
		if (inverse) {
			active = !active;
		}
		
		String text = (active)?(deactivateMessage):(activateMessage);
		setToolTipText(text);
		setText(text);
		
		if (active) {
			viewer.addFilter(filter);
		}
		else {
			viewer.removeFilter(filter);
		}
		
		viewer.refresh();
	}

}
