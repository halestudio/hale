/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.mapping;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.zest.core.viewers.GraphViewer;

/**
 * TODO Type description
 * 
 * @author Kai
 */
public class FilterCellAction extends Action {

	private final GraphViewer viewer;
	private final AlignmentViewContentProvider contentProvider;
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
	 * @param contentProvider the content provider of the viewer
	 * @param filter the filter
	 * @param initiallyChecked if the action should be initially checked
	 * @param inverse if the mode is inverse - meaning if the action is checked,
	 *            the filter is disabled
	 */
	public FilterCellAction(String activateMessage, String deactivateMessage,
			ImageDescriptor imageDesc, GraphViewer viewer,
			AlignmentViewContentProvider contentProvider, ViewerFilter filter,
			boolean initiallyChecked, boolean inverse) {
		super(activateMessage, AS_CHECK_BOX);
		this.viewer = viewer;
		this.contentProvider = contentProvider;
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
			contentProvider.addFilter(filter);
		}
		else {
			contentProvider.removeFilter(filter);
		}

		viewer.refresh();
		// XXX refresh(Object) documentation of GraphViewer lies! no automatic
		// layout!
		viewer.applyLayout();
	}
}
