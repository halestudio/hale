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

package eu.esdihumboldt.hale.rcp.views.table.tree;

import org.opengis.feature.Feature;

import eu.esdihumboldt.hale.rcp.utils.tree.MultiColumnTreeNodeLabelProvider;

/**
 * Label provider for a feature column, falls back to {@link MultiColumnTreeNodeLabelProvider}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PropertyItemLabelProvider extends MultiColumnTreeNodeLabelProvider {
	
	/**
	 * The feature representing the column
	 */
	private final Feature feature;

	/**
	 * Creates a new feature column label provider
	 * 
	 * @param feature the feature representing the column
	 * @param columnIndex the column index
	 */
	public PropertyItemLabelProvider(Feature feature, int columnIndex) {
		super(columnIndex);
		
		this.feature = feature;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PropertyItem) {
			return ((PropertyItem) element).getText(feature);
		}
		else {
			return super.getText(element);
		}
	}

}