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

package eu.esdihumboldt.hale.ui.views.data.internal.explore;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;

import eu.esdihumboldt.hale.ui.views.data.internal.MetadataActionProvider;
import eu.esdihumboldt.util.Pair;

/**
 * Provides metadata actions for {@link TreeViewer}s used for data views explore
 * mode
 * 
 * @author Sebastian Reinhardt
 */
public class MetadataExploreActionProvider extends MetadataActionProvider {

	/**
	 * Standard Constructor
	 * 
	 * @param treeViewer the TreeViewer to which the action will be applied
	 */
	public MetadataExploreActionProvider(TreeViewer treeViewer) {
		super(treeViewer);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.data.internal.MetadataActionProvider#retrieveMetadata(org.eclipse.jface.viewers.ViewerCell)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Pair<Object, Object> retrieveMetadata(ViewerCell cell) {
		return (Pair<Object, Object>) cell.getElement();

	}

}
