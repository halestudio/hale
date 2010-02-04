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

package eu.esdihumboldt.hale.rcp.utils.tree;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;

/**
 * Label provider for a column of a tree with {@link TreeNode}s or
 * {@link AbstractMultiColumnTreeNode}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MultiColumnTreeNodeLabelProvider extends LabelProvider {
	
	/**
	 * The index of the column
	 */
	private final int columnIndex;

	/**
	 * Creates a new label provider for the column with the given index
	 * 
	 * @param columnIndex the column index (the index of the first column is zero)
	 */
	public MultiColumnTreeNodeLabelProvider(final int columnIndex) {
		super();
		
		this.columnIndex = columnIndex;
	}

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof TreeNode) {
			Object value = ((TreeNode) element).getValue();
			if (value != null) {
				if (value.getClass().isArray()) {
					Object[] values = (Object[]) value;
					if (columnIndex < values.length) {
						return getValueText(values[columnIndex]);
					}
				}
				else if (columnIndex == 0) {
					return getValueText(value);
				}
			}
		}
		
		return getDefaultText();
	}

	/**
	 * Get the default text when no value is available
	 * 
	 * @return the default text
	 */
	protected String getDefaultText() {
		return "";
	}

	/**
	 * Get the text for the given value
	 * 
	 * @param value the value
	 * 
	 * @return the text representing the value
	 */
	protected String getValueText(Object value) {
		return value.toString();
	}
	
}
