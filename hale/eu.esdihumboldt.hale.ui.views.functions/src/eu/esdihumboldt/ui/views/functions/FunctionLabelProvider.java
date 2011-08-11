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

package eu.esdihumboldt.ui.views.functions;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.align.extension.category.Category;
import eu.esdihumboldt.hale.align.extension.function.AbstractFunction;

/**
 * Function label provider
 * @author Simon Templer
 */
public class FunctionLabelProvider extends LabelProvider {

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Category) {
			return ((Category) element).getName();
		}
		if (element instanceof AbstractFunction) {
			return ((AbstractFunction) element).getDisplayName();
		}
		
		return super.getText(element);
	}

	/**
	 * @see LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		//TODO get image based on getIconURL in AbstractFunction (an cache them) 
		
		// TODO Auto-generated method stub
		return super.getImage(element);
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		//TODO dispose any images created
		
		// TODO Auto-generated method stub
		super.dispose();
	}

}
