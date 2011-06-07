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

package eu.esdihumboldt.hale.ui.views.data.internal.tree;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerCell;

import eu.esdihumboldt.hale.instance.model.Group;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.Definition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionImages;

/**
 * Label provider for instances in a tree based on a 
 * {@link TypeDefinitionContentProvider}
 * @author Simon Templer
 */
public class DefinitionInstanceLabelProvider extends CellLabelProvider {

	private final Instance instance;
	
	private final DefinitionImages images = new DefinitionImages();
	
	/**
	 * Create an instance label provider for tree based on a 
	 * {@link TypeDefinition} 
	 * @param instance the instance to use 
	 */
	public DefinitionInstanceLabelProvider(Instance instance) {
		super();
		
		this.instance = instance;
	}

	/**
	 * @see CellLabelProvider#update(ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		TreePath treePath = cell.getViewerRow().getTreePath();
		
		// descend in instance
		Object value = instance;
		for (int i = 0; value != null && i < treePath.getSegmentCount(); i++) {
			Object segment = treePath.getSegment(i);
			if (segment instanceof ChildDefinition<?>) {
				ChildDefinition<?> child = (ChildDefinition<?>) segment;
				Object[] values = ((Group) value).getProperty(child.getName());
				if (values != null && values.length > 0) {
					value = values[0];
					//FIXME what about the other values? XXX mark cell? XXX create button for cell to see all for this instance?
				}
				else {
					value = null;
				}
			}
			else {
				//TODO log message?
				value = null;
			}
		}
		
		if (value == null) {
			cell.setText("");
		}
		else if (value instanceof Group) {
			cell.setText("+");
			//XXX use image instead
		}
		else {
			//TODO some kind of conversion?
			cell.setText(value.toString());
		}
		
		//XXX use definition images?
		Object lastSegment = treePath.getLastSegment();
		if (lastSegment instanceof Definition) {
			cell.setImage(images.getImage((Definition<?>) lastSegment));
		}
		
//		cell.setText(getText(element));
//		Image image = getImage(element);
//		cell.setImage(image);
//		cell.setBackground(getBackground(element));
//		cell.setForeground(getForeground(element));
//		cell.setFont(getFont(element));
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		images.dispose();
		
		super.dispose();
	}

	//TODO override some of the tooltip methods?!
	
}
