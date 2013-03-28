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

package eu.esdihumboldt.hale.ui.service.entity.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Changes the content provider of a tree viewer.
 * 
 * @author Simon Templer
 */
public class ContentProviderAction extends Action {

	private final TreeViewer treeViewer;
	private final IContentProvider contentProvider;

	/**
	 * Create a radio action to set the content provider of a tree viewer.
	 * 
	 * @param label the action label
	 * @param image the action image
	 * @param contentProvider the content provider to set
	 * @param treeViewer the tree viewer to set the content provider on
	 * @param initiallyEnabled if the action should be initially enabled
	 */
	public ContentProviderAction(String label, ImageDescriptor image, TreeViewer treeViewer,
			IContentProvider contentProvider, boolean initiallyEnabled) {
		super(label, AS_RADIO_BUTTON);
		this.treeViewer = treeViewer;
		this.contentProvider = contentProvider;

		setImageDescriptor(image);
		setChecked(initiallyEnabled);
	}

	@Override
	public void run() {
		treeViewer.setContentProvider(contentProvider);
		treeViewer.refresh();
	}

}
