/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Action that enables/disables a filter on a viewer.
 * 
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
	 * 
	 * @param activateMessage the message to show for activating the filer
	 * @param deactivateMessage the message to show for deactivating the filer
	 * @param imageDesc the image descriptor
	 * @param viewer the viewer the filter is to be applied to
	 * @param filter the filter
	 * @param initiallyChecked if the action should be initially checked
	 * @param inverse if the mode is inverse - meaning if the action is checked,
	 *            the filter is disabled
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

		String text = (active) ? (deactivateMessage) : (activateMessage);
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
