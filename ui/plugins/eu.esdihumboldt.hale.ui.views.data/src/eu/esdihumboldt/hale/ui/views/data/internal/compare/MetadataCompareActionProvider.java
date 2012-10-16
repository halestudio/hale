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

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;

import eu.esdihumboldt.hale.ui.views.data.internal.MetadataActionProvider;
import eu.esdihumboldt.util.Pair;

/**
 * Provides metadata actions for {@link TreeViewer}s used for data views compare
 * mode
 * 
 * @author Sebastian Reinhardt
 */
public class MetadataCompareActionProvider extends MetadataActionProvider {

	/**
	 * Standard Constructor
	 * 
	 * @param treeViewer the TreeViewer to which the action will be applied
	 */
	public MetadataCompareActionProvider(TreeViewer treeViewer) {
		super(treeViewer);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.data.internal.MetadataActionProvider#retrieveMetadata(org.eclipse.jface.viewers.ViewerCell)
	 */
	@Override
	protected Pair<Object, Object> retrieveMetadata(ViewerCell cell) {
		return new Pair<Object, Object>(cell.getElement().toString(), cell.getText());
	}

}
