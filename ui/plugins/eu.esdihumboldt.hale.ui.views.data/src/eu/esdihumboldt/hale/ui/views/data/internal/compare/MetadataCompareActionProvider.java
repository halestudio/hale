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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.ui.views.data.internal.MetadataActionProvider;
import eu.esdihumboldt.util.Pair;

/**
 * Provides metadata actions for {@link TreeViewer}s used for data views compare
 * mode
 * 
 * @author Sebastian Reinhardt
 */
public class MetadataCompareActionProvider extends MetadataActionProvider {

	private List<Instance> instances;
	private Map<Integer, DefinitionInstanceLabelProvider> labelProviders;

	/**
	 * Standard Constructor
	 * 
	 * @param treeViewer the TreeViewer to which the action will be applied
	 */
	public MetadataCompareActionProvider(TreeViewer treeViewer) {
		super(treeViewer);
		this.instances = new ArrayList<Instance>();
	}

	/**
	 * Stores all instances and labelProviders
	 * 
	 * @param instances the instances
	 * @param labelProviders the labelProviders
	 */
	public void setInput(List<Instance> instances,
			Map<Integer, DefinitionInstanceLabelProvider> labelProviders) {
		this.instances = instances;
		this.labelProviders = labelProviders;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.data.internal.MetadataActionProvider#retrieveMetadata(org.eclipse.jface.viewers.ViewerCell)
	 */
	@Override
	protected Pair<Object, Object> retrieveMetadata(ViewerCell cell) {
		if (cell.getViewerRow().getTreePath().getFirstSegment() instanceof Set<?>) {
			if (cell.getElement() instanceof Set<?>) {
				return null;
			}

			String key = cell.getElement().toString();

			List<Object> values = instances.get(cell.getColumnIndex() - 1).getMetaData(key);
			Object value = values.get(labelProviders.get(cell.getColumnIndex()).getMetaDataChoice(
					key));
			return new Pair<Object, Object>(key, value);
		}
		else
			return null;
	}
}
